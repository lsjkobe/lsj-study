package offer.model.excel.build;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Product .
 *
 * @author lsj
 * @date 2022-09-05 14:24
 */
@Data
public class OfferProductExcel implements Serializable {

    private static final long serialVersionUID = 3915926763646814453L;

    /**
     * 产品名称.
     */
    private String productName;

    /**
     * 销售区域
     */
    private String region;

    /**
     * 产品详情列表.
     */
    private List<OfferProductDetailExcel> productDetailList;
}
