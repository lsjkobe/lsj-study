package com.lsj.repush.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * RetryUtil
 *
 * @author lishangj
 * @version 1.0
 * @date 2022-01-17 16:44
 */
@Slf4j
public class RetryUtil {

//    /**
//     * 通用重试 超出重试次数抛出异常.
//     *
//     * @param handler    无参数无返回处理器（处理重试逻辑）
//     * @param retryCount 重试次数
//     * @param pauseTime  重试停顿时间
//     * @throws Exception .
//     */
//    public static void retryCommon(INoneArgHandler handler, int retryCount, long pauseTime) throws Exception {
//        retryCommon(handler, retryCount, pauseTime, "");
//    }
//
//    /**
//     * 通用重试 超出重试次数抛出异常.
//     *
//     * @param handler    无参数无返回处理器（处理重试逻辑）
//     * @param retryCount 重试次数
//     * @param pauseTime  重试停顿时间(毫秒)
//     * @throws Exception .
//     */
//    public static void retryCommon(INoneArgHandler handler, int retryCount, long pauseTime, String businessLog) throws Exception {
//        int curRetryNum = 0;
//        for (; ; ) {
//            try {
//                handler.handle();
//                break;
//            } catch (Exception e) {
//                if (++curRetryNum > retryCount) {
//                    throw new Exception("超出重试次数", e);
//                }
//                log.info("retryCommon:{} 重试：第 {} 次", businessLog, curRetryNum);
//                TimeUnit.MILLISECONDS.sleep(pauseTime);
//            }
//        }
//    }
//
//    /**
//     * 超出重试时间抛出异常.
//     *
//     * @param handler      无参数无返回处理器（处理重试逻辑）
//     * @param startTime 开始时间 *注意：同一个重试每次传进来的时间一定要相等
//     * @param timeout      重试时间(毫秒)
//     * @param pauseTime    重试停顿时间(毫秒)
//     * @param canRetryFunc 是否能重试
//     * @throws Exception .
//     */
//    public static void retryWithTimeout(INoneArgCheckHandler handler, Date startTime, long timeout, long pauseTime, Function<Exception, Boolean> canRetryFunc) throws Exception {
//        for (; ; ) {
//            try {
//                handler.handle();
//                break;
//            } catch (Exception e) {
//                if (canRetryFunc != null && !canRetryFunc.apply(e)) {
//                    throw e;
//                }
//                if (DateUtil.betweenMs(startTime, new Date()) > timeout) {
//                    throw new Exception("超出重试时间:" + timeout + "毫秒", e);
//                }
//                TimeUnit.MILLISECONDS.sleep(pauseTime);
//            }
//        }
//    }
}
