USE money_cloud_note;

ALTER TABLE note
    ADD COLUMN content_type VARCHAR(16) NOT NULL DEFAULT 'html' COMMENT '内容类型: html/markdown' AFTER content,
    ADD COLUMN pinned TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶' AFTER deleted_at,
    ADD COLUMN starred TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否星标收藏' AFTER pinned;

CREATE TABLE IF NOT EXISTS note_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    user_id BIGINT NOT NULL COMMENT '所属用户，0表示系统预置',
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    content_type VARCHAR(16) NOT NULL DEFAULT 'html' COMMENT '内容类型: html/markdown',
    content TEXT NOT NULL COMMENT '模板内容',
    is_system TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否系统预置',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_template_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='笔记模板表';

INSERT INTO note_template (user_id, name, content_type, content, is_system) VALUES
(0, '周报', 'markdown', '# 周报\n\n## 本周完成\n\n- \n\n## 未完成事项\n\n- \n\n## 下周计划\n\n- \n\n## 备注\n\n', 1),
(0, '会议纪要', 'markdown', '# 会议纪要\n\n**日期：**\n\n**参会人：**\n\n## 议题\n\n1. \n\n## 结论\n\n- \n\n## 待办事项\n\n- [ ] \n', 1),
(0, '日报', 'markdown', '# 日报\n\n## 今日完成\n\n- \n\n## 明日计划\n\n- \n\n## 遇到的问题\n\n- \n', 1),
(0, '需求分析', 'markdown', '# 需求分析\n\n## 背景\n\n\n\n## 需求描述\n\n\n\n## 技术方案\n\n\n\n## 排期\n\n| 阶段 | 时间 | 说明 |\n|------|------|------|\n|      |      |      |\n', 1),
(0, 'Bug 记录', 'markdown', '# Bug 记录\n\n**严重程度：**\n\n**复现步骤：**\n\n1. \n\n**期望结果：**\n\n\n\n**实际结果：**\n\n\n\n**解决方案：**\n\n', 1),
(0, '读书笔记', 'markdown', '# 读书笔记\n\n**书名：**\n\n**作者：**\n\n## 核心观点\n\n\n\n## 精彩摘录\n\n> \n\n## 个人感悟\n\n', 1);
