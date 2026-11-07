package com.macro.mall.dao;

import com.macro.mall.model.PmsSkuStock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义商品SKU管理Dao
 * Created by macro on 2018/4/26.
 */
public interface PmsSkuStockDao {
    /**
     * 批量插入操作
     */
    int insertList(@Param("list")List<PmsSkuStock> skuStockList);

    /**
     * 批量插入或更新操作，更新时不触碰lock_stock和promotion_price，防止后台编辑商品覆盖下单锁定数据
     */
    int upsertList(@Param("list")List<PmsSkuStock> skuStockList);
}
