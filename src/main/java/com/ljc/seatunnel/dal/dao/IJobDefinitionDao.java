package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.domain.PageInfo;

public interface IJobDefinitionDao {

    JobDefinition getJob(long id);

    void updateJob(JobDefinition jobDefinition);

    PageInfo<JobDefinition> getJob(String name, Integer pageNo, Integer pageSize, String jobMode);

}
