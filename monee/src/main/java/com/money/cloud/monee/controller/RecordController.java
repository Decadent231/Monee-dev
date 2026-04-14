package com.money.cloud.monee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.monee.dto.RecordRequest;
import com.money.cloud.monee.entity.Record;
import com.money.cloud.monee.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getRecords(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(required = false) Long categoryId,
                                                       @RequestParam(required = false) String keyword) {
        IPage<Record> recordPage = recordService.getRecords(page, size, startDate, endDate, type, categoryId, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("records", recordPage.getRecords());
        result.put("total", recordPage.getTotal());
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Record> getRecordById(@PathVariable Long id) {
        return recordService.getRecordById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "记录不存在"));
    }

    @PostMapping
    public ApiResponse<Record> createRecord(@Valid @RequestBody RecordRequest request) {
        return ApiResponse.success(recordService.createRecord(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Record> updateRecord(@PathVariable Long id, @Valid @RequestBody RecordRequest request) {
        return recordService.updateRecord(id, request)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "记录不存在"));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRecord(@PathVariable Long id) {
        return recordService.deleteRecord(id) ? ApiResponse.success() : ApiResponse.error(404, "记录不存在");
    }
}
