package com.money.cloud.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TodoStatusRequest {
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "todo|doing|done", message = "状态不正确")
    private String status;
}
