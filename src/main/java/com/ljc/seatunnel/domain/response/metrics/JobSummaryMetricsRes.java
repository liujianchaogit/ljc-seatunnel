package com.ljc.seatunnel.domain.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobSummaryMetricsRes {
    private long jobInstanceId;

    private long jobEngineId;

    private long readRowCount;

    private long writeRowCount;

    private String status;
}
