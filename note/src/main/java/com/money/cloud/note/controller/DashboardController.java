package com.money.cloud.note.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.success(dashboardService.overview());
    }

    @GetMapping("/weekly-report")
    public ApiResponse<Map<String, Object>> weeklyReport() {
        return ApiResponse.success(dashboardService.weeklyReport());
    }

    @GetMapping("/knowledge-growth")
    public ApiResponse<Map<String, Long>> knowledgeGrowth(@RequestParam(defaultValue = "30") int days) {
        return ApiResponse.success(dashboardService.knowledgeGrowth(days));
    }

    @GetMapping("/activity-heatmap")
    public ApiResponse<Map<String, Long>> activityHeatmap(@RequestParam(defaultValue = "90") int days) {
        return ApiResponse.success(dashboardService.activityHeatmap(days));
    }

    @GetMapping("/top-todos")
    public ApiResponse<List<Map<String, Object>>> topTodos(@RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(dashboardService.topTodos(limit));
    }
}
