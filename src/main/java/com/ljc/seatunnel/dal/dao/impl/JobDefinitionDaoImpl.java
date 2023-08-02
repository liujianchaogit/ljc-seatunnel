package com.ljc.seatunnel.dal.dao.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.seatunnel.dal.dao.IJobDefinitionDao;
import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.dal.mapper.JobMapper;
import com.ljc.seatunnel.domain.PageInfo;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobDefinitionDaoImpl implements IJobDefinitionDao {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public void add(JobDefinition job) {
        jobMapper.insert(job);
    }

    @Override
    public JobDefinition getJob(long id) {
        return jobMapper.selectById(id);
    }

    @Override
    public void updateJob(JobDefinition jobDefinition) {
        jobMapper.updateById(jobDefinition);
    }

    @Override
    public PageInfo<JobDefinition> getJob(String searchName, Integer pageNo, Integer pageSize, String jobMode) {
        IPage<JobDefinition> jobDefinitionIPage;
        if (StringUtils.isEmpty(jobMode)) {
            jobDefinitionIPage =
                    jobMapper.queryJobListPaging(new Page<>(pageNo, pageSize), searchName);
        } else {
            jobDefinitionIPage =
                    jobMapper.queryJobListPagingWithJobMode(
                            new Page<>(pageNo, pageSize), searchName, jobMode);
        }
        PageInfo<JobDefinition> jobs = new PageInfo<>();
        jobs.setData(jobDefinitionIPage.getRecords());
        jobs.setPageSize(pageSize);
        jobs.setPageNo(pageNo);
        jobs.setTotalCount((int) jobDefinitionIPage.getTotal());
        return jobs;
    }

    @Override
    public JobDefinition getJobByName(@NonNull String name) {
        return jobMapper.queryJob(name);
    }

    @Override
    public void delete(long id) {
        jobMapper.delete(
                Wrappers.lambdaQuery(new JobDefinition()).and(i -> i.eq(JobDefinition::getId, id)));
    }
}
