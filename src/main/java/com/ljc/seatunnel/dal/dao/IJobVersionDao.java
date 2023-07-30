package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobVersion;

public interface IJobVersionDao {

    JobVersion getLatestVersion(long jobId);

}
