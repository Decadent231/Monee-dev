package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.note.entity.FileAsset;
import com.money.cloud.note.mapper.FileAssetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileAssetService {

    private final FileAssetMapper fileAssetMapper;
    private final ActivityLogService activityLogService;

    @Value("${app.file.storage-path:/data/note-files}")
    private String storagePath;

    @Value("${app.file.max-size-mb:50}")
    private int maxSizeMb;

    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            "exe", "bat", "cmd", "sh", "ps1", "vbs", "js", "msi", "dll", "com", "pif", "scr", "hta"
    );

    @Transactional
    public FileAsset upload(MultipartFile file, String folder, String remark) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }
        long maxSize = (long) maxSizeMb * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException(400, "文件大小不能超过 " + maxSizeMb + "MB");
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName)) {
            originalName = "unnamed";
        }

        String extension = getExtension(originalName).toLowerCase();
        if (BLOCKED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "不允许上传此类型的文件");
        }

        Long userId = UserContext.requireUserId();
        String storedName = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.hasText(extension)) {
            storedName = storedName + "." + extension;
        }
        String relativePath = userId + "/" + storedName;

        Path absolutePath = Paths.get(storagePath, relativePath);
        try {
            Files.createDirectories(absolutePath.getParent());
            file.transferTo(absolutePath.toFile());
        } catch (IOException e) {
            throw new BusinessException(500, "文件保存失败: " + e.getMessage());
        }

        FileAsset asset = new FileAsset();
        asset.setUserId(userId);
        asset.setOriginalName(originalName);
        asset.setStoredName(storedName);
        asset.setRelativePath(relativePath);
        asset.setFileSize(file.getSize());
        asset.setMimeType(file.getContentType());
        asset.setExtension(extension);
        asset.setFolder(StringUtils.hasText(folder) ? folder.trim() : null);
        asset.setRemark(StringUtils.hasText(remark) ? remark.trim() : null);
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());
        fileAssetMapper.insert(asset);

        activityLogService.log("file", "create", asset.getId(), originalName);
        return asset;
    }

    public IPage<FileAsset> page(int current, int size, String keyword, String folder) {
        LambdaQueryWrapper<FileAsset> wrapper = new LambdaQueryWrapper<FileAsset>()
                .eq(FileAsset::getUserId, UserContext.requireUserId())
                .orderByDesc(FileAsset::getCreatedAt);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(q -> q.like(FileAsset::getOriginalName, keyword)
                    .or().like(FileAsset::getRemark, keyword));
        }
        if (StringUtils.hasText(folder)) {
            wrapper.eq(FileAsset::getFolder, folder);
        }
        return fileAssetMapper.selectPage(new Page<>(current, size), wrapper);
    }

    public FileAsset getById(Long id) {
        return getOwnedAsset(id);
    }

    public Path getFilePath(Long id) {
        FileAsset asset = getOwnedAsset(id);
        return Paths.get(storagePath, asset.getRelativePath());
    }

    public List<String> listFolders() {
        Long userId = UserContext.requireUserId();
        List<FileAsset> all = fileAssetMapper.selectList(new LambdaQueryWrapper<FileAsset>()
                .eq(FileAsset::getUserId, userId)
                .isNotNull(FileAsset::getFolder)
                .select(FileAsset::getFolder)
                .groupBy(FileAsset::getFolder));
        List<String> folders = new ArrayList<>();
        for (FileAsset a : all) {
            if (a.getFolder() != null) {
                folders.add(a.getFolder());
            }
        }
        return folders;
    }

    public Map<String, Object> getStorageStats() {
        Long userId = UserContext.requireUserId();
        List<FileAsset> all = fileAssetMapper.selectList(new LambdaQueryWrapper<FileAsset>()
                .eq(FileAsset::getUserId, userId));
        long totalSize = all.stream().mapToLong(f -> f.getFileSize() != null ? f.getFileSize() : 0).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("fileCount", all.size());
        result.put("totalSize", totalSize);
        return result;
    }

    @Transactional
    public FileAsset updateRemark(Long id, String remark, String folder) {
        FileAsset asset = getOwnedAsset(id);
        if (remark != null) asset.setRemark(remark.trim());
        if (folder != null) asset.setFolder(folder.trim().isEmpty() ? null : folder.trim());
        asset.setUpdatedAt(LocalDateTime.now());
        fileAssetMapper.updateById(asset);
        return asset;
    }

    @Transactional
    public void delete(Long id) {
        FileAsset asset = getOwnedAsset(id);
        Path filePath = Paths.get(storagePath, asset.getRelativePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // ignore file delete error, still remove db record
        }
        fileAssetMapper.deleteById(id);
        activityLogService.log("file", "delete", id, asset.getOriginalName());
    }

    public long countByUser() {
        return fileAssetMapper.selectCount(new LambdaQueryWrapper<FileAsset>()
                .eq(FileAsset::getUserId, UserContext.requireUserId()));
    }

    private FileAsset getOwnedAsset(Long id) {
        FileAsset asset = fileAssetMapper.selectById(id);
        if (asset == null || !asset.getUserId().equals(UserContext.requireUserId())) {
            throw new BusinessException(404, "文件不存在");
        }
        return asset;
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename)) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "";
    }
}
