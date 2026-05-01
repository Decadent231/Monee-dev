CREATE TABLE IF NOT EXISTS note_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL COMMENT '笔记ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_note_file (note_id, file_id),
    INDEX idx_nf_note (note_id),
    INDEX idx_nf_file (file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
