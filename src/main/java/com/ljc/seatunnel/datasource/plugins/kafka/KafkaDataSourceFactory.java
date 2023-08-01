package com.ljc.seatunnel.datasource.plugins.kafka;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.ljc.seatunnel.datasource.plugins.api.DataSourceChannel;
import com.ljc.seatunnel.datasource.plugins.api.DataSourceFactory;
import com.ljc.seatunnel.datasource.plugins.api.DataSourcePluginInfo;
import com.ljc.seatunnel.datasource.plugins.api.DatasourcePluginTypeEnum;

import java.util.Set;

@AutoService(DataSourceFactory.class)
public class KafkaDataSourceFactory implements DataSourceFactory {

    public static final String KAFKA_PLUGIN_NAME = "Kafka";
    public static final String KAFKA_PLUGIN_ICON = "kafka";
    public static final String KAFKA_PLUGIN_VERSION = "1.0.0";

    @Override
    public String factoryIdentifier() {
        return KAFKA_PLUGIN_NAME;
    }

    @Override
    public Set<DataSourcePluginInfo> supportedDataSources() {
        return Sets.newHashSet(
                DataSourcePluginInfo.builder()
                        .name(KAFKA_PLUGIN_NAME)
                        .icon(KAFKA_PLUGIN_ICON)
                        .version(KAFKA_PLUGIN_VERSION)
                        .supportVirtualTables(true)
                        .type(DatasourcePluginTypeEnum.NO_STRUCTURED.getCode())
                        .build());
    }

    @Override
    public DataSourceChannel createChannel() {
        return new KafkaDataSourceChannel();
    }
}
