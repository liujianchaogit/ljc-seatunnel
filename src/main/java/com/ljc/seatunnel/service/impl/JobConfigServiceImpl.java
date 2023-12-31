package com.ljc.seatunnel.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljc.seatunnel.common.EngineType;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.dal.dao.IJobDefinitionDao;
import com.ljc.seatunnel.dal.dao.IJobVersionDao;
import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.dal.entity.JobVersion;
import com.ljc.seatunnel.domain.request.job.JobConfig;
import com.ljc.seatunnel.domain.response.job.JobConfigRes;
import com.ljc.seatunnel.service.IJobConfigService;
import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.common.constants.JobMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Service
public class JobConfigServiceImpl implements IJobConfigService {

    @Autowired
    private IJobVersionDao jobVersionDao;

    @Autowired
    private IJobDefinitionDao jobDefinitionDao;
    private static final String JOB_MODE = "job.mode";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public JobConfigRes getJobConfig(long jobVersionId) {
        JobVersion jobVersion = jobVersionDao.getVersionById(jobVersionId);
        JobDefinition jobDefinition = jobDefinitionDao.getJob(jobVersion.getJobId());
        JobConfigRes jobConfigRes = new JobConfigRes();
        jobConfigRes.setName(jobDefinition.getName());
        jobConfigRes.setId(jobVersion.getId());
        jobConfigRes.setDescription(jobDefinition.getDescription());
        try {
            jobConfigRes.setEnv(
                    StringUtils.isEmpty(jobVersion.getEnv())
                            ? null
                            : OBJECT_MAPPER.readValue(jobVersion.getEnv(), Map.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jobConfigRes.setEngine(EngineType.valueOf(jobVersion.getEngineName()));
        return jobConfigRes;
    }

    @Override
    public void updateJobConfig(int userId, long jobVersionId, JobConfig jobConfig) throws JsonProcessingException {
        JobVersion version = jobVersionDao.getVersionById(jobVersionId);
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setId(version.getJobId());
        jobDefinition.setUpdateUserId(userId);
        jobDefinition.setName(jobConfig.getName());
        jobDefinition.setDescription(jobConfig.getDescription());
        jobDefinitionDao.updateJob(jobDefinition);
        if (jobConfig.getEnv().containsKey(JOB_MODE)) {
            JobMode jobMode = JobMode.valueOf(jobConfig.getEnv().get(JOB_MODE).toString());
            jobVersionDao.updateVersion(
                    JobVersion.builder()
                            .jobId(version.getJobId())
                            .id(version.getId())
                            .jobMode(jobMode.name())
                            .engineName(jobConfig.getEngine().name())
                            .updateUserId(userId)
                            .env(OBJECT_MAPPER.writeValueAsString(jobConfig.getEnv()))
                            .build());
        } else {
            throw new SeatunnelException(SeatunnelErrorEnum.ILLEGAL_STATE, "job mode is not set");
        }
    }
}
