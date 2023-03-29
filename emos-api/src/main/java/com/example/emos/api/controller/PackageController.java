package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbPackage;
import com.example.emos.api.db.pojo.TbReim;
import com.example.emos.api.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/package")
@Tag(name = "PackageController", description = "快递接口")
public class PackageController {
    @Autowired
    private PackageService packageService;


    @PostMapping("/searchPackageByPage")
    @Operation(summary = "查询快递分页数据")
    @SaCheckLogin
    public R searchPackageByPage(@Valid @RequestBody SearchPackageByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("start", start);
        PageUtils pageUtils = packageService.searchPackageByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @Operation(summary = "添加快递记录")
    @SaCheckLogin
    public R insert(@Valid @RequestBody InsertPackageForm form) {
        TbPackage tbPackage = JSONUtil.parse(form).toBean(TbPackage.class);
        tbPackage.setUserId(StpUtil.getLoginIdAsInt());
        int rows = packageService.insert(tbPackage);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/update")
    @Operation(summary = "修改快递记录")
    @SaCheckLogin
    public R update(@Valid @RequestBody UpdatePackageForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        int rows = packageService.update(param);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deletePackageByIds")
    @Operation(summary = "删除快递记录")
    @SaCheckLogin
    public R deletePackageByIds(@Valid @RequestBody DeletePackageByIdsForm form) {
        int rows = packageService.deletePackageByIds(form.getIds());
        return R.ok().put("rows", rows);
    }
}
