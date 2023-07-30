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

public class ElasticSearchDataSourceConfigSwitcher extends AbstractDataSourceConfigSwitcher {

    public static final ElasticSearchDataSourceConfigSwitcher INSTANCE =
            new ElasticSearchDataSourceConfigSwitcher();

    private static final String SOURCE = "source";
    private static final String SCHEMA = "schema";
    private static final String PRIMARY_KEYS = "primary_keys";
    private static final String INDEX = "index";

    private ElasticSearchDataSourceConfigSwitcher() {}

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
            // DELETE source/schema
            excludedKeys.addAll(Arrays.asList(SOURCE, SCHEMA, INDEX));
        } else if (PluginType.SINK.equals(pluginType)) {
            excludedKeys.add(INDEX);
            // DELETE primary_keys
            if (businessMode.equals(BusinessMode.DATA_REPLICA)) {
                excludedKeys.add(PRIMARY_KEYS);
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
            if (businessMode.equals(BusinessMode.DATA_INTEGRATION)) {
                // Add source
                connectorConfig =
                        connectorConfig.withValue(
                                INDEX,
                                ConfigValueFactory.fromAnyRef(dataSourceOption.getTables().get(0)));
                connectorConfig =
                        connectorConfig.withValue(
                                SOURCE,
                                ConfigValueFactory.fromIterable(
                                        selectTableFields.getTableFields()));
            } else {
                throw new UnsupportedOperationException(
                        "Unsupported business mode: " + businessMode);
            }
        } else if (PluginType.SINK.equals(pluginType)) {
            // TODO Add primary_keys
            if (businessMode.equals(BusinessMode.DATA_INTEGRATION)) {
                // Add Index
                connectorConfig =
                        connectorConfig.withValue(
                                INDEX,
                                ConfigValueFactory.fromAnyRef(dataSourceOption.getTables().get(0)));
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
}
