package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.mapper.CmsSubjectMapper;
import com.macro.mall.model.CmsSubject;
import com.macro.mall.service.CmsSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品专题管理Service实现类
 * Created by macro on 2018/6/1.
 */
@Service
public class CmsSubjectServiceImpl implements CmsSubjectService {
    @Autowired
    private CmsSubjectMapper subjectMapper;

    @Override
    public List<CmsSubject> listAll() {
        return subjectMapper.selectList(null);
    }

    @Override
    public Page<CmsSubject> list(String keyword, Integer pageNum, Integer pageSize) {
        return subjectMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<CmsSubject>()
                .like(!StringUtils.isEmpty(keyword), CmsSubject::getTitle, "%" + keyword + "%"));
    }
}
