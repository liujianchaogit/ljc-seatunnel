package com.ljc.seatunnel.datasource.plugins.api;

import java.util.Set;

public interface DataSourceFactory {

    String factoryIdentifier();

    Set<DataSourcePluginInfo> supportedDataSources();

    DataSourceChannel createChannel();
}
