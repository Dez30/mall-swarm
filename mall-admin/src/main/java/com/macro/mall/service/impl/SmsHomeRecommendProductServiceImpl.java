package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.mapper.SmsHomeRecommendProductMapper;
import com.macro.mall.model.SmsHomeRecommendProduct;
import com.macro.mall.service.SmsHomeRecommendProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 首页人气推荐管理Service实现类
 * Created by macro on 2018/11/7.
 */
@Service
public class SmsHomeRecommendProductServiceImpl implements SmsHomeRecommendProductService {
    @Autowired
    private SmsHomeRecommendProductMapper recommendProductMapper;
    @Override
    public int create(List<SmsHomeRecommendProduct> homeRecommendProductList) {
        for (SmsHomeRecommendProduct recommendProduct : homeRecommendProductList) {
            recommendProduct.setRecommendStatus(1);
            recommendProduct.setSort(0);
            recommendProductMapper.insert(recommendProduct);
        }
        return homeRecommendProductList.size();
    }

    @Override
    public int updateSort(Long id, Integer sort) {
        SmsHomeRecommendProduct recommendProduct = new SmsHomeRecommendProduct();
        recommendProduct.setId(id);
        recommendProduct.setSort(sort);
        return recommendProductMapper.updateById(recommendProduct);
    }

    @Override
    public int delete(List<Long> ids) {
        return recommendProductMapper.delete(new LambdaQueryWrapper<SmsHomeRecommendProduct>()
                .in(SmsHomeRecommendProduct::getId, ids));
    }

    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        return recommendProductMapper.update(null, new LambdaUpdateWrapper<SmsHomeRecommendProduct>()
                .in(SmsHomeRecommendProduct::getId, ids)
                .set(SmsHomeRecommendProduct::getRecommendStatus, recommendStatus));
    }

    @Override
    public Page<SmsHomeRecommendProduct> list(String productName, Integer recommendStatus, Integer pageSize, Integer pageNum) {
        return recommendProductMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<SmsHomeRecommendProduct>()
                .like(!StringUtils.isEmpty(productName), SmsHomeRecommendProduct::getProductName, "%" + productName + "%")
                .eq(recommendStatus != null, SmsHomeRecommendProduct::getRecommendStatus, recommendStatus)
                .orderByDesc(SmsHomeRecommendProduct::getSort));
    }
}
