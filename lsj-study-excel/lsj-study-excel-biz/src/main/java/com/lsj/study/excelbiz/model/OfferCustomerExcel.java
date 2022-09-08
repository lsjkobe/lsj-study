package com.lsj.study.excelbiz.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Customer .
 *
 * @author lsj
 * @date 2022-09-05 14:18
 */
@Data
@Builder
public class OfferCustomerExcel implements Serializable {

    private static final long serialVersionUID = -4579255105414583872L;

    /**
     * 客户名称.
     */
    private String customerName;

    /**
     * 客户编码.
     */
    private String customerCode;

    /**
     * 客户层级.
     */
    private String customerLevel;

    /**
     * FS销售.
     */
    private String fsTraceName;

    /**
     * 分公司名称.
     */
    private String orgDesc;

    /**
     * 最近30天货量(票).
     */
    private Integer lastThirtyQuantity;

    /**
     * 最近30天泡比.
     */
    private BigDecimal lastThirtyRatioPucker;

    /**
     * 申请价格类型.
     */
    private String quotationType;

    /**
     * 申请时长.
     */
    private String validityDay;

    /**
     * 产品列表.
     */
    private List<OfferProductExcel> productList;
}
