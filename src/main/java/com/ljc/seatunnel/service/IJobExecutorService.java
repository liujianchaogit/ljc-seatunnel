package com.ljc.seatunnel.service;

import com.ljc.seatunnel.common.Result;

public interface IJobExecutorService {

    Result<Long> jobExecute(Integer userId, Long jobDefineId);

}
