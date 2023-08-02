package com.ljc.seatunnel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ljc.seatunnel.dal.entity.JobInstance;
import com.ljc.seatunnel.domain.response.metrics.JobDAG;
import com.ljc.seatunnel.domain.response.metrics.JobPipelineDetailMetricsRes;
import com.ljc.seatunnel.domain.response.metrics.JobPipelineSummaryMetricsRes;
import com.ljc.seatunnel.domain.response.metrics.JobSummaryMetricsRes;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Map;

public interface IJobMetricsService {

    List<JobPipelineSummaryMetricsRes> getJobPipelineSummaryMetrics(
            @NonNull Integer userId, @NonNull Long jobInstanceId);

    List<JobPipelineDetailMetricsRes> getJobPipelineDetailMetricsRes(
            @NonNull Integer userId, @NonNull Long jobInstanceId);

    JobDAG getJobDAG(@NonNull Integer userId, @NonNull Long jobInstanceId)
            throws JsonProcessingException;

    ImmutablePair<Long, String> getInstanceIdAndEngineId(@NonNull String key);

    void syncJobDataToDb(
            @NonNull JobInstance jobInstance, @NonNull Integer userId, @NonNull String jobEngineId);

    JobSummaryMetricsRes getJobSummaryMetrics(
            @NonNull Integer userId, @NonNull Long jobInstanceId, @NonNull String jobEngineId);

    Map<Long, JobSummaryMetricsRes> getALLJobSummaryMetrics(
            @NonNull Integer userId,
            @NonNull Map<Long, Long> jobInstanceIdAndJobEngineIdMap,
            @NonNull List<Long> jobInstanceIdList,
            @NonNull String syncTaskType);
}
