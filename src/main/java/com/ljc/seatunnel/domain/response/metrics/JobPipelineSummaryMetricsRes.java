package com.ljc.seatunnel.domain.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobPipelineSummaryMetricsRes {
    private long pipelineId;

    private long readRowCount;

    private long writeRowCount;

    private String status;
}
