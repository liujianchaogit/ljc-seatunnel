package com.ljc.seatunnel.domain.response.connector;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataSourceInstance {

    private DataSourceInfo dataSourceInfo;

    private String dataSourceInstanceName;

    private Long dataSourceInstanceId;
}
