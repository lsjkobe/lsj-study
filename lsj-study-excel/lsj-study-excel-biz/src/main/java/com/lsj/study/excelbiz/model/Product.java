package com.lsj.study.excelbiz.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Product .
 *
 * @author lsj
 * @date 2022-09-05 14:24
 */
@Builder
@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 3915926763646814453L;

    private String name;

    private String code;

    private String salesArea;

    private List<ProductDetail> productDetailList;
}
