package com.money.cloud.monee.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.monee.service.StatisticsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsQueryService statisticsService;

    @GetMapping("/monthly")
    public ApiResponse<Map<String, Object>> getMonthlyStatistics(@RequestParam(required = false) String month) {
        return ApiResponse.success(statisticsService.getMonthlyStatistics(month));
    }

    @GetMapping("/category")
    public ApiResponse<List<Map<String, Object>>> getCategoryStatistics(@RequestParam(required = false) String month,
                                                                        @RequestParam(required = false) String type) {
        return ApiResponse.success(statisticsService.getCategoryStatistics(month, type));
    }

    @GetMapping("/trend")
    public ApiResponse<Map<String, Object>> getTrendStatistics(@RequestParam(required = false) String month) {
        return ApiResponse.success(statisticsService.getTrendStatistics(month));
    }

    @GetMapping("/yearly")
    public ApiResponse<Map<String, Object>> getYearlyStatistics(@RequestParam(required = false) Integer year) {
        return ApiResponse.success(statisticsService.getYearlyStatistics(year));
    }

    @GetMapping("/category/yearly")
    public ApiResponse<List<Map<String, Object>>> getYearlyCategoryStatistics(@RequestParam(required = false) Integer year,
                                                                              @RequestParam(required = false) String type) {
        return ApiResponse.success(statisticsService.getYearlyCategoryStatistics(year, type));
    }

    @GetMapping("/trend/yearly")
    public ApiResponse<Map<String, Object>> getYearlyTrendStatistics(@RequestParam(required = false) Integer year) {
        return ApiResponse.success(statisticsService.getYearlyTrendStatistics(year));
    }
}
