package com.lsj.study.spicore;

import com.lsj.study.spiinter.People;

import java.util.ServiceLoader;

/**
 * PeopleFactory
 *
 * @author by lishangj
 * @date 2022/8/31 14:57
 */
public class PeopleFactory {
    public static People getPeople() {
        ServiceLoader<People> peopleServiceLoader = ServiceLoader.load(People.class);
        for (People people : peopleServiceLoader) {
            return people;
        }
        throw new RuntimeException("未添加people实现");
    }
}
