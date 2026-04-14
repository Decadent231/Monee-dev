package com.money.cloud.note.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("todo_item")
public class TodoItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    private String title;

    private String description;

    private String status;

    private String priority;

    @TableField("due_date")
    private LocalDate dueDate;

    @TableField("reminder_enabled")
    private Boolean reminderEnabled;

    @TableField("reminder_at")
    private LocalDateTime reminderAt;

    @TableField("reminder_email")
    private String reminderEmail;

    @TableField("reminder_sent")
    private Boolean reminderSent;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
