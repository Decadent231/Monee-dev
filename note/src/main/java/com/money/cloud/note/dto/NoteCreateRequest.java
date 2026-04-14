package com.money.cloud.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteCreateRequest {
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @Size(max = 64, message = "分类长度不能超过64")
    private String category;

    @Size(max = 255, message = "标签长度不能超过255")
    private String tags;

    @Size(max = 500, message = "摘要长度不能超过500")
    private String summary;

    @NotBlank(message = "内容不能为空")
    @Size(max = 30000, message = "内容长度不能超过30000")
    private String content;
}
