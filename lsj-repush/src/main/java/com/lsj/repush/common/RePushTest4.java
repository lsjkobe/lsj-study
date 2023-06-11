package com.lsj.repush.common;

import cn.hutool.json.JSONUtil;
import com.lsj.repush.ExcelReader;
import com.lsj.repush.ParcelActivity;
import com.lsj.repush.QfTest;
import com.lsj.repush.ResponseData;
import com.lsj.repush.utils.BatchHandleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
public class RePushTest4 {
    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 50, 10000, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        RePushTest4 rePushTest = new RePushTest4();
        // 创建 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();
        // 创建连接池管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connectionManager.setMaxTotal(200);
        // 设置每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(200);
        // 创建自定义的HttpClient
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        // 创建自定义的连接工厂
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        rePushTest.doRepush(restTemplate);
    }

    private static final String PRE_PATH = "E:\\lsj\\workspace\\lsj\\backend\\lsj-study\\lsj-repush\\切分\\";

    private void doRepush(RestTemplate restTemplate) {
        int startCount = 0;
        int endCount = 1;
        for (int i = startCount; i < endCount; i++) {
            String repushPath = PRE_PATH + "split_" + (i + 1) + ".xlsx";
            repush(restTemplate, repushPath);
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
        CountDownLatch countDownLatch = new CountDownLatch(finalSet.size());
        for (String lpCode : finalSet) {
            threadPoolExecutor.execute(() -> {
                boolean isSuccess = Boolean.TRUE;
                try {
                    ParcelActivity parcelActivity = buildParcelActivity(lpCode);
                    String str = JSONUtil.toJsonStr(parcelActivity);
//                    log.info("{}-生成的请求报文:{}", lpCode, str);
                    doRepushSingle(restTemplate, lpCode, str);
                } catch (Exception e) {
                    log.error("单号:{},请求失败", lpCode, e);
                    isSuccess = Boolean.FALSE;
                } finally {
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
                "http://localhost:6060/repush/test",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
//        log.info("单号:{},请求参数{}, 请求返回{}", lpCode, str, JSONUtil.toJsonStr(response));
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
        return "";
    }
}
