package com.money.cloud.note.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.entity.FileAsset;
import com.money.cloud.note.entity.WikiPage;
import com.money.cloud.note.entity.WikiSpace;
import com.money.cloud.note.service.WikiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wiki")
@RequiredArgsConstructor
public class WikiController {

    private final WikiService wikiService;

    // ---- Space ----

    @PostMapping("/spaces")
    public ApiResponse<WikiSpace> createSpace(@Valid @RequestBody WikiSpace space) {
        return ApiResponse.success(wikiService.createSpace(space));
    }

    @PutMapping("/spaces/{id}")
    public ApiResponse<WikiSpace> updateSpace(@PathVariable Long id, @RequestBody WikiSpace space) {
        return ApiResponse.success(wikiService.updateSpace(id, space));
    }

    @DeleteMapping("/spaces/{id}")
    public ApiResponse<Void> deleteSpace(@PathVariable Long id) {
        wikiService.deleteSpace(id);
        return ApiResponse.success();
    }

    @GetMapping("/spaces")
    public ApiResponse<List<WikiSpace>> listSpaces() {
        return ApiResponse.success(wikiService.listSpaces());
    }

    @GetMapping("/spaces/{id}")
    public ApiResponse<WikiSpace> getSpaceById(@PathVariable Long id) {
        return ApiResponse.success(wikiService.getSpaceById(id));
    }

    // ---- Page ----

    @PostMapping("/pages")
    public ApiResponse<WikiPage> createPage(@Valid @RequestBody WikiPage page) {
        return ApiResponse.success(wikiService.createPage(page));
    }

    @PutMapping("/pages/{id}")
    public ApiResponse<WikiPage> updatePage(@PathVariable Long id, @RequestBody WikiPage page) {
        return ApiResponse.success(wikiService.updatePage(id, page));
    }

    @DeleteMapping("/pages/{id}")
    public ApiResponse<Void> deletePage(@PathVariable Long id) {
        wikiService.deletePage(id);
        return ApiResponse.success();
    }

    @GetMapping("/pages/{id}")
    public ApiResponse<WikiPage> getPageById(@PathVariable Long id) {
        return ApiResponse.success(wikiService.getPageById(id));
    }

    @GetMapping("/spaces/{spaceId}/pages")
    public ApiResponse<List<WikiPage>> listPagesBySpace(@PathVariable Long spaceId) {
        return ApiResponse.success(wikiService.listPagesBySpace(spaceId));
    }

    @GetMapping("/spaces/{spaceId}/tree")
    public ApiResponse<List<Map<String, Object>>> getPageTree(@PathVariable Long spaceId) {
        return ApiResponse.success(wikiService.getPageTree(spaceId));
    }

    // ---- Page-File Association ----

    @PostMapping("/pages/{id}/files")
    public ApiResponse<Void> linkFiles(@PathVariable Long id, @RequestBody List<Long> fileIds) {
        wikiService.linkFiles(id, fileIds);
        return ApiResponse.success();
    }

    @DeleteMapping("/pages/{id}/files/{fileId}")
    public ApiResponse<Void> unlinkFile(@PathVariable Long id, @PathVariable Long fileId) {
        wikiService.unlinkFile(id, fileId);
        return ApiResponse.success();
    }

    @GetMapping("/pages/{id}/files")
    public ApiResponse<List<FileAsset>> listLinkedFiles(@PathVariable Long id) {
        return ApiResponse.success(wikiService.listLinkedFiles(id));
    }
}
