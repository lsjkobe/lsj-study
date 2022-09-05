package com.lsj.study.excelbiz.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * ProductDetail .
 *
 * @author lsj
 * @date 2022-09-05 17:14
 */
@Data
@Builder
public class ProductDetail implements Serializable {
    private String country;

    private String weightSegment;

    private String publishedExpressShipping;

    private String publishedRegistrationFee;

    private String expressShipping;

    private String registrationFee;

    private String validityPeriod;

    private String applyExpressShipping;

    private String applyRegistrationFee;

    private String comparativeExpressShipping;

    private String comparativeRegistrationFee;

    private String commitmentDailyVolume;

    private String averageTicketWeight;
}
