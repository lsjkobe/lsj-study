package com.lsj.study.excelbiz.model.excel.download;

import lombok.Data;

/**
 * OfferWeightSegmentExcelDownloadDTO
 *
 * @author by lishangj
 * @date 2022/9/6 15:37
 */
@Data
public class OfferWeightSegmentDownloadExcelDTO {

    /**
     * 重量段.
     */
    private String weightSegment;

    /**
     * 运费.
     */
    private String expressFreight;

    /**
     * 挂号费.
     */
    private String registrationFee;

}
