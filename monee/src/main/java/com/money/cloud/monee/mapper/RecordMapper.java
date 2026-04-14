package com.money.cloud.monee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.money.cloud.monee.entity.Record;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RecordMapper extends BaseMapper<Record> {
    IPage<Record> selectByFilters(Page<Record> page,
                                  @Param("userId") Long userId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  @Param("type") String type,
                                  @Param("categoryId") Long categoryId,
                                  @Param("keyword") String keyword);

    BigDecimal sumByUserIdAndTypeAndYearMonth(@Param("userId") Long userId,
                                              @Param("type") String type,
                                              @Param("year") int year,
                                              @Param("month") int month);

    List<Record> selectByUserIdAndYearMonth(@Param("userId") Long userId,
                                            @Param("year") int year,
                                            @Param("month") int month);

    List<Record> selectByUserIdAndYear(@Param("userId") Long userId,
                                       @Param("year") int year);

    List<Record> selectByUserIdOrderByDateDesc(@Param("userId") Long userId);
}
