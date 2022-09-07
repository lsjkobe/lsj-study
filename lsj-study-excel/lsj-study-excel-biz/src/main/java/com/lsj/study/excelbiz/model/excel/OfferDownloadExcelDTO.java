package com.lsj.study.excelbiz.model.excel;

import lombok.Data;

import java.util.List;

/**
 * QuoteDownloadExcel
 *
 * @author by lishangj
 * @date 2022/9/6 15:37
 */
@Data
public class OfferDownloadExcelDTO {

    /**
     * 客户名称.
     */
    private String customerName;

    /**
     * 销售电话.
     */
    private String salesPhoneNumber;

    /**
     * 产品列表.
     */
    private List<OfferProductDownloadExcelDTO> productList;

}
