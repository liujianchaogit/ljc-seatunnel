package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobMetrics;
import com.ljc.seatunnel.dal.mapper.JobMetricsMapper;
import lombok.NonNull;

import java.util.List;

public interface IJobMetricsDao {
    List<JobMetrics> getByInstanceId(@NonNull Long jobInstanceId);

    JobMetricsMapper getJobMetricsMapper();
}
