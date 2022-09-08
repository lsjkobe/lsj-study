package offer.handler.cell;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * @author lishangj
 */
public class OfferSheetWriteHandler implements SheetWriteHandler {

    /**
     * 下拉的区间设置对象.
     */
    CellRangeAddressList cellRangeAddressList;

    /**
     * 下拉的值.
     */
    String[] data;

    public OfferSheetWriteHandler(CellRangeAddressList cellRangeAddressList, String[] data) {
        this.cellRangeAddressList = cellRangeAddressList;
        this.data = data;
    }

    /**
     * Called before create the sheet
     *
     * @param writeWorkbookHolder .
     * @param writeSheetHolder .
     */
    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    /**
     * Called after the sheet is created
     *
     * @param writeWorkbookHolder .
     * @param writeSheetHolder .
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
//        // 区间设置 1行到2行，2列到4列添加下拉框
//        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(0, 1, 1, 2);
        DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(data);
        DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
        writeSheetHolder.getSheet().addValidationData(dataValidation);
    }
}
