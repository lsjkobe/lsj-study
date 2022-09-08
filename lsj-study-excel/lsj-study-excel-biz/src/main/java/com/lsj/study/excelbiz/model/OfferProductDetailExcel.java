package com.lsj.study.excelbiz.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ProductDetail .
 *
 * @author lsj
 * @date 2022-09-05 17:14
 */
@Data
@Builder
public class OfferProductDetailExcel implements Serializable {

    /**
     * 国家/地区.
     */
    private String country;

    /**
     * 重量段（eg:0-100G） .
     */
    private String weightSegment;

    /**
     * 公布价-速递运费.
     */
    private BigDecimal publishedExpressFreight;

    /**
     * 公布价-挂号费.
     */
    private BigDecimal publishedRegistrationFee;

    /**
     * 原等级价/协议价-速递运费.
     */
    private BigDecimal agreementExpressFreight;

    /**
     * 原等级价/协议价-挂号费.
     */
    private BigDecimal agreementRegistrationFee;

    /**
     * 原等级价/协议价-有效期 (eg:2020-09-09—2020-09-10).
     */
    private String agreementDatePeriod;

    /**
     * 申请价格-速递运费.
     */
    private BigDecimal applyExpressFreight;

    /**
     * 申请价格-挂号费.
     */
    private BigDecimal applyRegistrationFee;

    /**
     * 对比公布价-速递运费.
     */
    private String publishedExpressFellFreight;

    /**
     * 对比公布价-挂号费.
     */
    private String publishedRegistrationFellFee;

    /**
     * 承诺日均货量(票).
     */
    private Integer dailyAvgGoods;

    /**
     * 票均重量(克).
     */
    private Integer dailyAvgWeight;

    /**
     * 毛利率(%).
     */
    private String grossProfitStr;
}
