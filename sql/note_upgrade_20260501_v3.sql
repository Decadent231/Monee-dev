-- note_upgrade_20260501_v3.sql
-- 日历日程模块 + 知识库 Wiki 模块

USE money_cloud_note;

-- 日历事件表
CREATE TABLE IF NOT EXISTS calendar_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    all_day TINYINT NOT NULL DEFAULT 0 COMMENT '是否全天事件 0=否 1=是',
    color VARCHAR(20) DEFAULT NULL COMMENT '颜色标签 hex',
    location VARCHAR(200) DEFAULT NULL,
    reminder_minutes INT DEFAULT NULL COMMENT '提前提醒分钟数',
    todo_item_id BIGINT DEFAULT NULL COMMENT '关联待办ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_event_user_time (user_id, start_time),
    INDEX idx_event_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 知识库空间表
CREATE TABLE IF NOT EXISTS wiki_space (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    icon VARCHAR(20) DEFAULT '📋',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_space_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 知识库页面表
CREATE TABLE IF NOT EXISTS wiki_page (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    space_id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT NULL COMMENT '父页面ID，NULL=顶级',
    title VARCHAR(200) NOT NULL,
    content MEDIUMTEXT,
    content_type VARCHAR(20) NOT NULL DEFAULT 'markdown' COMMENT '内容类型 html/markdown',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '同级排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_page_space (space_id),
    INDEX idx_page_parent (parent_id),
    INDEX idx_page_user_space (user_id, space_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
