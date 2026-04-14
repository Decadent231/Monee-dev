CREATE DATABASE IF NOT EXISTS money_cloud_note DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE money_cloud_note;

CREATE TABLE IF NOT EXISTS note (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '笔记ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    category VARCHAR(64) DEFAULT NULL COMMENT '分类',
    tags VARCHAR(255) DEFAULT NULL COMMENT '标签，逗号分隔',
    summary VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    content LONGTEXT NOT NULL COMMENT '富文本内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_updated (user_id, updated_at),
    INDEX idx_user_category (user_id, category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户笔记表';

CREATE TABLE IF NOT EXISTS vault_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '保险箱记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    category VARCHAR(64) DEFAULT NULL COMMENT '分类',
    username VARCHAR(100) DEFAULT NULL COMMENT '账号',
    password_encrypted VARCHAR(512) NOT NULL COMMENT '加密密码',
    website VARCHAR(255) DEFAULT NULL COMMENT '网址',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_vault_user_updated (user_id, updated_at),
    INDEX idx_vault_user_category (user_id, category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='账号密码保险箱表';

CREATE TABLE IF NOT EXISTS todo_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '待办ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description VARCHAR(1000) DEFAULT NULL COMMENT '描述',
    status VARCHAR(20) NOT NULL DEFAULT 'todo' COMMENT '状态：todo/doing/done',
    priority VARCHAR(20) NOT NULL DEFAULT 'medium' COMMENT '优先级：low/medium/high',
    due_date DATE DEFAULT NULL COMMENT '截止日期',
    reminder_enabled TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启提醒',
    reminder_at DATETIME DEFAULT NULL COMMENT '提醒时间',
    reminder_email VARCHAR(100) DEFAULT NULL COMMENT '提醒接收邮箱',
    reminder_sent TINYINT(1) NOT NULL DEFAULT 1 COMMENT '提醒是否已发送',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_todo_user_status (user_id, status),
    INDEX idx_todo_user_due (user_id, due_date),
    INDEX idx_todo_reminder (reminder_enabled, reminder_sent, reminder_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='待办事项表';
