package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.domain.PageInfo;
import lombok.NonNull;

public interface IJobDefinitionDao {

    void add(JobDefinition job);

    JobDefinition getJob(long id);

    void updateJob(JobDefinition jobDefinition);

    PageInfo<JobDefinition> getJob(String name, Integer pageNo, Integer pageSize, String jobMode);
    JobDefinition getJobByName(@NonNull String name);
    void delete(long id);
}
