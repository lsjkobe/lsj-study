package com.lsj.study.excelbiz.customer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lsj.study.excelbiz.demo.AssignRowsAndColumnsToMergeStrategy;
import com.lsj.study.excelbiz.model.OfferCustomerExcel;
import com.lsj.study.excelbiz.model.OfferBuildExcel;
import com.lsj.study.excelbiz.model.OfferProductExcel;
import com.lsj.study.excelbiz.model.OfferProductDetailExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.*;

/**
 * CustomerDemo .
 *
 * @author lsj
 * @date 2022-09-05 14:37
 */
@Slf4j
public class CustomerDemo {

    private static String fileName = "D:/tmp/excel/build/" + System.currentTimeMillis() + ".xlsx";

    public static void main(String[] args) {
        List<OfferCustomerExcel> offerCustomerExcelList = initData();
        Gson gson = new Gson();
        log.info("初始化数据：{}", gson.toJson(offerCustomerExcelList));
        List<OfferBuildExcel> offerBuildExcelList = new ArrayList<>();
        List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();
        int startRow = 2;
        toExcelData(startRow, offerCustomerExcelList, offerBuildExcelList, cellRangeAddressList);
        log.info("");
        EasyExcel.write(fileName)
                .head(OfferBuildExcel.class)
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 20, (short) 17))
                .registerWriteHandler(new HeadCellStyleStrategy())
                .registerWriteHandler(new AssignRowsAndColumnsToMergeStrategy(startRow, cellRangeAddressList))
                .sheet("模板")
                .doWrite(offerBuildExcelList);
    }

    /**
     * excel导出有三层结构，一（客户）二层（产品）都需要分别合并，三层不需合并（具体的详情）
     * 转为excel导出需要的数据.
     *
     * @param startRowCount        开始的行号
     * @param offerCustomerExcelList         客户对象列表
     * @param offerBuildExcelList    返回的excel对象列表
     * @param cellRangeAddressList 合并的对象列表
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
        offerBuildExcel.setValidityDay(offerCustomerExcel.getValidityDay());

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
        offerBuildExcel.setPublishedExpressFellFreight(offerProductDetailExcel.getPublishedExpressFellFreight());
        offerBuildExcel.setPublishedRegistrationFellFee(offerProductDetailExcel.getPublishedRegistrationFellFee());
        offerBuildExcel.setDailyAvgGoods(offerProductDetailExcel.getDailyAvgGoods());
        offerBuildExcel.setDailyAvgWeight(offerProductDetailExcel.getDailyAvgWeight());
        return offerBuildExcel;
    }

    private static List<OfferCustomerExcel> initData() {
        return new Gson().fromJson(DATA_JSON, new TypeToken<List<OfferCustomerExcel>>() {}.getType());
    }

    private static final String DATA_JSON = "[{\"customerName\":\"张三\",\"customerCode\":\"QWE121\",\"customerLevel\":\"大型客户\",\"fsTraceName\":\"王五\",\"orgDesc\":\"广州分公司\",\"lastThirtyQuantity\":300,\"lastThirtyRatioPucker\":1.2,\"quotationType\":\"等级价1档\",\"validityDay\":\"30天\",\"productList\":[{\"productName\":\"联邮通经济挂号-普货\",\"region\":\"华南\",\"productDetailList\":[{\"country\":\"美国\",\"weightSegment\":\"0-100G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":45,\"dailyAvgWeight\":50},{\"country\":\"美国\",\"weightSegment\":\"101-200G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":46,\"dailyAvgWeight\":150},{\"country\":\"美国\",\"weightSegment\":\"201-450G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":47,\"dailyAvgWeight\":250},{\"country\":\"美国\",\"weightSegment\":\"451-700G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":48,\"dailyAvgWeight\":550}]},{\"productName\":\"联邮通经济挂号-带电\",\"region\":\"华南\",\"productDetailList\":[{\"country\":\"美国\",\"weightSegment\":\"701-1000G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":49,\"dailyAvgWeight\":850},{\"country\":\"美国\",\"weightSegment\":\"1001-3000G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":50,\"dailyAvgWeight\":1550},{\"country\":\"英国\",\"weightSegment\":\"0-100G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":51,\"dailyAvgWeight\":850},{\"country\":\"英国\",\"weightSegment\":\"101-200G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":52,\"dailyAvgWeight\":1550}]}]},{\"customerName\":\"李四\",\"customerCode\":\"QW321\",\"customerLevel\":\"大型客户\",\"fsTraceName\":\"王五\",\"orgDesc\":\"广州分公司\",\"lastThirtyQuantity\":300,\"lastThirtyRatioPucker\":1.2,\"quotationType\":\"等级价1档\",\"validityDay\":\"30天\",\"productList\":[{\"productName\":\"联邮通经济挂号-普货\",\"region\":\"华南\",\"productDetailList\":[{\"country\":\"美国\",\"weightSegment\":\"0-100G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":45,\"dailyAvgWeight\":50},{\"country\":\"美国\",\"weightSegment\":\"101-200G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":46,\"dailyAvgWeight\":150},{\"country\":\"美国\",\"weightSegment\":\"201-450G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":47,\"dailyAvgWeight\":250},{\"country\":\"美国\",\"weightSegment\":\"451-700G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":48,\"dailyAvgWeight\":550}]},{\"productName\":\"联邮通经济挂号-带电\",\"region\":\"华南\",\"productDetailList\":[{\"country\":\"美国\",\"weightSegment\":\"701-1000G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":49,\"dailyAvgWeight\":850},{\"country\":\"美国\",\"weightSegment\":\"1001-3000G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":50,\"dailyAvgWeight\":1550},{\"country\":\"英国\",\"weightSegment\":\"0-100G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":51,\"dailyAvgWeight\":850},{\"country\":\"英国\",\"weightSegment\":\"101-200G\",\"publishedExpressFreight\":66,\"publishedRegistrationFee\":14,\"agreementExpressFreight\":65,\"agreementRegistrationFee\":13,\"agreementDatePeriod\":\"2020-09-09—2020-09-10\",\"applyExpressFreight\":66,\"applyRegistrationFee\":14,\"publishedExpressFellFreight\":\"下降1元\",\"publishedRegistrationFellFee\":\"下降1元\",\"dailyAvgGoods\":52,\"dailyAvgWeight\":1550}]}]}]";
}
