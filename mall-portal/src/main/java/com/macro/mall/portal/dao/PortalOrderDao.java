package com.macro.mall.portal.dao;

import com.macro.mall.portal.domain.OmsOrderDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 前台订单自定义Dao
 * Created by macro on 2018/9/4.
 */
public interface PortalOrderDao {
    /**
     * 获取订单及下单商品详情
     */
    OmsOrderDetail getDetail(@Param("orderId") Long orderId);

    /**
     * 原子锁定库存：仅当可用库存(stock - lock_stock) >= 购买数量时锁定成功
     * @param skuId    商品SKU的id
     * @param quantity 购买数量
     * @return 影响行数，0表示库存不足
     */
    int lockSkuStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 支付成功扣减真实库存：stock与lock_stock同时扣减，仅当锁定库存足够时执行
     * @param skuId    商品SKU的id
     * @param quantity 购买数量
     * @return 影响行数，0表示锁定库存不足（状态异常）
     */
    int deductSkuStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 获取超时订单
     * @param minute 超时时间（分）
     */
    List<OmsOrderDetail> getTimeOutOrders(@Param("minute") Integer minute);

    /**
     * 批量修改订单状态
     */
    int updateOrderStatus(@Param("ids") List<Long> ids,@Param("status") Integer status);

    /**
     * 解除取消订单的库存锁定，仅当锁定库存足够时执行，防止减成负数
     * @param skuId    商品SKU的id
     * @param quantity 购买数量
     * @return 影响行数，0表示无锁定库存可释放
     */
    int releaseSkuStockLock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

}
