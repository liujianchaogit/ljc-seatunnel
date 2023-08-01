package com.ljc.seatunnel.service;

import com.ljc.seatunnel.domain.request.job.JobTaskInfo;

public interface IJobTaskService {
    JobTaskInfo getTaskConfig(long jobVersionId);
}
