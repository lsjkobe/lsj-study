package com.lsj.repush;

import cn.hutool.json.JSONUtil;
import com.lsj.repush.utils.BatchHandleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lishangj
 */
@Slf4j
public class RePushTest {
    public static void main(String[] args) {
        RePushTest rePushTest = new RePushTest();
        // 创建 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();
        // 设置连接参数
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000); // 设置连接超时时间为5秒
        requestFactory.setReadTimeout(5000); // 设置读取超时时间为5秒
        restTemplate.setRequestFactory(requestFactory);
        rePushTest.doRepush(restTemplate);
    }

    private static final String PRE_PATH = "C:\\Users\\lishangj\\Desktop\\lsj\\首公里\\菜鸟\\重推\\切分\\";

    private void doRepush(RestTemplate restTemplate) {
        int startCount = 1;
        int endCount = 10;
        for (int i = startCount; i < endCount; i++) {
            String repushPath = PRE_PATH + "split_" + (i + 1) + ".xlsx";
            repush(restTemplate, repushPath);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void repush(RestTemplate restTemplate, String path) {
        boolean isSuccess = Boolean.TRUE;
        try {
            List<QfTest.DataModel> dataModelList = ExcelReader.readExcelToList(path);
            BatchHandleUtil.batchHandle(dataModelList, 500, subList -> {
                Set<String> finalSet = subList.stream().map(item -> StringUtils.trim(item.getValue())).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                log.info("{}-原始个数:{}-去重后个数:{}", path, subList.size(), finalSet.size());
                repushWithPool(restTemplate, finalSet);
            });
        } catch (Exception e) {
            log.error("失败路径-{}", path, e);
            isSuccess = Boolean.FALSE;
        } finally {
            log.info("[repush]执行:{}-{}", path, isSuccess);
        }
    }

    public void repushWithPool(RestTemplate restTemplate, Set<String> finalSet) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 10000, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
        CountDownLatch countDownLatch = new CountDownLatch(finalSet.size());
        for (String lpCode : finalSet) {
            threadPoolExecutor.execute(() -> {
                boolean isSuccess = Boolean.TRUE;
                try {
                    ParcelActivity parcelActivity = buildParcelActivity(lpCode);
                    String str = JSONUtil.toJsonStr(parcelActivity);
                    log.info("{}-生成的请求报文:{}", lpCode, str);
                    doRepushSingle(restTemplate, lpCode, str);
                } catch (Exception e) {
                    log.error("单号:{},请求失败", lpCode, e);
                    isSuccess = Boolean.FALSE;
                }finally {
                    countDownLatch.countDown();
                    log.info("[repushWithPool][lpCode]单号{},执行结果:{}", lpCode, isSuccess);
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }





    private void doRepushSingle(RestTemplate restTemplate, String lpCode, String str) {
        // 设置请求头部
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 设置Cookie值
        headers.set("Cookie", getCookie());
        // 构建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(str, headers);

        // 发送 POST 请求
        ResponseEntity<String> response = restTemplate.exchange(
                "https://os.gpn.cainiao-inc.com/packagenetwork-default-workspace/GLOBAL-PACKAGE-NEWWORK/reActivity/doResend",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        log.info("单号:{},请求参数{}, 请求返回{}", lpCode, str, JSONUtil.toJsonStr(response));
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            ResponseData responseData = JSONUtil.toBean(response.getBody(), ResponseData.class);
        } else {
            throw new RuntimeException(response.getBody());
        }
    }

    private ParcelActivity buildParcelActivity(String lpCode) {
        ParcelActivity parcelActivity = new ParcelActivity();
        ParcelActivity.Params params = new ParcelActivity.Params();
        params.setActivityType("parcel");
        params.setSelectType("LP_CODE");
        params.setNodeCode("SORTING_CENTER");
        params.setActivityCode("SORTING_CENTER_ORDER_INFO_NOTIFY");
        params.setNeedUpdateState(false);
        params.setResourceCode("Tran_Store_13452544");
        params.setNeedPreActivityCheck(false);
        params.setPreActivityType("parcel");
        params.setCroBorSecStage(false);
        params.setOrderType(0);
        params.setMainCode(lpCode);
        parcelActivity.setParams(params);
        parcelActivity.setTenantCode("AE_GLOBAL@CAINIAO_GLOBAL");
        return parcelActivity;
    }

    private String getCookie() {
        return "SSO_LANG_V2=ZH-CN; SSO_EMPID_HASH_V2=4dff3da6f5a0747f30609518e8972652; SSO_BU_HASH_V2=1e387a787818f77b803a555735045ffa; cna=GfLTG2JNhX0CAWoLHwuK0rq4; XSRF-TOKEN=a95c5c3b-342c-4dc3-b269-4b507011e155; cn-groot_USER_COOKIE=8E6B977E10D651C60D056AABFC0C909148A7BCF68487812D0FEADE7E7571D04648AE9035AEE262F5D9689AAF790FBDDDF6E3B00277AC8CADB76A7671A11272BA520F1A08FA0B1D5BF2286EA1C072B8028BF40300D92CCCBC312F388A4E7B21F8CC457FAEEEF748913F68E14CDC6C078F71D8678414B4DD470E4DF08A752B33E9BF9A5734DE4519BF3E0E8C436F15C7B7B48157B976495AE6D68B7F0BB0221DF004DA7C3A435A0AF44710283464DAD605A5AC85920DF07FFB6077DBD8D54DCA70AB67388529380081C17652CCB64B085F921CAE29648B1DD9EEEC2F22901240EA7F9A7475314CB86F9CE4AC405451174D963540E457B4B6301B040DC6623518D768CAFBE3A407178E005531AE5870F65944171F890BA1EE7974D9F1C8EE99D57C8D4788C40A47F5B20F88ECFBDE94BAB15736685C53588BB32A9C26D458F114C6014552938B962AA88F7A19492A32B874641A6D0188FFD45D66CF61BB6EBEA214788AF3E27468C09C9B52F3B52289B8FA; xlly_s=1; cn-groot_SSO_TOKEN_V2=4B43A44A399652829AFC744703A0B46B482BCF8BA43F2E0C34100DF736F81FF871334C41D1040B3A75B525866761F93207ACE819E2B88DB986D7E2B0E02E04CD; tfstk=cBqCB_mMNBABprvNG06acKajiOodC8vjFwG3dsaweGSAFNIts31mOnlfatzkw0k-C; isg=BFNTgR2uM_hCI__mdWbtMyl64td9COfK30AIVwV9C3OrhGwmltgOGnKVvvTqID_C; lang=zh-CN; x-hng=lang=zh-CN; gcep.mcms.language=zh-CN";
    }
}
