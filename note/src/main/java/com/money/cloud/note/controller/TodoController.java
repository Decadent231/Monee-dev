package com.money.cloud.note.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.dto.TodoItemRequest;
import com.money.cloud.note.dto.TodoStatusRequest;
import com.money.cloud.note.entity.TodoItem;
import com.money.cloud.note.service.TodoService;
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

import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ApiResponse<List<TodoItem>> list(@RequestParam(required = false) String status,
                                            @RequestParam(required = false) String priority) {
        return ApiResponse.success(todoService.list(status, priority));
    }

    @GetMapping("/{id}")
    public ApiResponse<TodoItem> getById(@PathVariable Long id) {
        return ApiResponse.success(todoService.getById(id));
    }

    @PostMapping
    public ApiResponse<TodoItem> create(@Valid @RequestBody TodoItemRequest request) {
        return ApiResponse.success(todoService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TodoItem> update(@PathVariable Long id, @Valid @RequestBody TodoItemRequest request) {
        return ApiResponse.success(todoService.update(id, request));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<TodoItem> updateStatus(@PathVariable Long id, @Valid @RequestBody TodoStatusRequest request) {
        return ApiResponse.success(todoService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ApiResponse.success();
    }
}
