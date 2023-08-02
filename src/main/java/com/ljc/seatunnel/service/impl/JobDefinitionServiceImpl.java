package com.ljc.seatunnel.service.impl;

import com.ljc.seatunnel.common.CodeGenerateUtils;
import com.ljc.seatunnel.common.EngineType;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.dal.dao.IJobDefinitionDao;
import com.ljc.seatunnel.dal.dao.IJobVersionDao;
import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.dal.entity.JobVersion;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.request.job.JobReq;
import com.ljc.seatunnel.domain.response.job.JobDefinitionRes;
import com.ljc.seatunnel.service.IJobDefinitionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.common.constants.JobMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobDefinitionServiceImpl implements IJobDefinitionService {

    @Autowired
    private IJobDefinitionDao jobDefinitionDao;
    @Autowired
    private IJobVersionDao jobVersionDao;
    private static final String DEFAULT_VERSION = "1.0";

    @Override
    public long createJob(int userId, JobReq jobReq) throws CodeGenerateUtils.CodeGenerateException {
        long uuid = CodeGenerateUtils.getInstance().genCode();
        jobDefinitionDao.add(
                JobDefinition.builder()
                        .id(uuid)
                        .name(jobReq.getName())
                        .description(jobReq.getDescription())
                        .createUserId(userId)
                        .updateUserId(userId)
                        .jobType(jobReq.getJobType().name())
                        .build());
        jobVersionDao.createVersion(
                JobVersion.builder()
                        .jobId(uuid)
                        .createUserId(userId)
                        .updateUserId(userId)
                        .name(DEFAULT_VERSION)
                        .id(uuid)
                        .engineName(EngineType.SeaTunnel.name())
                        .jobMode(JobMode.BATCH.name())
                        .engineVersion("2.3.0")
                        .build());
        return uuid;
    }

    @Override
    public PageInfo<JobDefinitionRes> getJob(String searchName, Integer pageNo, Integer pageSize, String jobMode) {
        if (StringUtils.isNotEmpty(jobMode)) {
            try {
                JobMode.valueOf(jobMode);
            } catch (Exception e) {
                throw new SeatunnelException(
                        SeatunnelErrorEnum.ILLEGAL_STATE, "Unsupported JobMode");
            }
        }
        PageInfo<JobDefinition> jobDefinitionPageInfo =
                jobDefinitionDao.getJob(searchName, pageNo, pageSize, jobMode);
        List<Integer> userIds =
                jobDefinitionPageInfo.getData().stream()
                        .map(JobDefinition::getCreateUserId)
                        .collect(Collectors.toList());
        userIds.addAll(
                jobDefinitionPageInfo.getData().stream()
                        .map(JobDefinition::getUpdateUserId)
                        .collect(Collectors.toList()));
        List<JobDefinitionRes> jobDefinitionResList = new ArrayList<>();
        for (int i = 0; i < jobDefinitionPageInfo.getData().size(); i++) {
            JobDefinition jobDefinition = jobDefinitionPageInfo.getData().get(i);
            JobDefinitionRes jobDefinitionRes = new JobDefinitionRes();
            jobDefinitionRes.setId(jobDefinition.getId());
            jobDefinitionRes.setName(jobDefinition.getName());
            jobDefinitionRes.setDescription(jobDefinition.getDescription());
            jobDefinitionRes.setJobType(jobDefinition.getJobType());
            jobDefinitionRes.setCreateUserId(jobDefinition.getCreateUserId());
            jobDefinitionRes.setUpdateUserId(jobDefinitionRes.getUpdateUserId());
            jobDefinitionRes.setCreateTime(jobDefinition.getCreateTime());
            jobDefinitionRes.setUpdateTime(jobDefinition.getUpdateTime());
            jobDefinitionResList.add(jobDefinitionRes);
        }
        PageInfo<JobDefinitionRes> pageInfo = new PageInfo<>();
        pageInfo.setPageNo(jobDefinitionPageInfo.getPageNo());
        pageInfo.setPageSize(jobDefinitionPageInfo.getPageSize());
        pageInfo.setTotalCount(jobDefinitionPageInfo.getTotalCount());
        pageInfo.setData(jobDefinitionResList);
        return pageInfo;
    }

    @Override
    public JobDefinition getJobDefinitionByJobId(long jobId) {
        return jobDefinitionDao.getJob(jobId);
    }

    @Override
    public void deleteJob(long id) {
        jobDefinitionDao.delete(id);
    }
}
