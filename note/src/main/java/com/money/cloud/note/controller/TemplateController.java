package com.money.cloud.note.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.entity.NoteTemplate;
import com.money.cloud.note.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public ApiResponse<List<NoteTemplate>> list() {
        return ApiResponse.success(templateService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<NoteTemplate> getById(@PathVariable Long id) {
        return ApiResponse.success(templateService.getById(id));
    }

    @PostMapping
    public ApiResponse<NoteTemplate> create(@Valid @RequestBody NoteTemplate template) {
        return ApiResponse.success(templateService.create(template));
    }

    @PutMapping("/{id}")
    public ApiResponse<NoteTemplate> update(@PathVariable Long id, @Valid @RequestBody NoteTemplate template) {
        return ApiResponse.success(templateService.update(id, template));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return ApiResponse.success();
    }
}
