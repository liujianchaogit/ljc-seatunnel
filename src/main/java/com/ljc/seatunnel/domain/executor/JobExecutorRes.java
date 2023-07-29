package com.ljc.seatunnel.domain.executor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobExecutorRes {

    private final Long jobInstanceId;

    private final String jobConfig;

    /** engine name Spark/Flink/SeaTunnel */
    private final String engine;

    /** The driver run mode, only spark use now, support 'client' and 'cluster' */
    private final String deployMode;

    /** The engine run mode, for SeaTunnel Engine only support 'local' and null */
    private final String master;

    private final String jobMode;
}
