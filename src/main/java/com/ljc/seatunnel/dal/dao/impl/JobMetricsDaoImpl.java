package com.ljc.seatunnel.dal.dao.impl;

import com.ljc.seatunnel.dal.dao.IJobMetricsDao;
import com.ljc.seatunnel.dal.entity.JobMetrics;
import com.ljc.seatunnel.dal.mapper.JobMetricsMapper;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class JobMetricsDaoImpl implements IJobMetricsDao {

    @Resource private JobMetricsMapper jobMetricsMapper;

    @Override
    public List<JobMetrics> getByInstanceId(@NonNull Long jobInstanceId) {
        return jobMetricsMapper.queryJobMetricsByInstanceId(jobInstanceId);
    }

    @Override
    public JobMetricsMapper getJobMetricsMapper() {
        return jobMetricsMapper;
    }
}
