package com.ljc.seatunnel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.config.ConnectorDataSourceMapperConfig;
import com.ljc.seatunnel.dal.dao.IDatasourceDao;
import com.ljc.seatunnel.dal.dao.IVirtualTableDao;
import com.ljc.seatunnel.dal.entity.Datasource;
import com.ljc.seatunnel.dal.entity.VirtualTable;
import com.ljc.seatunnel.datasource.plugins.api.DataSourcePluginInfo;
import com.ljc.seatunnel.datasource.plugins.api.DatasourcePluginTypeEnum;
import com.ljc.seatunnel.datasource.plugins.model.TableField;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.response.datasource.DatasourceDetailRes;
import com.ljc.seatunnel.domain.response.datasource.DatasourceRes;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableFieldRes;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.service.IDatasourceService;
import com.ljc.seatunnel.service.ITableSchemaService;
import com.ljc.seatunnel.thirdparty.datasource.DataSourceClientFactory;
import com.ljc.seatunnel.utils.SeaTunnelOptionRuleWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasourceServiceImpl implements IDatasourceService {

    private static final String VIRTUAL_TABLE_DATABASE_NAME = "default";
    @Autowired
    private IDatasourceDao datasourceDao;
    @Autowired
    private IVirtualTableDao virtualTableDao;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired private ConnectorDataSourceMapperConfig dataSourceMapperConfig;

    protected static final String DEFAULT_DATASOURCE_PLUGIN_VERSION = "1.0.0";


    @Override
    public boolean testDatasourceConnectionAble(Integer userId, String pluginName, String pluginVersion, Map<String, String> datasourceConfig) {
        return DataSourceClientFactory.getDataSourceClient()
                .checkDataSourceConnectivity(pluginName, datasourceConfig);
    }

    @Override
    public PageInfo<DatasourceRes> queryDatasourceList(Integer userId, String searchVal, String pluginName, Integer pageNo, Integer pageSize) {
        Page<Datasource> page = new Page<>(pageNo, pageSize);
        PageInfo<DatasourceRes> pageInfo = new PageInfo<>();
//        List<Long> ids =
//                availableResourceRange(
//                        SeatunnelResourcePermissionModuleEnum.DATASOURCE.name(), userId);
//        if (org.springframework.util.CollectionUtils.isEmpty(ids)) {
//            return pageInfo;
//        }
        IPage<Datasource> datasourcePage =
                datasourceDao.selectDatasourceByParam(page, null, searchVal, pluginName);
        pageInfo = new PageInfo<>();
        pageInfo.setPageNo((int) datasourcePage.getPages());
        pageInfo.setPageSize((int) datasourcePage.getSize());
        pageInfo.setTotalCount((int) datasourcePage.getTotal());
        if (CollectionUtils.isEmpty(datasourcePage.getRecords())) {
            pageInfo.setData(new ArrayList<>());
            return pageInfo;
        }
        List<Integer> userIds = new ArrayList<>();
        datasourcePage
                .getRecords()
                .forEach(
                        datasource -> {
                            userIds.add(datasource.getCreateUserId());
                            userIds.add(datasource.getUpdateUserId());
                        });

        List<DatasourceRes> datasourceResList =
                datasourcePage.getRecords().stream()
                        .map(
                                datasource -> {
                                    DatasourceRes datasourceRes = new DatasourceRes();
                                    datasourceRes.setId(datasource.getId().toString());
                                    datasourceRes.setDatasourceName(datasource.getDatasourceName());
                                    datasourceRes.setPluginName(datasource.getPluginName());
                                    datasourceRes.setPluginVersion(datasource.getPluginVersion());
                                    datasourceRes.setDescription(datasource.getDescription());
                                    datasourceRes.setCreateTime(datasource.getCreateTime());
                                    datasourceRes.setUpdateTime(datasource.getUpdateTime());
                                    Map<String, String> datasourceConfig =
                                            JsonUtils.toMap(
                                                    datasource.getDatasourceConfig(),
                                                    String.class,
                                                    String.class);
                                    datasourceRes.setDatasourceConfig(datasourceConfig);
                                    datasourceRes.setCreateUserId(datasource.getCreateUserId());
                                    datasourceRes.setUpdateUserId(datasource.getUpdateUserId());
                                    datasourceRes.setUpdateTime(datasource.getUpdateTime());
                                    return datasourceRes;
                                })
                        .collect(Collectors.toList());

        pageInfo.setData(datasourceResList);
        return pageInfo;
    }

    @Override
    public DatasourceDetailRes queryDatasourceDetailById(Integer userId, String datasourceId) {
        return this.queryDatasourceDetailById(datasourceId);
    }

    @Override
    public DatasourceDetailRes queryDatasourceDetailById(String datasourceId) {
        long datasourceIdLong = Long.parseLong(datasourceId);
        Datasource datasource = datasourceDao.selectDatasourceById(datasourceIdLong);
        if (null == datasource) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_FOUND, datasourceId);
        }
        DatasourceDetailRes datasourceDetailRes = new DatasourceDetailRes();
        datasourceDetailRes.setId(datasource.getId().toString());
        datasourceDetailRes.setDatasourceName(datasource.getDatasourceName());
        datasourceDetailRes.setPluginName(datasource.getPluginName());
        datasourceDetailRes.setPluginVersion(datasource.getPluginVersion());
        datasourceDetailRes.setDescription(datasource.getDescription());
        datasourceDetailRes.setCreateTime(datasource.getCreateTime());
        datasourceDetailRes.setUpdateTime(datasource.getUpdateTime());

        Map<String, String> datasourceConfig =
                JsonUtils.toMap(datasource.getDatasourceConfig(), String.class, String.class);
        // convert option rule
        datasourceDetailRes.setDatasourceConfig(datasourceConfig);

        return datasourceDetailRes;
    }

    @Override
    public Map<String, String> queryDatasourceConfigById(String datasourceId) {
        long datasourceIdLong = Long.parseLong(datasourceId);
        Datasource datasource = datasourceDao.selectDatasourceById(datasourceIdLong);
        if (null == datasource) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_FOUND, datasourceId);
        }
        String configJson = datasource.getDatasourceConfig();
        return JsonUtils.toMap(configJson, String.class, String.class);
    }

    @Override
    public List<String> queryDatabaseByDatasourceName(String datasourceName) {
        Datasource datasource = datasourceDao.queryDatasourceByName(datasourceName);
        if (null == datasource) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_FOUND, datasourceName);
        }
        String pluginName = datasource.getPluginName();
        if (Boolean.FALSE.equals(checkIsSupportVirtualTable(pluginName))) {
            String config = datasource.getDatasourceConfig();
            Map<String, String> datasourceConfig =
                    JsonUtils.toMap(config, String.class, String.class);

            return DataSourceClientFactory.getDataSourceClient()
                    .getDatabases(pluginName, datasourceConfig);
        }
        long dataSourceId = datasource.getId();
        boolean hasVirtualTable = virtualTableDao.checkHasVirtualTable(dataSourceId);
        if (hasVirtualTable) {
            return Collections.singletonList(VIRTUAL_TABLE_DATABASE_NAME);
        }
        return new ArrayList<>();
    }

    private boolean checkIsSupportVirtualTable(String pluginName) {
        return DataSourceClientFactory.getDataSourceClient().listAllDataSources().stream()
                .anyMatch(d -> d.getName().equals(pluginName) && d.getSupportVirtualTables());
    }

    @Override
    public List<String> queryTableNames(String datasourceName, String databaseName) {
        Datasource datasource = datasourceDao.queryDatasourceByName(datasourceName);
        if (null == datasource) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_FOUND, datasourceName);
        }
        String config = datasource.getDatasourceConfig();
        Map<String, String> datasourceConfig = JsonUtils.toMap(config, String.class, String.class);
        Map<String, String> options = new HashMap<>();
        String pluginName = datasource.getPluginName();
        if (BooleanUtils.isNotTrue(checkIsSupportVirtualTable(pluginName))) {
            return DataSourceClientFactory.getDataSourceClient()
                    .getTables(pluginName, databaseName, datasourceConfig, options);
        }
        long dataSourceId = datasource.getId();
        return virtualTableDao.getVirtualTableNames(VIRTUAL_TABLE_DATABASE_NAME, dataSourceId);
    }

    @Override
    public Map<String, String> queryDatasourceNameByPluginName(String pluginName) {
        Map<String, String> datasourceNameMap = new HashMap<>();
        List<Datasource> datasourceList =
                datasourceDao.selectDatasourceByPluginName(
                        pluginName, DEFAULT_DATASOURCE_PLUGIN_VERSION);
        datasourceList.forEach(
                datasource ->
                        datasourceNameMap.put(
                                datasource.getId().toString(), datasource.getDatasourceName()));
        return datasourceNameMap;
    }

    @Override
    public OptionRule queryOptionRuleByPluginName(String pluginName) {
        return DataSourceClientFactory.getDataSourceClient().queryDataSourceFieldByName(pluginName);
    }

    @Override
    public OptionRule queryVirtualTableOptionRuleByPluginName(String pluginName) {
        if (checkIsSupportVirtualTable(pluginName)) {
            return DataSourceClientFactory.getDataSourceClient()
                    .queryMetadataFieldByName(pluginName);
        }
        return OptionRule.builder().build();
    }

    @Override
    public List<TableField> queryTableSchema(String datasourceName, String databaseName, String tableName) {
        Datasource datasource = datasourceDao.queryDatasourceByName(datasourceName);
        if (null == datasource) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_FOUND, datasourceName);
        }
        String config = datasource.getDatasourceConfig();
        Map<String, String> datasourceConfig = JsonUtils.toMap(config, String.class, String.class);
        String pluginName = datasource.getPluginName();
        ITableSchemaService tableSchemaService =
                (ITableSchemaService) applicationContext.getBean("tableSchemaServiceImpl");
        if (BooleanUtils.isNotTrue(checkIsSupportVirtualTable(pluginName))) {
            List<TableField> tableFields =
                    DataSourceClientFactory.getDataSourceClient()
                            .getTableFields(pluginName, datasourceConfig, databaseName, tableName);

            tableSchemaService.getAddSeaTunnelSchema(tableFields, pluginName);
            return tableFields;
        }
        VirtualTable virtualTable = virtualTableDao.selectVirtualTableByTableName(tableName);
        if (virtualTable == null) {
            throw new SeatunnelException(SeatunnelErrorEnum.VIRTUAL_TABLE_NOT_FOUND, tableName);
        }

        // convert virtual table to table field
        // virtualTable.getTableFields()
        List<TableField> tableFields = convertTableSchema(virtualTable.getTableFields());
        tableSchemaService.getAddSeaTunnelSchema(tableFields, pluginName);
        return tableFields;
    }

    @Override
    public Map<Integer, List<DataSourcePluginInfo>> queryAllDatasourcesGroupByType(Boolean onlyShowVirtualDatasource) {
        Map<Integer, List<DataSourcePluginInfo>> dataSourcePluginInfoMap = new HashMap<>();
        for (DatasourcePluginTypeEnum value : DatasourcePluginTypeEnum.values()) {
            dataSourcePluginInfoMap.put(value.getCode(), new ArrayList<>());
        }

        List<DataSourcePluginInfo> dataSourcePluginInfos =
                DataSourceClientFactory.getDataSourceClient().listAllDataSources();
        for (DataSourcePluginInfo dataSourcePluginInfo : dataSourcePluginInfos) {
            // query datasource types
            if (BooleanUtils.isNotTrue(onlyShowVirtualDatasource)) {
                List<DataSourcePluginInfo> dataSourcePluginInfoList =
                        dataSourcePluginInfoMap.computeIfAbsent(
                                dataSourcePluginInfo.getType(), k -> new ArrayList<>());
                dataSourcePluginInfoList.add(dataSourcePluginInfo);
                continue;
            }

            if (Boolean.TRUE.equals(dataSourcePluginInfo.getSupportVirtualTables())) {
                List<DataSourcePluginInfo> dataSourcePluginInfoList =
                        dataSourcePluginInfoMap.computeIfAbsent(
                                dataSourcePluginInfo.getType(), k -> new ArrayList<>());
                dataSourcePluginInfoList.add(dataSourcePluginInfo);
            }
        }
        return dataSourcePluginInfoMap;
    }

    @Override
    public String  getDynamicForm(String pluginName) {
        OptionRule optionRule =
                DataSourceClientFactory.getDataSourceClient()
                        .queryDataSourceFieldByName(pluginName);
        // If the plugin doesn't have connector will directly use pluginName
        String connectorForDatasourceName =
                dataSourceMapperConfig
                        .findConnectorForDatasourceName(pluginName)
                        .orElse(pluginName);
        FormStructure testForm =
                SeaTunnelOptionRuleWrapper.wrapper(optionRule, connectorForDatasourceName);
        return JsonUtils.toJsonString(testForm);
    }

    private List<TableField> convertTableSchema(String virtualTableFieldJson) {
        List<TableField> fields = new ArrayList<>();
        List<VirtualTableFieldRes> virtualTableFields =
                JsonUtils.toList(virtualTableFieldJson, VirtualTableFieldRes.class);
        if (CollectionUtils.isEmpty(virtualTableFields)) {
            return fields;
        }
        virtualTableFields.forEach(
                virtualTableField -> {
                    TableField tableField = new TableField();
                    tableField.setPrimaryKey(virtualTableField.getPrimaryKey());
                    tableField.setName(virtualTableField.getFieldName());
                    tableField.setType(virtualTableField.getFieldType());
                    tableField.setComment(virtualTableField.getFieldComment());
                    tableField.setNullable(virtualTableField.getNullable());
                    tableField.setDefaultValue(virtualTableField.getDefaultValue());
                    fields.add(tableField);
                });
        return fields;
    }
}
