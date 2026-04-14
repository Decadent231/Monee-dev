package com.money.cloud.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VaultItemRequest {
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @Size(max = 64, message = "分类长度不能超过64")
    private String category;

    @Size(max = 100, message = "账号长度不能超过100")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 255, message = "密码长度不能超过255")
    private String password;

    @Size(max = 255, message = "网址长度不能超过255")
    private String website;

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
}
