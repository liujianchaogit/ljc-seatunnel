package com.ljc.seatunnel.dal.dao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ljc.seatunnel.dal.dao.IJobTaskDao;
import com.ljc.seatunnel.dal.entity.JobTask;
import com.ljc.seatunnel.dal.mapper.JobTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobTaskDaoImpl implements IJobTaskDao {

    @Autowired
    private JobTaskMapper jobTaskMapper;

    @Override
    public List<JobTask> getTasksByVersionId(long jobVersionId) {
        return jobTaskMapper.selectList(
                Wrappers.lambdaQuery(new JobTask()).eq(JobTask::getVersionId, jobVersionId));
    }

}
