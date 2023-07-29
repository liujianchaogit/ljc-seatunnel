package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobInstance;
import lombok.NonNull;

public interface IJobInstanceDao {

    JobInstance getJobInstance(@NonNull Long jobInstanceId);

    void update(@NonNull JobInstance jobInstance);

    void insert(@NonNull JobInstance jobInstance);
}
