package com.ljc.seatunnel.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljc.seatunnel.common.CodeGenerateUtils;
import com.ljc.seatunnel.common.Constants;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.dal.dao.IJobInstanceDao;
import com.ljc.seatunnel.dal.dao.IJobInstanceHistoryDao;
import com.ljc.seatunnel.dal.dao.IJobMetricsDao;
import com.ljc.seatunnel.dal.entity.JobInstance;
import com.ljc.seatunnel.dal.entity.JobInstanceHistory;
import com.ljc.seatunnel.dal.entity.JobMetrics;
import com.ljc.seatunnel.domain.response.engine.Engine;
import com.ljc.seatunnel.domain.response.metrics.JobDAG;
import com.ljc.seatunnel.domain.response.metrics.JobPipelineDetailMetricsRes;
import com.ljc.seatunnel.domain.response.metrics.JobPipelineSummaryMetricsRes;
import com.ljc.seatunnel.domain.response.metrics.JobSummaryMetricsRes;
import com.ljc.seatunnel.service.IJobMetricsService;
import com.ljc.seatunnel.thirdparty.engine.SeaTunnelEngineProxy;
import com.ljc.seatunnel.thirdparty.metrics.EngineMetricsExtractorFactory;
import com.ljc.seatunnel.thirdparty.metrics.IEngineMetricsExtractor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobMetricsServiceImpl implements IJobMetricsService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Autowired
    private IJobMetricsDao jobMetricsDao;
    @Autowired
    private IJobInstanceHistoryDao jobInstanceHistoryDao;
    @Autowired
    private IJobInstanceDao jobInstanceDao;

    @Override
    public List<JobPipelineSummaryMetricsRes> getJobPipelineSummaryMetrics(
            @NonNull Integer userId, @NonNull Long jobInstanceId) {
        JobInstance jobInstance = jobInstanceDao.getJobInstance(jobInstanceId);
        String jobEngineId = jobInstance.getJobEngineId();
        List<JobMetrics> jobPipelineDetailMetrics =
                getJobPipelineMetrics(userId, jobInstanceId, jobEngineId);
        return summaryMetrics(jobPipelineDetailMetrics);
    }

    @Override
    public JobSummaryMetricsRes getJobSummaryMetrics(
            @NonNull Integer userId, @NonNull Long jobInstanceId, @NonNull String jobEngineId) {
        JobInstance jobInstance = jobInstanceDao.getJobInstance(jobInstanceId);
        Engine engine = new Engine(jobInstance.getEngineName(), jobInstance.getEngineVersion());
        IEngineMetricsExtractor engineMetricsExtractor =
                (new EngineMetricsExtractorFactory(engine)).getEngineMetricsExtractor();
        String jobStatus = engineMetricsExtractor.getJobStatus(jobEngineId);

        List<JobMetrics> jobPipelineDetailMetrics =
                getJobPipelineMetrics(userId, jobInstanceId, jobEngineId);
        long readCount =
                jobPipelineDetailMetrics.stream().mapToLong(JobMetrics::getReadRowCount).sum();
        long writeCount =
                jobPipelineDetailMetrics.stream().mapToLong(JobMetrics::getWriteRowCount).sum();

        JobSummaryMetricsRes jobSummaryMetricsRes =
                new JobSummaryMetricsRes(
                        jobInstanceId, Long.valueOf(jobEngineId), readCount, writeCount, jobStatus);
        return jobSummaryMetricsRes;
    }

    @Override
    public Map<Long, JobSummaryMetricsRes> getALLJobSummaryMetrics(
            @NonNull Integer userId,
            @NonNull Map<Long, Long> jobInstanceIdAndJobEngineIdMap,
            @NonNull List<Long> jobInstanceIdList,
            @NonNull String syncTaskType) {
        log.info("jobInstanceIdAndJobEngineIdMap={}", jobInstanceIdAndJobEngineIdMap);

        List<JobInstance> allJobInstance = jobInstanceDao.getAllJobInstance(jobInstanceIdList);
        Map<Long, JobSummaryMetricsRes> result = null;
        Map<Long, HashMap<Integer, JobMetrics>> allRunningJobMetricsFromEngine =
                getAllRunningJobMetricsFromEngine(
                        allJobInstance.get(0).getEngineName(),
                        allJobInstance.get(0).getEngineVersion());

        if (syncTaskType.equals("BATCH")) {

            result =
                    getMatricsListIfTaskTypeIsBatch(
                            allJobInstance,
                            userId,
                            allRunningJobMetricsFromEngine,
                            jobInstanceIdAndJobEngineIdMap);
        } else if (syncTaskType.equals("STREAMING")) {
            result =
                    getMatricsListIfTaskTypeIsStreaming(
                            allJobInstance,
                            userId,
                            allRunningJobMetricsFromEngine,
                            jobInstanceIdAndJobEngineIdMap);
        }

        log.info("result is {}", result.toString());
        return result;
    }

    private Map<Long, JobSummaryMetricsRes> getMatricsListIfTaskTypeIsBatch(
            List<JobInstance> allJobInstance,
            Integer userId,
            Map<Long, HashMap<Integer, JobMetrics>> allRunningJobMetricsFromEngine,
            Map<Long, Long> jobInstanceIdAndJobEngineIdMap) {

        HashMap<Long, JobSummaryMetricsRes> jobSummaryMetricsResMap = new HashMap<>();

        log.info("allRunningJobMetricsFromEngine is {}", allRunningJobMetricsFromEngine.toString());

        // Traverse all jobInstances in allJobInstance
        for (JobInstance jobInstance : allJobInstance) {
            log.info("jobEngineId={}", jobInstance.getJobEngineId());

            if (jobInstance.getJobStatus() == null
                    || jobInstance.getJobStatus().equals("FAILED")
                    || jobInstance.getJobStatus().equals("RUNNING")) {
                // Obtain monitoring information from the collection of running jobs returned from
                // the engine
                if (!allRunningJobMetricsFromEngine.isEmpty()
                        && allRunningJobMetricsFromEngine.containsKey(
                        jobInstanceIdAndJobEngineIdMap.get(jobInstance.getId()))) {
                    JobSummaryMetricsRes jobMetricsFromEngineRes =
                            getRunningJobMetricsFromEngine(
                                    allRunningJobMetricsFromEngine,
                                    jobInstanceIdAndJobEngineIdMap,
                                    jobInstance);
                    jobSummaryMetricsResMap.put(jobInstance.getId(), jobMetricsFromEngineRes);
                    modifyAndUpdateJobInstanceAndJobMetrics(
                            jobInstance,
                            allRunningJobMetricsFromEngine,
                            jobInstanceIdAndJobEngineIdMap,
                            userId);

                } else {
                    log.info(
                            "The job does not exist on the engine, it is directly returned from the database");
                    JobSummaryMetricsRes jobMetriceFromDb =
                            getJobSummaryMetricsResByDb(
                                    jobInstance,
                                    userId,
                                    Long.toString(
                                            jobInstanceIdAndJobEngineIdMap.get(
                                                    jobInstance.getId())));
                    if (jobMetriceFromDb != null) {
                        jobSummaryMetricsResMap.put(jobInstance.getId(), jobMetriceFromDb);
                    }
                    // 将数据库中的jobInstance和jobMetrics的作业状态改为finished
                    jobInstance.setJobStatus("FINISHED");
                    jobInstanceDao.getJobInstanceMapper().updateById(jobInstance);
                }
            } else if (jobInstance.getJobStatus().equals("FINISHED")
                    || jobInstance.getJobStatus().equals("CANCELED")) {
                // If the status of the job is finished or cancelled, the monitoring information is
                // directly obtained from MySQL
                JobSummaryMetricsRes jobMetriceFromDb =
                        getJobSummaryMetricsResByDb(
                                jobInstance,
                                userId,
                                Long.toString(
                                        jobInstanceIdAndJobEngineIdMap.get(jobInstance.getId())));
                log.info("jobStatus=finish oe canceled,JobSummaryMetricsRes={}", jobMetriceFromDb);
                jobSummaryMetricsResMap.put(jobInstance.getId(), jobMetriceFromDb);
            }
        }

        return jobSummaryMetricsResMap;
    }

    private void modifyAndUpdateJobInstanceAndJobMetrics(
            JobInstance jobInstance,
            Map<Long, HashMap<Integer, JobMetrics>> allRunningJobMetricsFromEngine,
            Map<Long, Long> jobInstanceIdAndJobEngineIdMap,
            Integer userId) {
        jobInstance.setJobStatus("RUNNING");
        HashMap<Integer, JobMetrics> jobMetricsFromEngine =
                allRunningJobMetricsFromEngine.get(
                        jobInstanceIdAndJobEngineIdMap.get(jobInstance.getId()));
        List<JobMetrics> jobMetricsFromDb = jobMetricsDao.getByInstanceId(jobInstance.getId());
        log.info("001jobMetricsFromDb={}", jobMetricsFromDb);

        if (jobMetricsFromDb.isEmpty()) {
            log.info("002jobMetricsFromDb == null");
            syncMetricsToDbRunning(jobInstance, userId, jobMetricsFromEngine);
            jobInstanceDao.update(jobInstance);
        } else {
            jobMetricsFromDb.stream()
                    .forEach(
                            jobMetrics ->
                                    jobMetrics.setReadRowCount(
                                            jobMetricsFromEngine
                                                    .get(jobMetrics.getPipelineId())
                                                    .getReadRowCount()));
            jobMetricsFromDb.stream()
                    .forEach(
                            jobMetrics ->
                                    jobMetrics.setWriteRowCount(
                                            jobMetricsFromEngine
                                                    .get(jobMetrics.getPipelineId())
                                                    .getWriteRowCount()));
            jobMetricsFromDb.stream().forEach(jobMetrics -> jobMetrics.setStatus("RUNNING"));

            updateJobInstanceAndMetrics(jobInstance, jobMetricsFromDb);
        }
    }

    private Map<Long, JobSummaryMetricsRes> getMatricsListIfTaskTypeIsStreaming(
            List<JobInstance> allJobInstance,
            Integer userId,
            Map<Long, HashMap<Integer, JobMetrics>> allRunningJobMetricsFromEngine,
            Map<Long, Long> jobInstanceIdAndJobEngineIdMap) {

        HashMap<Long, JobSummaryMetricsRes> jobSummaryMetricsResMap = new HashMap<>();

        // Traverse all jobInstances in allJobInstance
        for (JobInstance jobInstance : allJobInstance) {

            if (jobInstance.getJobStatus() != null
                    && jobInstance.getJobStatus().equals("CANCELED")) {
                // If the status of the job is finished or cancelled
                // the monitoring information is directly obtained from MySQL
                JobSummaryMetricsRes jobMetriceFromDb =
                        getJobSummaryMetricsResByDb(
                                jobInstance,
                                userId,
                                Long.toString(
                                        jobInstanceIdAndJobEngineIdMap.get(jobInstance.getId())));
                jobSummaryMetricsResMap.put(jobInstance.getId(), jobMetriceFromDb);

            } else if (jobInstance.getJobStatus() != null
                    && (jobInstance.getJobStatus().equals("FINISHED")
                    || jobInstance.getJobStatus().equals("FAILED"))) {
                // Obtain monitoring information from the collection of running jobs returned from
                // the engine
                if (!allRunningJobMetricsFromEngine.isEmpty()
                        && allRunningJobMetricsFromEngine.containsKey(
                        jobInstanceIdAndJobEngineIdMap.get(jobInstance.getId()))) {
                    // If it can be found, update the information in MySQL and return it to the
                    // front-end data
                    modifyAndUpdateJobInstanceAndJobMetrics(
                            jobInstance,
                            allRunningJobMetricsFromEngine,
                            jobInstanceIdAndJobEngineIdMap,
                            userId);

                    /** Return data from the front-end */
                    JobSummaryMetricsRes jobMetricsFromEngineRes =
                            getRunningJobMetricsFromEngine(
                                    allRunningJobMetricsFromEngine,
                                    jobInstanceIdAndJobEngineIdMap,
                                    jobInstance);
                    jobSummaryMetricsResMap.put(jobInstance.getId(), jobMetricsFromEngineRes);
                } else {
                    // If not found, obtain information from MySQL
                    JobSummaryMetricsRes jobMetriceFromDb =
                            getJobSummaryMetricsResByDb(
                                    jobInstance,
                                    userId,
                                    Long.toString(
                                            jobInstanceIdAndJobEngineIdMap.get(
                                                    jobInstance.getId())));
                    jobSummaryMetricsResMap.put(jobInstance.getId(), jobMetriceFromDb);
                }
            } else {
                // Obtain monitoring information from the collection of running jobs returned from
                // the engine
                if (!allRunningJobMetricsFromEngine.isEmpty()
                        && allRunningJobMetricsFromEngine.containsKey(
                        jobInstanceIdAndJobEngineIdMap.get(jobInstance.getId()))) {
                    modifyAndUpdateJobInstanceAndJobMetrics(
                            jobInstance,
                            allRunningJobMetricsFromEngine,
                            jobInstanceIdAndJobEngineIdMap,
                            userId);
                    /** Return data from the front-end */
                    JobSummaryMetricsRes jobMetricsFromEngineRes =
                            getRunningJobMetricsFromEngine(
                                    allRunningJobMetricsFromEngine,
                                    jobInstanceIdAndJobEngineIdMap,
                                    jobInstance);
                    jobSummaryMetricsResMap.put(jobInstance.getId(), jobMetricsFromEngineRes);
                } else {
                    String jobStatusByJobEngineId =
                            getJobStatusByJobEngineId(
                                    String.valueOf(
                                            jobInstanceIdAndJobEngineIdMap.get(
                                                    jobInstance.getId())));
                    if (jobStatusByJobEngineId != null) {
                        jobInstance.setJobStatus(jobStatusByJobEngineId);
                        jobInstanceDao.update(jobInstance);
                        JobSummaryMetricsRes jobSummaryMetricsResByDb =
                                getJobSummaryMetricsResByDb(
                                        jobInstance,
                                        userId,
                                        String.valueOf(
                                                jobInstanceIdAndJobEngineIdMap.get(
                                                        jobInstance.getId())));
                        jobSummaryMetricsResMap.put(jobInstance.getId(), jobSummaryMetricsResByDb);
                        List<JobMetrics> jobMetricsFromDb =
                                getJobMetricsFromDb(
                                        jobInstance,
                                        userId,
                                        String.valueOf(
                                                jobInstanceIdAndJobEngineIdMap.get(
                                                        jobInstance.getId())));
                        if (!jobMetricsFromDb.isEmpty()) {
                            jobMetricsFromDb.stream()
                                    .forEach(
                                            jobMetrics ->
                                                    jobMetrics.setStatus(jobStatusByJobEngineId));
                            for (JobMetrics jobMetrics : jobMetricsFromDb) {
                                jobMetricsDao.getJobMetricsMapper().updateById(jobMetrics);
                            }
                        }
                    }
                }
            }
        }
        return jobSummaryMetricsResMap;
    }

    private JobSummaryMetricsRes getRunningJobMetricsFromEngine(
            Map<Long, HashMap<Integer, JobMetrics>> allRunningJobMetricsFromEngine,
            Map<Long, Long> jobInstanceIdAndJobEngineIdMap,
            JobInstance jobInstance) {

        // If there is job information in the engine
        HashMap<Integer, JobMetrics> jobMetricsFromEngine =
                allRunningJobMetricsFromEngine.get(
                        jobInstanceIdAndJobEngineIdMap.get(jobInstance.getId()));
        log.info("0706jobMetricsFromEngine={}", jobMetricsFromEngine);
        long readCount = 0l;
        long writeCount = 0l;

        Collection<JobMetrics> values = jobMetricsFromEngine.values();
        Iterator<JobMetrics> iterator = values.iterator();
        while (iterator.hasNext()) {
            JobMetrics next = iterator.next();
            readCount += next.getReadRowCount();
        }

        writeCount =
                jobMetricsFromEngine.values().stream()
                        .mapToLong(JobMetrics::getWriteRowCount)
                        .sum();

        log.info("jobInstance={}", jobInstance.toString());

        JobSummaryMetricsRes jobSummaryMetricsRes =
                new JobSummaryMetricsRes(jobInstance.getId(), 1l, readCount, writeCount, "RUNNING");

        return jobSummaryMetricsRes;
    }

    private JobSummaryMetricsRes getJobSummaryMetricsResByDb(
            JobInstance jobInstance, Integer userId, String jobEngineId) {
        List<JobMetrics> jobMetricsFromDb = getJobMetricsFromDb(jobInstance, userId, jobEngineId);
        if (!jobMetricsFromDb.isEmpty()) {
            long readCount = jobMetricsFromDb.stream().mapToLong(JobMetrics::getReadRowCount).sum();
            long writeCount =
                    jobMetricsFromDb.stream().mapToLong(JobMetrics::getWriteRowCount).sum();
            JobSummaryMetricsRes jobSummaryMetricsRes =
                    new JobSummaryMetricsRes(
                            jobInstance.getId(),
                            Long.valueOf(jobInstance.getJobEngineId()),
                            readCount,
                            writeCount,
                            jobInstance.getJobStatus());
            return jobSummaryMetricsRes;
        }
        return null;
    }

    private Map<Long, HashMap<Integer, JobMetrics>> getAllRunningJobMetricsFromEngine(
            String engineName, String engineVersion) {
        Engine engine = new Engine(engineName, engineVersion);

        IEngineMetricsExtractor engineMetricsExtractor =
                (new EngineMetricsExtractorFactory(engine)).getEngineMetricsExtractor();

        return engineMetricsExtractor.getAllRunningJobMetrics();
    }

    private void updateJobInstanceAndMetrics(JobInstance jobInstance, List<JobMetrics> jobMetrics) {
        if (jobInstance != null && jobMetrics != null) {
            jobInstanceDao.update(jobInstance);
            // jobMetricsFromDb
            for (JobMetrics jobMetric : jobMetrics) {
                jobMetricsDao.getJobMetricsMapper().updateById(jobMetric);
            }
        }
    }

    private String getJobStatusByJobEngineId(String jobEngineId) {
        return SeaTunnelEngineProxy.getInstance().getJobStatus(jobEngineId);
    }

    private Map<Integer, JobMetrics> getJobMetricsFromEngineMap(
            @NonNull JobInstance jobInstance, @NonNull String jobEngineId) {

        log.info("enter getJobMetricsFromEngine");
        Engine engine = new Engine(jobInstance.getEngineName(), jobInstance.getEngineVersion());

        IEngineMetricsExtractor engineMetricsExtractor =
                (new EngineMetricsExtractorFactory(engine)).getEngineMetricsExtractor();

        return engineMetricsExtractor.getMetricsByJobEngineIdRTMap(jobEngineId);
    }

    private List<JobMetrics> getJobPipelineDetailMetrics(
            @NonNull JobInstance jobInstance,
            @NonNull Integer userId,
            @NonNull String jobEngineId,
            @NonNull String jobStatus,
            @NonNull IEngineMetricsExtractor engineMetricsExtractor) {

        // If job is not end state, get metrics from engine.
        List<JobMetrics> jobMetrics = new ArrayList<>();
        if (engineMetricsExtractor.isJobEndStatus(jobStatus)) {
            jobMetrics = getJobMetricsFromDb(jobInstance, userId, jobEngineId);
            if (CollectionUtils.isEmpty(jobMetrics)) {
                syncMetricsToDb(jobInstance, userId, jobEngineId);
                jobMetrics = getJobMetricsFromEngine(jobInstance, jobEngineId);
            }
        } else {
            jobMetrics = getJobMetricsFromEngine(jobInstance, jobEngineId);
        }

        return jobMetrics;
    }

    @Override
    public List<JobPipelineDetailMetricsRes> getJobPipelineDetailMetricsRes(
            @NonNull Integer userId, @NonNull Long jobInstanceId) {
        JobInstance jobInstance = jobInstanceDao.getJobInstance(jobInstanceId);
        String jobEngineId = jobInstance.getJobEngineId();
        List<JobMetrics> jobPipelineDetailMetrics =
                getJobPipelineMetrics(userId, jobInstanceId, jobEngineId);
        return jobPipelineDetailMetrics.stream()
                .map(this::wrapperJobMetrics)
                .collect(Collectors.toList());
    }

    private List<JobMetrics> getJobPipelineMetrics(
            @NonNull Integer userId, @NonNull Long jobInstanceId, @NonNull String jobEngineId) {

        JobInstance jobInstance = jobInstanceDao.getJobInstance(jobInstanceId);
        Engine engine = new Engine(jobInstance.getEngineName(), jobInstance.getEngineVersion());
        IEngineMetricsExtractor engineMetricsExtractor =
                (new EngineMetricsExtractorFactory(engine)).getEngineMetricsExtractor();
        String jobStatus = engineMetricsExtractor.getJobStatus(jobEngineId);
        List<JobMetrics> jobPipelineDetailMetrics =
                getJobPipelineDetailMetrics(
                        jobInstance, userId, jobEngineId, jobStatus, engineMetricsExtractor);
        return jobPipelineDetailMetrics;
    }

    @Override
    public JobDAG getJobDAG(@NonNull Integer userId, @NonNull Long jobInstanceId)
            throws JsonProcessingException {
        JobInstance jobInstance = jobInstanceDao.getJobInstance(jobInstanceId);
        String jobEngineId = jobInstance.getJobEngineId();
        JobInstanceHistory history = getJobHistoryFromDb(jobInstance, userId, jobEngineId);
        if (history != null) {
            String dag = history.getDag();
            try {
                return OBJECT_MAPPER.readValue(dag, JobDAG.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Engine engine = new Engine(jobInstance.getEngineName(), jobInstance.getEngineVersion());
        IEngineMetricsExtractor engineMetricsExtractor =
                (new EngineMetricsExtractorFactory(engine)).getEngineMetricsExtractor();

        if (engineMetricsExtractor.isJobEnd(jobEngineId)) {
            syncHistoryJobInfoToDb(jobInstance, jobEngineId);
            history = getJobHistoryFromDb(jobInstance, userId, jobEngineId);
        } else {
            history = getJobHistoryFromEngine(jobInstance, jobEngineId);
        }
        if (history != null) {
            String dag = history.getDag();
            try {
                return OBJECT_MAPPER.readValue(dag, JobDAG.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private JobInstanceHistory getJobHistoryFromEngine(
            @NonNull JobInstance jobInstance, String jobEngineId) {

        Engine engine = new Engine(jobInstance.getEngineName(), jobInstance.getEngineVersion());

        IEngineMetricsExtractor engineMetricsExtractor =
                (new EngineMetricsExtractorFactory(engine)).getEngineMetricsExtractor();

        return engineMetricsExtractor.getJobHistoryById(jobEngineId);
    }

    private JobInstanceHistory getJobHistoryFromDb(
            @NonNull JobInstance jobInstance, Integer userId, String jobEngineId) {
        // relation jobInstanceId and jobEngineId
        relationJobInstanceAndJobEngineId(jobInstance, userId, jobEngineId);
        return jobInstanceHistoryDao.getByInstanceId(jobInstance.getId());
    }

    @Override
    public void syncJobDataToDb(
            @NonNull JobInstance jobInstance,
            @NonNull Integer userId,
            @NonNull String jobEngineId) {
        relationJobInstanceAndJobEngineId(jobInstance, userId, jobEngineId);
        syncMetricsToDb(jobInstance, userId, jobEngineId);
        syncHistoryJobInfoToDb(jobInstance, jobEngineId);
        syncCompleteJobInfoToDb(jobInstance);
    }

    private void syncMetricsToDb(
            @NonNull JobInstance jobInstance,
            @NonNull Integer userId,
            @NonNull String jobEngineId) {
        Map<Integer, JobMetrics> jobMetricsFromEngineMap =
                getJobMetricsFromEngineMap(jobInstance, jobEngineId);

        List<JobMetrics> jobMetricsFromDb = getJobMetricsFromDb(jobInstance, userId, jobEngineId);
        if (jobMetricsFromDb.isEmpty()) {
            List<JobMetrics> jobMetricsFromEngine =
                    Arrays.asList(jobMetricsFromEngineMap.values().toArray(new JobMetrics[0]));
            jobMetricsFromEngine.forEach(
                    metrics -> {
                        try {
                            metrics.setId(CodeGenerateUtils.getInstance().genCode());
                        } catch (CodeGenerateUtils.CodeGenerateException e) {
                            throw new SeatunnelException(
                                    SeatunnelErrorEnum.JOB_RUN_GENERATE_UUID_ERROR);
                        }
                        metrics.setJobInstanceId(jobInstance.getId());
                        metrics.setCreateUserId(userId);
                        metrics.setUpdateUserId(userId);
                    });

            if (!jobMetricsFromEngine.isEmpty()) {
                jobMetricsDao.getJobMetricsMapper().insertBatchMetrics(jobMetricsFromEngine);
            }
        } else {
            String jobStatus = getJobStatusByJobEngineId(jobEngineId);
            for (JobMetrics jobMetrics : jobMetricsFromDb) {
                Integer pipelineId = jobMetrics.getPipelineId();
                JobMetrics currentPiplinejobMetricsFromEngine =
                        jobMetricsFromEngineMap.get(pipelineId);
                jobMetrics.setWriteQps(currentPiplinejobMetricsFromEngine.getWriteQps());
                jobMetrics.setReadQps(currentPiplinejobMetricsFromEngine.getReadQps());
                jobMetrics.setReadRowCount(currentPiplinejobMetricsFromEngine.getReadRowCount());
                jobMetrics.setWriteRowCount(currentPiplinejobMetricsFromEngine.getWriteRowCount());
                jobMetrics.setStatus(jobStatus);
                jobMetricsDao.getJobMetricsMapper().updateById(jobMetrics);
            }
        }
    }

    private void syncHistoryJobInfoToDb(
            @NonNull JobInstance jobInstance, @NonNull String jobEngineId) {
        JobInstanceHistory jobHistoryFromEngine = getJobHistoryFromEngine(jobInstance, jobEngineId);

        jobHistoryFromEngine.setId(jobInstance.getId());

        jobInstanceHistoryDao.insert(jobHistoryFromEngine);
    }

    private void syncCompleteJobInfoToDb(@NonNull JobInstance jobInstance) {
        jobInstance.setEndTime(new Date());
        jobInstanceDao.update(jobInstance);
    }

    private void relationJobInstanceAndJobEngineId(
            @NonNull JobInstance jobInstance,
            @NonNull Integer userId,
            @NonNull String jobEngineId) {
        // relation jobInstanceId and jobEngineId
        if (StringUtils.isEmpty(jobInstance.getJobEngineId())) {
            jobInstance.setJobEngineId(jobEngineId);
            jobInstance.setUpdateUserId(userId);
            jobInstanceDao.update(jobInstance);
        }
    }

    private List<JobMetrics> getJobMetricsFromEngine(
            @NonNull JobInstance jobInstance, @NonNull String jobEngineId) {
        Engine engine = new Engine(jobInstance.getEngineName(), jobInstance.getEngineVersion());

        IEngineMetricsExtractor engineMetricsExtractor =
                (new EngineMetricsExtractorFactory(engine)).getEngineMetricsExtractor();

        return engineMetricsExtractor.getMetricsByJobEngineId(jobEngineId);
    }

    private List<JobPipelineSummaryMetricsRes> summaryMetrics(
            @NonNull List<JobMetrics> jobPipelineDetailedMetrics) {
        return jobPipelineDetailedMetrics.stream()
                .map(
                        metrics ->
                                new JobPipelineSummaryMetricsRes(
                                        metrics.getPipelineId(),
                                        metrics.getReadRowCount(),
                                        metrics.getWriteRowCount(),
                                        metrics.getStatus()))
                .collect(Collectors.toList());
    }

    private List<JobMetrics> getJobMetricsFromDb(
            @NonNull JobInstance jobInstance,
            @NonNull Integer userId,
            @NonNull String jobEngineId) {

        // relation jobInstanceId and jobEngineId
        relationJobInstanceAndJobEngineId(jobInstance, userId, jobEngineId);

        // get metrics from db
        return jobMetricsDao.getByInstanceId(jobInstance.getId());
    }

    @Override
    public ImmutablePair<Long, String> getInstanceIdAndEngineId(@NonNull String key) {
        if (!key.contains(Constants.METRICS_QUERY_KEY_SPLIT)
                || key.split(Constants.METRICS_QUERY_KEY_SPLIT).length != 2) {
            throw new SeatunnelException(SeatunnelErrorEnum.JOB_METRICS_QUERY_KEY_ERROR, key);
        }

        String[] split = key.split(Constants.METRICS_QUERY_KEY_SPLIT);
        Long jobInstanceId = Long.valueOf(split[0]);
        String jobEngineId = split[1];
        return new ImmutablePair<>(jobInstanceId, jobEngineId);
    }

    private JobPipelineDetailMetricsRes wrapperJobMetrics(@NonNull JobMetrics metrics) {
        return new JobPipelineDetailMetricsRes(
                metrics.getId(),
                metrics.getPipelineId(),
                metrics.getReadRowCount(),
                metrics.getWriteRowCount(),
                metrics.getSourceTableNames(),
                metrics.getSinkTableNames(),
                metrics.getReadQps(),
                metrics.getWriteQps(),
                metrics.getRecordDelay(),
                metrics.getStatus());
    }

    private void syncMetricsToDbRunning(
            @NonNull JobInstance jobInstance,
            @NonNull Integer userId,
            @NonNull Map<Integer, JobMetrics> jobMetricsMap) {

        ArrayList<JobMetrics> list = new ArrayList<>();
        for (Map.Entry<Integer, JobMetrics> entry : jobMetricsMap.entrySet()) {
            JobMetrics jobMetrics = entry.getValue();
            jobMetrics.setId(CodeGenerateUtils.getInstance().genCode());
            jobMetrics.setJobInstanceId(jobInstance.getId());
            jobMetrics.setCreateUserId(userId);
            jobMetrics.setUpdateUserId(userId);
            list.add(jobMetrics);
        }
        if (!list.isEmpty()) {
            log.info("003list={}", list);
            jobMetricsDao.getJobMetricsMapper().insertBatchMetrics(list);
        }
    }
}
