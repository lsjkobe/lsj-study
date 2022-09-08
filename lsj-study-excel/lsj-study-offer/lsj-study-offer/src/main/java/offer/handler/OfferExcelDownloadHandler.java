package offer.handler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import offer.handler.cell.HyperlinkCellWriteHandler;
import offer.handler.cell.RowsAndColumnMergeStrategy;
import offer.handler.utils.BeanCopierUtil;
import offer.model.excel.download.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author lishangj
 */
@Slf4j
public class OfferExcelDownloadHandler {

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

    /**
     * 下载模板.
     */
    private static final String PATH_DOWNLOAD_TEMP_PATH = "D:\\lsj\\workspace\\lsj\\lsj-study\\lsj-study-excel\\lsj-study-offer\\lsj-study-offer\\src\\main\\resources\\excel\\联邮通报价模板.xlsx";

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
        ExcelWriter excelWriter = buildExcelWriter(os, Files.newInputStream(Paths.get(PATH_DOWNLOAD_TEMP_PATH)), offerProductDownloadExcelList);
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
        Map<String, String> hyperlinkDataMap = new HashMap<>(8);
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
                    .registerWriteHandler(new RowsAndColumnMergeStrategy(PRODUCT_ROW_START_INDEX, cellRangeAddressList))
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
}
