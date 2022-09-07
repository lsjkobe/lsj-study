package com.lsj.study.excelbiz.model.quote;

import lombok.Data;

import java.util.List;

/**
 * QuoteDownloadExcel
 *
 * @author by lishangj
 * @date 2022/9/6 15:37
 */
@Data
public class QuoteDownloadExcel {

    /**
     * 客户名称.
     */
    private String customerName;

    /**
     * 销售电话.
     */
    private String salesPhoneNumber;

    /**
     * 报价日期.
     */
    private String quoteDate;

    /**
     * vat税率序号（根据产品个数变化）
     */
    private Integer vatRateIndex;

    /**
     * 异常件序号（根据产品个数变化）
     */
    private Integer abnormalIndex;

    /**
     * 产品列表.
     */
    private List<QuoteProductExcel> quoteProductExcelList;

    public void setVatRateIndex(Integer vatRateIndex) {
        this.vatRateIndex = vatRateIndex;
        //固定，异常件序号为vat税率+1
        if (vatRateIndex != null) {
            this.abnormalIndex = vatRateIndex + 1;
        }
    }
}
