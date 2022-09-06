package com.lsj.study.excelbiz.demo;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.lsj.study.excelbiz.model.quote.QuoteDownloadExcel;
import com.lsj.study.excelbiz.model.quote.QuoteProductExcel;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * DemoFillMain
 *
 * @author by lishangj
 * @date 2022/9/6 15:59
 */
public class DemoFillMain {

    // 方案1 根据对象填充
    static String fileName = "F:\\tmp\\excel/lsjtest/fill/" + System.currentTimeMillis() + ".xlsx";

    public static void main(String[] args) throws IOException {
        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        String templateFileName = "H:\\lsj\\workspace\\lsj\\lsj-study\\lsj-study-excel\\lsj-study-excel-biz\\src\\main\\resources\\temp\\excel/联邮通报价模板-2.xlsx";
        QuoteDownloadExcel quoteDownloadExcel = initData();
        List<QuoteProductExcel> quoteProductExcelList = initData2();
        ExcelWriter excelWriter = buildExcelWriter(new FileInputStream(templateFileName), quoteProductExcelList);
        fillBaseSheet(excelWriter, quoteDownloadExcel, quoteProductExcelList);
        fillDynamicProductSheet(excelWriter, quoteProductExcelList);
        excelWriter.finish();
    }

    private static ExcelWriter buildExcelWriter(InputStream sourceIs, List<QuoteProductExcel> productExcelList) throws IOException {
        ExcelWriter excelWriter;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(sourceIs);
            for (int i = 0; i < productExcelList.size(); i++) {
                QuoteProductExcel productExcel = productExcelList.get(i);
                String sheetName = genSheetName(productExcel);
                //复制隐藏的sheet
                workbook.cloneSheet(1, sheetName);
                workbook.setSheetOrder(sheetName, i + 2);
            }
            //写到流里
            workbook.write(bos);
            byte[] bArray = bos.toByteArray();
            InputStream is = new ByteArrayInputStream(bArray);
            excelWriter = EasyExcel.write(fileName).withTemplate(is).build();
        }
        return excelWriter;
    }

    private static void fillBaseSheet(ExcelWriter excelWriter, QuoteDownloadExcel quoteDownloadExcel, List<QuoteProductExcel> quoteProductExcelList) {
        WriteSheet writeSheet = EasyExcel.writerSheet("价格表目录").build();
        excelWriter.fill(quoteDownloadExcel, writeSheet);
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(quoteProductExcelList, fillConfig, writeSheet);
    }

    private static void fillDynamicProductSheet(ExcelWriter excelWriter, List<QuoteProductExcel> quoteProductExcelList) {
        for (QuoteProductExcel productExcel : quoteProductExcelList) {
            String sheetName = genSheetName(productExcel);
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            excelWriter.fill(productExcel, writeSheet);
        }
    }

    private static QuoteDownloadExcel initData() {
        // 这里 会填充到第一个sheet， 然后文件流会自动关闭
        QuoteDownloadExcel quoteDownloadExcel = new QuoteDownloadExcel();
        quoteDownloadExcel.setCustomerName("lsj");
        quoteDownloadExcel.setSalesPhoneNumber("123456789");
        quoteDownloadExcel.setQuoteDate(DateUtils.format(new Date(), "yyyy年MM月dd日"));
        return quoteDownloadExcel;
    }

    private static String genSheetName(QuoteProductExcel productExcel) {
        return MessageFormat.format("{0}（{1}）", productExcel.getName(), productExcel.getCode());
    }

    private static List<QuoteProductExcel> initData2() {
        List<QuoteProductExcel> quotes = new ArrayList<>();
        quotes.add(genQuoteProductExcel(1));
        quotes.add(genQuoteProductExcel(2));
        return quotes;
    }

    private static QuoteProductExcel genQuoteProductExcel(int index) {
        QuoteProductExcel quoteProductExcel = new QuoteProductExcel();
        quoteProductExcel.setIndex(index);
        quoteProductExcel.setChannel("优先挂号-普货专线");
        quoteProductExcel.setCountry("法国等4国");
        quoteProductExcel.setSalesArea("华南");
        quoteProductExcel.setTimely("6-8");
        quoteProductExcel.setDesc("仅限普货，不接受香港交货，全程可跟踪，签收率高，价格优");
        quoteProductExcel.setName("联邮通优先挂号-普货");
        quoteProductExcel.setCode("" + new Random().nextInt(1000));
        return quoteProductExcel;
    }
}
