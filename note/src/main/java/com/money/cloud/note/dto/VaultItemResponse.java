package com.money.cloud.note.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VaultItemResponse {
    private Long id;
    private String title;
    private String category;
    private String username;
    private String password;
    private String website;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
