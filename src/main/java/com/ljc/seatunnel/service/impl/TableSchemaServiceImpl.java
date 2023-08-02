package com.ljc.seatunnel.service.impl;

import com.ljc.seatunnel.bean.connector.ConnectorCache;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.config.ConnectorDataSourceMapperConfig;
import com.ljc.seatunnel.datasource.plugins.model.TableField;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.service.IDatasourceService;
import com.ljc.seatunnel.service.ITableSchemaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.seatunnel.api.table.catalog.DataTypeConvertor;
import org.apache.seatunnel.api.table.factory.DataTypeConvertorFactory;
import org.apache.seatunnel.api.table.type.SeaTunnelDataType;
import org.apache.seatunnel.common.config.Common;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.common.utils.FileUtils;
import org.apache.seatunnel.plugin.discovery.PluginIdentifier;
import org.apache.seatunnel.plugin.discovery.seatunnel.SeaTunnelSinkPluginDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TableSchemaServiceImpl implements ITableSchemaService {

    @Autowired
    private IDatasourceService dataSourceService;
    @Autowired
    private ConnectorDataSourceMapperConfig connectorDataSourceMapperConfig;
    @Autowired
    private ConnectorCache connectorCache;

    private final DataTypeConvertorFactory factory;

    public TableSchemaServiceImpl() throws IOException {
        Common.setStarter(true);
        Path path = new SeaTunnelSinkPluginDiscovery().getPluginDir();
        if (path.toFile().exists()) {
            List<URL> files = FileUtils.searchJarFiles(path);
            files.addAll(FileUtils.searchJarFiles(Common.pluginRootDir()));
            factory = new DataTypeConvertorFactory(new URLClassLoader(files.toArray(new URL[0])));
        } else {
            factory = new DataTypeConvertorFactory();
        }
    }

    @Override
    public DataSourceOption checkDatabaseAndTable(String datasourceId, DataSourceOption dataSourceOption) {
        List<String> notExistDatabases = new ArrayList<>();
        String datasourceName =
                dataSourceService.queryDatasourceDetailById(datasourceId).getDatasourceName();
        if (dataSourceOption.getDatabases() != null) {
            List<String> databases =
                    dataSourceService.queryDatabaseByDatasourceName(datasourceName);
            notExistDatabases.addAll(
                    dataSourceOption.getDatabases().stream()
                            .filter(database -> !databases.contains(database))
                            .collect(Collectors.toList()));
        }
        Map<String, Set<String>> tables = new HashMap<>();
        if (dataSourceOption.getTables() != null) {
            List<String> notExistTables = new ArrayList<>();
            dataSourceOption
                    .getTables()
                    .forEach(
                            tableStr -> {
                                String database;
                                String table;
                                //                                if (tableStr.contains(".")) {
                                //                                    String[] split =
                                // tableStr.split("\\.");
                                //                                    database = split[0];
                                //                                    table = split[1];
                                //                                } else {
                                database = dataSourceOption.getDatabases().get(0);
                                table = tableStr;
                                //                                }
                                if (!tables.containsKey(database)) {
                                    if (notExistDatabases.contains(database)) {
                                        notExistTables.add(tableStr);
                                        return;
                                    } else {
                                        tables.put(
                                                database,
                                                new HashSet<>(
                                                        dataSourceService.queryTableNames(
                                                                datasourceName, database)));
                                    }
                                }
                                if (!tables.get(database).contains(table)) {
                                    notExistTables.add(tableStr);
                                }
                            });
            return new DataSourceOption(notExistDatabases, notExistTables);
        }
        return new DataSourceOption(notExistDatabases, new ArrayList<>());
    }

    @Override
    public void getAddSeaTunnelSchema(List<TableField> tableFields, String pluginName) {
        pluginName = pluginName.toUpperCase();
        if (pluginName.endsWith("-CDC")) {
            pluginName = pluginName.replace("-CDC", "");
        } else if (pluginName.startsWith("JDBC_")) {
            pluginName = pluginName.replace("JDBC_", "");
        } else if (pluginName.startsWith("JDBC-")) {
            pluginName = pluginName.replace("JDBC-", "");
        }
        DataTypeConvertor<?> convertor;
        try {
            convertor = factory.getDataTypeConvertor(pluginName);
        } catch (Exception e) {
            return;
        }
        for (TableField field : tableFields) {
            try {
                SeaTunnelDataType<?> dataType = convertor.toSeaTunnelType(field.getType());
                field.setUnSupport(false);
                field.setOutputDataType(dataType.toString());
            } catch (Exception exception) {
                field.setUnSupport(true);
                log.warn(
                        "Database {} , field {} is unSupport",
                        pluginName,
                        field.getType(),
                        exception);
            }
        }
    }

    @Override
    public boolean getColumnProjection(String pluginName) {
        String connector =
                connectorDataSourceMapperConfig
                        .findConnectorForDatasourceName(pluginName)
                        .orElseThrow(
                                () ->
                                        new SeatunnelException(
                                                SeatunnelErrorEnum.ILLEGAL_STATE,
                                                "Unsupported Data Source Name"));
        return connectorCache
                .getConnectorFeature(
                        PluginIdentifier.of("seatunnel", PluginType.SOURCE.getType(), connector))
                .isSupportColumnProjection();
    }
}
