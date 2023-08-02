package com.ljc.seatunnel.domain.dto.job;

import com.ljc.seatunnel.dal.entity.JobInstance;
import lombok.Data;

@Data
public class SeaTunnelJobInstanceDto extends JobInstance {
    private String jobDefineName;

    private long readRowCount;

    private long writeRowCount;

    private Long runningTime;
}
