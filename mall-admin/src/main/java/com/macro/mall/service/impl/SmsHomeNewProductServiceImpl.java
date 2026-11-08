package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.mapper.SmsHomeNewProductMapper;
import com.macro.mall.model.SmsHomeNewProduct;
import com.macro.mall.service.SmsHomeNewProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 首页新品推荐管理Service实现类
 * Created by macro on 2018/11/6.
 */
@Service
public class SmsHomeNewProductServiceImpl implements SmsHomeNewProductService {
    @Autowired
    private SmsHomeNewProductMapper homeNewProductMapper;
    @Override
    public int create(List<SmsHomeNewProduct> homeNewProductList) {
        for (SmsHomeNewProduct SmsHomeNewProduct : homeNewProductList) {
            SmsHomeNewProduct.setRecommendStatus(1);
            SmsHomeNewProduct.setSort(0);
            homeNewProductMapper.insert(SmsHomeNewProduct);
        }
        return homeNewProductList.size();
    }

    @Override
    public int updateSort(Long id, Integer sort) {
        SmsHomeNewProduct homeNewProduct = new SmsHomeNewProduct();
        homeNewProduct.setId(id);
        homeNewProduct.setSort(sort);
        return homeNewProductMapper.updateById(homeNewProduct);
    }

    @Override
    public int delete(List<Long> ids) {
        return homeNewProductMapper.delete(new LambdaQueryWrapper<SmsHomeNewProduct>()
                .in(SmsHomeNewProduct::getId, ids));
    }

    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        return homeNewProductMapper.update(null, new LambdaUpdateWrapper<SmsHomeNewProduct>()
                .in(SmsHomeNewProduct::getId, ids)
                .set(SmsHomeNewProduct::getRecommendStatus, recommendStatus));
    }

    @Override
    public Page<SmsHomeNewProduct> list(String productName, Integer recommendStatus, Integer pageSize, Integer pageNum) {
        return homeNewProductMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<SmsHomeNewProduct>()
                .like(!StringUtils.isEmpty(productName), SmsHomeNewProduct::getProductName, "%" + productName + "%")
                .eq(recommendStatus != null, SmsHomeNewProduct::getRecommendStatus, recommendStatus)
                .orderByDesc(SmsHomeNewProduct::getSort));
    }
}
