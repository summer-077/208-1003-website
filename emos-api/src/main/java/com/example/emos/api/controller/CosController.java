package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.DeleteCosFileForm;
import com.example.emos.api.controller.form.DownloadCosFileForm;
import com.example.emos.api.controller.form.PreviewCosFileForm;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.oss.CosUtil;
import com.example.emos.api.oss.TypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

@RestController
@RequestMapping("/cos")
@Slf4j
@Tag(name="CosController",description = "对象存储Web接口")
public class CosController {
    @Autowired
    private CosUtil cosUtil;

    @PostMapping("/uploadCosFile")
    @SaCheckLogin
    @Operation(summary = "上传文件")
    public R uploadCosFile(@Param("file") MultipartFile file, @Param("type") String type){
        TypeEnum typeEnum=TypeEnum.findByKey(type);
        if(typeEnum==null){
            throw new EmosException("type类型错误");
        }
        try{
            HashMap map=cosUtil.uploadFile(file,typeEnum);
            return R.ok(map);
        }catch (IOException e){
            log.error("文件上传到腾讯云错误", e);
            throw new EmosException("文件上传到腾讯云错误");
        }
    }

    @PostMapping("/deleteCosFile")
    @SaCheckLogin
    @Operation(summary = "删除文件")
    public R deleteCosFile(@Valid @RequestBody DeleteCosFileForm form){
        cosUtil.deleteFile(form.getPathes());
        return R.ok();
    }






    @PostMapping("/uploadCosfile")
    @SaCheckLogin
    @Operation(summary = "上传文件")
    public R uploadCosfile(@Param("file") MultipartFile file){
        try{
            HashMap map=cosUtil.uploadfile(file);
            return R.ok(map);
        }catch (IOException e){
            log.error("文件上传到腾讯云错误", e);
            throw new EmosException("文件上传到腾讯云错误");
        }
    }

    @PostMapping("/downloadCosFile")
    @SaCheckLogin
    @Operation(summary = "下载文件")
    public R downloadCosFile(@Valid @RequestBody DownloadCosFileForm form){
        HashMap map=cosUtil.downloadFile(form.getPath());
        return R.ok(map);
    }

    @PostMapping("/searchCosFileByPage")
    @SaCheckLogin
    @Operation(summary = "查询文件分页数据")
    public R searchCosFileByPage(){
        PageUtils pageUtils=cosUtil.searchFileByPage();
        return R.ok().put("page", pageUtils);
    }


    @PostMapping("/previewCosFile")
    @SaCheckLogin
    @Operation(summary = "预览文件")
    public R previewCosFile(@Valid @RequestBody PreviewCosFileForm form){
        try {
            HashMap map=cosUtil.previewFile(form.getPath());
            return R.ok(map);
        } catch (URISyntaxException e) {
            log.error("文件预览错误", e);
            throw new EmosException("文件预览错误");
        }
    }

}
