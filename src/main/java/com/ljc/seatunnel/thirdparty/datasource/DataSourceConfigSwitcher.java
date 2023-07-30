package com.ljc.seatunnel.thirdparty.datasource;

import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.domain.request.job.SelectTableFields;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

import java.util.List;

public interface DataSourceConfigSwitcher {

    /**
     * OptionRule OptionRule
     *
     * @param connectorName connectorName
     * @param dataSourceOptionRule OptionRule
     * @param virtualTableOptionRule OptionRule
     * @param businessMode businessMode
     * @param pluginType Source or Sink
     * @param connectorOptionRule OptionRule
     * @param excludedKeys excludedKeys
     * @return form
     */
    FormStructure filterOptionRule(
            String connectorName,
            OptionRule dataSourceOptionRule,
            OptionRule virtualTableOptionRule,
            BusinessMode businessMode,
            PluginType pluginType,
            OptionRule connectorOptionRule,
            List<String> excludedKeys);

    /**
     * mergeDatasourceConfig
     *
     * @param dataSourceInstanceConfig dataSourceInstanceConfig
     * @param virtualTableDetail virtualTableDetail
     * @param dataSourceOption dataSourceOption
     * @param selectTableFields selectTableFields
     * @param businessMode businessMode
     * @param pluginType source or sink
     * @param connectorConfig connectorConfig
     * @return config
     */
    Config mergeDatasourceConfig(
            Config dataSourceInstanceConfig,
            VirtualTableDetailRes virtualTableDetail,
            DataSourceOption dataSourceOption,
            SelectTableFields selectTableFields,
            BusinessMode businessMode,
            PluginType pluginType,
            Config connectorConfig);
}
