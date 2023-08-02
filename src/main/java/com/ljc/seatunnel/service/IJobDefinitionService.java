package com.ljc.seatunnel.service;

import com.ljc.seatunnel.common.CodeGenerateUtils;
import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.request.job.JobReq;
import com.ljc.seatunnel.domain.response.job.JobDefinitionRes;

public interface IJobDefinitionService {

    long createJob(int userId, JobReq jobReq) throws CodeGenerateUtils.CodeGenerateException;
    PageInfo<JobDefinitionRes> getJob(String searchName, Integer pageNo, Integer pageSize, String jobMode);

    JobDefinition getJobDefinitionByJobId(long jobId);

    void deleteJob(long id);

}
