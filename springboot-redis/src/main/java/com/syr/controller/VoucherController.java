package com.syr.controller;

import com.syr.dto.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/voucher")
public class VoucherController {

    public Result addSeckillVoucher(@RequestBody Voucher voucher) {

    }

}
