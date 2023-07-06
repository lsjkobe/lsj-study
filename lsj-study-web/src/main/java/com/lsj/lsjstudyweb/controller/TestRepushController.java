package com.lsj.lsjstudyweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * TestRepushController .
 *
 * @author lsj
 * @date 2023-06-10 11:06
 */
@Slf4j
@RestController
@RequestMapping("/repush")
public class TestRepushController {

    @PostMapping("/test")
    public ResponseData test(@RequestBody ParcelActivity parcelActivity) throws InterruptedException {
        log.info("接收:{}", parcelActivity.getParams().getMainCode());
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(Boolean.TRUE);
        ResponseData.ResData resData = new ResponseData.ResData();
        Integer randomInt = new Random().nextInt(1000);
        if (randomInt == 1) {
            responseData.setSuccess(Boolean.FALSE);
            responseData.setErrorMsg("非平台订单");
        } else if (randomInt == 2) {
            responseData.setSuccess(Boolean.FALSE);
            responseData.setErrorMsg("测试错误");
        }
        resData.setSuccOrders(Collections.singletonList(parcelActivity.getParams().getMainCode()));
        responseData.setData(resData);
        Thread.sleep(500);
        return responseData;
    }
}
