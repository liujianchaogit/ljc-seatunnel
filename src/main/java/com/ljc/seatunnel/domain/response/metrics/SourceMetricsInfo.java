package com.ljc.seatunnel.domain.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceMetricsInfo {
    private long readRowCount;
}
