package com.example.emos.workflow.service.impl;

import com.example.emos.workflow.db.dao.TbReimDao;
import com.example.emos.workflow.exception.EmosException;
import com.example.emos.workflow.service.ReimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ReimServiceImpl implements ReimService {

    @Autowired
    private TbReimDao reimDao;

    @Override
    public HashMap searchReimByInstanceId(String instanceId) {
        HashMap map = reimDao.searchReimByInstanceId(instanceId);
        return map;
    }

    @Override
    public int updateReimReject(HashMap param) {
        int rows = reimDao.updateReimReject(param);
        if(rows != 1){
            throw new EmosException("驳回理由填写失败");
        }
        return rows;
    }
}
