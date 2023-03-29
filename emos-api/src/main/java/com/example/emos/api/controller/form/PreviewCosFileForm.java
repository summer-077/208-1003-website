package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "预览腾讯云COS文件")
public class PreviewCosFileForm {
    @NotEmpty(message = "path不能为空")
    private String path;
}
