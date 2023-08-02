package com.ljc.seatunnel.dal.dao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ljc.seatunnel.dal.dao.IJobInstanceHistoryDao;
import com.ljc.seatunnel.dal.entity.JobInstanceHistory;
import com.ljc.seatunnel.dal.mapper.JobInstanceHistoryMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class JobInstanceHistoryDaoImpl implements IJobInstanceHistoryDao {

    @Resource private JobInstanceHistoryMapper jobInstanceHistoryMapper;

    @Override
    public JobInstanceHistory getByInstanceId(Long jobInstanceId) {
        return jobInstanceHistoryMapper.selectOne(
                Wrappers.lambdaQuery(new JobInstanceHistory())
                        .eq(JobInstanceHistory::getId, jobInstanceId));
    }

    @Override
    public void insert(JobInstanceHistory jobInstanceHistory) {
        jobInstanceHistoryMapper.insert(jobInstanceHistory);
    }
}
