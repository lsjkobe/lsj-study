package offer.handler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import offer.handler.cell.HeadCellStyleStrategy;
import offer.handler.cell.OfferSheetWriteHandler;
import offer.handler.cell.RowsAndColumnMergeStrategy;
import offer.model.excel.build.OfferBuildExcel;
import offer.model.excel.build.OfferCustomerExcel;
import offer.model.excel.build.OfferProductDetailExcel;
import offer.model.excel.build.OfferProductExcel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lishangj
 */
@Slf4j
public class OfferExcelBuildHandler {

    /**
     * 列表开始的行数.
     */
    private static final int ROW_START_INDEX = 3;

    /**
     * 毛利率列index.
     */
    private static final int GROSS_PROFIT_COLUMN_INDEX = 24;

    /**
     * 是否通过列index.
     */
    private static final int PASS_STATUS_COLUMN_INDEX = 25;

    /**
     * 创建报价申请excel.
     *
     * @param os                     .
     * @param offerCustomerExcelList .
     */
    public static void buildOfferExcel(OutputStream os, List<OfferCustomerExcel> offerCustomerExcelList) {
        log.info("生成报价申请excel参数：{}", new Gson().toJson(offerCustomerExcelList));
        buildOfferExcelCommon(os, offerCustomerExcelList, Boolean.FALSE);
    }

    /**
     * 创建报价申请excel(毛利率).
     *
     * @param os                     .
     * @param offerCustomerExcelList .
     */
    public static void buildOfferExcelWithGrossProfit(OutputStream os, List<OfferCustomerExcel> offerCustomerExcelList) {
        log.info("生成报价申请-毛利率excel参数：{}", new Gson().toJson(offerCustomerExcelList));
        buildOfferExcelCommon(os, offerCustomerExcelList, Boolean.TRUE);
    }

    /**
     * 创建报价申请excel.
     *
     * @param os                     .
     * @param offerCustomerExcelList .
     * @param isGrossProfit          是否毛利率.
     */
    private static void buildOfferExcelCommon(OutputStream os, List<OfferCustomerExcel> offerCustomerExcelList,
                                              boolean isGrossProfit) {
        List<OfferBuildExcel> offerBuildExcelList = new ArrayList<>();
        List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();
        //从0开始，所以使用要-1
        int startRow = ROW_START_INDEX - 1;
        //SimpleRowHeightStyleStrategy设置单元格行高（head和内容）
        //HeadCellStyleStrategy设置head单元格样式
        //RowsAndColumnMergeStrategy合并单元格
        toExcelData(startRow, offerCustomerExcelList, offerBuildExcelList, cellRangeAddressList);
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(os);
        if (isGrossProfit) {
            CellRangeAddressList passStatusCellRangeAddressList =
                    new CellRangeAddressList(startRow, startRow + offerBuildExcelList.size() - 1, PASS_STATUS_COLUMN_INDEX, PASS_STATUS_COLUMN_INDEX);
            excelWriterBuilder.registerWriteHandler(new OfferSheetWriteHandler(passStatusCellRangeAddressList, new String[]{"通过", "驳回"}));
        } else {
            //如果非毛利率则排除毛利率和是否通过列 (如果列有改动，需要修改这两个值index)
            excelWriterBuilder.excludeColumnIndexes(Arrays.asList(GROSS_PROFIT_COLUMN_INDEX, PASS_STATUS_COLUMN_INDEX));
        }
        excelWriterBuilder.head(OfferBuildExcel.class)
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 20, (short) 17))
                .registerWriteHandler(new HeadCellStyleStrategy())
                .registerWriteHandler(new RowsAndColumnMergeStrategy(startRow, cellRangeAddressList))
                .sheet("模板")
                .doWrite(offerBuildExcelList);
    }

    /**
     * excel导出有三层结构，一（客户）二层（产品）都需要分别合并，三层不需合并（具体的详情）
     * 转为excel导出需要的数据.
     *
     * @param startRowCount          开始的行号
     * @param offerCustomerExcelList 客户对象列表
     * @param offerBuildExcelList    返回的excel对象列表
     * @param cellRangeAddressList   合并的对象列表
     */
    private static void toExcelData(int startRowCount, List<OfferCustomerExcel> offerCustomerExcelList,
                                    List<OfferBuildExcel> offerBuildExcelList, List<CellRangeAddress> cellRangeAddressList) {
        //一层合并字段的列范围，1-9列
        int[] customerMergeColSegment = {0, 8};
        //二级合并字段的列范围，10-11列
        int[] productMergeColSegment = {9, 10};
        //定义当前行数
        int curStartRow = startRowCount;
        //遍历客户信息列表
        for (OfferCustomerExcel offerCustomerExcel : offerCustomerExcelList) {
            //重置当前客户的行数
            int customerRowCount = 0;
            int curProductStartRow = curStartRow;
            if (CollectionUtils.isEmpty(offerCustomerExcel.getProductList())) {
                continue;
            }
            //遍历客户下产品列表
            for (OfferProductExcel offerProductExcel : offerCustomerExcel.getProductList()) {
                int productRowCount;
                if (CollectionUtils.isEmpty(offerProductExcel.getProductDetailList())) {
                    continue;
                }
                //遍历产品下的具体详情列表
                for (OfferProductDetailExcel offerProductDetailExcel : offerProductExcel.getProductDetailList()) {
                    OfferBuildExcel offerBuildExcel = toCustomerExcel(offerCustomerExcel, offerProductExcel, offerProductDetailExcel);
                    offerBuildExcelList.add(offerBuildExcel);
                }
                productRowCount = offerProductExcel.getProductDetailList().size();
                //构造二层（产品）合并的合并对象
                List<CellRangeAddress> productCellRangeAddressList = genCellRangeAddress(productMergeColSegment, curProductStartRow, productRowCount);
                cellRangeAddressList.addAll(productCellRangeAddressList);
                //当前产品开始行数+产品占的行数=下一产品开始行数
                curProductStartRow = curProductStartRow + productRowCount;
                //当前客户所占行数=每个产品的产品详情数量累加
                customerRowCount += offerProductExcel.getProductDetailList().size();
            }
            //构造一层（客户）合并的合并对象
            List<CellRangeAddress> customerCellRangeAddressList = genCellRangeAddress(customerMergeColSegment, curStartRow, customerRowCount);
            cellRangeAddressList.addAll(customerCellRangeAddressList);
            //当前客户开始行数+当前客户占的行数=下一客户开始行数
            curStartRow = curStartRow + customerRowCount;
        }
    }

    /**
     * 创建合并对象.
     *
     * @param mergeColSegment 合并的列范围.
     * @param startRow        开始行号.
     * @param rowCount        合并的行数.
     * @return .
     */
    private static List<CellRangeAddress> genCellRangeAddress(int[] mergeColSegment, int startRow, int rowCount) {
        List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();
        //参数定义了当前层级需要合并的列范围，比如当前一层1-9列需要合并，二层10-11需要合并
        for (int i = mergeColSegment[0]; i <= mergeColSegment[1]; i++) {
            int endRow = startRow + rowCount - 1;
            //只有开始行小于结束行才需要创建合并（等于相当于只有一行，也不需要合并）
            if (startRow >= endRow) {
                continue;
            }
            CellRangeAddress cellRangeAddress = new CellRangeAddress(startRow, endRow, i, i);
            cellRangeAddressList.add(cellRangeAddress);
        }
        return cellRangeAddressList;
    }

    /**
     * 创建excel对象.
     *
     * @param offerCustomerExcel      客户信息.
     * @param offerProductExcel       客户产品信息.
     * @param offerProductDetailExcel 产品详情.
     * @return .
     */
    private static OfferBuildExcel toCustomerExcel(OfferCustomerExcel offerCustomerExcel, OfferProductExcel offerProductExcel, OfferProductDetailExcel offerProductDetailExcel) {
        OfferBuildExcel offerBuildExcel = new OfferBuildExcel();
        offerBuildExcel.setCustomerCode(offerCustomerExcel.getCustomerCode());
        offerBuildExcel.setCustomerName(offerCustomerExcel.getCustomerName());
        offerBuildExcel.setCustomerLevel(offerCustomerExcel.getCustomerLevel());
        offerBuildExcel.setLastThirtyQuantity(offerCustomerExcel.getLastThirtyQuantity());
        offerBuildExcel.setLastThirtyRatioPucker(offerCustomerExcel.getLastThirtyRatioPucker());
        offerBuildExcel.setOrgDesc(offerCustomerExcel.getOrgDesc());
        offerBuildExcel.setFsTraceName(offerCustomerExcel.getFsTraceName());
        offerBuildExcel.setQuotationType(offerCustomerExcel.getQuotationType());
        offerBuildExcel.setValidityDay(offerCustomerExcel.getValidityDay() + "天");

        offerBuildExcel.setProductName(offerProductExcel.getProductName());
        offerBuildExcel.setRegion(offerProductExcel.getRegion());

        offerBuildExcel.setCountry(offerProductDetailExcel.getCountry());
        offerBuildExcel.setWeightSegment(offerProductDetailExcel.getWeightSegment());
        offerBuildExcel.setPublishedExpressFreight(offerProductDetailExcel.getPublishedExpressFreight());
        offerBuildExcel.setPublishedRegistrationFee(offerProductDetailExcel.getPublishedRegistrationFee());
        offerBuildExcel.setAgreementExpressFreight(offerProductDetailExcel.getAgreementExpressFreight());
        offerBuildExcel.setAgreementRegistrationFee(offerProductDetailExcel.getAgreementRegistrationFee());
        offerBuildExcel.setAgreementDatePeriod(offerProductDetailExcel.getAgreementDatePeriod());
        offerBuildExcel.setApplyExpressFreight(offerProductDetailExcel.getApplyExpressFreight());
        offerBuildExcel.setApplyRegistrationFee(offerProductDetailExcel.getApplyRegistrationFee());
        offerBuildExcel.setPublishedExpressFellFreight(MessageFormat.format("下降{0}元", offerProductDetailExcel.getPublishedExpressFellFreight()));
        offerBuildExcel.setPublishedRegistrationFellFee(MessageFormat.format("下降{0}元", offerProductDetailExcel.getPublishedRegistrationFellFee()));
        offerBuildExcel.setDailyAvgGoods(offerProductDetailExcel.getDailyAvgGoods());
        offerBuildExcel.setDailyAvgWeight(offerProductDetailExcel.getDailyAvgWeight());
        offerBuildExcel.setGrossProfit(offerProductDetailExcel.getGrossProfit() + "%");
        offerBuildExcel.setPassStatus(offerProductDetailExcel.getPassStatus());
        return offerBuildExcel;
    }
}
