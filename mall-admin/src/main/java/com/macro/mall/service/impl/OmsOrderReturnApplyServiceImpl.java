package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.dao.OmsOrderReturnApplyDao;
import com.macro.mall.dto.OmsOrderReturnApplyResult;
import com.macro.mall.dto.OmsReturnApplyQueryParam;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.mapper.OmsOrderReturnApplyMapper;
import com.macro.mall.model.OmsOrderReturnApply;
import com.macro.mall.service.OmsOrderReturnApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 订单退货管理Service
 * Created by macro on 2018/10/18.
 */
@Service
public class OmsOrderReturnApplyServiceImpl implements OmsOrderReturnApplyService {
    @Autowired
    private OmsOrderReturnApplyDao returnApplyDao;
    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;
    @Override
    public Page<OmsOrderReturnApply> list(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum) {
        //原OmsOrderReturnApplyDao.getList的查询逻辑（列子集+动态条件），改用MP分页
        return returnApplyMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<OmsOrderReturnApply>()
                .select(OmsOrderReturnApply::getId,
                        OmsOrderReturnApply::getCreateTime,
                        OmsOrderReturnApply::getMemberUsername,
                        OmsOrderReturnApply::getProductRealPrice,
                        OmsOrderReturnApply::getProductCount,
                        OmsOrderReturnApply::getReturnName,
                        OmsOrderReturnApply::getStatus,
                        OmsOrderReturnApply::getHandleTime)
                .eq(queryParam.getId() != null, OmsOrderReturnApply::getId, queryParam.getId())
                .eq(queryParam.getStatus() != null, OmsOrderReturnApply::getStatus, queryParam.getStatus())
                .eq(!StringUtils.isEmpty(queryParam.getHandleMan()), OmsOrderReturnApply::getHandleMan, queryParam.getHandleMan())
                .likeRight(!StringUtils.isEmpty(queryParam.getCreateTime()), OmsOrderReturnApply::getCreateTime, queryParam.getCreateTime())
                .likeRight(!StringUtils.isEmpty(queryParam.getHandleTime()), OmsOrderReturnApply::getHandleTime, queryParam.getHandleTime())
                .and(!StringUtils.isEmpty(queryParam.getReceiverKeyword()), wrapper -> wrapper
                        .like(OmsOrderReturnApply::getReturnName, queryParam.getReceiverKeyword())
                        .or()
                        .like(OmsOrderReturnApply::getReturnPhone, queryParam.getReceiverKeyword())));
    }

    @Override
    public int delete(List<Long> ids) {
        return returnApplyMapper.delete(new LambdaQueryWrapper<OmsOrderReturnApply>()
                .in(OmsOrderReturnApply::getId, ids)
                .eq(OmsOrderReturnApply::getStatus, 3));
    }

    @Override
    public int updateStatus(Long id, OmsUpdateStatusParam statusParam) {
        Integer status = statusParam.getStatus();
        OmsOrderReturnApply returnApply = new OmsOrderReturnApply();
        if(status.equals(1)){
            //确认退货
            returnApply.setId(id);
            returnApply.setStatus(1);
            returnApply.setReturnAmount(statusParam.getReturnAmount());
            returnApply.setCompanyAddressId(statusParam.getCompanyAddressId());
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        }else if(status.equals(2)){
            //完成退货
            returnApply.setId(id);
            returnApply.setStatus(2);
            returnApply.setReceiveTime(new Date());
            returnApply.setReceiveMan(statusParam.getReceiveMan());
            returnApply.setReceiveNote(statusParam.getReceiveNote());
        }else if(status.equals(3)){
            //拒绝退货
            returnApply.setId(id);
            returnApply.setStatus(3);
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        }else{
            return 0;
        }
        return returnApplyMapper.updateById(returnApply);
    }

    @Override
    public OmsOrderReturnApplyResult getItem(Long id) {
        return returnApplyDao.getDetail(id);
    }
}
