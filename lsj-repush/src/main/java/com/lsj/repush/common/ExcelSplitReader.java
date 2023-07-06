package com.lsj.repush.common;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.lsj.repush.QfTest;
import lombok.Getter;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lishangj
 */
public class ExcelSplitReader {
    public static <T> List<T> readExcelToList(String filePath, Class<T> head) {
        DataModelListener<T> dataModelListener = new DataModelListener<>();
        // 构建 Excel 读取器
        ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(filePath, head, dataModelListener);

        // 设置读取的 Sheet，这里默认读取第一个 Sheet（下标从 0 开始）
        ExcelReaderSheetBuilder sheetBuilder = excelReaderBuilder.sheet();

        // 开始读取
        sheetBuilder.doRead();

        return dataModelListener.getDataList();
    }

    /**
     * 自定义监听器，用于读取数据并添加到 dataList
     */
    @Getter
    private static class DataModelListener<T> extends AnalysisEventListener<T> {
        private final List<T> dataList = new ArrayList<>();

        @Override
        public void invoke(T data, AnalysisContext context) {
            dataList.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 读取完成后的操作
        }

    }
}
