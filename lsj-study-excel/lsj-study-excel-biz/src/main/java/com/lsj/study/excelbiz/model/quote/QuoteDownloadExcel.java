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
}
