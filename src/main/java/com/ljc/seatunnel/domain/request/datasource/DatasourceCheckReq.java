package com.ljc.seatunnel.domain.request.datasource;

import lombok.Data;

import java.util.Map;

@Data
public class DatasourceCheckReq {
    private String pluginName;
    private Map<String, String> datasourceConfig;
}
