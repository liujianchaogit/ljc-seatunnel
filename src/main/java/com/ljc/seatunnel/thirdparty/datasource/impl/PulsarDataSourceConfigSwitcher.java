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

import java.util.List;

import static com.ljc.seatunnel.datasource.plugins.pulsar.PulsarOptionRule.TOPIC;

public class PulsarDataSourceConfigSwitcher extends AbstractDataSourceConfigSwitcher {

    private static final PulsarDataSourceConfigSwitcher INSTANCE =
            new PulsarDataSourceConfigSwitcher();

    private static final String SCHEMA = "schema";
//    private static final String TOPIC = "topic";
//    private static final String TABLE = "table";
//    private static final String FORMAT = "format";
//
//    private static final String DEBEZIUM_FORMAT = "COMPATIBLE_DEBEZIUM_JSON";

    @Override
    public FormStructure filterOptionRule(
            String connectorName,
            OptionRule dataSourceOptionRule,
            OptionRule virtualTableOptionRule,
            BusinessMode businessMode,
            PluginType pluginType,
            OptionRule connectorOptionRule,
            List<String> excludedKeys) {
        if (pluginType == PluginType.SOURCE) {
            excludedKeys.add(SCHEMA);
//            excludedKeys.add(TOPIC);
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
        if (pluginType == PluginType.SOURCE) {
            // Use field to generate the schema
            connectorConfig =
                    connectorConfig.withValue(
                            TOPIC.key(),
                            ConfigValueFactory.fromAnyRef(
                                    virtualTableDetail.getDatasourceProperties().get(TOPIC.key())));
            connectorConfig =
                    connectorConfig.withValue(
                            SCHEMA,
                            KafkaKingbaseDataSourceConfigSwitcher.SchemaGenerator
                                    .generateSchemaBySelectTableFields(
                                            virtualTableDetail, selectTableFields)
                                    .root());
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

    private PulsarDataSourceConfigSwitcher() {}

    public static PulsarDataSourceConfigSwitcher getInstance() {
        return INSTANCE;
    }
}
