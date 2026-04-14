package com.money.cloud.monee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.money.cloud.monee.entity.Budget;
import org.apache.ibatis.annotations.Param;

public interface BudgetMapper extends BaseMapper<Budget> {
    Budget selectByUserIdAndMonth(@Param("userId") Long userId, @Param("month") String month);
}
