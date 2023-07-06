package com.lsj.repush.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * BatchHandleUtil 分批处理工具类.
 *
 * @author lishangj
 * @version 1.0
 * @date 2022-02-16 14:26
 */
@Slf4j
public class BatchHandleUtil {

    /**
     * 分批次处理
     *
     * @param handleList 需要分批的列表
     * @param pageSize   每批次数量
     * @param consumer   分批后处理方法
     */
    public static <T> void batchHandle(List<T> handleList, int pageSize, Consumer<List<T>> consumer) {
        int totalPage = PageUtil.totalPage(handleList.size(), pageSize);
        for (int i = 0; i < totalPage; i++) {
            //  子集合
            List<T> pageList = ListUtil.page(i, pageSize, handleList);
            consumer.accept(pageList);
        }
    }

    /**
     * 分批次处理
     *
     * @param handleList 需要分批的列表
     * @param pageSize   每批次数量
     * @param consumer   分批后处理方法
     */
    public static <T> void batchHandleWithThreadPool(List<T> handleList, int pageSize, Consumer<List<T>> consumer, ThreadPoolTaskExecutor threadPool) {
        int totalPage = PageUtil.totalPage(handleList.size(), pageSize);
        CountDownLatch batchCountDownLatch = new CountDownLatch(totalPage);
        for (int i = 0; i < totalPage; i++) {
            //  子集合
            List<T> pageList = ListUtil.page(i, pageSize, handleList);
            threadPool.execute(() -> {
                try {
                    consumer.accept(pageList);
                } finally {
                    batchCountDownLatch.countDown();
                }
            });
        }
        try {
            batchCountDownLatch.await();
        } catch (InterruptedException e) {
            log.error("线程池分批次处理失败", e);
        }
    }
}
