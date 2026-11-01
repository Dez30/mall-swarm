package com.macro.mall.portal.dao;

import org.apache.ibatis.annotations.Param;

/**
 * 会员信息自定义Dao
 */
public interface UmsMemberDao {

    /**
     * 原子扣减会员积分，仅当积分充足时执行
     * @param memberId    会员id
     * @param integration 扣减的积分数
     * @return 影响行数，0表示积分不足
     */
    int deductIntegration(@Param("memberId") Long memberId, @Param("integration") Integer integration);

    /**
     * 增加会员积分（取消订单返还等场景）
     * @param memberId    会员id
     * @param integration 增加的积分数
     * @return 影响行数
     */
    int addIntegration(@Param("memberId") Long memberId, @Param("integration") Integer integration);
}
