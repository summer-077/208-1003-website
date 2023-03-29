package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbLeave;
import com.example.emos.api.db.pojo.TbPackage;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbPackageDao {
    public ArrayList<HashMap> searchPackageByPage(HashMap param);

    public long searchPackageCount(HashMap param);

    public HashMap searchPackageById(HashMap param);

    public int insert(TbPackage tbPackage);

    public int update(HashMap param);

    public int deletePackageByIds(Integer[] ids);

}
