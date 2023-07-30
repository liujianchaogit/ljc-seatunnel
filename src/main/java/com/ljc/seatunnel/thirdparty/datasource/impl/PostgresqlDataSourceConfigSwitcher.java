package com.ljc.seatunnel.thirdparty.datasource.impl;

import com.ljc.seatunnel.thirdparty.datasource.DataSourceConfigSwitcher;
import com.ljc.seatunnel.utils.JdbcUtils;
import org.apache.seatunnel.common.utils.SeaTunnelException;

import java.util.List;

public class PostgresqlDataSourceConfigSwitcher extends BaseJdbcDataSourceConfigSwitcher {

    private static final PostgresqlDataSourceConfigSwitcher INSTANCE =
            new PostgresqlDataSourceConfigSwitcher();

    private PostgresqlDataSourceConfigSwitcher() {}

    protected String tableFieldsToSql(List<String> tableFields, String database, String fullTable) {

        String[] split = fullTable.split("\\.");
        if (split.length != 2) {
            throw new SeaTunnelException(
                    "The tableName for postgres must be schemaName.tableName, but tableName is "
                            + fullTable);
        }

        String schemaName = split[0];
        String tableName = split[1];

        return generateSql(tableFields, database, schemaName, tableName);
    }

    protected String quoteIdentifier(String identifier) {
        return "\"" + identifier + "\"";
    }

    protected String replaceDatabaseNameInUrl(String url, String databaseName) {
        return JdbcUtils.replaceDatabase(url, databaseName);
    }

    public static DataSourceConfigSwitcher getInstance() {
        return (DataSourceConfigSwitcher) INSTANCE;
    }
}
