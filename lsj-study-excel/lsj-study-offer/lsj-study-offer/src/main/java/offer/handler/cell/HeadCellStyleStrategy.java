package offer.handler.cell;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.AbstractVerticalCellStyleStrategy;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * HeadCellStyleStrategy
 *
 * @author by lishangj
 * @date 2022/9/6 10:06
 */
public class HeadCellStyleStrategy extends AbstractVerticalCellStyleStrategy {

    @Override
    protected WriteCellStyle headCellStyle(Head head) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        // 字体策略
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 11);
        contentWriteFont.setBold(Boolean.FALSE);
        writeCellStyle.setWriteFont(contentWriteFont);
//        HSSFWorkbook wb = new HSSFWorkbook();
//        HSSFPalette palette = wb.getCustomPalette();
//        HSSFColor hssfColor = palette.findSimilarColor(226, 239, 218);
        writeCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        return writeCellStyle;
    }

    @Override
    protected WriteCellStyle contentCellStyle(Head head) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        return writeCellStyle;
    }
}
