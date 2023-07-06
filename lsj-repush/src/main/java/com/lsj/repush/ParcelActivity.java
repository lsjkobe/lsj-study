package com.lsj.repush;

import lombok.Data;

/**
 * @author lishangj
 */
@Data
public class ParcelActivity {
    private Params params;
    private String tenantCode;


    @Data
    public static class Params {
        private String activityType;
        private String selectType;
        private String nodeCode;
        private String activityCode;
        private boolean needUpdateState;
        private String resourceCode;
        private boolean needPreActivityCheck;
        private String preActivityType;
        private boolean isCroBorSecStage;
        private int orderType;
        private String mainCode;

    }
}
