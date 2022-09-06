package com.lsj.study.excelbiz.demo;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * AssignRowsAndColumnsToMergeStrategy .
 *
 * @author lsj
 * @date 2022-09-05 14:09
 */
public class AssignRowsAndColumnsToMergeStrategy  extends AbstractMergeStrategy {
    // 合并坐标集合
    private List<CellRangeAddress> cellRangeAddresses;

    private int startRowIndex;

    // 构造
    public AssignRowsAndColumnsToMergeStrategy() {
    }

    public AssignRowsAndColumnsToMergeStrategy(int startRowIndex,List<CellRangeAddress> cellRangeAddresses) {
        this.startRowIndex = startRowIndex;
        this.cellRangeAddresses = cellRangeAddresses;
    }

    /**
     * 合并操作：对每个单元格执行！！！
     *
     * @param sheet            sheet对象
     * @param cell             当前单元格
     * @param head             表头对象
     * @param relativeRowIndex 相关行索引
     */
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        /*
         * 合并单元格
         *
         * 由于merge()方法会在写每个单元格(cell)时执行，因此需要保证合并策略只被添加一次。否则如果每个单元格都添加一次
         * 合并策略，则会造成重复合并。例如合并A2:A3,当cell为A2时，合并A2:A3，但是当cell为A3时，又要合并A2:A3，而此时
         * 的A2已经是之前的A2和A3合并后的结果了。
         * 由于此处的表头占了两行，因此数据单元格是从(2, 0)开始的，所以就对这个单元格(cell.getRowIndex() == 2 && cell.getColumnIndex() == 0)
         * 添加一次合并策略就可以了。如果表头只有一行，则判断条件改为「cell.getRowIndex() == 1 && cell.getColumnIndex() == 0」就可以了。
         */
        if (cell.getRowIndex() == startRowIndex && cell.getColumnIndex() == 0) {
            for (CellRangeAddress item : cellRangeAddresses) {
                sheet.addMergedRegion(item);
            }
        }

        /*
         * 如果不作判断，可以使用addMergedRegionUnsafe()方法，
         * 这样生成的Excel文件可以打开，只是打开时会提示内容有问题，修复后可以打开
         */
        // for (CellRangeAddress item : cellRangeAddresses) {
        //     sheet.addMergedRegionUnsafe(item);
        // }
    }
}
