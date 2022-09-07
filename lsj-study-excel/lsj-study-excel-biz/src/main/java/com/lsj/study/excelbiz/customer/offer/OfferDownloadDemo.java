package com.lsj.study.excelbiz.customer.offer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.google.gson.Gson;
import com.lsj.study.excelbiz.model.excel.OfferDownloadExcel;
import com.lsj.study.excelbiz.model.excel.OfferDownloadExcelDTO;
import com.lsj.study.excelbiz.model.excel.OfferProductDownloadExcel;
import com.lsj.study.excelbiz.utils.BeanCopierUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * OfferDownloadDemo
 *
 * @author by lishangj
 * @date 2022/9/7 16:30
 */
public class OfferDownloadDemo {
    // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
    private static final String PATH_DOWNLOAD_TEMP_PATH =
            "H:\\lsj\\workspace\\lsj\\lsj-study\\lsj-study-excel\\lsj-study-excel-biz\\src\\main\\resources\\temp\\excel/联邮通报价模板_2.xlsx";

    /**
     * 产品sheet的模板位置.
     */
    private static final int PRODUCT_TEMP_SHEET_INDEX = 1;

    /**
     * 动态添加的sheet的开始位置(该值与动态生成的产品sheet数据填充关联，如果修改做好测试).
     */
    private static final int PRODUCT_SHEET_START_INDEX = 2;

    // 方案1 根据对象填充
    static String fileName = "F:\\tmp\\excel/lsjtest/fill/" + System.currentTimeMillis() + ".xlsx";

    public static void main(String[] args) throws IOException {
        OfferDownloadExcelDTO offerDownloadExcelDTO = initData();
        OfferDownloadExcel offerDownloadExcel = getOfferDownloadExcel(offerDownloadExcelDTO);
        List<OfferProductDownloadExcel> offerProductDownloadExcelList = getOfferProductDownloadExcel(offerDownloadExcelDTO);
        //设置序号
        for (int i = 0; i < offerProductDownloadExcelList.size(); i++) {
            offerProductDownloadExcelList.get(i).setIndex(i + 1);
        }
        offerDownloadExcel.setQuoteDate(DateUtils.format(new Date(), "yyyy年MM月dd日"));
        //设置vat税率的序号（自动设置异常件序号）
        offerDownloadExcel.setVatRateIndex(offerProductDownloadExcelList.size() + 1);
        //构建excel的writer
        ExcelWriter excelWriter = buildExcelWriter(new FileInputStream(PATH_DOWNLOAD_TEMP_PATH), offerProductDownloadExcelList);
        fillBaseSheetData(excelWriter, offerDownloadExcel, offerProductDownloadExcelList);
        fillDynamicProductSheetData(excelWriter, offerProductDownloadExcelList);
        excelWriter.finish();
    }

    /**
     * 构建excel writer.
     *
     * @param sourceIs                 模板excel流.
     * @param productDownloadExcelList .
     * @return .
     * @throws IOException .
     */
    private static ExcelWriter buildExcelWriter(InputStream sourceIs, List<OfferProductDownloadExcel> productDownloadExcelList) throws IOException {
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
            excelWriter = EasyExcel.write(fileName).withTemplate(is).build();
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
        WriteSheet writeSheet = EasyExcel.writerSheet("价格表目录").build();
        excelWriter.fill(offerDownloadExcel, writeSheet);
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(productDownloadExcelList, fillConfig, writeSheet);
    }

    /**
     * 填充动态产品sheet数据.
     *
     * @param excelWriter              .
     * @param productDownloadExcelList .
     */
    private static void fillDynamicProductSheetData(ExcelWriter excelWriter, List<OfferProductDownloadExcel> productDownloadExcelList) {
        for (int i = 0; i < productDownloadExcelList.size(); i++) {
            OfferProductDownloadExcel productExcel = productDownloadExcelList.get(i);
            WriteSheet writeSheet = EasyExcel.writerSheet(i + PRODUCT_SHEET_START_INDEX).build();
            excelWriter.fill(productExcel, writeSheet);
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(productExcel.getProductDetailList(), fillConfig, writeSheet);
        }
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

    private static OfferDownloadExcelDTO initData() {
        Gson gson = new Gson();
        return gson.fromJson(DATA_JSON, OfferDownloadExcelDTO.class);
    }

    private static final String DATA_JSON = "{\n" +
            "    \"customerName\":\"李上健测试一哈\",\n" +
            "    \"salesPhoneNumber\":\"12345678901\",\n" +
            "    \"productList\":[\n" +
            "        {\n" +
            "            \"countries\":\"法国等4国\",\n" +
            "            \"channel\":\"优先挂号-普货专线\",\n" +
            "            \"code\":\"PX\",\n" +
            "            \"name\":\"联邮通优先挂号-普货\",\n" +
            "            \"desc\":\"仅限普货，不接受香港交货，全程可跟踪，签收率高，价格优\",\n" +
            "            \"timely\":\"6-8\",\n" +
            "            \"region\":\"华南\",\n" +
            "            \"productDetailList\":[\n" +
            "                {\n" +
            "                    \"country\":\"法国\",\n" +
            "                    \"productRemark\":\"无服务邮编编码 前两位： 97、98、00，POBOX，军事地址：字符里有armee或者armée 及 字符里有或邮编为00100和00200\",\n" +
            "                    \"timely\":\"4-6\",\n" +
            "                    \"sizeLimit\":\"最大尺寸限制： 60 * 40* 40CM 最小尺寸限制：16*11CM\",\n" +
            "                    \"weightSegmentList\":[\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"20\",\n" +
            "                            \"expressFreight\":\"48\",\n" +
            "                            \"weightSegment\":\"0-500G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"23\",\n" +
            "                            \"expressFreight\":\"43\",\n" +
            "                            \"weightSegment\":\"501-3000G\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"country\":\"美国\",\n" +
            "                    \"productRemark\":\"1. 无服务邮编 前3位： 006-009，967-969，995-999等；具体有无服务以能否打印面单为准。 2. Tophatter平台客户请选择TOPHATTER-3PL优先-普货（B5)下单！\",\n" +
            "                    \"timely\":\"5-8\",\n" +
            "                    \"sizeLimit\":\"≤60*40*35CM\",\n" +
            "                    \"weightSegmentList\":[\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"24\",\n" +
            "                            \"expressFreight\":\"90\",\n" +
            "                            \"weightSegment\":\"0-100G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"24\",\n" +
            "                            \"expressFreight\":\"90\",\n" +
            "                            \"weightSegment\":\"101-200G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"28\",\n" +
            "                            \"expressFreight\":\"100\",\n" +
            "                            \"weightSegment\":\"201-340G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"28\",\n" +
            "                            \"expressFreight\":\"100\",\n" +
            "                            \"weightSegment\":\"341-450G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"41\",\n" +
            "                            \"expressFreight\":\"122\",\n" +
            "                            \"weightSegment\":\"451-700G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"61\",\n" +
            "                            \"expressFreight\":\"102\",\n" +
            "                            \"weightSegment\":\"701-1000G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"61\",\n" +
            "                            \"expressFreight\":\"102\",\n" +
            "                            \"weightSegment\":\"1001-2000G\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"country\":\"英国\",\n" +
            "                    \"productRemark\":\"无服务邮编编码 前两位： 97、98、00，POBOX，军事地址：字符里有armee或者armée 及 字符里有或邮编为00100和00200\",\n" +
            "                    \"timely\":\"6-8\",\n" +
            "                    \"sizeLimit\":\"最长边60CM，体积不超过0.031M³\",\n" +
            "                    \"weightSegmentList\":[\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"18\",\n" +
            "                            \"expressFreight\":\"54\",\n" +
            "                            \"weightSegment\":\"0-3000G\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"countries\":\"美国等18国\",\n" +
            "            \"channel\":\"标准挂号-普货专线\",\n" +
            "            \"code\":\"O5\",\n" +
            "            \"name\":\"联邮通经济挂号-普货\",\n" +
            "            \"desc\":\"仅限普货，不接受香港交货，仓位充足，全程可跟踪，签收率高，价格优\",\n" +
            "            \"timely\":\"5-14\",\n" +
            "            \"region\":\"华南\",\n" +
            "            \"productDetailList\":[\n" +
            "                {\n" +
            "                    \"country\":\"英国\",\n" +
            "                    \"productRemark\":\"\",\n" +
            "                    \"timely\":\"10-12\",\n" +
            "                    \"sizeLimit\":\"≤60x40x40CM\",\n" +
            "                    \"weightSegmentList\":[\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"15\",\n" +
            "                            \"expressFreight\":\"24\",\n" +
            "                            \"weightSegment\":\"0-1000G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"15\",\n" +
            "                            \"expressFreight\":\"24\",\n" +
            "                            \"weightSegment\":\"1001-2000G\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"country\":\"德国\",\n" +
            "                    \"productRemark\":\"不提供P.O.BOX地址派送服务；\",\n" +
            "                    \"timely\":\"7-10\",\n" +
            "                    \"sizeLimit\":\"最大尺寸为60*40*40 CM； 最小尺寸为21*10*1CM；\",\n" +
            "                    \"weightSegmentList\":[\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"18\",\n" +
            "                            \"expressFreight\":\"38\",\n" +
            "                            \"weightSegment\":\"0-400G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"21\",\n" +
            "                            \"expressFreight\":\"31\",\n" +
            "                            \"weightSegment\":\"401-2000G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"21\",\n" +
            "                            \"expressFreight\":\"31\",\n" +
            "                            \"weightSegment\":\"2001-3000G\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"country\":\"美国\",\n" +
            "                    \"productRemark\":\"1. 无服务邮编 ：①邮编前3位为006-009，967-969，995-999的地区；②APO/FPO军事地址，详见 【无服务邮编】；③不接受亚马逊仓库地址；④具体有无服务以能否打印面单为准。\",\n" +
            "                    \"timely\":\"15-25\",\n" +
            "                    \"sizeLimit\":\"最长边≤60CM，长+（宽+高）*2≤210CM；最小尺寸要求：10*15CM\",\n" +
            "                    \"weightSegmentList\":[\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"17.5\",\n" +
            "                            \"expressFreight\":\"74\",\n" +
            "                            \"weightSegment\":\"0-100G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"17.5\",\n" +
            "                            \"expressFreight\":\"74\",\n" +
            "                            \"weightSegment\":\"101-200G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"16\",\n" +
            "                            \"expressFreight\":\"70\",\n" +
            "                            \"weightSegment\":\"201-450G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"16\",\n" +
            "                            \"expressFreight\":\"70\",\n" +
            "                            \"weightSegment\":\"451-700G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"9\",\n" +
            "                            \"expressFreight\":\"66\",\n" +
            "                            \"weightSegment\":\"7001-1000G\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"registrationFee\":\"9\",\n" +
            "                            \"expressFreight\":\"66\",\n" +
            "                            \"weightSegment\":\"1001-3000G\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";
}
