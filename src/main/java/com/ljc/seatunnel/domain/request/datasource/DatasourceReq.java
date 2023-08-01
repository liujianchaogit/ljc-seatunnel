package com.ljc.seatunnel.domain.request.datasource;

import lombok.Data;

@Data
public class DatasourceReq {
    private String datasourceName;
    private String pluginName;
    private String description;
    private String datasourceConfig;
}
