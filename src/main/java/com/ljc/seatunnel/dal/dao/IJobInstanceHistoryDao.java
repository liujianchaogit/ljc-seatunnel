package com.ljc.seatunnel.dal.dao;


import com.ljc.seatunnel.dal.entity.JobInstanceHistory;

public interface IJobInstanceHistoryDao {
    JobInstanceHistory getByInstanceId(Long jobInstanceId);

    void insert(JobInstanceHistory jobInstanceHistory);
}
