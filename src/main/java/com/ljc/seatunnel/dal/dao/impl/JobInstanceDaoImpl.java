package com.ljc.seatunnel.dal.dao.impl;

import com.ljc.seatunnel.dal.dao.IJobInstanceDao;
import com.ljc.seatunnel.dal.entity.JobInstance;
import com.ljc.seatunnel.dal.mapper.JobInstanceMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobInstanceDaoImpl implements IJobInstanceDao {

    @Autowired
    private JobInstanceMapper jobInstanceMapper;

    @Override
    public JobInstance getJobInstance(@NonNull Long jobInstanceId) {
        return jobInstanceMapper.selectById(jobInstanceId);
    }

    @Override
    public void update(@NonNull JobInstance jobInstance) {
        jobInstanceMapper.updateById(jobInstance);
    }

    @Override
    public void insert(@NonNull JobInstance jobInstance) {
        jobInstanceMapper.insert(jobInstance);
    }
}
