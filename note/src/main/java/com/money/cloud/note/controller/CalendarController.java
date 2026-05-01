package com.money.cloud.note.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.entity.CalendarEvent;
import com.money.cloud.note.service.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ApiResponse<CalendarEvent> create(@Valid @RequestBody CalendarEvent event) {
        return ApiResponse.success(calendarService.create(event));
    }

    @PutMapping("/{id}")
    public ApiResponse<CalendarEvent> update(@PathVariable Long id, @RequestBody CalendarEvent event) {
        return ApiResponse.success(calendarService.update(id, event));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        calendarService.delete(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    public ApiResponse<CalendarEvent> getById(@PathVariable Long id) {
        return ApiResponse.success(calendarService.getById(id));
    }

    @GetMapping
    public ApiResponse<List<CalendarEvent>> listByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.success(calendarService.listByMonth(year, month));
    }

    @GetMapping("/range")
    public ApiResponse<List<CalendarEvent>> listByRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ApiResponse.success(calendarService.listByDateRange(startDate, endDate));
    }

    @GetMapping("/today")
    public ApiResponse<List<CalendarEvent>> listToday() {
        return ApiResponse.success(calendarService.listToday());
    }
}
