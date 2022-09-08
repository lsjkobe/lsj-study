package com.lsj.study.excelbiz.demo;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ModelBuildEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * DemoMain .
 *
 * @author lsj
 * @date 2022-09-05 11:42
 */
public class DemoMain {

    private static String fileName = "E:\\tmp\\excel/lsjtest/" + System.currentTimeMillis() + ".xlsx";

    public static void main(String[] args) {
//        EasyExcel.write(fileName)
//                .head(header())
//                .registerWriteHandler(new ExcelMergeHandler(2, new int[]{0, 1, 2, 3, 5, 8, 9}))
//                .sheet("Sheet1")
//                .doWrite(data());
//
//        // 合并策略：指定要合并的行列范围
//        int[][] toMergeRows = {{2, 3}, {4, 6}};
//        int[] toMergeColumns = {0, 1, 2, 3, 8, 9};
//        List<CellRangeAddress> list = new ArrayList<>();
//        for (int[] toMergeRow : toMergeRows) {
//            for (int toMergeColumn : toMergeColumns) {
//                list.add(new CellRangeAddress(toMergeRow[0], toMergeRow[1], toMergeColumn, toMergeColumn));
//            }
//        }
//
//        EasyExcel.write(fileName)
//                .head(header())
//                .registerWriteHandler(new AssignRowsAndColumnsToMergeStrategy(2, list))
//                .sheet("模板")
//                .doWrite(data());

        EasyExcel.read("C:\\Users\\lishangj\\Desktop\\lsj\\联邮通报价下载模板.xlsx", new ModelBuildEventListener()).doReadAll();
    }

    /**
     * 创建表头
     */
    private static List<List<String>> header() {
        List<List<String>> headers = new ArrayList<>();
        headers.add(Arrays.asList("提交人用户名", "提交人用户名"));
        headers.add(Arrays.asList("提交人姓名", "提交人姓名"));
        headers.add(Arrays.asList("创建时间", "创建时间"));
        headers.add(Arrays.asList("更新时间", "更新时间"));
        headers.add(Arrays.asList("学习经历", "时间"));
        headers.add(Arrays.asList("学习经历", "学校"));
        headers.add(Arrays.asList("学习经历", "专业"));
        headers.add(Arrays.asList("学习经历", "学位"));
        headers.add(Arrays.asList("工作单位", "工作单位"));
        headers.add(Arrays.asList("国籍", "国籍"));
        headers.add(Arrays.asList("获奖经历", "时间"));
        headers.add(Arrays.asList("获奖经历", "何种奖励"));
        return headers;
    }

    /**
     * 创建数据
     */
    private static List<List<Object>> data() {
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("fengqingyang", "风清扬", "2022-01-25 11:08", "2022-01-25 11:08",
                "2013.9 ~ 2017.7", "华山派", "剑宗", "剑宗高手", "隐居思过崖", "中国", "2015.12", "华山剑法高手"));
        data.add(Arrays.asList("fengqingyang", "风清扬", "2022-01-25 11:08", "2022-01-25 11:08",
                "2017.9 ~ 2020.7", "华山派", "独孤剑法", "剑术通神", "隐居思过崖", "中国", "2019.12", "剑法高手"));
        data.add(Arrays.asList("linghuchong", "令狐冲", "2022-01-25 11:08", "2022-01-25 11:08",
                "2020.9 ~ 2024.7", "华山派", "气宗", "气宗庸手", "漂泊江湖", "中国", "2022.12", "华山剑法庸手"));
        data.add(Arrays.asList("linghuchong", "令狐冲", "2022-01-25 12:08", "2022-01-25 12:08",
                "2024.9 ~ 2027.7", "风清扬", "独孤剑法", "剑法高手", "漂泊江湖", "中国", "2025.12", "剑法高手"));
        data.add(Arrays.asList("linghuchong", "令狐冲", "2022-01-25 12:08", "2022-01-25 12:08",
                "2027.9 ~ 2030.7", "少林寺", "易筋经", "内功高手", "漂泊江湖", "中国", "2029.12", "内功高手"));
        return data;
    }
}
