package com.ljc.seatunnel.thirdparty.datasource.impl;

import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.domain.request.job.SelectTableFields;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.datasource.AbstractDataSourceConfigSwitcher;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigValueFactory;

import java.util.Arrays;
import java.util.List;

public class StarRocksDataSourceConfigSwitcher extends AbstractDataSourceConfigSwitcher {

    private static final String TABLE = "table";

    private static final String DATABASE = "database";

    private StarRocksDataSourceConfigSwitcher() {}

    public static final StarRocksDataSourceConfigSwitcher INSTANCE =
            new StarRocksDataSourceConfigSwitcher();

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
            throw new UnsupportedOperationException("Unsupported PluginType: " + pluginType);
        } else if (PluginType.SINK.equals(pluginType)) {
            excludedKeys.addAll(Arrays.asList(TABLE, DATABASE));
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
            throw new UnsupportedOperationException("Unsupported PluginType: " + pluginType);
        } else if (PluginType.SINK.equals(pluginType)) {
            if (businessMode.equals(BusinessMode.DATA_INTEGRATION)) {
                // Add Table
                connectorConfig =
                        connectorConfig.withValue(
                                TABLE,
                                ConfigValueFactory.fromAnyRef(dataSourceOption.getTables().get(0)));
            }
            connectorConfig =
                    connectorConfig.withValue(
                            DATABASE,
                            ConfigValueFactory.fromAnyRef(dataSourceOption.getDatabases().get(0)));
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
}
