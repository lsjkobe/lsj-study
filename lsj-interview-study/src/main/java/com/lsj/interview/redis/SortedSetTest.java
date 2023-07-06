package com.lsj.interview.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lishangj
 */
@Service
public class SortedSetTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void test() {
        redisTemplate.opsForZSet().add("", "", 1);
    }
}
