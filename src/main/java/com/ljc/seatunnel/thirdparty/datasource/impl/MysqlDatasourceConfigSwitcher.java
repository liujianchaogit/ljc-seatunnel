package com.ljc.seatunnel.thirdparty.datasource.impl;

import java.util.Optional;

public class MysqlDatasourceConfigSwitcher extends BaseJdbcDataSourceConfigSwitcher {
    public static MysqlDatasourceConfigSwitcher INSTANCE = new MysqlDatasourceConfigSwitcher();
    private static final String CATALOG_NAME = "MySQL";

    private MysqlDatasourceConfigSwitcher() {}

    protected Optional<String> getCatalogName() {
        return Optional.of(CATALOG_NAME);
    }

    protected boolean isSupportPrefixOrSuffix() {
        return true;
    }

    protected boolean isSupportToggleCase() {
        return true;
    }
}
