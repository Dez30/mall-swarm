package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.mapper.SmsCouponHistoryMapper;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.service.SmsCouponHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 优惠券领取记录管理Service实现类
 * Created by macro on 2018/11/6.
 */
@Service
public class SmsCouponHistoryServiceImpl implements SmsCouponHistoryService {
    @Autowired
    private SmsCouponHistoryMapper historyMapper;
    @Override
    public Page<SmsCouponHistory> list(Long couponId, Integer useStatus, String orderSn, Integer pageSize, Integer pageNum) {
        return historyMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<SmsCouponHistory>()
                .eq(couponId != null, SmsCouponHistory::getCouponId, couponId)
                .eq(useStatus != null, SmsCouponHistory::getUseStatus, useStatus)
                .eq(!StringUtils.isEmpty(orderSn), SmsCouponHistory::getOrderSn, orderSn));
    }
}
