package com.money.cloud.note.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.note.entity.FileAsset;
import com.money.cloud.note.entity.Note;
import com.money.cloud.note.entity.WikiPage;
import com.money.cloud.note.service.FileAssetService;
import com.money.cloud.note.service.NoteService;
import com.money.cloud.note.service.WikiService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileAssetController {

    private final FileAssetService fileAssetService;
    private final NoteService noteService;
    private final WikiService wikiService;

    @PostMapping("/upload")
    public ApiResponse<FileAsset> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(required = false) String folder,
                                         @RequestParam(required = false) String remark) {
        return ApiResponse.success(fileAssetService.upload(file, folder, remark));
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "20") Integer size,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String folder,
                                                  @RequestParam(required = false) String linkedTitle) {
        IPage<FileAsset> pageData = fileAssetService.page(current, size, keyword, folder, linkedTitle);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageData.getRecords());
        result.put("total", pageData.getTotal());
        result.put("current", pageData.getCurrent());
        result.put("size", pageData.getSize());
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<FileAsset> getById(@PathVariable Long id) {
        return ApiResponse.success(fileAssetService.getById(id));
    }

    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        FileAsset asset = fileAssetService.getById(id);
        Path filePath = fileAssetService.getFilePath(id);
        if (!Files.exists(filePath)) {
            response.setStatus(404);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"message\":\"文件不存在\"}");
            return;
        }
        response.setContentType(asset.getMimeType() != null ? asset.getMimeType() : "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + 
                java.net.URLEncoder.encode(asset.getOriginalName(), "UTF-8") + "\"");
        response.setContentLengthLong(asset.getFileSize());
        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            StreamUtils.copy(fis, response.getOutputStream());
        }
    }

    @GetMapping("/{id}/preview")
    public void preview(@PathVariable Long id, HttpServletResponse response) throws IOException {
        FileAsset asset = fileAssetService.getById(id);
        Path filePath = fileAssetService.getFilePath(id);
        if (!Files.exists(filePath)) {
            response.setStatus(404);
            return;
        }
        String mime = asset.getMimeType() != null ? asset.getMimeType() : "application/octet-stream";
        if (!isPreviewable(mime)) {
            response.setStatus(400);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":400,\"message\":\"该文件类型不支持预览\"}");
            return;
        }
        response.setContentType(mime);
        response.setContentLengthLong(asset.getFileSize());
        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            StreamUtils.copy(fis, response.getOutputStream());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<FileAsset> update(@PathVariable Long id,
                                          @RequestParam(required = false) String remark,
                                          @RequestParam(required = false) String folder) {
        return ApiResponse.success(fileAssetService.updateRemark(id, remark, folder));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        fileAssetService.delete(id);
        return ApiResponse.success();
    }

    @GetMapping("/folders")
    public ApiResponse<List<String>> listFolders() {
        return ApiResponse.success(fileAssetService.listFolders());
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.success(fileAssetService.getStorageStats());
    }

    @GetMapping("/{id}/notes")
    public ApiResponse<List<Note>> listLinkedNotes(@PathVariable Long id) {
        return ApiResponse.success(noteService.listByFileId(id));
    }

    @GetMapping("/{id}/wiki-pages")
    public ApiResponse<List<WikiPage>> listLinkedWikiPages(@PathVariable Long id) {
        return ApiResponse.success(wikiService.listByFileId(id));
    }

    private boolean isPreviewable(String mime) {
        return mime.startsWith("image/") || mime.equals("application/pdf") || mime.startsWith("text/");
    }
}
