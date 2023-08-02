package com.ljc.seatunnel.dal.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ljc.seatunnel.dal.entity.JobInstance;
import com.ljc.seatunnel.dal.mapper.JobInstanceMapper;
import com.ljc.seatunnel.domain.dto.job.SeaTunnelJobInstanceDto;
import lombok.NonNull;

import java.util.Date;
import java.util.List;

public interface IJobInstanceDao {

    JobInstance getJobInstance(@NonNull Long jobInstanceId);

    void update(@NonNull JobInstance jobInstance);

    void insert(@NonNull JobInstance jobInstance);

    JobInstanceMapper getJobInstanceMapper();

    IPage<SeaTunnelJobInstanceDto> queryJobInstanceListPaging(
            IPage<JobInstance> page,
            Date startTime,
            Date endTime,
            Long jobDefineId,
            String jobMode);

    List<JobInstance> getAllJobInstance(@NonNull List<Long> jobInstanceIdList);
}
