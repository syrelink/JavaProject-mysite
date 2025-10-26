package com.syr.controller;

import com.syr.dto.Result;
import com.syr.service.IVoucherOrderService;
import com.syr.service.IVoucherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    private IVoucherOrderService iVoucherOrderService;
    /**
     * 优惠券秒杀下单，添加优惠订单
    * */
    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {

        return iVoucherOrderService.seckillVoucher(voucherId);
    }

}
