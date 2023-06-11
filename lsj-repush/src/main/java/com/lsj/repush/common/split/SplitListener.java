package com.lsj.repush.common.split;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * SplitListener .
 *
 * @author lsj
 * @date 2023-06-11 14:07
 */
public class SplitListener extends AnalysisEventListener<SplitModel> {

    private final String targetDirectory;
    private final Long batchSize;
    private final List<SplitModel> batchData;
    private int currentFileIndex;
    private int currentDataIndex;

    public SplitListener(String targetDirectory, Long batchSize) {
        this.targetDirectory = targetDirectory;
        this.batchSize = batchSize;
        this.batchData = new ArrayList<>();
        this.currentFileIndex = 1;
        this.currentDataIndex = 0;
    }

    @Override
    public void invoke(SplitModel data, AnalysisContext context) {
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
        EasyExcel.write(fileName, SplitModel.class).sheet("Sheet1").doWrite(batchData);
        currentFileIndex++;
    }
}
