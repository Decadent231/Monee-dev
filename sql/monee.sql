CREATE DATABASE IF NOT EXISTS money_cloud_monee DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE money_cloud_monee;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    user_id BIGINT NULL COMMENT '用户ID，NULL表示默认分类',
    type VARCHAR(20) NOT NULL COMMENT '收支类型',
    icon VARCHAR(50) NULL COMMENT '图标',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) NULL COMMENT '分类描述',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_type_sort (user_id, type, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收支分类表';

CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预算ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    month VARCHAR(7) NOT NULL COMMENT '月份，格式yyyy-MM',
    amount DECIMAL(12, 2) NOT NULL COMMENT '预算金额',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_month (user_id, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='月预算表';

CREATE TABLE IF NOT EXISTS records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    date DATE NOT NULL COMMENT '记账日期',
    type VARCHAR(20) NOT NULL COMMENT '收支类型',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    amount DECIMAL(12, 2) NOT NULL COMMENT '金额',
    remark VARCHAR(500) NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_date (user_id, date),
    INDEX idx_user_type (user_id, type),
    INDEX idx_user_category (user_id, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='记账记录表';

INSERT INTO categories (user_id, type, icon, name, description, sort) VALUES
(NULL, 'expense', 'food', '餐饮', '日常吃喝消费', 1),
(NULL, 'expense', 'traffic', '交通', '通勤与出行支出', 2),
(NULL, 'expense', 'shopping', '购物', '日常购物消费', 3),
(NULL, 'income', 'salary', '工资', '工资收入', 1),
(NULL, 'income', 'bonus', '奖金', '奖金补贴收入', 2);
