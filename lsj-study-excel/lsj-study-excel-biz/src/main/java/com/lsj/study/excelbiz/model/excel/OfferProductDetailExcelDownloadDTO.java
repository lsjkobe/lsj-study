package com.lsj.study.excelbiz.model.excel;

import lombok.Data;

import java.util.List;

/**
 * QuoteProductExcel
 *
 * @author by lishangj
 * @date 2022/9/6 15:37
 */
@Data
public class OfferProductDetailExcelDownloadDTO {

    /**
     * 国家/地区.
     */
    private String country;

    /**
     * 尺寸限制.
     */
    private String sizeLimit;

    /**
     * 时效.
     */
    private String timely;

    /**
     * 产品备注.
     */
    private String productRemark;

    /**
     * 重量段详情.
     */
    private List<OfferWeightSegmentExcelDownloadDTO> weightSegmentList;
}
