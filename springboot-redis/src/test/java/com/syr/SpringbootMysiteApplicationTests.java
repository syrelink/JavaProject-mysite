package com.syr;

import com.syr.service.ShopServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class SpringbootMysiteApplicationTests {
    @Resource
    private ShopServiceImpl shopService;

    @Test
    public void testSaveShop() {
        shopService.saveShop2Redis(1L,10L);
    }
}
