package com.lsj.repush;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lishangj
 */
public class QfTest {

    private final String path = "C:\\Users\\lishangj\\Desktop\\lsj\\首公里\\菜鸟\\重推\\LP-idms-0609.xlsx";

    private final String targetDirectory = "C:\\Users\\lishangj\\Desktop\\lsj\\首公里\\菜鸟\\重推\\切分";

    public static void main(String[] args) {
        QfTest qfTest = new QfTest();
//        qfTest.createMockExcel();
        qfTest.qf();
    }

    public void createMockExcel() {
        this.generateExcel(path);
        System.out.println("Excel generated successfully!");
    }

    public void generateExcel(String filePath) {
        List<DataModel> data = generateData(300000);
        EasyExcel.write(filePath, DataModel.class).sheet("Sheet1").doWrite(data);
    }

    private List<DataModel> generateData(int rowCount) {
        List<DataModel> data = new ArrayList<>();

        for (int i = 1; i <= rowCount; i++) {
            String value = "LP" + String.format("%010d", i);
            DataModel model = new DataModel();
            model.setValue(value);
            data.add(model);
        }

        return data;
    }

    private void qf() {
        splitExcel(path, targetDirectory, 500);
    }

    public void splitExcel(String sourceFile, String targetDirectory, int batchSize) {
        EasyExcel.read(sourceFile, DataModel.class, new SplitListener(targetDirectory, batchSize)).sheet().doRead();
    }

    public static class SplitListener extends AnalysisEventListener<DataModel> {

        private String targetDirectory;
        private int batchSize;
        private List<DataModel> batchData;
        private int currentFileIndex;
        private int currentDataIndex;

        public SplitListener(String targetDirectory, int batchSize) {
            this.targetDirectory = targetDirectory;
            this.batchSize = batchSize;
            this.batchData = new ArrayList<>();
            this.currentFileIndex = 1;
            this.currentDataIndex = 0;
        }

        @Override
        public void invoke(DataModel data, AnalysisContext context) {
            batchData.add(data);
            currentDataIndex++;

            if (currentDataIndex >= batchSize) {
                writeBatchData();
                batchData.clear();
                currentDataIndex = 0;
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            if (!batchData.isEmpty()) {
                writeBatchData();
                batchData.clear();
            }
        }

        private void writeBatchData() {
            String fileName = targetDirectory + File.separator + "split_" + currentFileIndex + ".xlsx";
            EasyExcel.write(fileName, DataModel.class).sheet("Sheet1").doWrite(batchData);
            currentFileIndex++;
        }
    }

    public static class DataModel {
        @ExcelProperty("value")
        private String value;


        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
