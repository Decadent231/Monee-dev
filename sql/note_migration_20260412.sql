SET @stmt = IF (
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='money_cloud_note' AND TABLE_NAME='note' AND COLUMN_NAME='category'),
  'SELECT 1',
  'ALTER TABLE money_cloud_note.note ADD COLUMN category VARCHAR(64) DEFAULT NULL COMMENT ''分类'' AFTER title'
);
PREPARE s FROM @stmt; EXECUTE s; DEALLOCATE PREPARE s;
SET @stmt = IF (
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='money_cloud_note' AND TABLE_NAME='note' AND COLUMN_NAME='tags'),
  'SELECT 1',
  'ALTER TABLE money_cloud_note.note ADD COLUMN tags VARCHAR(255) DEFAULT NULL COMMENT ''标签，逗号分隔'' AFTER category'
);
PREPARE s FROM @stmt; EXECUTE s; DEALLOCATE PREPARE s;
SET @stmt = IF (
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='money_cloud_note' AND TABLE_NAME='note' AND COLUMN_NAME='summary'),
  'SELECT 1',
  'ALTER TABLE money_cloud_note.note ADD COLUMN summary VARCHAR(500) DEFAULT NULL COMMENT ''摘要'' AFTER tags'
);
PREPARE s FROM @stmt; EXECUTE s; DEALLOCATE PREPARE s;
ALTER TABLE money_cloud_note.note MODIFY COLUMN content LONGTEXT NOT NULL COMMENT '富文本内容';
SET @stmt = IF (
  EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA='money_cloud_note' AND TABLE_NAME='note' AND INDEX_NAME='idx_user_category'),
  'SELECT 1',
  'ALTER TABLE money_cloud_note.note ADD INDEX idx_user_category (user_id, category)'
);
PREPARE s FROM @stmt; EXECUTE s; DEALLOCATE PREPARE s;
CREATE TABLE IF NOT EXISTS money_cloud_note.vault_item (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='账号密码保险箱';
CREATE TABLE IF NOT EXISTS money_cloud_note.todo_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '待办ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  title VARCHAR(100) NOT NULL COMMENT '标题',
  description VARCHAR(1000) DEFAULT NULL COMMENT '描述',
  status VARCHAR(20) NOT NULL DEFAULT 'todo' COMMENT '状态：todo/doing/done',
  priority VARCHAR(20) NOT NULL DEFAULT 'medium' COMMENT '优先级：low/medium/high',
  due_date DATE DEFAULT NULL COMMENT '截止日期',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_todo_user_status (user_id, status),
  INDEX idx_todo_user_due (user_id, due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='待办事项表';
