package com.ljc.seatunnel.thirdparty.datasource;

import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.domain.request.job.SelectTableFields;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.exception.UnSupportWrapperException;
import com.ljc.seatunnel.utils.SeaTunnelOptionRuleWrapper;
import org.apache.seatunnel.api.configuration.Option;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.api.configuration.util.RequiredOption;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigValue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractDataSourceConfigSwitcher implements DataSourceConfigSwitcher {
    @Override
    public FormStructure filterOptionRule(
            String connectorName,
            OptionRule dataSourceOptionRule,
            OptionRule virtualTableOptionRule,
            BusinessMode businessMode,
            PluginType pluginType,
            OptionRule connectorOptionRule,
            List<String> excludedKeys) {

        List<String> dataSourceRequiredAllKey =
                Stream.concat(
                                dataSourceOptionRule.getRequiredOptions().stream(),
                                virtualTableOptionRule.getRequiredOptions().stream())
                        .flatMap(ro -> ro.getOptions().stream().map(Option::key))
                        .collect(Collectors.toList());

        dataSourceRequiredAllKey.addAll(excludedKeys);

        List<RequiredOption> requiredOptions =
                connectorOptionRule.getRequiredOptions().stream()
                        .map(
                                requiredOption -> {
                                    if (requiredOption
                                            instanceof RequiredOption.AbsolutelyRequiredOptions) {
                                        RequiredOption.AbsolutelyRequiredOptions
                                                absolutelyRequiredOptions =
                                                        (RequiredOption.AbsolutelyRequiredOptions)
                                                                requiredOption;
                                        List<Option<?>> requiredOpList =
                                                absolutelyRequiredOptions.getOptions().stream()
                                                        .filter(
                                                                op -> {
                                                                    return !dataSourceRequiredAllKey
                                                                            .contains(op.key());
                                                                })
                                                        .collect(Collectors.toList());
                                        return requiredOpList.isEmpty()
                                                ? null
                                                : OptionRule.builder()
                                                        .required(
                                                                requiredOpList.toArray(
                                                                        new Option<?>[0]))
                                                        .build()
                                                        .getRequiredOptions()
                                                        .get(0);
                                    }

                                    if (requiredOption
                                            instanceof RequiredOption.BundledRequiredOptions) {
                                        List<Option<?>> bundledRequiredOptions =
                                                requiredOption.getOptions();
                                        return bundledRequiredOptions.stream()
                                                        .anyMatch(
                                                                op ->
                                                                        dataSourceRequiredAllKey
                                                                                .contains(op.key()))
                                                ? null
                                                : requiredOption;
                                    }

                                    if (requiredOption
                                            instanceof RequiredOption.ExclusiveRequiredOptions) {
                                        List<Option<?>> exclusiveOptions =
                                                requiredOption.getOptions();
                                        return exclusiveOptions.stream()
                                                        .anyMatch(
                                                                op ->
                                                                        dataSourceRequiredAllKey
                                                                                .contains(op.key()))
                                                ? null
                                                : requiredOption;
                                    }

                                    if (requiredOption
                                            instanceof RequiredOption.ConditionalRequiredOptions) {
                                        List<Option<?>> conditionalRequiredOptions =
                                                requiredOption.getOptions();
                                        return conditionalRequiredOptions.stream()
                                                        .anyMatch(
                                                                op ->
                                                                        dataSourceRequiredAllKey
                                                                                .contains(op.key()))
                                                ? null
                                                : requiredOption;
                                    }

                                    throw new UnSupportWrapperException(
                                            connectorName, "Unknown", requiredOption.toString());
                                })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        List<String> dataSourceOptionAllKey =
                Stream.concat(
                                dataSourceOptionRule.getOptionalOptions().stream(),
                                virtualTableOptionRule.getOptionalOptions().stream())
                        .map(Option::key)
                        .collect(Collectors.toList());

        dataSourceOptionAllKey.addAll(excludedKeys);

        List<Option<?>> optionList =
                connectorOptionRule.getOptionalOptions().stream()
                        .filter(option -> !dataSourceOptionAllKey.contains(option.key()))
                        .collect(Collectors.toList());

        return SeaTunnelOptionRuleWrapper.wrapper(
                optionList, requiredOptions, connectorName, pluginType);
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

        Config mergedConfig = connectorConfig;

        for (Map.Entry<String, ConfigValue> en : dataSourceInstanceConfig.entrySet()) {
            mergedConfig = mergedConfig.withValue(en.getKey(), en.getValue());
        }

        return mergedConfig;
    }
}
