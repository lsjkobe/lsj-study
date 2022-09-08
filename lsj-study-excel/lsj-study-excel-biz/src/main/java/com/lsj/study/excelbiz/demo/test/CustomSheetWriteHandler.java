package com.lsj.study.excelbiz.demo.test;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;

public class CustomSheetWriteHandler implements SheetWriteHandler {

    /**
     * Called before create the sheet
     *
     * @param writeWorkbookHolder
     * @param writeSheetHolder
     */
    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    /**
     * Called after the sheet is created
     *
     * @param writeWorkbookHolder
     * @param writeSheetHolder
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        CellRangeAddressList list = new CellRangeAddressList(1, 20, 2, 3); //1到20行、2到3列添加下拉框

        DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[]{"男", "女"});
        DataValidation dataValidation = helper.createValidation(constraint, list);
        writeSheetHolder.getSheet().addValidationData(dataValidation);
    }
}
