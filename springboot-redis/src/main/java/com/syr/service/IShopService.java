package com.syr.service;

import com.syr.dto.Result;
import com.syr.entity.Shop;


public interface IShopService {

   public Result getById(Long id);

   public Result update(Shop shop);
}