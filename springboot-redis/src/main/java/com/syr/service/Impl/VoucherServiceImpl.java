package com.syr.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syr.entity.SeckillVoucher;
import com.syr.entity.Voucher;
import com.syr.mapper.VoucherMapper;
import com.syr.service.ISeckillVoucherService;
import com.syr.service.IVoucherService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper,Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());

        seckillVoucherService.save(seckillVoucher);
    }
}
