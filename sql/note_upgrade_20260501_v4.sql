-- note_upgrade_20260501_v4.sql
-- 文件库模块 + wiki_page content_type 字段

USE money_cloud_note;

-- wiki_page 新增 content_type 字段（如果还未添加）
ALTER TABLE wiki_page ADD COLUMN IF NOT EXISTS content_type VARCHAR(20) NOT NULL DEFAULT 'markdown' COMMENT '内容类型 html/markdown' AFTER content;

-- 文件资产表
CREATE TABLE IF NOT EXISTS file_asset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name VARCHAR(255) NOT NULL COMMENT '存储文件名（UUID）',
    relative_path VARCHAR(500) NOT NULL COMMENT '相对路径（userId/storedName）',
    file_size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
    mime_type VARCHAR(100) DEFAULT NULL COMMENT 'MIME 类型',
    extension VARCHAR(20) DEFAULT NULL COMMENT '文件扩展名',
    folder VARCHAR(100) DEFAULT NULL COMMENT '虚拟文件夹',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_file_user (user_id),
    INDEX idx_file_folder (user_id, folder),
    INDEX idx_file_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
