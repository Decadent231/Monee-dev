package com.money.cloud.note.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.dto.NoteCreateRequest;
import com.money.cloud.note.dto.NoteUpdateRequest;
import com.money.cloud.note.entity.Note;
import com.money.cloud.note.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ApiResponse<Note> create(@Valid @RequestBody NoteCreateRequest request) {
        return ApiResponse.success(noteService.create(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Note> update(@PathVariable Long id, @Valid @RequestBody NoteUpdateRequest request) {
        return ApiResponse.success(noteService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<Note> getById(@PathVariable Long id) {
        return ApiResponse.success(noteService.getById(id));
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer current,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String category,
                                                 @RequestParam(required = false) String tag) {
        IPage<Note> pageData = noteService.page(current, size, keyword, category, tag);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageData.getRecords());
        result.put("total", pageData.getTotal());
        result.put("current", pageData.getCurrent());
        result.put("size", pageData.getSize());
        return ApiResponse.success(result);
    }
}
