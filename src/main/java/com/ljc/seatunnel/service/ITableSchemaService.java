package com.ljc.seatunnel.service;

import com.ljc.seatunnel.datasource.plugins.model.TableField;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;

import java.util.List;

public interface ITableSchemaService {
    void getAddSeaTunnelSchema(List<TableField> tableFields, String pluginName);

    boolean getColumnProjection(String pluginName);

    DataSourceOption checkDatabaseAndTable(String datasourceId, DataSourceOption dataSourceOption);

}
