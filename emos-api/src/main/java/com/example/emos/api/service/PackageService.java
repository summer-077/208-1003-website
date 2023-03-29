package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbLeave;
import com.example.emos.api.db.pojo.TbPackage;

import java.util.HashMap;

public interface PackageService {
    public PageUtils searchPackageByPage(HashMap param);
    public int insert(TbPackage tbPackage);
    public int update(HashMap param);
    public int deletePackageByIds(Integer[] ids);

}
