package com.money.cloud.monee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "^(expense|income)$", message = "类型必须为 expense 或 income")
    private String type;

    @Size(max = 50, message = "图标长度不能超过50")
    private String icon;

    @NotBlank(message = "名称不能为空")
    @Size(max = 50, message = "名称长度不能超过50")
    private String name;

    @Size(max = 200, message = "描述长度不能超过200")
    private String description;

    private Integer sort = 0;
}
