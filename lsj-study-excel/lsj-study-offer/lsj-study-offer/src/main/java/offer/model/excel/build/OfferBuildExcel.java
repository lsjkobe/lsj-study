package offer.model.excel.build;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * CustomerExcel .
 *
 * @author lsj
 * @date 2022-09-05 14:26
 */
@Setter
@Getter
public class OfferBuildExcel {

    @ExcelProperty(value = "客户名称")
    private String customerName;

    @ExcelProperty(value = "客户编码")
    private String customerCode;

    @ExcelProperty(value = "客户层级")
    private String customerLevel;

    @ExcelProperty(value = "FS销售")
    private String fsTraceName;

    @ColumnWidth(12)
    @ExcelProperty(value = "分公司名称")
    private String orgDesc;

    @ColumnWidth(19)
    @ExcelProperty(value = "最近30天货量(票)")
    private Integer lastThirtyQuantity;


    @ColumnWidth(15)
    @ExcelProperty(value = "最近30天泡比")
    private BigDecimal lastThirtyRatioPucker;

    @ColumnWidth(12)
    @ExcelProperty(value = "申请价格类型")
    private String quotationType;

    @ColumnWidth(9)
    @ExcelProperty(value = "申请时长")
    private String validityDay;

    @ColumnWidth(24)
    @ExcelProperty(value = "产品")
    private String productName;

    @ColumnWidth(15)
    @ExcelProperty(value = "销售区域")
    private String region;

    @ExcelProperty(value = "国家")
    private String country;

    /**
     * 重量段（eg:0-100G） .
     */
    @ColumnWidth(15)
    @ExcelProperty(value = "重量段(克)")
    private String weightSegment;

    @ColumnWidth(16)
    @ExcelProperty(value = {"公布价", "速递运费"})
    private BigDecimal publishedExpressFreight;

    @ColumnWidth(16)
    @ExcelProperty(value = {"公布价", "挂号费"})
    private BigDecimal publishedRegistrationFee;

    @ColumnWidth(16)
    @ExcelProperty(value = {"原等级价/协议价", "速递运费"})
    private BigDecimal agreementExpressFreight;

    @ColumnWidth(16)
    @ExcelProperty(value = {"原等级价/协议价", "挂号费"})
    private BigDecimal agreementRegistrationFee;

    /**
     * 有效期 (eg:2020-09-09—2020-09-10).
     */
    @ColumnWidth(32)
    @ExcelProperty(value = {"原等级价/协议价", "有效期"})
    private String agreementDatePeriod;

    @ColumnWidth(16)
    @ExcelProperty(value = {"申请价格", "速递运费"})
    private BigDecimal applyExpressFreight;

    @ColumnWidth(16)
    @ExcelProperty(value = {"申请价格", "挂号费"})
    private BigDecimal applyRegistrationFee;

    @ColumnWidth(14)
    @ExcelProperty(value = {"对比公布价", "速递运费"})
    private String publishedExpressFellFreight;

    @ColumnWidth(14)
    @ExcelProperty(value = {"对比公布价", "挂号费"})
    private String publishedRegistrationFellFee;

    @ColumnWidth(16)
    @ExcelProperty(value = "承诺日均货量(票)")
    private Integer dailyAvgGoods;

    @ColumnWidth(16)
    @ExcelProperty(value = "票均重量(克)")
    private Integer dailyAvgWeight;

    /**
     * 毛利率(%).
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "毛利率")
    private String grossProfit;

    /**
     * 是否通过 (通过、驳回).
     */
    @ColumnWidth(16)
    @ExcelProperty(value = "是否通过")
    private String passStatus;
}
