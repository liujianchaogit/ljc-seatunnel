package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobVersion;

public interface IJobVersionDao {

    void createVersion(JobVersion jobVersion);

    void updateVersion(JobVersion version);
    JobVersion getLatestVersion(long jobId);

    JobVersion getVersionById(long jobVersionId);
}
