package com.lsj.repush.common.split;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * SplitModel .
 *
 * @author lsj
 * @date 2023-06-11 14:08
 */
@Data
public class SplitModel {
    @ExcelProperty("value")
    private String value;
}
