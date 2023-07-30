package com.ljc.seatunnel.datasource.plugins.jdbc.mysql;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.ljc.seatunnel.datasource.plugins.api.DataSourceChannel;
import com.ljc.seatunnel.datasource.plugins.api.DataSourceFactory;
import com.ljc.seatunnel.datasource.plugins.api.DataSourcePluginInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@AutoService(DataSourceFactory.class)
public class MysqlJdbcDataSourceFactory implements DataSourceFactory {

    @Override
    public String factoryIdentifier() {
        return MysqlDataSourceConfig.PLUGIN_NAME;
    }

    @Override
    public Set<DataSourcePluginInfo> supportedDataSources() {
        return Sets.newHashSet(MysqlDataSourceConfig.MYSQL_DATASOURCE_PLUGIN_INFO);
    }

    @Override
    public DataSourceChannel createChannel() {
        return new MysqlJdbcDataSourceChannel();
    }
}
