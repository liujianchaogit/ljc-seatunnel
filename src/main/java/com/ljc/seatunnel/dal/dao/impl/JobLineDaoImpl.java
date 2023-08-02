package com.ljc.seatunnel.dal.dao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ljc.seatunnel.dal.dao.IJobLineDao;
import com.ljc.seatunnel.dal.entity.JobLine;
import com.ljc.seatunnel.dal.mapper.JobLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobLineDaoImpl implements IJobLineDao {

    @Autowired
    private JobLineMapper jobLineMapper;

    @Override
    public void deleteLinesByVersionId(long jobVersionId) {
        jobLineMapper.deleteLinesByVersionId(jobVersionId);
    }

    @Override
    public void insertLines(List<JobLine> lines) {
        jobLineMapper.insertBatchLines(lines);
    }

    @Override
    public List<JobLine> getLinesByVersionId(long jobVersionId) {
        return jobLineMapper.selectList(
                Wrappers.lambdaQuery(new JobLine()).eq(JobLine::getVersionId, jobVersionId));
    }
}
