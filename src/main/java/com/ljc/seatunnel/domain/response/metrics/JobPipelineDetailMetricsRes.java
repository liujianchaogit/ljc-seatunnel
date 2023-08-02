package com.ljc.seatunnel.domain.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobPipelineDetailMetricsRes {

    private Long id;

    private Integer pipelineId;

    private long readRowCount;

    private long writeRowCount;

    private String sourceTableNames;

    private String sinkTableNames;

    private long readQps;

    private long writeQps;

    private long recordDelay;

    private String status;
}
