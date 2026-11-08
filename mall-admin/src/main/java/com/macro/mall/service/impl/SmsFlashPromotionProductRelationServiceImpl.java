package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.dao.SmsFlashPromotionProductRelationDao;
import com.macro.mall.dto.SmsFlashPromotionProduct;
import com.macro.mall.mapper.SmsFlashPromotionProductRelationMapper;
import com.macro.mall.model.SmsFlashPromotionProductRelation;
import com.macro.mall.service.SmsFlashPromotionProductRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 限时购商品关联管理Service实现类
 * Created by macro on 2018/11/16.
 */
@Service
public class SmsFlashPromotionProductRelationServiceImpl implements SmsFlashPromotionProductRelationService {
    @Autowired
    private SmsFlashPromotionProductRelationMapper relationMapper;
    @Autowired
    private SmsFlashPromotionProductRelationDao relationDao;
    @Override
    public int create(List<SmsFlashPromotionProductRelation> relationList) {
        for (SmsFlashPromotionProductRelation relation : relationList) {
            relationMapper.insert(relation);
        }
        return relationList.size();
    }

    @Override
    public int update(Long id, SmsFlashPromotionProductRelation relation) {
        relation.setId(id);
        return relationMapper.updateById(relation);
    }

    @Override
    public int delete(Long id) {
        return relationMapper.deleteById(id);
    }

    @Override
    public SmsFlashPromotionProductRelation getItem(Long id) {
        return relationMapper.selectById(id);
    }

    @Override
    public Page<SmsFlashPromotionProduct> list(Long flashPromotionId, Long flashPromotionSessionId, Integer pageSize, Integer pageNum) {
        //自定义Dao的联表查询无法直接套用MP分页，先全量查询再手工分页组装Page
        List<SmsFlashPromotionProduct> allList = relationDao.getList(flashPromotionId, flashPromotionSessionId);
        Page<SmsFlashPromotionProduct> page = new Page<>(pageNum, pageSize);
        long total = allList.size();
        page.setTotal(total);
        page.setPages(total == 0 ? 0 : (total + pageSize - 1) / pageSize);
        long fromIndex = (long) (pageNum - 1) * pageSize;
        if (fromIndex < total) {
            long toIndex = Math.min(fromIndex + pageSize, total);
            page.setRecords(allList.stream().skip(fromIndex).limit(toIndex - fromIndex).collect(Collectors.toList()));
        }
        return page;
    }

    @Override
    public long getCount(Long flashPromotionId, Long flashPromotionSessionId) {
        return relationMapper.selectCount(new LambdaQueryWrapper<SmsFlashPromotionProductRelation>()
                .eq(SmsFlashPromotionProductRelation::getFlashPromotionId, flashPromotionId)
                .eq(SmsFlashPromotionProductRelation::getFlashPromotionSessionId, flashPromotionSessionId));
    }
}
