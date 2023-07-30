package com.ljc.seatunnel.thirdparty.datasource.impl;

import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.domain.request.job.SelectTableFields;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableFieldRes;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.datasource.AbstractDataSourceConfigSwitcher;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigFactory;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ljc.seatunnel.domain.request.connector.BusinessMode.DATA_INTEGRATION;
import static com.ljc.seatunnel.domain.request.connector.BusinessMode.DATA_REPLICA;

public class PostgresCDCDataSourceConfigSwitcher extends AbstractDataSourceConfigSwitcher {

    private int three = 3;
    private int two = 2;

    private PostgresCDCDataSourceConfigSwitcher() {}

    public static final PostgresCDCDataSourceConfigSwitcher INSTANCE =
            new PostgresCDCDataSourceConfigSwitcher();

    private static final String FACTORY = "factory";

    private static final String CATALOG = "catalog";

    private static final String TABLE_NAMES = "table-names";

    private static final String DATABASE_NAMES = "database-names";

    private static final String FORMAT_KEY = "format";

    private static final String DEBEZIUM_FORMAT = "COMPATIBLE_DEBEZIUM_JSON";

    private static final String DEFAULT_FORMAT = "DEFAULT";

    private static final String SCHEMA = "schema";

    @Override
    public FormStructure filterOptionRule(
            String connectorName,
            OptionRule dataSourceOptionRule,
            OptionRule virtualTableOptionRule,
            BusinessMode businessMode,
            PluginType pluginType,
            OptionRule connectorOptionRule,
            List<String> excludedKeys) {
        if (PluginType.SOURCE.equals(pluginType)) {
            excludedKeys.add(DATABASE_NAMES);
            excludedKeys.add(TABLE_NAMES);
            if (businessMode.equals(DATA_INTEGRATION)) {
                excludedKeys.add(FORMAT_KEY);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported plugin type: " + pluginType);
        }
        return super.filterOptionRule(
                connectorName,
                dataSourceOptionRule,
                virtualTableOptionRule,
                businessMode,
                pluginType,
                connectorOptionRule,
                excludedKeys);
    }

    @Override
    public Config mergeDatasourceConfig(
            Config dataSourceInstanceConfig,
            VirtualTableDetailRes virtualTableDetail,
            DataSourceOption dataSourceOption,
            SelectTableFields selectTableFields,
            BusinessMode businessMode,
            PluginType pluginType,
            Config connectorConfig) {
        if (PluginType.SOURCE.equals(pluginType)) {
            // Add table-names
            Config config = ConfigFactory.empty();
            config = config.withValue(FACTORY, ConfigValueFactory.fromAnyRef("Postgres"));
            connectorConfig = connectorConfig.withValue(CATALOG, config.root());
            connectorConfig =
                    connectorConfig.withValue(
                            DATABASE_NAMES,
                            ConfigValueFactory.fromIterable(dataSourceOption.getDatabases()));
            connectorConfig =
                    connectorConfig.withValue(
                            TABLE_NAMES,
                            ConfigValueFactory.fromIterable(
                                    mergeDatabaseAndTables(dataSourceOption)));

            if (businessMode.equals(DATA_INTEGRATION)) {
                connectorConfig =
                        connectorConfig.withValue(
                                FORMAT_KEY, ConfigValueFactory.fromAnyRef(DEFAULT_FORMAT));
            } else if (businessMode.equals(DATA_REPLICA)
                    && connectorConfig
                            .getString(FORMAT_KEY)
                            .toUpperCase(Locale.ROOT)
                            .equals(DEBEZIUM_FORMAT)) {
                connectorConfig =
                        connectorConfig.withValue(SCHEMA, generateDebeziumFormatSchema().root());
            }
        } else {
            throw new UnsupportedOperationException("Unsupported plugin type: " + pluginType);
        }
        return super.mergeDatasourceConfig(
                dataSourceInstanceConfig,
                virtualTableDetail,
                dataSourceOption,
                selectTableFields,
                businessMode,
                pluginType,
                connectorConfig);
    }

    private Config generateDebeziumFormatSchema() {
        List<VirtualTableFieldRes> fieldResList = new ArrayList<>();
        fieldResList.add(new VirtualTableFieldRes("topic", "string", false, null, false, "", ""));
        fieldResList.add(new VirtualTableFieldRes("key", "string", false, null, false, "", ""));
        fieldResList.add(new VirtualTableFieldRes("value", "string", false, null, false, "", ""));

        Config schema = ConfigFactory.empty();
        for (VirtualTableFieldRes virtualTableFieldRes : fieldResList) {
            schema =
                    schema.withValue(
                            virtualTableFieldRes.getFieldName(),
                            ConfigValueFactory.fromAnyRef(virtualTableFieldRes.getFieldType()));
        }
        return schema.atKey("fields");
    }

    private List<String> mergeDatabaseAndTables(DataSourceOption dataSourceOption) {
        List<String> tables = new ArrayList<>();
        dataSourceOption
                .getDatabases()
                .forEach(
                        database -> {
                            dataSourceOption
                                    .getTables()
                                    .forEach(
                                            table -> {
                                                final String[] tableFragments = table.split("\\.");
                                                if (tableFragments.length == three) {
                                                    tables.add(table);
                                                } else if (tableFragments.length == two) {
                                                    tables.add(
                                                            getDatabaseAndTable(database, table));
                                                } else {
                                                    throw new IllegalArgumentException(
                                                            "Illegal postgres table-name: "
                                                                    + table);
                                                }
                                            });
                        });
        return tables;
    }

    private String getDatabaseAndTable(String database, String table) {
        return String.format("%s.%s", database, table);
    }
}
