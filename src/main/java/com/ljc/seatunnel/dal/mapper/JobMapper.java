package com.ljc.seatunnel.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ljc.seatunnel.dal.entity.JobDefinition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobMapper extends BaseMapper<JobDefinition> {
    IPage<JobDefinition> queryJobListPaging(
            IPage<JobDefinition> page, @Param("searchName") String searchName);

    IPage<JobDefinition> queryJobListPagingWithJobMode(
            IPage<JobDefinition> page,
            @Param("searchName") String searchName,
            @Param("jobMode") String jobMode);

    List<JobDefinition> queryJobList(@Param("searchName") String searchName);

    JobDefinition queryJob(@Param("searchName") String searchName);
}
