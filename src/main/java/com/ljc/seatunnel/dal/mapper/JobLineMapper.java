package com.ljc.seatunnel.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljc.seatunnel.dal.entity.JobLine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobLineMapper extends BaseMapper<JobLine> {

    void deleteLinesByVersionId(@Param("versionId") long jobVersionId);

    void insertBatchLines(@Param("lines") List<JobLine> lines);
}
