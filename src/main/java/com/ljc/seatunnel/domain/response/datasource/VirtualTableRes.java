package com.ljc.seatunnel.domain.response.datasource;

import com.ljc.seatunnel.domain.response.BaseInfo;
import lombok.Data;

@Data
public class VirtualTableRes extends BaseInfo {

    private String tableId;

    private String datasourceId;

    private String datasourceName;

    private String databaseName;

    private String tableName;

    private String description;

    private String pluginName;

    private int createUserId;

    private int updateUserId;
}
