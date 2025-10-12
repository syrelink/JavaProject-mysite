package com.syr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.syr.dto.Result;
import com.syr.service.IShopTypeService;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("shop-type")
public class ShopTypeController {

    @Resource
    private IShopTypeService shopTypeService;

    @GetMapping("list")
    public Result getShopTypeList() {
        return Result.ok(shopTypeService.getShopTypeList());
    }
}
