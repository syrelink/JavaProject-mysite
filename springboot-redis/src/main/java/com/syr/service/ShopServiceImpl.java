package com.syr.service;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syr.dto.Result;
import com.syr.entity.Shop;
import com.syr.mapper.ShopMapper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.syr.utils.RedisConstants.*;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    //获取商铺信息
    public Result getById(Long id) {
        // 1.从redis查商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        // 2.判断该商铺在redis中的缓存是否存在
        if(StrUtil.isNotBlank(shopJson)) {
            // 3.存在，直接返回信息
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        // 如果是空值
        if ("".equals(shopJson) ) {
            // 返回错误信息
            return Result.fail("店铺信息不存在！");
        }
        // 4.不存在，根据id去数据库查，如果数据库没有，返回错误
        Shop shop = lambdaQuery().eq(Shop::getId, id).one();

        if(shop == null) {
            // 将空值写入redis
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
            return Result.fail("商铺不存在！");
        }
        // 5.数据库存在，写入redis，并设置过期时间
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(shop),CACHE_SHOP_TTL, TimeUnit.MINUTES);

        return Result.ok(shop);
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    private void saveShopRedis(Long id) {
        // 1.查询商铺信息
        lambdaQuery().eq(Shop::getId,id).one();
        // 2.封装逻辑过期时间

        // 3.写入redis
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
