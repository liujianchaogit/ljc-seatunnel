package com.ljc.seatunnel.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ljc.seatunnel.dal.entity.JobInstance;
import com.ljc.seatunnel.domain.dto.job.SeaTunnelJobInstanceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface JobInstanceMapper extends BaseMapper<JobInstance> {
    IPage<SeaTunnelJobInstanceDto> queryJobInstanceListPaging(
            IPage<JobInstance> page,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("jobDefineId") Long jobDefineId,
            @Param("jobMode") String jobMode);
}
