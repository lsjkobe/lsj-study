package com.lsj.study.excelbiz.customer.offer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lsj.study.excelbiz.demo.AssignRowsAndColumnsToMergeStrategy;
import com.lsj.study.excelbiz.model.excel.download.*;
import com.lsj.study.excelbiz.utils.BeanCopierUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * OfferDownloadDemo
 *
 * @author by lishangj
 * @date 2022/9/7 16:30
 */
@Slf4j
public class OfferDownloadDemo {
    // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
    private static final String PATH_DOWNLOAD_TEMP_PATH =
            "H:\\lsj\\workspace\\lsj\\lsj-study\\lsj-study-excel\\lsj-study-excel-biz\\src\\main\\resources\\temp\\excel/联邮通报价模板.xlsx";

    /**
     * 产品sheet的模板位置.
     */
    private static final int PRODUCT_TEMP_SHEET_INDEX = 1;

    /**
     * 动态添加的sheet的开始位置(该值与动态生成的产品sheet数据填充关联，如果修改做好测试).
     */
    private static final int PRODUCT_SHEET_START_INDEX = 2;

    /**
     * 价格表目录列表开始行数
     */
    private static final int PRICE_LIST_ROW_START_INDEX = 5;

    /**
     * 产品详情列表开始行数
     */
    private static final int PRODUCT_ROW_START_INDEX = 5;

    /**
     * 超链接在第几列
     */
    private static final int HYPERLINK_COLUMN_INDEX = 7;

    // 方案1 根据对象填充
    static String fileName = "F:\\tmp\\excel/lsjtest/fill/" + System.currentTimeMillis() + ".xlsx";

    public static void main(String[] args) throws IOException {
        OfferDownloadExcelDTO offerDownloadExcelDTO = initData();
        List<OfferVatRateDownloadExcel> vatRateList = initVatList();
        FileOutputStream fos = new FileOutputStream(fileName);
        buildOfferDownloadExcel(fos, offerDownloadExcelDTO, vatRateList);
    }

    /**
     * 构造报价下载excel.
     * @param os 输出流
     * @param offerDownloadExcelDTO 报价填充数据
     * @param vatRateList vat税率数据
     * @throws IOException .
     */
    public static void buildOfferDownloadExcel(OutputStream os, OfferDownloadExcelDTO offerDownloadExcelDTO,
                                               List<OfferVatRateDownloadExcel> vatRateList) throws IOException {
        log.info("构造报价下载excel的参数{},VAT税率参数{}", new Gson().toJson(offerDownloadExcelDTO), new Gson().toJson(vatRateList));
        OfferDownloadExcel offerDownloadExcel = getOfferDownloadExcel(offerDownloadExcelDTO);
        List<OfferProductDownloadExcel> offerProductDownloadExcelList = getOfferProductDownloadExcel(offerDownloadExcelDTO);
        initIndex(offerDownloadExcel, offerProductDownloadExcelList, vatRateList);
        //构建excel的writer
        ExcelWriter excelWriter = buildExcelWriter(os, new FileInputStream(PATH_DOWNLOAD_TEMP_PATH), offerProductDownloadExcelList);
        //填充价格表目录数据
        fillBaseSheetData(excelWriter, offerDownloadExcel, offerProductDownloadExcelList);
        //填充动态产品sheet数据
        fillDynamicProductSheetData(excelWriter, offerDownloadExcelDTO.getProductList());
        //填充vat税率数据
        fillVatRateSheetData(calcVatSheetIndex(offerProductDownloadExcelList.size()), excelWriter, vatRateList);
        excelWriter.finish();
    }

    /**
     * 初始化数据的一些序号.
     *
     * @param offerDownloadExcel            .
     * @param offerProductDownloadExcelList .
     * @param vatRateList                   .
     */
    private static void initIndex(OfferDownloadExcel offerDownloadExcel,
                                  List<OfferProductDownloadExcel> offerProductDownloadExcelList,
                                  List<OfferVatRateDownloadExcel> vatRateList) {
        //设置序号
        for (int i = 0; i < offerProductDownloadExcelList.size(); i++) {
            offerProductDownloadExcelList.get(i).setIndex(i + 1);
        }
        offerDownloadExcel.setQuoteDate(DateUtils.format(new Date(), "yyyy年MM月dd日"));
        //设置vat税率的序号（自动设置异常件序号）
        offerDownloadExcel.setVatRateIndex(offerProductDownloadExcelList.size() + 1);
        for (int i = 0; i < vatRateList.size(); i++) {
            vatRateList.get(i).setIndex(i + 1);
        }
    }

    /**
     * 构建excel writer.
     *
     * @param sourceIs                 模板excel流.
     * @param productDownloadExcelList .
     * @return .
     * @throws IOException .
     */
    private static ExcelWriter buildExcelWriter(OutputStream os, InputStream sourceIs, List<OfferProductDownloadExcel> productDownloadExcelList) throws IOException {
        ExcelWriter excelWriter;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(sourceIs);
            //隐藏模板
            workbook.setSheetHidden(PRODUCT_TEMP_SHEET_INDEX, Boolean.TRUE);
            for (int i = 0; i < productDownloadExcelList.size(); i++) {
                OfferProductDownloadExcel productExcel = productDownloadExcelList.get(i);
                String sheetName = genSheetName(productExcel);
                //复制隐藏的sheet
                workbook.cloneSheet(1, sheetName);
                //调整动态添加的sheet的开始位置
                workbook.setSheetOrder(sheetName, i + PRODUCT_SHEET_START_INDEX);
            }
            //写到流里
            workbook.write(bos);
            byte[] bArray = bos.toByteArray();
            InputStream is = new ByteArrayInputStream(bArray);
            excelWriter = EasyExcel.write(os).withTemplate(is).build();
        }
        return excelWriter;
    }

    /**
     * 填充基础数据（第一个sheet）
     *
     * @param excelWriter              .
     * @param offerDownloadExcel       .
     * @param productDownloadExcelList .
     */
    private static void fillBaseSheetData(ExcelWriter excelWriter, OfferDownloadExcel offerDownloadExcel,
                                          List<OfferProductDownloadExcel> productDownloadExcelList) {
        //行列index拼接字符串与超链接地址的map
        Map<String, String> hyperlinkDataMap = new HashMap<>();
        for (int i = 0; i < productDownloadExcelList.size(); i++) {
            int rowIndex = PRICE_LIST_ROW_START_INDEX - 1 + i;
            String sheetName = genSheetName(productDownloadExcelList.get(i));
            String rowColumnSplicing = HyperlinkCellWriteHandler.genRowColumnSplicing(rowIndex, HYPERLINK_COLUMN_INDEX);
            hyperlinkDataMap.put(rowColumnSplicing, sheetName);
        }
        WriteSheet writeSheet = EasyExcel.writerSheet()
                .registerWriteHandler(new HyperlinkCellWriteHandler(hyperlinkDataMap))
                .build();
        excelWriter.fill(offerDownloadExcel, writeSheet);
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(productDownloadExcelList, fillConfig, writeSheet);
    }

    /**
     * 填充动态产品sheet数据.
     *
     * @param excelWriter                 .
     * @param productDownloadExcelDTOList .
     */
    private static void fillDynamicProductSheetData(ExcelWriter excelWriter, List<OfferProductDownloadExcelDTO> productDownloadExcelDTOList) {
        for (int i = 0; i < productDownloadExcelDTOList.size(); i++) {
            OfferProductDownloadExcelDTO productExcelDTO = productDownloadExcelDTOList.get(i);
            //构造的详情数据列表
            List<OfferProductDetailDownloadExcel> productDetailDownloadExcelList = new ArrayList<>();
            //计算出需要合并单元格的对象
            List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();
            //计算数据
            calcOfferExcelData(productExcelDTO, productDetailDownloadExcelList, cellRangeAddressList);
            WriteSheet writeSheet = EasyExcel
                    .writerSheet(i + PRODUCT_SHEET_START_INDEX)
                    .registerWriteHandler(new AssignRowsAndColumnsToMergeStrategy(PRODUCT_ROW_START_INDEX, cellRangeAddressList))
                    .build();
            excelWriter.fill(productExcelDTO, writeSheet);
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(productDetailDownloadExcelList, fillConfig, writeSheet);
        }
    }

    /**
     * 填充vat税率数据.
     *
     * @param vatSheetIndex vat税率所在sheet的index.
     * @param excelWriter   .
     * @param vatRateList   填充数据.
     */
    private static void fillVatRateSheetData(int vatSheetIndex, ExcelWriter excelWriter, List<OfferVatRateDownloadExcel> vatRateList) {
        WriteSheet writeSheet = EasyExcel
                .writerSheet(vatSheetIndex)
                .build();
        excelWriter.fill(vatRateList, writeSheet);
    }

    private static void calcOfferExcelData(OfferProductDownloadExcelDTO productExcelDTO,
                                                     List<OfferProductDetailDownloadExcel> productDetailDownloadExcelList,
                                                     List<CellRangeAddress> cellRangeAddressList) {
        //一层合并字段的列
        int[] mergeColIndex = {0, 1, 5, 6, 7};
        //定义当前行数
        int curStartRow = PRODUCT_ROW_START_INDEX - 1;
        List<OfferProductDetailDownloadExcelDTO> productDetailList = productExcelDTO.getProductDetailList();
        //每块详情（国家/地区）占的行数
        int detailRowCount;
        for (int i = 0; i < productDetailList.size(); i++) {
            OfferProductDetailDownloadExcelDTO productDetail = productDetailList.get(i);
            //所占行数就是重量段的数量
            detailRowCount = productDetail.getWeightSegmentList().size();
            for (int j = 0; j < productDetail.getWeightSegmentList().size(); j++) {
                OfferWeightSegmentDownloadExcelDTO weightSegment = productDetail.getWeightSegmentList().get(j);
                OfferProductDetailDownloadExcel offerProductDetailDownloadExcel = genQuoteProductDetailExcel(i, productDetail, weightSegment);
                productDetailDownloadExcelList.add(offerProductDetailDownloadExcel);
            }
            cellRangeAddressList.addAll(genCellRangeAddress(mergeColIndex, curStartRow, detailRowCount));
            //当前开始行数+每块详情（国家/地区）占的行数=下一开始行数
            curStartRow = curStartRow + detailRowCount;
        }
    }

    /**
     * 构造OfferProductDetailDownloadExcel对象.
     *
     * @param index         序号.
     * @param productDetail 产品详情.
     * @param weightSegment 重量段信息.
     * @return .
     */
    private static OfferProductDetailDownloadExcel genQuoteProductDetailExcel(int index, OfferProductDetailDownloadExcelDTO productDetail,
                                                                              OfferWeightSegmentDownloadExcelDTO weightSegment) {
        OfferProductDetailDownloadExcel detailDownloadExcel = new OfferProductDetailDownloadExcel();
        detailDownloadExcel.setIndex(index);
        detailDownloadExcel.setCountry(productDetail.getCountry());
        detailDownloadExcel.setTimely(productDetail.getTimely());
        detailDownloadExcel.setProductRemark(productDetail.getProductRemark());
        detailDownloadExcel.setSizeLimit(productDetail.getSizeLimit());

        detailDownloadExcel.setWeightSegment(weightSegment.getWeightSegment());
        detailDownloadExcel.setExpressFreight(weightSegment.getExpressFreight());
        detailDownloadExcel.setRegistrationFee(weightSegment.getRegistrationFee());
        return detailDownloadExcel;
    }

    /**
     * 创建合并对象.
     *
     * @param mergeColIndex 合并的列index.
     * @param startRow      开始行号.
     * @param rowCount      合并的行数.
     * @return .
     */
    private static List<CellRangeAddress> genCellRangeAddress(int[] mergeColIndex, int startRow, int rowCount) {
        List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();
        for (int colIndex : mergeColIndex) {
            int endRow = startRow + rowCount - 1;
            //只有开始行小于结束行才需要创建合并（等于相当于只有一行，也不需要合并）
            if (startRow >= endRow) {
                continue;
            }
            CellRangeAddress cellRangeAddress = new CellRangeAddress(startRow, endRow, colIndex, colIndex);
            cellRangeAddressList.add(cellRangeAddress);
        }
        return cellRangeAddressList;
    }

    private static List<OfferProductDownloadExcel> getOfferProductDownloadExcel(OfferDownloadExcelDTO offerDownloadExcelDTO) {
        return BeanCopierUtil.toNewObjects(offerDownloadExcelDTO.getProductList(), OfferProductDownloadExcel.class);
    }

    private static OfferDownloadExcel getOfferDownloadExcel(OfferDownloadExcelDTO offerDownloadExcelDTO) {
        return BeanCopierUtil.toNewObject(offerDownloadExcelDTO, OfferDownloadExcel.class);
    }

    private static String genSheetName(OfferProductDownloadExcel productExcel) {
        return MessageFormat.format("{0}（{1}）", productExcel.getName(), productExcel.getCode());
    }

    /**
     * vat税率的sheet位置与产品个数有关.
     *
     * @param productCount .
     * @return .
     */
    private static int calcVatSheetIndex(int productCount) {
        //产品sheet开始的位置+产品的个数
        return PRODUCT_SHEET_START_INDEX + productCount;
    }

    private static OfferDownloadExcelDTO initData() {
        Gson gson = new Gson();
        return gson.fromJson(DATA_JSON, OfferDownloadExcelDTO.class);
    }

    private static List<OfferVatRateDownloadExcel> initVatList() {
        return new Gson().fromJson(VAT_JSON, new TypeToken<List<OfferVatRateDownloadExcel>>() {
        }.getType());
    }

    private static final String VAT_JSON = "[{\"country\":\"奥地利\",\"taxRate\":\"20%\"},{\"country\":\"比利时\",\"taxRate\":\"21%\"},{\"country\":\"保加利亚\",\"taxRate\":\"20%\"},{\"country\":\"克罗地亚\",\"taxRate\":\"25%\"},{\"country\":\"塞浦路斯\",\"taxRate\":\"19%\"},{\"country\":\"捷克共和国\",\"taxRate\":\"21%\"},{\"country\":\"丹麦\",\"taxRate\":\"25%\"},{\"country\":\"爱沙尼亚\",\"taxRate\":\"20%\"},{\"country\":\"芬兰\",\"taxRate\":\"24%\"},{\"country\":\"法国\",\"taxRate\":\"20%\"},{\"country\":\"德国\",\"taxRate\":\"19%\"},{\"country\":\"希腊\",\"taxRate\":\"24%\"},{\"country\":\"匈牙利\",\"taxRate\":\"27%\"},{\"country\":\"爱尔兰\",\"taxRate\":\"23%\"},{\"country\":\"意大利\",\"taxRate\":\"22%\"},{\"country\":\"拉脱维亚\",\"taxRate\":\"21%\"},{\"country\":\"立陶宛\",\"taxRate\":\"21%\"},{\"country\":\"卢森堡\",\"taxRate\":\"17%\"},{\"country\":\"马耳他\",\"taxRate\":\"18%\"},{\"country\":\"荷兰\",\"taxRate\":\"21%\"},{\"country\":\"波兰\",\"taxRate\":\"23%\"},{\"country\":\"葡萄牙\",\"taxRate\":\"23%\"},{\"country\":\"罗马尼亚\",\"taxRate\":\"19%\"},{\"country\":\"斯洛伐克\",\"taxRate\":\"20%\"},{\"country\":\"斯洛文尼亚\",\"taxRate\":\"22%\"},{\"country\":\"西班牙\",\"taxRate\":\"21%\"},{\"country\":\"瑞典\",\"taxRate\":\"25%\"}]";


    private static final String DATA_JSON = "{\"customerName\":\"测试一哈\",\"salesPhoneNumber\":\"12345678901\",\"productList\":[{\"countries\":\"法国等4国\",\"channel\":\"优先挂号-普货专线\",\"code\":\"PX\",\"name\":\"联邮通优先挂号-普货\",\"desc\":\"仅限普货，不接受香港交货，全程可跟踪，签收率高，价格优\",\"timely\":\"6-8\",\"region\":\"华南\",\"productDetailList\":[{\"country\":\"法国\",\"productRemark\":\"无服务邮编编码 前两位： 97、98、00，POBOX，军事地址：字符里有armee或者armée 及 字符里有或邮编为00100和00200\",\"timely\":\"4-6\",\"sizeLimit\":\"最大尺寸限制： 60 * 40* 40CM 最小尺寸限制：16*11CM\",\"weightSegmentList\":[{\"registrationFee\":\"20\",\"expressFreight\":\"48\",\"weightSegment\":\"0-500G\"},{\"registrationFee\":\"23\",\"expressFreight\":\"43\",\"weightSegment\":\"501-3000G\"}]},{\"country\":\"美国\",\"productRemark\":\"1. 无服务邮编 前3位： 006-009，967-969，995-999等；具体有无服务以能否打印面单为准。 2. Tophatter平台客户请选择TOPHATTER-3PL优先-普货（B5)下单！\",\"timely\":\"5-8\",\"sizeLimit\":\"≤60*40*35CM\",\"weightSegmentList\":[{\"registrationFee\":\"24\",\"expressFreight\":\"90\",\"weightSegment\":\"0-100G\"},{\"registrationFee\":\"24\",\"expressFreight\":\"90\",\"weightSegment\":\"101-200G\"},{\"registrationFee\":\"28\",\"expressFreight\":\"100\",\"weightSegment\":\"201-340G\"},{\"registrationFee\":\"28\",\"expressFreight\":\"100\",\"weightSegment\":\"341-450G\"},{\"registrationFee\":\"41\",\"expressFreight\":\"122\",\"weightSegment\":\"451-700G\"},{\"registrationFee\":\"61\",\"expressFreight\":\"102\",\"weightSegment\":\"701-1000G\"},{\"registrationFee\":\"61\",\"expressFreight\":\"102\",\"weightSegment\":\"1001-2000G\"}]},{\"country\":\"英国\",\"productRemark\":\"无服务邮编编码 前两位： 97、98、00，POBOX，军事地址：字符里有armee或者armée 及 字符里有或邮编为00100和00200\",\"timely\":\"6-8\",\"sizeLimit\":\"最长边60CM，体积不超过0.031M³\",\"weightSegmentList\":[{\"registrationFee\":\"18\",\"expressFreight\":\"54\",\"weightSegment\":\"0-3000G\"}]}]},{\"countries\":\"美国等18国\",\"channel\":\"标准挂号-普货专线\",\"code\":\"O5\",\"name\":\"联邮通经济挂号-普货\",\"desc\":\"仅限普货，不接受香港交货，仓位充足，全程可跟踪，签收率高，价格优\",\"timely\":\"5-14\",\"region\":\"华南\",\"productDetailList\":[{\"country\":\"英国\",\"productRemark\":\"\",\"timely\":\"10-12\",\"sizeLimit\":\"≤60x40x40CM\",\"weightSegmentList\":[{\"registrationFee\":\"15\",\"expressFreight\":\"24\",\"weightSegment\":\"0-1000G\"},{\"registrationFee\":\"15\",\"expressFreight\":\"24\",\"weightSegment\":\"1001-2000G\"}]},{\"country\":\"德国\",\"productRemark\":\"不提供P.O.BOX地址派送服务；\",\"timely\":\"7-10\",\"sizeLimit\":\"最大尺寸为60*40*40 CM； 最小尺寸为21*10*1CM；\",\"weightSegmentList\":[{\"registrationFee\":\"18\",\"expressFreight\":\"38\",\"weightSegment\":\"0-400G\"},{\"registrationFee\":\"21\",\"expressFreight\":\"31\",\"weightSegment\":\"401-2000G\"},{\"registrationFee\":\"21\",\"expressFreight\":\"31\",\"weightSegment\":\"2001-3000G\"}]},{\"country\":\"美国\",\"productRemark\":\"1. 无服务邮编 ：①邮编前3位为006-009，967-969，995-999的地区；②APO/FPO军事地址，详见 【无服务邮编】；③不接受亚马逊仓库地址；④具体有无服务以能否打印面单为准。\",\"timely\":\"15-25\",\"sizeLimit\":\"最长边≤60CM，长+（宽+高）*2≤210CM；最小尺寸要求：10*15CM\",\"weightSegmentList\":[{\"registrationFee\":\"17.5\",\"expressFreight\":\"74\",\"weightSegment\":\"0-100G\"},{\"registrationFee\":\"17.5\",\"expressFreight\":\"74\",\"weightSegment\":\"101-200G\"},{\"registrationFee\":\"16\",\"expressFreight\":\"70\",\"weightSegment\":\"201-450G\"},{\"registrationFee\":\"16\",\"expressFreight\":\"70\",\"weightSegment\":\"451-700G\"},{\"registrationFee\":\"9\",\"expressFreight\":\"66\",\"weightSegment\":\"7001-1000G\"},{\"registrationFee\":\"9\",\"expressFreight\":\"66\",\"weightSegment\":\"1001-3000G\"}]}]},{\"countries\":\"美国等18国\",\"channel\":\"标准挂号-普货测试\",\"code\":\"ABC\",\"name\":\"联邮通经济挂号-普货\",\"desc\":\"仅限普货，不接受香港交货，仓位充足，全程可跟踪，签收率高，价格优\",\"timely\":\"5-14\",\"region\":\"华南\",\"productDetailList\":[{\"country\":\"英国\",\"productRemark\":\"\",\"timely\":\"10-12\",\"sizeLimit\":\"≤60x40x40CM\",\"weightSegmentList\":[{\"registrationFee\":\"15\",\"expressFreight\":\"24\",\"weightSegment\":\"0-1000G\"},{\"registrationFee\":\"15\",\"expressFreight\":\"24\",\"weightSegment\":\"1001-2000G\"}]},{\"country\":\"德国\",\"productRemark\":\"不提供P.O.BOX地址派送服务；\",\"timely\":\"7-10\",\"sizeLimit\":\"最大尺寸为60*40*40 CM； 最小尺寸为21*10*1CM；\",\"weightSegmentList\":[{\"registrationFee\":\"18\",\"expressFreight\":\"38\",\"weightSegment\":\"0-400G\"},{\"registrationFee\":\"21\",\"expressFreight\":\"31\",\"weightSegment\":\"401-2000G\"},{\"registrationFee\":\"21\",\"expressFreight\":\"31\",\"weightSegment\":\"2001-3000G\"}]},{\"country\":\"美国\",\"productRemark\":\"1. 无服务邮编 ：①邮编前3位为006-009，967-969，995-999的地区；②APO/FPO军事地址，详见 【无服务邮编】；③不接受亚马逊仓库地址；④具体有无服务以能否打印面单为准。\",\"timely\":\"15-25\",\"sizeLimit\":\"最长边≤60CM，长+（宽+高）*2≤210CM；最小尺寸要求：10*15CM\",\"weightSegmentList\":[{\"registrationFee\":\"17.5\",\"expressFreight\":\"74\",\"weightSegment\":\"0-100G\"},{\"registrationFee\":\"17.5\",\"expressFreight\":\"74\",\"weightSegment\":\"101-200G\"},{\"registrationFee\":\"16\",\"expressFreight\":\"70\",\"weightSegment\":\"201-450G\"},{\"registrationFee\":\"16\",\"expressFreight\":\"70\",\"weightSegment\":\"451-700G\"},{\"registrationFee\":\"9\",\"expressFreight\":\"66\",\"weightSegment\":\"7001-1000G\"},{\"registrationFee\":\"9\",\"expressFreight\":\"66\",\"weightSegment\":\"1001-3000G\"}]}]},{\"countries\":\"美国等18国\",\"channel\":\"标准挂号-哈哈专线\",\"code\":\"DNF\",\"name\":\"联邮通经济挂号-普货\",\"desc\":\"仅限普货，不接受香港交货，仓位充足，全程可跟踪，签收率高，价格优\",\"timely\":\"5-14\",\"region\":\"华南\",\"productDetailList\":[{\"country\":\"英国\",\"productRemark\":\"\",\"timely\":\"10-12\",\"sizeLimit\":\"≤60x40x40CM\",\"weightSegmentList\":[{\"registrationFee\":\"15\",\"expressFreight\":\"24\",\"weightSegment\":\"0-1000G\"},{\"registrationFee\":\"15\",\"expressFreight\":\"24\",\"weightSegment\":\"1001-2000G\"}]},{\"country\":\"德国\",\"productRemark\":\"不提供P.O.BOX地址派送服务；\",\"timely\":\"7-10\",\"sizeLimit\":\"最大尺寸为60*40*40 CM； 最小尺寸为21*10*1CM；\",\"weightSegmentList\":[{\"registrationFee\":\"18\",\"expressFreight\":\"38\",\"weightSegment\":\"0-400G\"},{\"registrationFee\":\"21\",\"expressFreight\":\"31\",\"weightSegment\":\"401-2000G\"},{\"registrationFee\":\"21\",\"expressFreight\":\"31\",\"weightSegment\":\"2001-3000G\"}]},{\"country\":\"美国\",\"productRemark\":\"1. 无服务邮编 ：①邮编前3位为006-009，967-969，995-999的地区；②APO/FPO军事地址，详见 【无服务邮编】；③不接受亚马逊仓库地址；④具体有无服务以能否打印面单为准。\",\"timely\":\"15-25\",\"sizeLimit\":\"最长边≤60CM，长+（宽+高）*2≤210CM；最小尺寸要求：10*15CM\",\"weightSegmentList\":[{\"registrationFee\":\"17.5\",\"expressFreight\":\"74\",\"weightSegment\":\"0-100G\"},{\"registrationFee\":\"17.5\",\"expressFreight\":\"74\",\"weightSegment\":\"101-200G\"},{\"registrationFee\":\"16\",\"expressFreight\":\"70\",\"weightSegment\":\"201-450G\"},{\"registrationFee\":\"16\",\"expressFreight\":\"70\",\"weightSegment\":\"451-700G\"},{\"registrationFee\":\"9\",\"expressFreight\":\"66\",\"weightSegment\":\"7001-1000G\"},{\"registrationFee\":\"9\",\"expressFreight\":\"66\",\"weightSegment\":\"1001-3000G\"}]}]}]}";
}
