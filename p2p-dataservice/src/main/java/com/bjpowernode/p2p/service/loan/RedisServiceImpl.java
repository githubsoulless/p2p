package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public void put(String key,String value){
//        存放60s
        redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);

    }

    @Override
    public String get(String key) {
        String value = (String) redisTemplate.opsForValue().get(key);
        return value;
    }

    @Override
    public Long getOnlyNumber() {
//        返回值的递增，从1开始递增
        return redisTemplate.opsForValue().increment(Constants.ONLY_NUMBER, 1);
    }

}
