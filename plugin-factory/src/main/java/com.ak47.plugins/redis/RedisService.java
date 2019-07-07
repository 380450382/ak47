package com.ak47.plugins.redis;

import java.util.List;

public interface RedisService {
    <T> List<T> getList(String key, Class<T> clazz);
    <T> T get(String key,Class<T> clazz);
    <T> boolean set(String key, T t,Long expire);
    <T> boolean set(String key, T t);
    boolean exists(String key);
    boolean delete(String key);
}
