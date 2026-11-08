package com.macro.mall.portal.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.OmsCartItemMapper;
import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.CartProduct;
import com.macro.mall.portal.domain.CartPromotionItem;
import com.macro.mall.portal.service.OmsCartItemService;
import com.macro.mall.portal.service.OmsPromotionService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车管理Service实现类
 * Created by macro on 2018/8/2.
 */
@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {
    @Autowired
    private OmsCartItemMapper cartItemMapper;
    @Autowired
    private PortalProductDao productDao;
    @Autowired
    private OmsPromotionService promotionService;
    @Autowired
    private UmsMemberService memberService;

    @Override
    public int add(OmsCartItem cartItem) {
        if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
            Asserts.fail("商品数量必须大于0");
        }
        UmsMember currentMember = memberService.getCurrentMember();
        cartItem.setMemberId(currentMember.getId());
        cartItem.setMemberNickname(currentMember.getNickname());
        cartItem.setDeleteStatus(0);
        OmsCartItem existCartItem = getCartItem(cartItem);
        if (existCartItem == null) {
            cartItem.setCreateDate(new Date());
            //MP的insert忽略null字段，与原来的insertSelective语义一致
            return cartItemMapper.insert(cartItem);
        } else {
            existCartItem.setQuantity(existCartItem.getQuantity() + cartItem.getQuantity());
            existCartItem.setModifyDate(new Date());
            return cartItemMapper.updateById(existCartItem);
        }
    }

    /**
     * 根据会员id,商品id和规格获取购物车中商品
     */
    private OmsCartItem getCartItem(OmsCartItem cartItem) {
        return cartItemMapper.selectOne(new LambdaQueryWrapper<OmsCartItem>()
                .eq(OmsCartItem::getMemberId, cartItem.getMemberId())
                .eq(OmsCartItem::getProductId, cartItem.getProductId())
                .eq(OmsCartItem::getDeleteStatus, 0)
                .eq(cartItem.getProductSkuId() != null, OmsCartItem::getProductSkuId, cartItem.getProductSkuId())
                .last("LIMIT 1"));
    }

    @Override
    public List<OmsCartItem> list(Long memberId) {
        return cartItemMapper.selectList(new LambdaQueryWrapper<OmsCartItem>()
                .eq(OmsCartItem::getDeleteStatus, 0)
                .eq(OmsCartItem::getMemberId, memberId));
    }

    @Override
    public List<CartPromotionItem> listPromotion(Long memberId, List<Long> cartIds) {
        List<OmsCartItem> cartItemList = list(memberId);
        if(CollUtil.isNotEmpty(cartIds)){
            cartItemList = cartItemList.stream().filter(item->cartIds.contains(item.getId())).collect(Collectors.toList());
        }
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(cartItemList)){
            cartPromotionItemList = promotionService.calcCartPromotion(cartItemList);
        }
        return cartPromotionItemList;
    }

    @Override
    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            Asserts.fail("商品数量必须大于0");
        }
        return cartItemMapper.update(null, new LambdaUpdateWrapper<OmsCartItem>()
                .eq(OmsCartItem::getDeleteStatus, 0)
                .eq(OmsCartItem::getId, id)
                .eq(OmsCartItem::getMemberId, memberId)
                .set(OmsCartItem::getQuantity, quantity));
    }

    @Override
    public int delete(Long memberId, List<Long> ids) {
        return cartItemMapper.update(null, new LambdaUpdateWrapper<OmsCartItem>()
                .in(OmsCartItem::getId, ids)
                .eq(OmsCartItem::getMemberId, memberId)
                .set(OmsCartItem::getDeleteStatus, 1));
    }

    @Override
    public CartProduct getCartProduct(Long productId) {
        return productDao.getCartProduct(productId);
    }

    @Override
    public int updateAttr(OmsCartItem cartItem) {
        //删除原购物车信息
        OmsCartItem updateCart = new OmsCartItem();
        updateCart.setId(cartItem.getId());
        updateCart.setModifyDate(new Date());
        updateCart.setDeleteStatus(1);
        cartItemMapper.updateById(updateCart);
        cartItem.setId(null);
        add(cartItem);
        return 1;
    }

    @Override
    public int clear(Long memberId) {
        return cartItemMapper.update(null, new LambdaUpdateWrapper<OmsCartItem>()
                .eq(OmsCartItem::getMemberId, memberId)
                .set(OmsCartItem::getDeleteStatus, 1));
    }
}
