package com.ljc.seatunnel.domain.request.datasource;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class VirtualTableReq {

    private String datasourceId;

    private String databaseName;

    private String tableName;

    private String description;

    private Map<String, String> databaseProperties;

    private List<VirtualTableFieldReq> tableFields;
}
