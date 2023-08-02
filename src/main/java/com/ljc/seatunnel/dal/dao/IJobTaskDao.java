package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobTask;

import java.util.List;

public interface IJobTaskDao {

    List<JobTask> getTasksByVersionId(long jobVersionId);

    void insertTask(JobTask jobTask);

    void updateTask(JobTask jobTask);

    JobTask getTask(long jobVersionId, String pluginId);

}
