package com.money.cloud.monee.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.monee.dto.BudgetRequest;
import com.money.cloud.monee.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getBudget(@RequestParam(required = false) String month) {
        return ApiResponse.success(budgetService.getBudgetInfo(month));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> setBudget(@Valid @RequestBody BudgetRequest request) {
        var budget = budgetService.setBudget(request);
        Map<String, Object> result = new HashMap<>();
        result.put("budget", budget.getAmount());
        result.put("month", budget.getMonth());
        return ApiResponse.success(result);
    }

    @GetMapping("/daily-available")
    public ApiResponse<Map<String, Object>> getDailyAvailable(@RequestParam(required = false) String month) {
        return ApiResponse.success(budgetService.getDailyAvailable(month));
    }
}
