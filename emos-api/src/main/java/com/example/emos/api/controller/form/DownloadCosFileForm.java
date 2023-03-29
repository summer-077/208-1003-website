package com.example.emos.api.controller.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "下载腾讯云COS文件")
public class DownloadCosFileForm {
    @NotEmpty(message = "path不能为空")
    private String path;

//    @NotEmpty(message = "localpath不能为空")
//    private String localpath;
}
