package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(description = "修改快递表单")
public class UpdatePackageForm {

    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    @Schema(description = "快递ID")
    private Integer id;

    @NotBlank(message = "number不能为空")
    @Schema(description = "快递单号")
    private String number;

    @NotBlank(message = "origin不能为空")
    @Schema(description = "出发地")
    private String origin;

    @NotBlank(message = "destination不能为空")
    @Schema(description = "目的地")
    private String destination;

    @NotNull(message = "price不能为空")
    @Schema(description = "价格")
    private BigDecimal price;
}
