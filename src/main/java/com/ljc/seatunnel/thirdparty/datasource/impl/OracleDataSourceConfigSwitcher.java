package com.ljc.seatunnel.thirdparty.datasource.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.seatunnel.common.utils.SeaTunnelException;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OracleDataSourceConfigSwitcher extends BaseJdbcDataSourceConfigSwitcher {
    public static final OracleDataSourceConfigSwitcher INSTANCE =
            new OracleDataSourceConfigSwitcher();

    protected String tableFieldsToSql(List<String> tableFields, String database, String fullTable) {
        String[] split = fullTable.split("\\.");
        if (split.length != 2) {
            throw new SeaTunnelException(
                    "The tableName for oracle must be schemaName.tableName, but tableName is "
                            + fullTable);
        }

        String schemaName = split[0];
        String tableName = split[1];

        return generateSql(tableFields, database, schemaName, tableName);
    }

    protected String quoteIdentifier(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    protected String generateSql(
            List<String> tableFields, String database, String schema, String table) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        for (int i = 0; i < tableFields.size(); i++) {
            sb.append(quoteIdentifier(tableFields.get(i)));
            if (i < tableFields.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" FROM ")
                .append(quoteIdentifier(schema))
                .append(".")
                .append(quoteIdentifier(table));
        return sb.toString();
    }
}
