package com.ljc.seatunnel.service;

import com.ljc.seatunnel.common.Result;

public interface ITaskInstanceService {

    Result getSyncTaskInstancePaging(
            Integer userId,
            String jobDefineName,
            String executorName,
            String stateType,
            String startTime,
            String endTime,
            String syncTaskType,
            Integer pageNo,
            Integer pageSize);
}
