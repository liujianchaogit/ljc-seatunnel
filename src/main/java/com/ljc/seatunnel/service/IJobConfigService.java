package com.ljc.seatunnel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ljc.seatunnel.domain.request.job.JobConfig;
import com.ljc.seatunnel.domain.response.job.JobConfigRes;

public interface IJobConfigService {
    JobConfigRes getJobConfig(long jobVersionId);
    void updateJobConfig(int userId, long jobVersionId, JobConfig jobConfig)
            throws JsonProcessingException;

}
