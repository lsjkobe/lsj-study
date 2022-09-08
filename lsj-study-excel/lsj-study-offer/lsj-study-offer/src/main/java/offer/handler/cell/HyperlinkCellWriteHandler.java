package offer.handler.cell;

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * HyperlinkCellWriteHandler
 *
 * @author by lishangj
 * @date 2022/9/8 9:38
 */
public class HyperlinkCellWriteHandler implements CellWriteHandler {

    /**
     * 行列数拼接与超链接地址的map.
     */
    private final Map<String, String> rowColumnSplicingHyperlinkMap;

    public HyperlinkCellWriteHandler(Map<String, String> rowColumnSplicingHyperlinkMap) {
        this.rowColumnSplicingHyperlinkMap = rowColumnSplicingHyperlinkMap;
    }

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

    }

    /**
     * Called after all operations on the cell have been completed
     *
     * @param writeSheetHolder .
     * @param writeTableHolder Nullable.It is null without using table writes.
     * @param cellDataList     Nullable.It is null in the case of add header.There may be several when fill the data.
     * @param cell             .
     * @param head             Nullable.It is null in the case of fill data and without head.
     * @param relativeRowIndex Nullable.It is null in the case of fill data.
     * @param isHead           .
     */
    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        String rowColumnSplicing = genRowColumnSplicing(cell.getRowIndex(), cell.getColumnIndex());
        String sheetName = rowColumnSplicingHyperlinkMap.get(rowColumnSplicing);
        if (StringUtils.isNotBlank(sheetName)) {
            Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
            CreationHelper createHelper = writeSheetHolder.getSheet().getWorkbook().getCreationHelper();
            Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
            hyperlink.setAddress("#'" + sheetName + "'!A1");
            //设置超链接样式
            cell.setHyperlink(hyperlink);
            CellStyle linkStyle = workbook.createCellStyle();
            linkStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            linkStyle.setAlignment(HorizontalAlignment.CENTER);
            XSSFFont cellFont = (XSSFFont) workbook.createFont();
            cellFont.setUnderline((byte) 1);
            cellFont.setFontName("微软雅黑");
            cellFont.setColor(HSSFColor.HSSFColorPredefined.ROYAL_BLUE.getIndex());
            linkStyle.setFont(cellFont);
            cell.setCellStyle(linkStyle);
        }
    }

    public static String genRowColumnSplicing(int row, int column) {
        return MessageFormat.format("{0}{1}", row, column);
    }
}
