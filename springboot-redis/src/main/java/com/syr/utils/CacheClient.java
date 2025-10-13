package com.syr.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.syr.utils.RedisConstants.*;

@Slf4j
@Component
public class CacheClient {
    private StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit) {
        // 将object任意java对象序列化为jason字符串
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,unit);
    }

    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));

        // 写入redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(redisData));
    }

    public <R,ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID,R> dbFallback,
                                         Long time, TimeUnit unit) {
        // 1.从redis查商铺缓存
        String json = stringRedisTemplate.opsForValue().get(keyPrefix + id);
        // 2.判断该商铺在redis中的缓存是否存在
        if(StrUtil.isNotBlank(json)) {
            // 3.存在，直接返回信息
            return JSONUtil.toBean(json, type);
        }
        // 如果是空值
        if ("".equals(json) ) {
            // 返回错误信息
            return null;
        }
        // 4.不存在，根据id去数据库查，如果数据库没有，将空值写入redis并返回错误
        R r = dbFallback.apply(id);

        if(r == null) {
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 5.数据库存在，写入redis，并设置过期时间
        this.set(keyPrefix + id,r,time,unit);
        return r;
    }
}

