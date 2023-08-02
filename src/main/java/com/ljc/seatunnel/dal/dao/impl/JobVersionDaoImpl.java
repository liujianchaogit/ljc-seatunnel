package com.ljc.seatunnel.dal.dao.impl;

import com.ljc.seatunnel.dal.dao.IJobVersionDao;
import com.ljc.seatunnel.dal.entity.JobVersion;
import com.ljc.seatunnel.dal.mapper.JobVersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class JobVersionDaoImpl implements IJobVersionDao {

    @Autowired
    private JobVersionMapper jobVersionMapper;

    @Override
    public void createVersion(JobVersion jobVersion) {
        jobVersionMapper.insert(jobVersion);
    }

    @Override
    public void updateVersion(JobVersion version) {
        jobVersionMapper.updateById(version);
    }

    @Override
    public JobVersion getLatestVersion(long jobId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("job_id", jobId);
        return jobVersionMapper.selectByMap(queryMap).get(0);
    }

    @Override
    public JobVersion getVersionById(long jobVersionId) {
        return jobVersionMapper.selectById(jobVersionId);
    }
}
