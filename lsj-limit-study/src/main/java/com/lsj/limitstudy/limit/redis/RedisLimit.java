package com.lsj.limitstudy.limit.redis;

import java.util.concurrent.atomic.AtomicInteger;

public class RedisLimit {
    AtomicInteger[] slots ;

    public RedisLimit(int wTime) {
        slots = new AtomicInteger[wTime * 1000];
    }

    public boolean allow() {
        long current = System.currentTimeMillis();
        return false;
    }
}
