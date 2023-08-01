package com.ljc.seatunnel.service;

import com.ljc.seatunnel.datasource.plugins.api.DataSourcePluginInfo;
import com.ljc.seatunnel.datasource.plugins.model.TableField;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.response.datasource.DatasourceDetailRes;
import com.ljc.seatunnel.domain.response.datasource.DatasourceRes;
import org.apache.seatunnel.api.configuration.util.OptionRule;

import java.util.List;
import java.util.Map;

public interface IDatasourceService {

    boolean testDatasourceConnectionAble(
            Integer userId,
            String pluginName,
            String pluginVersion,
            Map<String, String> datasourceConfig);

    PageInfo<DatasourceRes> queryDatasourceList(Integer userId, String searchVal, String pluginName, Integer pageNo, Integer pageSize);

    DatasourceDetailRes queryDatasourceDetailById(Integer userId, String datasourceId);

    DatasourceDetailRes queryDatasourceDetailById(String datasourceId);

    Map<String, String> queryDatasourceConfigById(String datasourceId);

    List<String> queryDatabaseByDatasourceName(String datasourceName);

    List<String> queryTableNames(String datasourceName, String databaseName);

    Map<String, String> queryDatasourceNameByPluginName(String pluginName);

    OptionRule queryOptionRuleByPluginName(String pluginName);

    OptionRule queryVirtualTableOptionRuleByPluginName(String pluginName);

    List<TableField> queryTableSchema(String datasourceName, String databaseName, String tableName);

    Map<Integer, List<DataSourcePluginInfo>> queryAllDatasourcesGroupByType(
            Boolean onlyShowVirtualDataSource);

    String getDynamicForm(String pluginName);

}