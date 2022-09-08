package com.lsj.study.excelbiz.model.excel.download;

import lombok.Data;

import java.util.List;

/**
 * OfferProductDownload
 *
 * @author by lishangj
 * @date 2022/9/7 15:51
 */
@Data
public class OfferProductDownloadExcelDTO {

    /**
     * 渠道.
     */
    private String channel;

    /**
     * 国家/分区.
     */
    private String countries;

    /**
     * 产品名称.
     */
    private String name;

    /**
     * 产品名称.
     */
    private String code;

    /**
     * 销售区域.
     */
    private String region;

    /**
     * 时效.
     */
    private String timely;

    /**
     * 产品介绍.
     */
    private String desc;

    /**
     * 产品详情.
     */
    private List<OfferProductDetailDownloadExcelDTO> productDetailList;
}
