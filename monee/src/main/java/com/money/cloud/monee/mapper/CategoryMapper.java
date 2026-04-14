package com.money.cloud.monee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.money.cloud.monee.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    List<Category> selectByUserIdOrDefault(@Param("userId") Long userId);
    List<Category> selectByTypeAndUserIdOrDefault(@Param("type") String type, @Param("userId") Long userId);
}
