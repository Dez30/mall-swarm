package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.constant.AuthConstant;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.UmsResourceMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台资源管理Service实现类
 * Created by macro on 2020/2/2.
 */
@Service
public class UmsResourceServiceImpl implements UmsResourceService {
    @Autowired
    private UmsResourceMapper resourceMapper;
    @Autowired
    private RedisService redisService;
    @Value("${spring.application.name}")
    private String applicationName;
    @Override
    public int create(UmsResource umsResource) {
        umsResource.setCreateTime(new Date());
        int count = resourceMapper.insert(umsResource);
        initPathResourceMap();
        return count;
    }

    @Override
    public int update(Long id, UmsResource umsResource) {
        umsResource.setId(id);
        int count = resourceMapper.updateById(umsResource);
        initPathResourceMap();
        return count;
    }

    @Override
    public UmsResource getItem(Long id) {
        return resourceMapper.selectById(id);
    }

    @Override
    public int delete(Long id) {
        int count = resourceMapper.deleteById(id);
        initPathResourceMap();
        return count;
    }

    @Override
    public Page<UmsResource> list(Long categoryId, String nameKeyword, String urlKeyword, Integer pageSize, Integer pageNum) {
        return resourceMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<UmsResource>()
                .eq(categoryId != null, UmsResource::getCategoryId, categoryId)
                .like(StrUtil.isNotEmpty(nameKeyword), UmsResource::getName, "%" + nameKeyword + "%")
                .like(StrUtil.isNotEmpty(urlKeyword), UmsResource::getUrl, "%" + urlKeyword + "%"));
    }

    @Override
    public List<UmsResource> listAll() {
        return resourceMapper.selectList(null);
    }

    @Override
    public Map<String,String> initPathResourceMap() {
        Map<String,String> pathResourceMap = new TreeMap<>();
        List<UmsResource> resourceList = resourceMapper.selectList(null);
        for (UmsResource resource : resourceList) {
            pathResourceMap.put("/"+applicationName+resource.getUrl(),resource.getId()+":"+resource.getName());
        }
        redisService.del(AuthConstant.PATH_RESOURCE_MAP);
        redisService.hSetAll(AuthConstant.PATH_RESOURCE_MAP, pathResourceMap);
        return pathResourceMap;

    }
}
