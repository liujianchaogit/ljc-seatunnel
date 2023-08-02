package com.ljc.seatunnel.service;

import com.ljc.seatunnel.domain.request.job.JobDAG;
import com.ljc.seatunnel.domain.request.job.JobTaskInfo;
import com.ljc.seatunnel.domain.request.job.PluginConfig;
import com.ljc.seatunnel.domain.response.job.JobTaskCheckRes;

public interface IJobTaskService {
    JobTaskInfo getTaskConfig(long jobVersionId);

    JobTaskCheckRes saveJobDAG(long jobVersionId, JobDAG jobDAG);

    void saveSingleTask(long jobVersionId, PluginConfig pluginConfig);

    PluginConfig getSingleTask(long jobVersionId, String pluginId);
}
