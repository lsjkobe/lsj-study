package com.lsj.study.excelbiz.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Getter;
import lombok.Setter;

/**
 * CustomerExcel .
 *
 * @author lsj
 * @date 2022-09-05 14:26
 */
@Setter
@Getter
public class CustomerExcel {

//    @ColumnWidth(8)
    @ExcelProperty(value = "客户名称")
    private String name;

//    @ColumnWidth(8)
    @ExcelProperty(value = "客户编码")
    private String code;

//    @ColumnWidth(8)
    @ExcelProperty(value = "客户层级")
    private String level;

//    @ColumnWidth(9)
    @ExcelProperty(value = "FS销售")
    private String fsSales;

    @ColumnWidth(12)
    @ExcelProperty(value = "分公司")
    private String branch;

    @ColumnWidth(19)
    @ExcelProperty(value = "最近30天货量(票)")
    private Integer recentShipments;


    @ColumnWidth(15)
    @ExcelProperty(value = "最近30天泡比")
    private String recentBubbleRatio;

    @ColumnWidth(12)
    @ExcelProperty(value = "申请价格类型")
    private String priceType;

    @ColumnWidth(9)
    @ExcelProperty(value = "申请时长")
    private String applicationTime;

    @ColumnWidth(24)
    @ExcelProperty(value = "产品")
    private String productName;

    @ColumnWidth(15)
    @ExcelProperty(value = "销售区域")
    private String productSalesArea;

    @ExcelProperty(value = "国家")
    private String country;

    @ColumnWidth(15)
    @ExcelProperty(value = "重量段(克)")
    private String weightSegment;

    @ColumnWidth(16)
    @ExcelProperty(value = {"公布价", "速递运费"})
    private String publishedExpressShipping;

    @ColumnWidth(16)
    @ExcelProperty(value = {"公布价", "挂号费"})
    private String publishedRegistrationFee;

    @ColumnWidth(16)
    @ExcelProperty(value = {"原等级价/协议价", "速递运费"})
    private String expressShipping;

    @ColumnWidth(16)
    @ExcelProperty(value = {"原等级价/协议价", "挂号费"})
    private String registrationFee;

    @ColumnWidth(32)
    @ExcelProperty(value = {"原等级价/协议价", "有效期"})
    private String validityPeriod;

    @ColumnWidth(16)
    @ExcelProperty(value = {"申请价格", "速递运费"})
    private String applyExpressShipping;

    @ColumnWidth(16)
    @ExcelProperty(value = {"申请价格", "挂号费"})
    private String applyRegistrationFee;

    @ColumnWidth(14)
    @ExcelProperty(value = {"对比公布价", "速递运费"})
    private String comparativeExpressShipping;

    @ColumnWidth(14)
    @ExcelProperty(value = {"对比公布价", "挂号费"})
    private String comparativeRegistrationFee;

    @ColumnWidth(16)
    @ExcelProperty(value = "承诺日均货量(票)")
    private Integer commitmentDailyVolume;

    @ColumnWidth(16)
    @ExcelProperty(value = "票均重量(克)")
    private Integer averageTicketWeight;
}
