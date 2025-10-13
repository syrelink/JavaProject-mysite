package com.syr.service;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syr.dto.Result;
import com.syr.entity.Shop;
import com.syr.mapper.ShopMapper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.syr.utils.RedisData;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.syr.utils.RedisConstants.*;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    //获取商铺信息
    public Result getById(Long id) {

        // 设置空缓存解决缓存穿透
//        Shop shop = queryWithPassThrough(id);
        // 设置互斥锁解决缓存击穿
//        Shop shop = queryWithMutex(id);
//        if(shop == null) {
//            return Result.fail("店铺不存在！");
//        }

        // 设置逻辑过期解决缓存击穿
        Shop shop = queryWithLogicalExpire(id);
        if(shop == null) {
            return Result.fail("店铺不存在！");
        }

        return Result.ok();
    }

    // 缓存穿透
    public Shop queryWithPassThrough(Long id) {
        // 1.从redis查商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        // 2.判断该商铺在redis中的缓存是否存在
        if(StrUtil.isNotBlank(shopJson)) {
            // 3.存在，直接返回信息
            return JSONUtil.toBean(shopJson, Shop.class);
        }
        // 如果是空值
        if ("".equals(shopJson) ) {
            // 返回错误信息
            return null;
        }
        // 4.不存在，根据id去数据库查，如果数据库没有，将空值写入redis并返回错误
        Shop shop = lambdaQuery().eq(Shop::getId, id).one();
        if(shop == null) {
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 5.数据库存在，写入redis，并设置过期时间
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(shop),CACHE_SHOP_TTL, TimeUnit.MINUTES);

        return shop;
    }

    // 缓存击穿
    public Shop queryWithMutex(Long id) {
        // 1.从redis查商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        // 2.判断该商铺在redis中的缓存是否存在
        if(StrUtil.isNotBlank(shopJson)) {
            // 3.存在，直接返回信息
            return JSONUtil.toBean(shopJson, Shop.class);
        }
        // 如果是空值
        if ("".equals(shopJson) ) {
            // 返回错误信息
            return null;
        }
        // 4.实现缓存重建
        // 4.1 获取互斥锁
        String lockKey = "lock:shop" + id;
        Shop shop;
        try {
            boolean isLock = tryLock(lockKey);
            // 4.2 判断是否获取成功
            if(!isLock) {
                // 失败，则休眠重试
                Thread.sleep(50);
                // 成功则重新开始查询缓存
                return queryWithMutex(id);
            }
            // 5. 缓存不存在，根据id去数据库查，
            shop = lambdaQuery().eq(Shop::getId, id).one();
            // 5.1 数据库不存在，将空值写入redis并返回错误
            if(shop == null) {
                stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            // 5.2 数据库存在，写入redis，并设置过期时间，
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(shop),CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            // 6 释放互斥锁
            unlock(lockKey);
        }
        return shop;
    }
    // 获取锁
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
    // 删除锁
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    // 逻辑过期解决缓存击穿
    public static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    public Shop queryWithLogicalExpire(long id) {
        // 1.从redis查商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        // 2.判断该商铺在redis中的缓存是否存在
        if(StrUtil.isBlank(shopJson)) {
            // 3.不存在，直接返回null
            return null;
        }
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        JSONObject jsonObject = (JSONObject) redisData.getData();
        Shop shop = JSONUtil.toBean(jsonObject, Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 判断是否过期
        if(expireTime.isAfter(LocalDateTime.now())) {
            return shop;
        }
        // 过期重建缓存
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        if(isLock) {
            // todo 开启新线程
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                    this.saveShop2Redis(id, CACHE_SHOP_TTL);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    unlock(lockKey);
                }
            });
        }
        return shop;

    }
    public void saveShop2Redis(Long id,Long expireSeconds) {
        // 1.查询商铺信息

        Shop shop = lambdaQuery().eq(Shop::getId,id).one();
        // 2.封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        // 3.写入redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,JSONUtil.toJsonStr(redisData));
    }

    //更新商铺信息
    @Transactional
    public Result update(Shop shop) {
        // 更新商品信息时，先操作数据库，再删除redis缓存
        if(shop.getId() == null) {
            return Result.fail("店铺商品id不能为空");
        }
        updateById(shop);
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());

        return Result.ok();
    }

}
