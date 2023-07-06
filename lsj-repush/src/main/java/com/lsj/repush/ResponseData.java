package com.lsj.repush;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ResponseData {
    private ResData data;
    private boolean success;

    private String errorCode;

    private String errorMsg;

    @Data
    public static class ResData {
        private int succNum;
        private List<String> succOrders;
        private int subTaskSuccessCount;
        private Map<String, String> failOrders;
        private int failNum;
        private int subTaskCount;
        private Map<String, Object> params;

        // Getters and Setters

        @Override
        public String toString() {
            return "Data{" +
                    "succNum=" + succNum +
                    ", succOrders=" + succOrders +
                    ", subTaskSuccessCount=" + subTaskSuccessCount +
                    ", failOrders=" + failOrders +
                    ", failNum=" + failNum +
                    ", subTaskCount=" + subTaskCount +
                    ", params=" + params +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "ResponseData{" +
                "data=" + data +
                ", success=" + success +
                ", errorCode=" + errorCode +
                ", errorMsg=" + errorMsg +
                '}';
    }
}
