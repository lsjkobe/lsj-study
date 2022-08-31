package com.lsj.study.spiboy;

import com.lsj.study.spiinter.People;

/**
 * Boy
 *
 * @author by lishangj
 * @date 2022/8/31 15:19
 */
public class Boy implements People {
    @Override
    public void say() {
        System.out.println("你好，我是男孩！");
    }
}
