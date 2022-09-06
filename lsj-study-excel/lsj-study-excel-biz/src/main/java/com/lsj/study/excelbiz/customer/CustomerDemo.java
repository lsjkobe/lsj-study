package com.lsj.study.excelbiz.customer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.google.gson.Gson;
import com.lsj.study.excelbiz.demo.AssignRowsAndColumnsToMergeStrategy;
import com.lsj.study.excelbiz.model.Customer;
import com.lsj.study.excelbiz.model.CustomerExcel;
import com.lsj.study.excelbiz.model.Product;
import com.lsj.study.excelbiz.model.ProductDetail;
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

    private static String fileName = "F:\\tmp\\excel/lsjtest/" + System.currentTimeMillis() + ".xlsx";

    public static void main(String[] args) {
        List<Customer> customerList = initData();
        Gson gson = new Gson();
        log.info("初始化数据：{}", gson.toJson(customerList));
        List<CustomerExcel> customerExcelList = new ArrayList<>();
        List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();
        toExcelData(2, customerList, customerExcelList, cellRangeAddressList);
        log.info("");
        EasyExcel.write(fileName)
                .head(CustomerExcel.class)
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 20, (short) 17))
                .registerWriteHandler(new HeadCellStyleStrategy())
                .registerWriteHandler(new AssignRowsAndColumnsToMergeStrategy(2, cellRangeAddressList))
                .sheet("模板")
                .doWrite(customerExcelList);
    }

    /**
     * excel导出有三层结构，一（客户）二层（产品）都需要分别合并，三层不需合并（具体的详情）
     * 转为excel导出需要的数据.
     *
     * @param startRowCount        开始的行号
     * @param customerList         客户对象列表
     * @param customerExcelList    返回的excel对象列表
     * @param cellRangeAddressList 合并的对象列表
     */
    private static void toExcelData(int startRowCount, List<Customer> customerList,
                                    List<CustomerExcel> customerExcelList, List<CellRangeAddress> cellRangeAddressList) {
        //一层合并字段的列范围，1-9列
        int[] customerMergeColSegment = {0, 8};
        //二级合并字段的列范围，10-11列
        int[] productMergeColSegment = {9, 10};
        //定义当前行数
        int curStartRow = startRowCount;
        //遍历客户信息列表
        for (Customer customer : customerList) {
            //重置当前客户的行数
            int customerRowCount = 0;
            int curProductStartRow = curStartRow;
            if (CollectionUtils.isEmpty(customer.getProductList())) {
                continue;
            }
            //遍历客户下产品列表
            for (Product product : customer.getProductList()) {
                int productRowCount;
                if (CollectionUtils.isEmpty(product.getProductDetailList())) {
                    continue;
                }
                //遍历产品下的具体详情列表
                for (ProductDetail productDetail : product.getProductDetailList()) {
                    CustomerExcel customerExcel = toCustomerExcel(customer, product, productDetail);
                    customerExcelList.add(customerExcel);
                }
                productRowCount = product.getProductDetailList().size();
                //构造二层（产品）合并的合并对象
                List<CellRangeAddress> productCellRangeAddressList = genCellRangeAddress(productMergeColSegment, curProductStartRow, productRowCount);
                cellRangeAddressList.addAll(productCellRangeAddressList);
                //当前产品开始行数+产品占的行数=下一产品开始行数
                curProductStartRow = curProductStartRow + productRowCount;
                //当前客户所占行数=每个产品的产品详情数量累加
                customerRowCount += product.getProductDetailList().size();
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
     * @param customer      客户信息.
     * @param product       客户产品信息.
     * @param productDetail 产品详情.
     * @return .
     */
    private static CustomerExcel toCustomerExcel(Customer customer, Product product, ProductDetail productDetail) {
        CustomerExcel customerExcel = new CustomerExcel();
        customerExcel.setCode(customer.getCode());
        customerExcel.setName(customer.getName());
        customerExcel.setLevel(customer.getLevel());
        customerExcel.setRecentShipments(customer.getRecentShipments());
        customerExcel.setRecentBubbleRatio(customer.getRecentBubbleRatio());
        customerExcel.setBranch(customer.getBranch());
        customerExcel.setFsSales(customer.getFsSales());
        customerExcel.setPriceType(customer.getPriceType());
        customerExcel.setApplicationTime(customer.getApplicationTime());

        customerExcel.setProductName(product.getName());
        customerExcel.setProductSalesArea(product.getSalesArea());

        customerExcel.setCountry(productDetail.getCountry());
        customerExcel.setWeightSegment(productDetail.getWeightSegment());
        customerExcel.setPublishedExpressShipping(productDetail.getPublishedExpressShipping());
        customerExcel.setPublishedRegistrationFee(productDetail.getPublishedRegistrationFee());
        customerExcel.setExpressShipping(productDetail.getExpressShipping());
        customerExcel.setRegistrationFee(productDetail.getRegistrationFee());
        customerExcel.setValidityPeriod(productDetail.getValidityPeriod());
        customerExcel.setApplyExpressShipping(productDetail.getApplyExpressShipping());
        customerExcel.setApplyRegistrationFee(productDetail.getApplyRegistrationFee());
        customerExcel.setComparativeExpressShipping(productDetail.getComparativeExpressShipping());
        customerExcel.setComparativeRegistrationFee(productDetail.getComparativeRegistrationFee());
        customerExcel.setCommitmentDailyVolume(productDetail.getCommitmentDailyVolume());
        customerExcel.setAverageTicketWeight(productDetail.getAverageTicketWeight());
        return customerExcel;
    }

    private static List<Customer> initData() {
        String comontStr = "lsj";
        String productNameStr = "产品";
        List<Customer> customerList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Customer.CustomerBuilder customerBuilder = Customer.builder()
                    .name(comontStr + i)
                    .code(UUID.randomUUID().toString())
                    .branch(comontStr + "-" + i + "公司")
                    .applicationTime(new Date().toString())
                    .fsSales("lsj")
                    .level("1")
                    .recentShipments(random.nextInt(30))
                    .recentBubbleRatio(String.valueOf(random.nextInt(30)))
                    .priceType("1");
            List<Product> productList = new ArrayList<>();
            customerBuilder.productList(productList);
            int productCount = random.nextInt(5);
            for (int j = 0; j < productCount; j++) {
                List<ProductDetail> productDetailList = new ArrayList<>();
                Product product = Product.builder()
                        .name(productNameStr + j)
                        .salesArea("华南")
                        .productDetailList(productDetailList)
                        .build();
                productList.add(product);
                int detailCount = random.nextInt(8);
                for (int k = 0; k < detailCount; k++) {
                    int weight = (k + 1) * 100;
                    ProductDetail productDetail = ProductDetail.builder()
                            .country("俄罗斯")
                            .weightSegment("0-100G")
                            .publishedExpressShipping("66")
                            .publishedRegistrationFee("14")
                            .expressShipping("65")
                            .registrationFee("13")
                            .validityPeriod("2020-09-09—2020-09-10")
                            .applyExpressShipping("66")
                            .applyRegistrationFee("14")
                            .comparativeExpressShipping("下降1元")
                            .comparativeRegistrationFee("下降1元")
                            .commitmentDailyVolume("100")
                            .averageTicketWeight("50")
                            .build();
                    productDetailList.add(productDetail);
                }
            }
            Customer customer = customerBuilder.build();
            customerList.add(customer);
        }
        return customerList;
    }
}
