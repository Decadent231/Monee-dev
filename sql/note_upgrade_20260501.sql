USE money_cloud_note;

ALTER TABLE note
    ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已删除(回收站)' AFTER updated_at,
    ADD COLUMN deleted_at DATETIME DEFAULT NULL COMMENT '移入回收站时间' AFTER deleted;

CREATE INDEX idx_note_user_deleted ON note (user_id, deleted);

CREATE TABLE IF NOT EXISTS activity_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    module VARCHAR(32) NOT NULL COMMENT '模块：note/vault/todo',
    action VARCHAR(32) NOT NULL COMMENT '动作：create/update/delete/restore/export',
    target_id BIGINT DEFAULT NULL COMMENT '目标记录ID',
    detail VARCHAR(500) DEFAULT NULL COMMENT '补充说明',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_activity_user_time (user_id, created_at),
    INDEX idx_activity_module (user_id, module)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户操作活动日志表';
