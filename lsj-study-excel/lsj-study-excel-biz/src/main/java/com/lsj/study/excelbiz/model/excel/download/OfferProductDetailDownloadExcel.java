package com.lsj.study.excelbiz.model.excel.download;
import lombok.Data;

/**
 * QuoteProductExcel
 *
 * @author by lishangj
 * @date 2022/9/6 15:37
 */
@Data
public class OfferProductDetailDownloadExcel {

    /**
     * 序号.
     */
    private Integer index;

    /**
     * 国家/地区.
     */
    private String country;

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
}
