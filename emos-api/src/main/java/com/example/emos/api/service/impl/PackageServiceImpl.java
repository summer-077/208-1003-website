package com.example.emos.api.service.impl;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbPackageDao;
import com.example.emos.api.db.pojo.TbPackage;
import com.example.emos.api.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
@Slf4j
public class PackageServiceImpl implements PackageService {

    @Autowired
    private TbPackageDao packageDao;

    @Override
    public PageUtils searchPackageByPage(HashMap param) {
        ArrayList<HashMap> list = packageDao.searchPackageByPage(param);
        long count = packageDao.searchPackageCount(param);
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    public int insert(TbPackage tbPackage) {
        int rows = packageDao.insert(tbPackage);
        return rows;
    }

    @Override
    public int update(HashMap param) {
        int rows = packageDao.update(param);
        return rows;
    }

    @Override
    public int deletePackageByIds(Integer[] ids) {
        int rows = packageDao.deletePackageByIds(ids);
        return rows;
    }
}
