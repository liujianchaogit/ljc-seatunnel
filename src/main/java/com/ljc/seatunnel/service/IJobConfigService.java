package com.ljc.seatunnel.service;

import com.ljc.seatunnel.domain.response.job.JobConfigRes;

public interface IJobConfigService {
    JobConfigRes getJobConfig(long jobVersionId);
}
