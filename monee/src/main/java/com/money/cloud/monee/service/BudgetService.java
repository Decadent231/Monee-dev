package com.money.cloud.monee.service;

import com.money.cloud.common.context.UserContext;
import com.money.cloud.monee.dto.BudgetRequest;
import com.money.cloud.monee.entity.Budget;
import com.money.cloud.monee.mapper.BudgetMapper;
import com.money.cloud.monee.mapper.RecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetMapper budgetMapper;
    private final RecordMapper recordMapper;

    public Map<String, Object> getBudgetInfo(String month) {
        String targetMonth = normalizeMonth(month);
        Long userId = UserContext.requireUserId();
        Budget budget = budgetMapper.selectByUserIdAndMonth(userId, targetMonth);
        BigDecimal budgetAmount = budget != null ? budget.getAmount() : BigDecimal.ZERO;

        YearMonth yearMonth = YearMonth.parse(targetMonth);
        BigDecimal spent = recordMapper.sumByUserIdAndTypeAndYearMonth(userId, "expense",
                yearMonth.getYear(), yearMonth.getMonthValue());
        spent = spent == null ? BigDecimal.ZERO : spent;

        Map<String, Object> result = new HashMap<>();
        result.put("budget", budgetAmount);
        result.put("spent", spent);
        result.put("remaining", budgetAmount.subtract(spent));
        return result;
    }

    public Map<String, Object> getDailyAvailable(String month) {
        String targetMonth = normalizeMonth(month);
        Map<String, Object> budgetInfo = getBudgetInfo(targetMonth);
        BigDecimal remaining = (BigDecimal) budgetInfo.get("remaining");
        YearMonth yearMonth = YearMonth.parse(targetMonth);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        long daysRemaining = Math.max(ChronoUnit.DAYS.between(LocalDate.now(), lastDay) + 1, 0);
        BigDecimal dailyAvailable = BigDecimal.ZERO;
        if (daysRemaining > 0 && remaining.compareTo(BigDecimal.ZERO) > 0) {
            dailyAvailable = remaining.divide(BigDecimal.valueOf(daysRemaining), 2, RoundingMode.HALF_UP);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("dailyAvailable", dailyAvailable);
        result.put("daysRemaining", daysRemaining);
        return result;
    }

    @Transactional
    public Budget setBudget(BudgetRequest request) {
        String targetMonth = normalizeMonth(request.getMonth());
        Long userId = UserContext.requireUserId();
        Budget budget = budgetMapper.selectByUserIdAndMonth(userId, targetMonth);
        LocalDateTime now = LocalDateTime.now();
        if (budget == null) {
            budget = new Budget();
            budget.setUserId(userId);
            budget.setMonth(targetMonth);
            budget.setAmount(request.getAmount());
            budget.setCreatedAt(now);
            budget.setUpdatedAt(now);
            budgetMapper.insert(budget);
            return budget;
        }
        budget.setAmount(request.getAmount());
        budget.setUpdatedAt(now);
        budgetMapper.updateById(budget);
        return budget;
    }

    private String normalizeMonth(String month) {
        if (month == null || month.isBlank()) {
            LocalDate now = LocalDate.now();
            return String.format("%04d-%02d", now.getYear(), now.getMonthValue());
        }
        return month;
    }
}
