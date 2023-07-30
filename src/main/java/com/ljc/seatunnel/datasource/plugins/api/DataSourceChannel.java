package com.ljc.seatunnel.datasource.plugins.api;

import com.google.common.collect.ImmutableList;
import com.ljc.seatunnel.datasource.plugins.model.TableField;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.seatunnel.api.configuration.util.OptionRule;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface DataSourceChannel {

    List<String> DEFAULT_DATABASES = ImmutableList.of("default");

    /**
     * get datasource metadata fields by datasource name
     *
     * @param pluginName plugin name
     * @return datasource metadata fields
     */
    OptionRule getDataSourceOptions(@NonNull String pluginName);

    /**
     * get datasource metadata fields by datasource name
     *
     * @param pluginName plugin name
     * @return datasource metadata fields
     */
    OptionRule getDatasourceMetadataFieldsByDataSourceName(@NonNull String pluginName);

    List<String> getTables(
            @NonNull String pluginName,
            Map<String, String> requestParams,
            String database,
            Map<String, String> options);

    List<String> getDatabases(
            @NonNull String pluginName, @NonNull Map<String, String> requestParams);

    boolean checkDataSourceConnectivity(
            @NonNull String pluginName, @NonNull Map<String, String> requestParams);

    default boolean canAbleGetSchema() {
        return false;
    }

    List<TableField> getTableFields(
            @NonNull String pluginName,
            @NonNull Map<String, String> requestParams,
            @NonNull String database,
            @NonNull String table);

    Map<String, List<TableField>> getTableFields(
            @NonNull String pluginName,
            @NonNull Map<String, String> requestParams,
            @NonNull String database,
            @NonNull List<String> tables);

    /**
     * just check metadata field is right and used by virtual table
     *
     * @param requestParams request param(connector params)
     * @return true if right
     */
    default Boolean checkMetadataFieldIsRight(Map<String, String> requestParams) {
        return true;
    }

    default Pair<String, String> getTableSyncMaxValue(
            String pluginName,
            Map<String, String> requestParams,
            String databaseName,
            String tableName,
            String updateFieldType) {
        return null;
    }

    default Connection getConnection(String pluginName, Map<String, String> requestParams) {
        return null;
    }
}
