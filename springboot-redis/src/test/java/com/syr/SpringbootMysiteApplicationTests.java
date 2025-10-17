package com.syr;

import com.syr.dto.Result;
import com.syr.entity.Shop;
import com.syr.service.ShopServiceImpl;
import com.syr.utils.CacheClient;
import com.syr.utils.RedisIdWorker;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.syr.utils.RedisConstants.*;


@SpringBootTest
class SpringbootMysiteApplicationTests {
    @Resource
    private RedisIdWorker redisIdWorker;


}
