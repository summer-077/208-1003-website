package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "删除快递表单")
public class DeletePackageByIdsForm {

    @NotEmpty(message = "ids不能为空")
    @Schema(description = "快递ID")
    private Integer[] ids;
}
