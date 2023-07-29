package com.ljc.seatunnel.service.impl;

import com.hazelcast.client.config.ClientConfig;
import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.dal.dao.IJobInstanceDao;
import com.ljc.seatunnel.dal.entity.JobInstance;
import com.ljc.seatunnel.domain.executor.JobExecutorRes;
import com.ljc.seatunnel.service.IJobExecutorService;
import com.ljc.seatunnel.service.IJobInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.seatunnel.common.config.Common;
import org.apache.seatunnel.common.config.DeployMode;
import org.apache.seatunnel.engine.client.SeaTunnelClient;
import org.apache.seatunnel.engine.client.job.ClientJobProxy;
import org.apache.seatunnel.engine.client.job.JobExecutionEnvironment;
import org.apache.seatunnel.engine.common.config.ConfigProvider;
import org.apache.seatunnel.engine.common.config.JobConfig;
import org.apache.seatunnel.engine.core.job.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class JobExecutorServiceImpl implements IJobExecutorService {

    @Autowired
    private IJobInstanceService jobInstanceService;
    @Autowired
    private IJobInstanceDao jobInstanceDao;

    @Override
    public Result<Long> jobExecute(Integer userId, Long jobDefineId) {
        JobExecutorRes executeResource = jobInstanceService.createExecuteResource(userId, jobDefineId);
        String jobConfig = executeResource.getJobConfig();
        String configFile = writeJobConfigIntoConfFile(jobConfig, jobDefineId);
        Long jobInstanceId = executeJobBySeaTunnel(userId, configFile, executeResource.getJobInstanceId());
        return Result.success(jobInstanceId);
    }

    public String writeJobConfigIntoConfFile(String jobConfig, Long jobDefineId) {
        String projectRoot = System.getProperty("user.dir");
        String filePath = projectRoot + "\\profile\\" + jobDefineId + ".conf";
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jobConfig);
            bufferedWriter.close();
            log.info("File created and content written successfully.");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return filePath;
    }

    public Long executeJobBySeaTunnel(Integer userId, String filePath, Long jobInstanceId) {
        Common.setDeployMode(DeployMode.CLIENT);
        JobConfig jobConfig = new JobConfig();
        jobConfig.setName(jobInstanceId + "_job");
        SeaTunnelClient seaTunnelClient = createSeaTunnelClient();
        try {
            JobExecutionEnvironment jobExecutionEnv =
                    seaTunnelClient.createExecutionContext(filePath, jobConfig);
            final ClientJobProxy clientJobProxy = jobExecutionEnv.execute();
            JobInstance jobInstance = jobInstanceDao.getJobInstance(jobInstanceId);
            jobInstance.setJobEngineId(Long.toString(clientJobProxy.getJobId()));
            jobInstanceDao.update(jobInstance);

            CompletableFuture.runAsync(
                    () -> {
                        waitJobFinish(
                                clientJobProxy,
                                userId,
                                jobInstanceId,
                                Long.toString(clientJobProxy.getJobId()),
                                seaTunnelClient);
                    });

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return jobInstanceId;
    }

    public void waitJobFinish(
            ClientJobProxy clientJobProxy,
            Integer userId,
            Long jobInstanceId,
            String jobEngineId,
            SeaTunnelClient seaTunnelClient) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        CompletableFuture<JobStatus> future =
                CompletableFuture.supplyAsync(clientJobProxy::waitForJobComplete, executor);
        try {
            JobStatus jobStatus = future.get();
            if (JobStatus.FINISHED.equals(jobStatus)) {
                jobInstanceService.complete(userId, jobInstanceId, jobEngineId);
                executor.shutdown();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            seaTunnelClient.close();
        }
    }

    private SeaTunnelClient createSeaTunnelClient() {
        ClientConfig clientConfig = ConfigProvider.locateAndGetClientConfig();
        clientConfig.setClusterName("seatunnel");
        return new SeaTunnelClient(clientConfig);
    }

}
