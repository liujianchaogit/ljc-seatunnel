package com.ljc.seatunnel.thirdparty.metrics;

import com.ljc.seatunnel.dal.entity.JobInstanceHistory;
import com.ljc.seatunnel.dal.entity.JobMetrics;
import lombok.NonNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IEngineMetricsExtractor {

    List<JobMetrics> getMetricsByJobEngineId(@NonNull String jobEngineId);

    LinkedHashMap<Integer, String> getJobPipelineStatus(@NonNull String jobEngineId);

    JobInstanceHistory getJobHistoryById(String jobEngineId);

    /** contains finished, failed, canceled */
    boolean isJobEnd(@NonNull String jobEngineId);

    boolean isJobEndStatus(@NonNull String jobStatus);

    List<Map<String, String>> getClusterHealthMetrics();

    String getJobStatus(@NonNull String jobEngineId);

    /** Obtain all running task metrics in the engine cluster */
    Map<Long, HashMap<Integer, JobMetrics>> getAllRunningJobMetrics();

    Map<Integer, JobMetrics> getMetricsByJobEngineIdRTMap(@NonNull String jobEngineId);
}
