package com.ljc.seatunnel.datasource.plugins.pulsar;

import com.ljc.seatunnel.datasource.plugins.api.DataSourceChannel;
import com.ljc.seatunnel.datasource.plugins.model.TableField;
import lombok.NonNull;
import org.apache.seatunnel.api.configuration.util.OptionRule;

import java.util.List;
import java.util.Map;

public class PulsarDataSourceChannel implements DataSourceChannel {
    @Override
    public OptionRule getDataSourceOptions(@NonNull String pluginName) {
        return PulsarOptionRule.optionRule();
    }

    @Override
    public OptionRule getDatasourceMetadataFieldsByDataSourceName(@NonNull String pluginName) {
        return PulsarOptionRule.metadataRule();
    }

    @Override
    public List<String> getTables(@NonNull String pluginName, Map<String, String> requestParams, String database, Map<String, String> options) {
        return null;
    }

    @Override
    public List<String> getDatabases(@NonNull String pluginName, @NonNull Map<String, String> requestParams) {
        return DEFAULT_DATABASES;
    }

    @Override
    public boolean checkDataSourceConnectivity(@NonNull String pluginName, @NonNull Map<String, String> requestParams) {
        return false;
    }

    @Override
    public List<TableField> getTableFields(@NonNull String pluginName, @NonNull Map<String, String> requestParams, @NonNull String database, @NonNull String table) {
        return null;
    }

    @Override
    public Map<String, List<TableField>> getTableFields(@NonNull String pluginName, @NonNull Map<String, String> requestParams, @NonNull String database, @NonNull List<String> tables) {
        return null;
    }
}
