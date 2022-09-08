package offer.model.excel.download;

import lombok.Data;

/**
 * QuoteDownloadExcel
 *
 * @author by lishangj
 * @date 2022/9/6 15:37
 */
@Data
public class OfferVatRateDownloadExcel {

    /**
     * 序号.
     */
    private Integer index;

    /**
     * 国家/地区.
     */
    private String country;

    /**
     * 税率(eg:20%).
     */
    private String taxRate;

}
