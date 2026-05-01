package com.money.cloud.note.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wiki_page_file")
public class WikiPageFile {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("page_id")
    private Long pageId;

    @TableField("file_id")
    private Long fileId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
