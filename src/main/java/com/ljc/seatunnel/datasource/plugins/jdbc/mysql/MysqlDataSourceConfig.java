package com.ljc.seatunnel.datasource.plugins.jdbc.mysql;

import com.google.common.collect.Sets;
import com.ljc.seatunnel.datasource.plugins.api.DataSourcePluginInfo;
import com.ljc.seatunnel.datasource.plugins.api.DatasourcePluginTypeEnum;
import org.apache.seatunnel.api.configuration.util.OptionRule;

import java.util.Set;

public class MysqlDataSourceConfig {

    public static final String PLUGIN_NAME = "JDBC-Mysql";

    public static final DataSourcePluginInfo MYSQL_DATASOURCE_PLUGIN_INFO =
            DataSourcePluginInfo.builder()
                    .name(PLUGIN_NAME)
                    .icon(PLUGIN_NAME)
                    .version("1.0.0")
                    .type(DatasourcePluginTypeEnum.DATABASE.getCode())
                    .build();

    public static final Set<String> MYSQL_SYSTEM_DATABASES =
            Sets.newHashSet("information_schema", "mysql", "performance_schema", "sys");

    public static final OptionRule OPTION_RULE =
            OptionRule.builder()
                    .required(MysqlOptionRule.URL, MysqlOptionRule.DRIVER)
                    .optional(MysqlOptionRule.USER, MysqlOptionRule.PASSWORD)
                    .build();

    public static final OptionRule METADATA_RULE =
            OptionRule.builder().required(MysqlOptionRule.DATABASE, MysqlOptionRule.TABLE).build();
}
