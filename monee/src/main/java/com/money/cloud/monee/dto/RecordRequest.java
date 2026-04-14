package com.money.cloud.monee.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordRequest {
    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "^(expense|income)$", message = "类型必须为 expense 或 income")
    private String type;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
}
