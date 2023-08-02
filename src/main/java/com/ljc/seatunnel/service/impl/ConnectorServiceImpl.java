package com.ljc.seatunnel.service.impl;

import com.ljc.seatunnel.bean.connector.ConnectorCache;
import com.ljc.seatunnel.config.ConnectorDataSourceMapperConfig;
import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.connector.ConnectorStatus;
import com.ljc.seatunnel.domain.request.connector.SceneMode;
import com.ljc.seatunnel.domain.request.job.transform.Transform;
import com.ljc.seatunnel.domain.response.connector.ConnectorInfo;
import com.ljc.seatunnel.domain.response.connector.DataSourceInfo;
import com.ljc.seatunnel.domain.response.connector.DataSourceInstance;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.service.IConnectorService;
import com.ljc.seatunnel.service.IDatasourceService;
import com.ljc.seatunnel.service.IJobDefinitionService;
import com.ljc.seatunnel.thirdparty.datasource.DataSourceConfigSwitcherUtils;
import com.ljc.seatunnel.thirdparty.transform.TransformConfigSwitcherUtils;
import lombok.NonNull;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConnectorServiceImpl implements IConnectorService {

    @Autowired
    private  ConnectorCache connectorCache;
    @Autowired
    private ConnectorDataSourceMapperConfig dataSourceMapperConfig;
    @Autowired
    private IDatasourceService datasourceService;
    @Autowired
    private IJobDefinitionService jobDefinitionService;
    private static final List<String> SKIP_SINK = Collections.singletonList("Console");

    private static final List<String> SKIP_SOURCE = Collections.emptyList();


    @Override
    public List<ConnectorInfo> listSources(ConnectorStatus status) {
        List<ConnectorInfo> connectorInfos;
        if (status == ConnectorStatus.ALL) {
            connectorInfos = connectorCache.getAllConnectors(PluginType.SOURCE);
        } else if (status == ConnectorStatus.DOWNLOADED) {
            connectorInfos = connectorCache.getDownLoadConnector(PluginType.SOURCE);
        } else {
            connectorInfos = connectorCache.getNotDownLoadConnector(PluginType.SOURCE);
        }
        return connectorInfos.stream()
                .filter(c -> !SKIP_SOURCE.contains(c.getPluginIdentifier().getPluginName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DataSourceInstance> listSourceDataSourceInstances(Long jobId, SceneMode sceneMode, ConnectorStatus status) {
        BusinessMode businessMode =
                BusinessMode.valueOf(
                        jobDefinitionService
                                .getJobDefinitionByJobId(jobId)
                                .getJobType()
                                .toUpperCase());

        return filterImplementDataSource(
                listSources(status), businessMode, sceneMode, PluginType.SOURCE)
                .stream()
                .flatMap(dataSourceInfo -> getDataSourcesInstance(dataSourceInfo).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<ConnectorInfo> listTransformsForJob(Long jobId) {
        BusinessMode businessMode =
                BusinessMode.valueOf(
                        jobDefinitionService
                                .getJobDefinitionByJobId(jobId)
                                .getJobType()
                                .toUpperCase());

        if (businessMode.equals(BusinessMode.DATA_INTEGRATION)) {
            return connectorCache.getTransform().stream()
                    .filter(
                            connectorInfo -> {
                                String pluginName =
                                        connectorInfo.getPluginIdentifier().getPluginName();
                                return pluginName.equals("FieldMapper")
                                        || pluginName.equals("FilterRowKind")
                                        || pluginName.equals("Replace")
                                        || pluginName.equals("Copy")
                                        || pluginName.equals("MultiFieldSplit")
                                        || pluginName.equals("Sql");
                            })
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<ConnectorInfo> listSinks(ConnectorStatus status) {
        List<ConnectorInfo> connectorInfos;
        if (status == ConnectorStatus.ALL) {
            connectorInfos = connectorCache.getAllConnectors(PluginType.SINK);
        } else if (status == ConnectorStatus.DOWNLOADED) {
            connectorInfos = connectorCache.getDownLoadConnector(PluginType.SINK);
        } else {
            connectorInfos = connectorCache.getNotDownLoadConnector(PluginType.SINK);
        }
        return connectorInfos.stream()
                .filter(c -> !SKIP_SINK.contains(c.getPluginIdentifier().getPluginName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DataSourceInstance> listSinkDataSourcesInstances(Long jobId, ConnectorStatus status) {
        BusinessMode businessMode =
                BusinessMode.valueOf(
                        jobDefinitionService
                                .getJobDefinitionByJobId(jobId)
                                .getJobType()
                                .toUpperCase());

        return filterImplementDataSource(listSinks(status), businessMode, null, PluginType.SINK)
                .stream()
                .flatMap(dataSourceInfo -> getDataSourcesInstance(dataSourceInfo).stream())
                .collect(Collectors.toList());
    }

    @Override
    public FormStructure getTransformFormStructure(@NonNull String pluginType, @NonNull String connectorName) {
        OptionRule optionRule = connectorCache.getOptionRule(pluginType, connectorName);
        return TransformConfigSwitcherUtils.getFormStructure(
                Transform.valueOf(connectorName.toUpperCase()), optionRule);

    }

    @Override
    public FormStructure getDatasourceFormStructure(@NonNull Long jobId, @NonNull Long dataSourceInstanceId, @NonNull String pluginType) {
        BusinessMode businessMode =
                BusinessMode.valueOf(
                        jobDefinitionService
                                .getJobDefinitionByJobId(jobId)
                                .getJobType()
                                .toUpperCase());

        // 1.dataSourceInstanceId query dataSourceName,
        String dataSourceName =
                datasourceService
                        .queryDatasourceDetailById(dataSourceInstanceId.toString())
                        .getPluginName();

        // 2.DataSourceName DataSourceName OptionRole
        OptionRule dataSourceNameOptionRole =
                datasourceService.queryOptionRuleByPluginName(dataSourceName);

        // 3.OptionRole
        OptionRule virtualTableOptionRule =
                datasourceService.queryVirtualTableOptionRuleByPluginName(dataSourceName);

        // 4.dataSourceName query connector
        String connectorName =
                dataSourceMapperConfig.findConnectorForDatasourceName(dataSourceName).get();

        // 5.call utils
        PluginType connectorPluginType = PluginType.valueOf(pluginType.toUpperCase(Locale.ROOT));

        return DataSourceConfigSwitcherUtils.filterOptionRule(
                dataSourceName,
                connectorName,
                dataSourceNameOptionRole,
                virtualTableOptionRule,
                connectorPluginType,
                businessMode,
                connectorCache.getOptionRule(pluginType, connectorName));

    }

    private List<DataSourceInfo> filterImplementDataSource(
            List<ConnectorInfo> connectorInfoS,
            BusinessMode businessMode,
            SceneMode sceneMode,
            PluginType pluginType) {
        List<DataSourceInfo> dataSourceList = new ArrayList<>();
        connectorInfoS.forEach(
                connectorInfo -> {
                    String connectorName = connectorInfo.getPluginIdentifier().getPluginName();
                    ConnectorDataSourceMapperConfig.ConnectorMapper connectorMapper =
                            dataSourceMapperConfig
                                    .getConnectorDatasourceMappers()
                                    .get(connectorName);

                    if (null != connectorMapper) {
                        connectorMapper
                                .getDataSources()
                                .forEach(
                                        datasourceName -> {
                                            Optional<List<SceneMode>> sceneModes =
                                                    dataSourceMapperConfig.supportedSceneMode(
                                                            datasourceName, pluginType);
                                            Optional<List<BusinessMode>> businessModes =
                                                    dataSourceMapperConfig.supportedBusinessMode(
                                                            datasourceName, pluginType);
                                            if ((businessMode == null
                                                    || businessModes.isPresent()
                                                    && businessModes
                                                    .get()
                                                    .contains(businessMode))
                                                    && (sceneMode == null
                                                    || (sceneModes.isPresent()
                                                    && sceneModes
                                                    .get()
                                                    .contains(
                                                            sceneMode)))) {
                                                dataSourceList.add(
                                                        new DataSourceInfo(
                                                                connectorInfo, datasourceName));
                                            }
                                        });
                    }
                });

        return dataSourceList;
    }

    private List<DataSourceInstance> getDataSourcesInstance(DataSourceInfo dataSourceInfo) {

        // dataSourceName DataSourceInstance
        return datasourceService.queryDatasourceNameByPluginName(dataSourceInfo.getDatasourceName())
                .entrySet().stream()
                .map(
                        en -> {
                            return new DataSourceInstance(
                                    dataSourceInfo, en.getValue(), Long.parseLong(en.getKey()));
                        })
                .collect(Collectors.toList());
    }
}
