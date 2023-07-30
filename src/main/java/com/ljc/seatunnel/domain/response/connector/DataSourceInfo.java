package com.ljc.seatunnel.domain.response.connector;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataSourceInfo {

    private ConnectorInfo connectorInfo;

    private String datasourceName;
}
