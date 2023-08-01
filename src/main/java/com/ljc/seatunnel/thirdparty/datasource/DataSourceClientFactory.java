package com.ljc.seatunnel.thirdparty.datasource;

import com.ljc.seatunnel.datasource.client.DataSourceClient;

public class DataSourceClientFactory {

    private static volatile DataSourceClient instance;

    private static final Object LOCK = new Object();

    public static DataSourceClient getDataSourceClient() {
        if (null == instance) {
            synchronized (LOCK) {
                if (null == instance) {
                    instance = new DataSourceClient();
                }
            }
        }
        return instance;
    }
}
