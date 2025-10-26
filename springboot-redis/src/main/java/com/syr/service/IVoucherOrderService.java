package com.syr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syr.dto.Result;
import com.syr.entity.VoucherOrder;

public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);
}
