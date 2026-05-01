package com.money.cloud.note.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.entity.ActivityLog;
import com.money.cloud.note.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ApiResponse<List<ActivityLog>> list(@RequestParam(required = false) String module,
                                               @RequestParam(defaultValue = "30") int limit) {
        return ApiResponse.success(activityLogService.listByModule(module, limit));
    }

    @GetMapping("/recent")
    public ApiResponse<List<ActivityLog>> recent(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(activityLogService.listRecent(limit));
    }

    @GetMapping("/stats/modules")
    public ApiResponse<Map<String, Long>> countByModule() {
        return ApiResponse.success(activityLogService.countByModule());
    }

    @GetMapping("/stats/daily")
    public ApiResponse<Map<String, Long>> countByDay(@RequestParam(defaultValue = "14") int days) {
        return ApiResponse.success(activityLogService.countByDay(days));
    }
}
