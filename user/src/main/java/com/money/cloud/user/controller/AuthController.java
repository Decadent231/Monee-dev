package com.money.cloud.user.controller;

import com.money.cloud.common.api.ApiResponse;
import com.money.cloud.user.dto.LoginRequest;
import com.money.cloud.user.dto.LoginResponse;
import com.money.cloud.user.dto.RegisterRequest;
import com.money.cloud.user.dto.SendCodeRequest;
import com.money.cloud.user.dto.ChangePasswordRequest;
import com.money.cloud.user.dto.UserInfoResponse;
import com.money.cloud.user.dto.UserProfileUpdateRequest;
import com.money.cloud.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/send-code")
    public ApiResponse<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        authService.sendRegisterCode(request.getEmail());
        return ApiResponse.success("验证码发送成功", null);
    }

    @PostMapping("/auth/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success("注册成功", null);
    }

    @PostMapping("/auth/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/users/me")
    public ApiResponse<UserInfoResponse> currentUser() {
        return ApiResponse.success(authService.currentUser());
    }

    @PutMapping("/users/profile")
    public ApiResponse<UserInfoResponse> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        return ApiResponse.success(authService.updateProfile(request));
    }

    @PutMapping("/users/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success("密码修改成功", null);
    }
}
