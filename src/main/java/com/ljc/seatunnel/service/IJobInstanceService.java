package com.ljc.seatunnel.service;

import com.ljc.seatunnel.dal.entity.JobLine;
import com.ljc.seatunnel.dal.entity.JobTask;
import com.ljc.seatunnel.domain.executor.JobExecutorRes;
import lombok.NonNull;

import java.util.List;

public interface IJobInstanceService {
    JobExecutorRes createExecuteResource(@NonNull Integer userId, @NonNull Long jobDefineId);

    String generateJobConfig(Long jobId, List<JobTask> tasks, List<JobLine> lines, String envStr);

    void complete(@NonNull Integer userId, @NonNull Long jobInstanceId, @NonNull String jobEngineId);
}
