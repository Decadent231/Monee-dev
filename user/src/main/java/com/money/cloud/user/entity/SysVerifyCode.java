package com.money.cloud.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_verify_code")
public class SysVerifyCode {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String email;
    private String code;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
