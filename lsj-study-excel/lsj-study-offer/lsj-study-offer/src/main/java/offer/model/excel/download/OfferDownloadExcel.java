package offer.model.excel.download;

import lombok.Data;

/**
 * QuoteDownloadExcel
 *
 * @author by lishangj
 * @date 2022/9/6 15:37
 */
@Data
public class OfferDownloadExcel {

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

    public void setVatRateIndex(Integer vatRateIndex) {
        this.vatRateIndex = vatRateIndex;
        //固定，异常件序号为vat税率+1
        if (vatRateIndex != null) {
            this.abnormalIndex = vatRateIndex + 1;
        }
    }
}
