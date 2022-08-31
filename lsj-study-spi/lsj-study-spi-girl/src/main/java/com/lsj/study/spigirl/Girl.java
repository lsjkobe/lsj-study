package com.lsj.study.spigirl;

import com.lsj.study.spiinter.People;

/**
 * Girl
 *
 * @author by lishangj
 * @date 2022/8/31 15:28
 */
public class Girl implements People {
    @Override
    public void say() {
        System.out.println("你好，我是女孩！");
    }
}
