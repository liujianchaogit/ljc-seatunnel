package com.ljc.seatunnel.datasource.client.classloader;

import com.google.common.collect.Sets;
import com.ljc.seatunnel.datasource.plugins.api.DataSourceChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DatasourceLoadConfig {
    public static final Map<String, String> classLoaderFactoryName;

    public static final Map<String, String> classLoaderJarName;
    public static final String[] DEFAULT_PARENT_FIRST_PATTERNS =
            new String[] {
                "java.",
                "javax.xml",
                "org.xml",
                "org.w3c",
                "scala.",
                "javax.annotation.",
                "org.slf4j",
                "org.apache.log4j",
                "org.apache.seatunnel.api",
                "org.apache.logging",
                "org.apache.commons",
                "com.fasterxml.jackson"
            };

    static {
        classLoaderFactoryName = new HashMap<>();
        classLoaderJarName = new HashMap<>();
        classLoaderFactoryName.put(
                "JDBC-MYSQL",
                "com.ljc.seatunnel.datasource.plugins.jdbc.mysql.MysqlJdbcDataSourceFactory");
        classLoaderFactoryName.put(
                "KAFKA", "com.ljc.seatunnel.datasource.plugins.kafka.KafkaDataSourceFactory");
        classLoaderFactoryName.put(
                "PULSAR",
                "com.ljc.seatunnel.datasource.plugins.pulsar.PulsarDataSourceFactory");
    }

    public static final Set<String> pluginSet =
            Sets.newHashSet(
                    "JDBC-Mysql",
                    "Kafka",
                    "Pulsar"
            );

    public static Map<String, DatasourceClassLoader> datasourceClassLoaders = new HashMap<>();

    public static Map<String, DataSourceChannel> classLoaderChannel = new HashMap<>();
}
