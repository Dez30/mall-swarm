package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.dto.PmsBrandParam;
import com.macro.mall.mapper.PmsBrandMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.service.PmsBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品品牌Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class PmsBrandServiceImpl implements PmsBrandService {
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> listAllBrand() {
        return brandMapper.selectList(null);
    }

    @Override
    public int createBrand(PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        //如果创建时首字母为空，取名称的第一个为首字母
        if (StringUtils.isEmpty(pmsBrand.getFirstLetter())) {
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        return brandMapper.insert(pmsBrand);
    }

    @Override
    public int updateBrand(Long id, PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        pmsBrand.setId(id);
        //如果创建时首字母为空，取名称的第一个为首字母
        if (StringUtils.isEmpty(pmsBrand.getFirstLetter())) {
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        //更新品牌时要更新商品中的品牌名称
        productMapper.update(null, new LambdaUpdateWrapper<PmsProduct>()
                .eq(PmsProduct::getBrandId, id)
                .set(PmsProduct::getBrandName, pmsBrand.getName()));
        return brandMapper.updateById(pmsBrand);
    }

    @Override
    public int deleteBrand(Long id) {
        return brandMapper.deleteById(id);
    }

    @Override
    public int deleteBrand(List<Long> ids) {
        return brandMapper.delete(new LambdaQueryWrapper<PmsBrand>()
                .in(PmsBrand::getId, ids));
    }

    @Override
    public Page<PmsBrand> listBrand(String keyword, int pageNum, int pageSize) {
        return brandMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<PmsBrand>()
                .orderByDesc(PmsBrand::getSort)
                .like(!StringUtils.isEmpty(keyword), PmsBrand::getName, "%" + keyword + "%"));
    }

    @Override
    public PmsBrand getBrand(Long id) {
        return brandMapper.selectById(id);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        return brandMapper.update(null, new LambdaUpdateWrapper<PmsBrand>()
                .in(PmsBrand::getId, ids)
                .set(PmsBrand::getShowStatus, showStatus));
    }

    @Override
    public int updateFactoryStatus(List<Long> ids, Integer factoryStatus) {
        return brandMapper.update(null, new LambdaUpdateWrapper<PmsBrand>()
                .in(PmsBrand::getId, ids)
                .set(PmsBrand::getFactoryStatus, factoryStatus));
    }
}
