package com.money.cloud.note.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.dto.VaultItemRequest;
import com.money.cloud.note.dto.VaultItemResponse;
import com.money.cloud.note.service.VaultService;
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
@RequestMapping("/vault-items")
@RequiredArgsConstructor
public class VaultController {

    private final VaultService vaultService;

    @GetMapping
    public ApiResponse<List<VaultItemResponse>> list(@RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) String category) {
        return ApiResponse.success(vaultService.list(keyword, category));
    }

    @GetMapping("/{id}")
    public ApiResponse<VaultItemResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(vaultService.getById(id));
    }

    @PostMapping
    public ApiResponse<VaultItemResponse> create(@Valid @RequestBody VaultItemRequest request) {
        return ApiResponse.success(vaultService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<VaultItemResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody VaultItemRequest request) {
        return ApiResponse.success(vaultService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        vaultService.delete(id);
        return ApiResponse.success();
    }
}
