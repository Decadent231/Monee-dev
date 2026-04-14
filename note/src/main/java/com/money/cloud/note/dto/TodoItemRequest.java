package com.money.cloud.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TodoItemRequest {

    @NotBlank(message = "待办标题不能为空")
    @Size(max = 100, message = "待办标题长度不能超过100")
    private String title;

    @Size(max = 1000, message = "待办描述长度不能超过1000")
    private String description;

    @Pattern(regexp = "todo|doing|done", message = "状态不正确")
    private String status;

    @Pattern(regexp = "low|medium|high", message = "优先级不正确")
    private String priority;

    private LocalDate dueDate;

    private Boolean reminderEnabled;

    private LocalDateTime reminderAt;

    @Size(max = 100, message = "提醒邮箱长度不能超过100")
    private String reminderEmail;
}
