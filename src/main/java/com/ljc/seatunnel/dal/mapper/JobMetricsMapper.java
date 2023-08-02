package com.ljc.seatunnel.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljc.seatunnel.dal.entity.JobMetrics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JobMetricsMapper extends BaseMapper<JobMetrics> {
    List<JobMetrics> queryJobMetricsByInstanceId(@Param("jobInstanceId") Long jobInstanceId);

    void insertBatchMetrics(@Param("jobMetrics") List<JobMetrics> jobMetrics);
}
