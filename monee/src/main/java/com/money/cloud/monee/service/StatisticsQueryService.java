package com.money.cloud.monee.service;

import com.money.cloud.common.context.UserContext;
import com.money.cloud.monee.entity.Category;
import com.money.cloud.monee.entity.Record;
import com.money.cloud.monee.mapper.CategoryMapper;
import com.money.cloud.monee.mapper.RecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsQueryService {

    private final RecordMapper recordMapper;
    private final CategoryMapper categoryMapper;

    public Map<String, Object> getMonthlyStatistics(String month) {
        String targetMonth = month == null || month.isBlank() ? currentMonth() : month;
        YearMonth yearMonth = YearMonth.parse(targetMonth);
        Long userId = UserContext.requireUserId();

        BigDecimal expense = safe(recordMapper.sumByUserIdAndTypeAndYearMonth(userId, "expense",
                yearMonth.getYear(), yearMonth.getMonthValue()));
        BigDecimal income = safe(recordMapper.sumByUserIdAndTypeAndYearMonth(userId, "income",
                yearMonth.getYear(), yearMonth.getMonthValue()));

        Map<String, Object> result = new HashMap<>();
        result.put("expense", expense);
        result.put("income", income);
        result.put("balance", income.subtract(expense));
        return result;
    }

    public List<Map<String, Object>> getCategoryStatistics(String month, String type) {
        String targetMonth = month == null || month.isBlank() ? currentMonth() : month;
        YearMonth yearMonth = YearMonth.parse(targetMonth);
        List<Record> records = recordMapper.selectByUserIdAndYearMonth(UserContext.requireUserId(),
                yearMonth.getYear(), yearMonth.getMonthValue());
        return buildCategoryStatistics(records, type);
    }

    public Map<String, Object> getTrendStatistics(String month) {
        String targetMonth = month == null || month.isBlank() ? currentMonth() : month;
        YearMonth yearMonth = YearMonth.parse(targetMonth);
        List<Record> records = recordMapper.selectByUserIdAndYearMonth(UserContext.requireUserId(),
                yearMonth.getYear(), yearMonth.getMonthValue());

        Map<LocalDate, BigDecimal> expenseByDate = new TreeMap<>();
        Map<LocalDate, BigDecimal> incomeByDate = new TreeMap<>();
        for (Record record : records) {
            if ("expense".equals(record.getType())) {
                expenseByDate.merge(record.getDate(), record.getAmount(), BigDecimal::add);
            } else {
                incomeByDate.merge(record.getDate(), record.getAmount(), BigDecimal::add);
            }
        }
        Set<LocalDate> allDates = new TreeSet<>();
        allDates.addAll(expenseByDate.keySet());
        allDates.addAll(incomeByDate.keySet());

        List<String> dates = new ArrayList<>();
        List<BigDecimal> expenses = new ArrayList<>();
        List<BigDecimal> incomes = new ArrayList<>();
        for (LocalDate date : allDates) {
            dates.add(date.toString());
            expenses.add(expenseByDate.getOrDefault(date, BigDecimal.ZERO));
            incomes.add(incomeByDate.getOrDefault(date, BigDecimal.ZERO));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("expenses", expenses);
        result.put("incomes", incomes);
        result.put("unit", "day");
        return result;
    }

    public Map<String, Object> getYearlyStatistics(Integer year) {
        int targetYear = year != null ? year : Year.now().getValue();
        List<Record> records = recordMapper.selectByUserIdAndYear(UserContext.requireUserId(), targetYear);
        Map<Long, String> categoryNames = getCategoryNameMap();
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        List<BigDecimal> monthlyIncome = zeroMonthList();
        List<BigDecimal> monthlyExpense = zeroMonthList();
        Set<LocalDate> activeDays = new HashSet<>();
        Map<Long, BigDecimal> expenseCategoryTotals = new HashMap<>();

        for (Record record : records) {
            int monthIndex = record.getDate().getMonthValue() - 1;
            activeDays.add(record.getDate());
            if ("income".equals(record.getType())) {
                income = income.add(record.getAmount());
                monthlyIncome.set(monthIndex, monthlyIncome.get(monthIndex).add(record.getAmount()));
            } else {
                expense = expense.add(record.getAmount());
                monthlyExpense.set(monthIndex, monthlyExpense.get(monthIndex).add(record.getAmount()));
                expenseCategoryTotals.merge(record.getCategoryId(), record.getAmount(), BigDecimal::add);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("year", targetYear);
        result.put("income", income);
        result.put("expense", expense);
        result.put("balance", income.subtract(expense));
        result.put("activeDays", activeDays.size());
        result.put("monthlyIncome", monthlyIncome);
        result.put("monthlyExpense", monthlyExpense);
        result.put("topExpenseCategory", buildTopExpenseCategory(expenseCategoryTotals, categoryNames));
        return result;
    }

    public List<Map<String, Object>> getYearlyCategoryStatistics(Integer year, String type) {
        int targetYear = year != null ? year : Year.now().getValue();
        return buildCategoryStatistics(recordMapper.selectByUserIdAndYear(UserContext.requireUserId(), targetYear), type);
    }

    public Map<String, Object> getYearlyTrendStatistics(Integer year) {
        int targetYear = year != null ? year : Year.now().getValue();
        List<Record> records = recordMapper.selectByUserIdAndYear(UserContext.requireUserId(), targetYear);
        List<String> labels = new ArrayList<>();
        List<BigDecimal> expenses = zeroMonthList();
        List<BigDecimal> incomes = zeroMonthList();
        for (int i = 1; i <= 12; i++) {
            labels.add(i + "月");
        }
        for (Record record : records) {
            int monthIndex = record.getDate().getMonthValue() - 1;
            if ("expense".equals(record.getType())) {
                expenses.set(monthIndex, expenses.get(monthIndex).add(record.getAmount()));
            } else {
                incomes.set(monthIndex, incomes.get(monthIndex).add(record.getAmount()));
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("expenses", expenses);
        result.put("incomes", incomes);
        result.put("unit", "month");
        return result;
    }

    private List<Map<String, Object>> buildCategoryStatistics(List<Record> records, String type) {
        Map<Long, String> categoryNames = getCategoryNameMap();
        Map<Long, BigDecimal> categoryAmounts = new HashMap<>();
        Map<Long, Integer> categoryCounts = new HashMap<>();
        for (Record record : records) {
            if (type == null || type.isBlank() || type.equals(record.getType())) {
                categoryAmounts.merge(record.getCategoryId(), record.getAmount(), BigDecimal::add);
                categoryCounts.merge(record.getCategoryId(), 1, Integer::sum);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : categoryAmounts.entrySet()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("categoryId", entry.getKey());
            stat.put("categoryName", categoryNames.getOrDefault(entry.getKey(), "Unknown"));
            stat.put("amount", entry.getValue());
            stat.put("count", categoryCounts.get(entry.getKey()));
            result.add(stat);
        }
        result.sort((left, right) -> ((BigDecimal) right.get("amount")).compareTo((BigDecimal) left.get("amount")));
        return result;
    }

    private Map<Long, String> getCategoryNameMap() {
        return categoryMapper.selectByUserIdOrDefault(UserContext.requireUserId()).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    private List<BigDecimal> zeroMonthList() {
        List<BigDecimal> values = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            values.add(BigDecimal.ZERO);
        }
        return values;
    }

    private Map<String, Object> buildTopExpenseCategory(Map<Long, BigDecimal> expenseCategoryTotals,
                                                        Map<Long, String> categoryNames) {
        Map<String, Object> result = new HashMap<>();
        if (expenseCategoryTotals.isEmpty()) {
            result.put("categoryId", null);
            result.put("categoryName", "No data");
            result.put("amount", BigDecimal.ZERO);
            return result;
        }
        Map.Entry<Long, BigDecimal> maxEntry = expenseCategoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        if (maxEntry == null) {
            result.put("categoryId", null);
            result.put("categoryName", "No data");
            result.put("amount", BigDecimal.ZERO);
            return result;
        }
        result.put("categoryId", maxEntry.getKey());
        result.put("categoryName", categoryNames.getOrDefault(maxEntry.getKey(), "Unknown"));
        result.put("amount", maxEntry.getValue());
        return result;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String currentMonth() {
        LocalDate now = LocalDate.now();
        return String.format("%04d-%02d", now.getYear(), now.getMonthValue());
    }
}
