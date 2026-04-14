package com.money.cloud.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称长度不能超过32")
    private String nickname;
}
