package com.lsj.study.spibiz;

import com.lsj.study.spicore.PeopleFactory;
import com.lsj.study.spiinter.People;

/**
 * TestSpi
 *
 * @author by lishangj
 * @date 2022/8/31 15:36
 */
public class TestSpi {

    public static void main(String[] args) {
        People people = PeopleFactory.getPeople();
        people.say();
    }
}
