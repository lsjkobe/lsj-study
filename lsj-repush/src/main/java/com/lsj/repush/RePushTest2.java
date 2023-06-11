package com.lsj.repush;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.json.JSONUtil;
import com.lsj.repush.utils.BatchHandleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author lishangj
 */
@Slf4j
public class RePushTest2 {

    private static volatile String COOKIES = "";

    private static final String PRE_PATH = "/Users/saibo.yf/Documents/Corps/Cainiao - 4PX/履行融合/切分/";

    private static final String errRetry = PRE_PATH + "failedList.csv";

    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 100, 30, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    static FileWriter fw = null;


    public static void main(String[] args) {

        try {
            File f = new File(errRetry);
            if (f.exists()) {
                f.delete();
            }
            fw = new FileWriter(f);

            RePushTest2 rePushTest = new RePushTest2();

            // 创建 RestTemplate 实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置连接参数
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

            requestFactory.setConnectTimeout(20000); // 设置连接超时时间为5秒

            requestFactory.setReadTimeout(500000); // 设置读取超时时间为5秒

            restTemplate.setRequestFactory(requestFactory);

            //for(int i=129; i<= 649; ){
            //
            //    //前开后闭
            //    rePushTest.doRepush(restTemplate,i,Math.min(649,i+10));
            //
            //    i+=10;
            //
            //}

            //前开后闭
            rePushTest.doRepush(restTemplate, 1, 20);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }




    private void doRepush(RestTemplate restTemplate, int startCount, int endCount) {
        CountDownLatch countDownLatch = new CountDownLatch(endCount-startCount+1);
        for (int i = startCount; i < endCount; i++) {
            String repushPath = PRE_PATH + "split_" + (i + 1) + ".xlsx";
            repush(restTemplate, repushPath);
            countDownLatch.countDown();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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

        //CountDownLatch countDownLatch = new CountDownLatch(finalSet.size());

        for (String lpCode : finalSet) {

            threadPoolExecutor.execute(() -> {

                boolean isSuccess = Boolean.TRUE;

                try {

                    ParcelActivity parcelActivity = buildParcelActivity(lpCode);

                    String str = JSONUtil.toJsonStr(parcelActivity);

                    //log.info("{}-生成的请求报文:{}", lpCode, str);

                    doRepushSingle(restTemplate, lpCode, str);

                } catch (Exception e) {

                    log.error("单号:{},请求失败", lpCode, e);
                    isSuccess = Boolean.FALSE;
                    try {
                        fw.write(lpCode+"\n");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }


                }finally {

                    //countDownLatch.countDown();

                    log.info("[repushWithPool][lpCode]单号{},执行结果:{}", lpCode, isSuccess);

                }

            });

        }

        //try {
        //
        //    countDownLatch.await();
        //
        //} catch (InterruptedException e) {
        //
        //    throw new RuntimeException(e);
        //
        //}

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

        //log.info("单号:{},请求参数{}, 请求返回{}", lpCode, str, JSONUtil.toJsonStr(response));

        if (HttpStatus.OK.equals(response.getStatusCode())) {

            ResponseData responseData = JSONUtil.toBean(response.getBody(), ResponseData.class);
            if (!responseData.isSuccess() && !responseData.getErrorMsg().endsWith("非GSDP平台订单")) {
                throw new RuntimeException(response.getBody());
            }

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
