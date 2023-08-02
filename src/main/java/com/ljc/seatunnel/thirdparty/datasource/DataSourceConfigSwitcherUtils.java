package com.ljc.seatunnel.thirdparty.datasource;

import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.domain.request.job.SelectTableFields;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.datasource.impl.*;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.common.utils.SeaTunnelException;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

public class DataSourceConfigSwitcherUtils {

    public static FormStructure filterOptionRule(
            String datasourceName,
            String connectorName,
            OptionRule dataSourceOptionRule,
            OptionRule virtualTableOptionRule,
            PluginType pluginType,
            BusinessMode businessMode,
            OptionRule connectorOptionRule) {
        DataSourceConfigSwitcher dataSourceConfigSwitcher =
                getDataSourceConfigSwitcher(datasourceName.toUpperCase());
        return dataSourceConfigSwitcher.filterOptionRule(
                connectorName,
                dataSourceOptionRule,
                virtualTableOptionRule,
                businessMode,
                pluginType,
                connectorOptionRule,
                new ArrayList<>());
    }

    public static Config mergeDatasourceConfig(
            String datasourceName,
            Config dataSourceInstanceConfig,
            VirtualTableDetailRes virtualTableDetail,
            DataSourceOption dataSourceOption,
            SelectTableFields selectTableFields,
            BusinessMode businessMode,
            PluginType pluginType,
            Config connectorConfig) {
        DataSourceConfigSwitcher dataSourceConfigSwitcher =
                getDataSourceConfigSwitcher(datasourceName.toUpperCase());
        return dataSourceConfigSwitcher.mergeDatasourceConfig(
                dataSourceInstanceConfig,
                virtualTableDetail,
                dataSourceOption,
                selectTableFields,
                businessMode,
                pluginType,
                connectorConfig);
    }

    private static DataSourceConfigSwitcher getDataSourceConfigSwitcher(String datasourceName) {
        checkNotNull(datasourceName, "datasourceName cannot be null");
        // Use SPI
        switch (datasourceName.toUpperCase()) {
            case "JDBC-MYSQL":
                return MysqlDatasourceConfigSwitcher.INSTANCE;
            case "ELASTICSEARCH":
                return ElasticSearchDataSourceConfigSwitcher.INSTANCE;
            case "KAFKA":
                return KafkaDataSourceConfigSwitcher.getInstance();
            case "PULSAR":
                return PulsarDataSourceConfigSwitcher.getInstance();
            case "STARROCKS":
                return StarRocksDataSourceConfigSwitcher.INSTANCE;
            case "MYSQL-CDC":
                return MysqlCDCDataSourceConfigSwitcher.INSTANCE;
            case "S3-REDSHIFT":
                return S3RedshiftDataSourceConfigSwitcher.getInstance();
            case "JDBC-CLICKHOUSE":
                return ClickhouseDataSourceConfigSwitcher.getInstance();
            case "JDBC-DAMENG":
                return DamengDataSourceConfigSwitcher.getInstance();
            case "JDBC-POSTGRES":
                return PostgresqlDataSourceConfigSwitcher.getInstance();
            case "JDBC-REDSHIFT":
                return RedshiftDataSourceConfigSwitcher.getInstance();
            case "JDBC-SQLSERVER":
                return SqlServerDataSourceConfigSwitcher.getInstance();
            case "JDBC-TIDB":
                return TidbDataSourceConfigSwitcher.INSTANCE;
            case "JDBC-ORACLE":
                return OracleDataSourceConfigSwitcher.INSTANCE;
//            case "S3":
//                return S3DataSourceConfigSwitcher.getInstance();
            case "SQLSERVER-CDC":
                return SqlServerCDCDataSourceConfigSwitcher.INSTANCE;
            case "POSTGRES-CDC":
                return PostgresCDCDataSourceConfigSwitcher.INSTANCE;
            case "JDBC-KINGBASE":
                return KingBaseDataSourceConfigSwitcher.getInstance();
            case "ORACLE-CDC":
                return OracleCDCDataSourceConfigSwitcher.INSTANCE;
            case "KAFKA-KINGBASE":
                return KafkaKingbaseDataSourceConfigSwitcher.getInstance();

            default:
                throw new SeaTunnelException(
                        "data source : "
                                + datasourceName
                                + " is no implementation class for DataSourceConfigSwitcher");
        }
    }
}
