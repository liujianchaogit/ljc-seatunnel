package com.ljc.seatunnel.datasource.plugins.pulsar;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.ljc.seatunnel.datasource.plugins.api.DataSourceChannel;
import com.ljc.seatunnel.datasource.plugins.api.DataSourceFactory;
import com.ljc.seatunnel.datasource.plugins.api.DataSourcePluginInfo;
import com.ljc.seatunnel.datasource.plugins.api.DatasourcePluginTypeEnum;

import java.util.Set;

@AutoService(DataSourceFactory.class)
public class PulsarDataSourceFactory implements DataSourceFactory {

    public static final String PULSAR_PLUGIN_NAME = "Pulsar";
    public static final String PULSAR_PLUGIN_ICON = "pulsar";
    public static final String PULSAR_PLUGIN_VERSION = "1.0.0";

    @Override
    public String factoryIdentifier() {
        return PULSAR_PLUGIN_NAME;
    }

    @Override
    public Set<DataSourcePluginInfo> supportedDataSources() {
        return Sets.newHashSet(
                DataSourcePluginInfo.builder()
                        .name(PULSAR_PLUGIN_NAME)
                        .icon(PULSAR_PLUGIN_ICON)
                        .version(PULSAR_PLUGIN_VERSION)
                        .supportVirtualTables(true)
                        .type(DatasourcePluginTypeEnum.NO_STRUCTURED.getCode())
                        .build());
    }

    @Override
    public DataSourceChannel createChannel() {
        return new PulsarDataSourceChannel();
    }
}
