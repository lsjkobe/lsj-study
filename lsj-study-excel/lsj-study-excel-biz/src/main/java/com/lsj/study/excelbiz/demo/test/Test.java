package com.lsj.study.excelbiz.demo.test;

import com.alibaba.excel.EasyExcel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test {
    /**
     * .
     */
    private static final String DOWNLOAD_FILENAME = "D:/tmp/excel/download/" + System.currentTimeMillis() + ".xlsx";

    public static void write() {
        EasyExcel.write(DOWNLOAD_FILENAME).registerWriteHandler(new CustomSheetWriteHandler())
                .sheet().doWrite(data());
    }

    private static List<Person> data() {
        List<Person> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Person person = new Person();
            person.setId(i);
            person.setName("瓜田李下 " + i);
            person.setAge(20 + i);

            list.add(person);
        }

        return list;
    }

    public static void main(String[] args) {
        write();
    }
}
