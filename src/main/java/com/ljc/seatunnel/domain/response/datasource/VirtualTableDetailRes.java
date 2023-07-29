package com.ljc.seatunnel.domain.response.datasource;

import com.ljc.seatunnel.domain.response.BaseInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class VirtualTableDetailRes extends BaseInfo {

    private String tableId;

    private String datasourceId;

    private String datasourceName;

    private String databaseName;

    private String pluginName;

    private String tableName;

    private String description;

    private List<VirtualTableFieldRes> fields;

    private Map<String, String> datasourceProperties;
}
