package com.lsj.repush;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lishangj
 */
public class ExcelReader {
    public static List<QfTest.DataModel> readExcelToList(String filePath) {
        DataModelListener dataModelListener = new DataModelListener();
        // 构建 Excel 读取器
        ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(filePath, QfTest.DataModel.class, dataModelListener);

        // 设置读取的 Sheet，这里默认读取第一个 Sheet（下标从 0 开始）
        ExcelReaderSheetBuilder sheetBuilder = excelReaderBuilder.sheet();

        // 开始读取
        sheetBuilder.doRead();

        return dataModelListener.getDataList();
    }

    /**
     * 自定义监听器，用于读取数据并添加到 dataList
     */
    private static class DataModelListener extends AnalysisEventListener<QfTest.DataModel> {
        private List<QfTest.DataModel> dataList = new ArrayList<>();

        @Override
        public void invoke(QfTest.DataModel data, AnalysisContext context) {
            dataList.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 读取完成后的操作
        }

        public List<QfTest.DataModel> getDataList() {
            return dataList;
        }
    }
}
