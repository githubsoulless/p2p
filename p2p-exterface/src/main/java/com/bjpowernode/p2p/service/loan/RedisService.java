package com.bjpowernode.p2p.service.loan;

public interface RedisService {
    public void put(String key,String value);
    public String get(String key);

    Long getOnlyNumber();
}
