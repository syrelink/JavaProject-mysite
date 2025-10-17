package com.syr.controller;

import com.syr.dto.Result;
import com.syr.entity.Shop;
import com.syr.service.IShopService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("shop")
public class ShopController {

    @Resource
    private IShopService shopService;

    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable Long id) {
        return Result.ok(shopService.queryById(id));
    }


    @PostMapping("/update")
    public Result updateShop(@RequestBody Shop shop) {
        return Result.ok(shopService.update(shop));
    }
}