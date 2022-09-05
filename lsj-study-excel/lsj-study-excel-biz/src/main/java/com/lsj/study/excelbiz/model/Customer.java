package com.lsj.study.excelbiz.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Customer .
 *
 * @author lsj
 * @date 2022-09-05 14:18
 */
@Data
@Builder
public class Customer implements Serializable {

    private static final long serialVersionUID = -4579255105414583872L;

    private String name;

    private String code;

    private String level;

    private String fsSales;

    private String branch;

    private String recentShipments;

    private String recentBubbleRatio;

    private String priceType;

    private String applicationTime;

    private List<Product> productList;
}
