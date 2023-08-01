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
                "ELASTICSEARCH",
                "org.apache.seatunnel.datasource.plugin.elasticsearch.ElasticSearchDataSourceFactory");
        classLoaderFactoryName.put(
                "JDBC-CLICKHOUSE",
                "org.apache.seatunnel.datasource.plugin.clickhouse.jdbc.ClickhouseJdbcDataSourceFactory");
        classLoaderFactoryName.put(
                "HIVE",
                "org.apache.seatunnel.datasource.plugin.hive.jdbc.HiveJdbcDataSourceFactory");
        classLoaderFactoryName.put(
                "JDBC-ORACLE",
                "org.apache.seatunnel.datasource.plugin.oracle.jdbc.OracleJdbcDataSourceFactory");
        classLoaderFactoryName.put(
                "JDBC-POSTGRES",
                "org.apache.seatunnel.datasource.plugin.postgresql.jdbc.PostgresqlDataSourceFactory");
        classLoaderFactoryName.put(
                "JDBC-REDSHIFT",
                "org.apache.seatunnel.datasource.plugin.redshift.jdbc.RedshiftDataSourceFactory");
        classLoaderFactoryName.put(
                "JDBC-SQLSERVER",
                "org.apache.seatunnel.datasource.plugin.sqlserver.jdbc.SqlServerDataSourceFactory");
        classLoaderFactoryName.put(
                "JDBC-TIDB",
                "org.apache.seatunnel.datasource.plugin.tidb.jdbc.TidbJdbcDataSourceFactory");
        classLoaderFactoryName.put(
                "KAFKA", "org.apache.seatunnel.datasource.plugin.kafka.KafkaDataSourceFactory");
        classLoaderFactoryName.put(
                "MYSQL-CDC",
                "org.apache.seatunnel.datasource.plugin.cdc.mysql.MysqlCDCDataSourceFactory");
        classLoaderFactoryName.put(
                "S3", "org.apache.seatunnel.datasource.plugin.s3.S3DataSourceFactory");
        classLoaderFactoryName.put(
                "S3-REDSHIFT",
                "org.apache.seatunnel.datasource.plugin.redshift.s3.S3RedshiftDataSourceFactory");
        classLoaderFactoryName.put(
                "SQLSERVER-CDC",
                "org.apache.seatunnel.datasource.plugin.cdc.sqlserver.SqlServerCDCDataSourceFactory");
        classLoaderFactoryName.put(
                "STARROCKS",
                "org.apache.seatunnel.datasource.plugin.starrocks.StarRocksDataSourceFactory");
        classLoaderFactoryName.put(
                "JDBC-STARROCKS",
                "org.apache.seatunnel.datasource.plugin.starrocks.jdbc.StarRocksJdbcDataSourceFactory");

        classLoaderJarName.put("JDBC-ORACLE", "datasource-jdbc-oracle-");
        classLoaderJarName.put("JDBC-CLICKHOUSE", "datasource-jdbc-clickhouse-");
        classLoaderJarName.put("JDBC-POSTGRES", "datasource-jdbc-postgresql-");
        classLoaderJarName.put("JDBC-TIDB", "datasource-jdbc-tidb-");
        classLoaderJarName.put("JDBC-REDSHIFT", "datasource-jdbc-redshift-");
        classLoaderJarName.put("JDBC-MYSQL", "datasource-jdbc-mysql-");
        classLoaderJarName.put("JDBC-SQLSERVER", "datasource-jdbc-sqlserver-");

        classLoaderJarName.put("SQLSERVER-CDC", "datasource-sqlserver-cdc-");
        classLoaderJarName.put("MYSQL-CDC", "datasource-mysql-cdc-");

        classLoaderJarName.put("ELASTICSEARCH", "datasource-elasticsearch-");
        classLoaderJarName.put("S3", "datasource-s3-");
        classLoaderJarName.put("HIVE", "datasource-jdbc-hive-");
        classLoaderJarName.put("KAFKA", "datasource-kafka-");
        classLoaderJarName.put("STARROCKS", "datasource-starrocks-");
        classLoaderJarName.put("S3-REDSHIFT", "datasource-s3redshift-");
        classLoaderJarName.put("JDBC-STARROCKS", "datasource-jdbc-starrocks-");
    }

    public static final Set<String> pluginSet =
            Sets.newHashSet(
                    "JDBC-Mysql",
//                    "ElasticSearch",
//                    "JDBC-ClickHouse",
//                    "Hive",
//                    "JDBC-Oracle",
//                    "JDBC-Postgres",
//                    "JDBC-Redshift",
//                    "JDBC-SQLServer",
//                    "JDBC-TiDB",
                    "Kafka",
                    "MySQL-CDC"
//                    "S3",
//                    "S3-Redshift",
//                    "SqlServer-CDC",
//                    "JDBC-StarRocks",
//                    "StarRocks"
            );

    public static Map<String, DatasourceClassLoader> datasourceClassLoaders = new HashMap<>();

    public static Map<String, DataSourceChannel> classLoaderChannel = new HashMap<>();
}