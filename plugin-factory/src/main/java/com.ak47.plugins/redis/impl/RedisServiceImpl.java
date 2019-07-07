package com.ak47.plugins.redis.impl;

import com.ak47.plugins.redis.RedisService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {
    private final static Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            if(exists(key)){
                Object result = redisTemplate.opsForValue().get(key);
                if(result instanceof String) {
                    return JSON.parseArray((String)redisTemplate.opsForValue().get(key), clazz);
                }
            }
        } catch (Exception e) {
            logger.error("获取redis数据时出错",e);
        }
        return null;
    }

    @Override
    public <T> T get(String key,Class<T> clazz) {
        try {
            if(exists(key)){
                Object result = redisTemplate.opsForValue().get(key);
                if(result instanceof String) {
                    return JSON.parseObject((String)redisTemplate.opsForValue().get(key), clazz);
                }
            }
        } catch (Exception e) {
            logger.error("获取redis数据时出错",e);
        }
        return null;
    }

    @Override
    public <T> boolean set(String key, T t, Long expire) {
        try {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(t), Duration.ofMillis(expire));
            return true;
        } catch (Exception e) {
            logger.error("写入redis时出错",e);
        }
        return false;
    }

    @Override
    public <T> boolean set(String key, T t) {
        set(key,t,-1L);
        return false;
    }

    @Override
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("校验redis数据是否存在时出错",e);
        }
        return false;
    }

    @Override
    public boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            logger.error("删除redis数据是时出错",e);
        }
        return false;
    }
}
