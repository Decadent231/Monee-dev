USE money_cloud_note;

SET @sql = IF(
    EXISTS(
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = 'money_cloud_note' AND TABLE_NAME = 'todo_item' AND COLUMN_NAME = 'reminder_enabled'
    ),
    'SELECT 1',
    'ALTER TABLE todo_item ADD COLUMN reminder_enabled TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''是否开启提醒'' AFTER due_date'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS(
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = 'money_cloud_note' AND TABLE_NAME = 'todo_item' AND COLUMN_NAME = 'reminder_at'
    ),
    'SELECT 1',
    'ALTER TABLE todo_item ADD COLUMN reminder_at DATETIME DEFAULT NULL COMMENT ''提醒时间'' AFTER reminder_enabled'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS(
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = 'money_cloud_note' AND TABLE_NAME = 'todo_item' AND COLUMN_NAME = 'reminder_email'
    ),
    'SELECT 1',
    'ALTER TABLE todo_item ADD COLUMN reminder_email VARCHAR(100) DEFAULT NULL COMMENT ''提醒接收邮箱'' AFTER reminder_at'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS(
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = 'money_cloud_note' AND TABLE_NAME = 'todo_item' AND COLUMN_NAME = 'reminder_sent'
    ),
    'SELECT 1',
    'ALTER TABLE todo_item ADD COLUMN reminder_sent TINYINT(1) NOT NULL DEFAULT 1 COMMENT ''提醒是否已发送'' AFTER reminder_email'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS(
        SELECT 1 FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = 'money_cloud_note' AND TABLE_NAME = 'todo_item' AND INDEX_NAME = 'idx_todo_reminder'
    ),
    'SELECT 1',
    'CREATE INDEX idx_todo_reminder ON todo_item (reminder_enabled, reminder_sent, reminder_at)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
