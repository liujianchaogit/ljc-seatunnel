package com.ljc.seatunnel.thirdparty.datasource.impl;

public class DamengDataSourceConfigSwitcher extends BaseJdbcDataSourceConfigSwitcher {
    private static final DamengDataSourceConfigSwitcher INSTANCE =
            new DamengDataSourceConfigSwitcher();

    public static final DamengDataSourceConfigSwitcher getInstance() {
        return INSTANCE;
    }

    private DamengDataSourceConfigSwitcher() {}
}
