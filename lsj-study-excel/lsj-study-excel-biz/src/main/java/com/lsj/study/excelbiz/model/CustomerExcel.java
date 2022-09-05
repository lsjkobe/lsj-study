package com.lsj.study.excelbiz.model;

import com.alibaba.excel.annotation.ExcelProperty;
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

    @ExcelProperty(value = "客户名称")
    private String name;

    @ExcelProperty(value = "客户编码")
    private String code;

    @ExcelProperty(value = "客户层级")
    private String level;

    @ExcelProperty(value = "FS销售")
    private String fsSales;

    @ExcelProperty(value = "分公司")
    private String branch;

    @ExcelProperty(value = "最近30天货量(票)")
    private String recentShipments;

    @ExcelProperty(value = "最近30天泡比")
    private String recentBubbleRatio;

    @ExcelProperty(value = "申请价格类型")
    private String priceType;

    @ExcelProperty(value = "申请时长")
    private String applicationTime;

    @ExcelProperty(value = "产品")
    private String productName;

    @ExcelProperty(value = "销售区域")
    private String productSalesArea;

    @ExcelProperty(value = "国家")
    private String country;

    @ExcelProperty(value = "重量段(克)")
    private String weightSegment;

    @ExcelProperty(value = {"公布价", "速递运费"})
    private String publishedExpressShipping;

    @ExcelProperty(value = {"公布价", "挂号费"})
    private String publishedRegistrationFee;

    @ExcelProperty(value = {"原等级价/协议价", "速递运费"})
    private String expressShipping;

    @ExcelProperty(value = {"原等级价/协议价", "挂号费"})
    private String registrationFee;

    @ExcelProperty(value = {"原等级价/协议价", "有效期"})
    private String validityPeriod;

    @ExcelProperty(value = {"申请价格", "速递运费"})
    private String applyExpressShipping;

    @ExcelProperty(value = {"申请价格", "挂号费"})
    private String applyRegistrationFee;

    @ExcelProperty(value = {"对比公布价", "速递运费"})
    private String comparativeExpressShipping;

    @ExcelProperty(value = {"对比公布价", "挂号费"})
    private String comparativeRegistrationFee;

    @ExcelProperty(value = "承诺日均货量(票)")
    private String commitmentDailyVolume;

    @ExcelProperty(value = "票均重量(克)")
    private String averageTicketWeight;
}
