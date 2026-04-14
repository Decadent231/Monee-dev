package com.money.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.LoginUser;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.common.util.JwtUtil;
import com.money.cloud.common.util.MailUtil;
import com.money.cloud.user.dto.ChangePasswordRequest;
import com.money.cloud.user.dto.LoginRequest;
import com.money.cloud.user.dto.LoginResponse;
import com.money.cloud.user.dto.RegisterRequest;
import com.money.cloud.user.dto.UserInfoResponse;
import com.money.cloud.user.dto.UserProfileUpdateRequest;
import com.money.cloud.user.entity.SysUser;
import com.money.cloud.user.entity.SysVerifyCode;
import com.money.cloud.user.mapper.SysUserMapper;
import com.money.cloud.user.mapper.SysVerifyCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysVerifyCodeMapper sysVerifyCodeMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailUtil mailUtil;

    @Transactional
    public void sendRegisterCode(String email) {
        if (existsUser(email)) {
            throw new BusinessException("该邮箱已注册");
        }

        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        LocalDateTime now = LocalDateTime.now();
        sysVerifyCodeMapper.delete(new LambdaQueryWrapper<SysVerifyCode>().eq(SysVerifyCode::getEmail, email));

        SysVerifyCode verifyCode = new SysVerifyCode();
        verifyCode.setEmail(email);
        verifyCode.setCode(code);
        verifyCode.setExpireTime(now.plusMinutes(5));
        verifyCode.setCreatedAt(now);
        sysVerifyCodeMapper.insert(verifyCode);

        mailUtil.sendTextMail(email, "Money Cloud 注册验证码", "您的验证码为: " + code + "，5分钟内有效。");
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (existsUser(request.getEmail())) {
            throw new BusinessException("该邮箱已注册");
        }

        SysVerifyCode verifyCode = sysVerifyCodeMapper.selectOne(new LambdaQueryWrapper<SysVerifyCode>()
                .eq(SysVerifyCode::getEmail, request.getEmail())
                .orderByDesc(SysVerifyCode::getCreatedAt)
                .last("limit 1"));
        if (verifyCode == null) {
            throw new BusinessException("请先获取验证码");
        }
        if (!verifyCode.getCode().equals(request.getCode())) {
            throw new BusinessException("验证码错误");
        }
        if (verifyCode.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("验证码已过期");
        }

        SysUser user = new SysUser();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setCreatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);
        sysVerifyCodeMapper.delete(new LambdaQueryWrapper<SysVerifyCode>().eq(SysVerifyCode::getEmail, request.getEmail()));
    }

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, request.getEmail())
                .last("limit 1"));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "邮箱或密码错误");
        }
        String token = jwtUtil.generateToken(new LoginUser(user.getId(), user.getEmail(), user.getNickname()));
        return new LoginResponse(token, new UserInfoResponse(user.getId(), user.getEmail(), user.getNickname()));
    }

    public UserInfoResponse currentUser() {
        Long userId = UserContext.requireUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return new UserInfoResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional
    public UserInfoResponse updateProfile(UserProfileUpdateRequest request) {
        Long userId = UserContext.requireUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setNickname(request.getNickname());
        sysUserMapper.updateById(user);
        return new UserInfoResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Long userId = UserContext.requireUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "原密码不正确");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        sysUserMapper.updateById(user);
    }

    private boolean existsUser(String email) {
        return sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email)) > 0;
    }
}
