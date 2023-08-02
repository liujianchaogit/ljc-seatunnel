package com.ljc.seatunnel.dal.dao.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ljc.seatunnel.dal.dao.IJobInstanceDao;
import com.ljc.seatunnel.dal.entity.JobInstance;
import com.ljc.seatunnel.dal.mapper.JobInstanceMapper;
import com.ljc.seatunnel.domain.dto.job.SeaTunnelJobInstanceDto;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class JobInstanceDaoImpl implements IJobInstanceDao {

    @Autowired
    private JobInstanceMapper jobInstanceMapper;

    @Override
    public JobInstance getJobInstance(@NonNull Long jobInstanceId) {
        return jobInstanceMapper.selectById(jobInstanceId);
    }

    @Override
    public void update(@NonNull JobInstance jobInstance) {
        jobInstanceMapper.updateById(jobInstance);
    }

    @Override
    public void insert(@NonNull JobInstance jobInstance) {
        jobInstanceMapper.insert(jobInstance);
    }

    @Override
    public JobInstanceMapper getJobInstanceMapper() {
        return jobInstanceMapper;
    }

    @Override
    public IPage<SeaTunnelJobInstanceDto> queryJobInstanceListPaging(IPage<JobInstance> page, Date startTime, Date endTime, Long jobDefineId, String jobMode) {
        IPage<SeaTunnelJobInstanceDto> jobInstanceIPage =
                jobInstanceMapper.queryJobInstanceListPaging(
                        page, startTime, endTime, jobDefineId, jobMode);
        return jobInstanceIPage;
    }

    @Override
    public List<JobInstance> getAllJobInstance(@NonNull List<Long> jobInstanceIdList) {
        ArrayList<JobInstance> jobInstances = new ArrayList<>();
        for (long jobInstanceId : jobInstanceIdList) {
            JobInstance jobInstance = jobInstanceMapper.selectById(jobInstanceId);
            jobInstances.add(jobInstance);
        }

        return jobInstances;
    }
}
