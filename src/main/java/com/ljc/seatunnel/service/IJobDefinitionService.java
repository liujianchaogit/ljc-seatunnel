package com.ljc.seatunnel.service;

import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.response.job.JobDefinitionRes;

public interface IJobDefinitionService {
    PageInfo<JobDefinitionRes> getJob(String searchName, Integer pageNo, Integer pageSize, String jobMode);

    JobDefinition getJobDefinitionByJobId(long jobId);
}
