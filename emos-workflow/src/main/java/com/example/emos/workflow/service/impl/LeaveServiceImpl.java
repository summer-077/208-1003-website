package com.example.emos.workflow.service.impl;

import com.example.emos.workflow.db.dao.TbLeaveDao;
import com.example.emos.workflow.exception.EmosException;
import com.example.emos.workflow.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private TbLeaveDao leaveDao;

    @Override
    public HashMap searchLeaveByInstanceId(String instanceId) {
        HashMap map = leaveDao.searchLeaveByInstanceId(instanceId);
        return map;
    }

    @Override
    public int updateLeaveReject(HashMap param) {
        int rows = leaveDao.updateLeaveReject(param);
        if(rows != 1){
            throw new EmosException("驳回理由填写失败");
        }
        return rows;
    }
}
