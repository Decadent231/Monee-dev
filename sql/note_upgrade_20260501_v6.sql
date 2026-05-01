CREATE TABLE IF NOT EXISTS wiki_page_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    page_id BIGINT NOT NULL COMMENT '知识库页面ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_wiki_page_file (page_id, file_id),
    INDEX idx_wpf_page (page_id),
    INDEX idx_wpf_file (file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
