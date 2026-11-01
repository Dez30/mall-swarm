package com.macro.mall.portal.dao;

import com.macro.mall.model.SmsCoupon;
import com.macro.mall.portal.domain.SmsCouponHistoryDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员优惠券领取历史自定义Dao
 * Created by macro on 2018/8/29.
 */
public interface SmsCouponHistoryDao {
    List<SmsCouponHistoryDetail> getDetailList(@Param("memberId") Long memberId);
    List<SmsCoupon> getCouponList(@Param("memberId") Long memberId, @Param("useStatus")Integer useStatus);

    /**
     * 原子修改优惠券使用状态，仅当当前状态与期望状态一致时才修改
     * @param memberId  会员id
     * @param couponId  优惠券id
     * @param fromStatus 期望的当前状态
     * @param toStatus   要修改成的状态
     * @return 影响行数，0表示状态已被其他事务修改
     */
    int updateUseStatus(@Param("memberId") Long memberId, @Param("couponId") Long couponId,
                        @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);
}
