package com.ljc.seatunnel.config;

import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.connector.SceneMode;
import com.ljc.seatunnel.domain.response.connector.ConnectorInfo;
import com.ljc.seatunnel.domain.response.connector.DataSourceInfo;
import lombok.Data;
import org.apache.seatunnel.common.constants.PluginType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Configuration
@PropertySource(
        value = "classpath:connector-datasource-mapper.yaml",
        factory = YamlSourceFactory.class)
@ConfigurationProperties(prefix = "connector-datasource-mapper")
public class ConnectorDataSourceMapperConfig {
    private Map<String, ConnectorMapper> connectorDatasourceMappers;
    private Map<String, DatasourceFeatures> sourceDatasourceFeatures;
    private Map<String, DatasourceFeatures> sinkDatasourceFeatures;

    @Data
    public static class ConnectorMapper {
        private List<String> dataSources;
    }

    @Data
    public static class DatasourceFeatures {
        private List<String> businessMode;
        private List<String> sceneMode;
    }

    public Optional<List<BusinessMode>> supportedBusinessMode(
            String datasourceName, PluginType pluginType) {
        if (pluginType.equals(PluginType.SOURCE)) {
            return Optional.ofNullable(sourceDatasourceFeatures.get(datasourceName))
                    .map(
                            cm ->
                                    cm.getBusinessMode().stream()
                                            .map(BusinessMode::valueOf)
                                            .collect(Collectors.toList()));
        }
        if (pluginType.equals(PluginType.SINK)) {
            return Optional.ofNullable(sinkDatasourceFeatures.get(datasourceName))
                    .map(
                            cm ->
                                    cm.getBusinessMode().stream()
                                            .map(BusinessMode::valueOf)
                                            .collect(Collectors.toList()));
        }
        throw new UnsupportedOperationException(
                "pluginType : " + pluginType + " not support BusinessMode");
    }

    public Optional<List<SceneMode>> supportedSceneMode(
            String datasourceName, PluginType pluginType) {
        if (pluginType.equals(PluginType.SOURCE)) {
            return Optional.ofNullable(sourceDatasourceFeatures.get(datasourceName))
                    .map(
                            cm ->
                                    cm.getSceneMode().stream()
                                            .map(SceneMode::valueOf)
                                            .collect(Collectors.toList()));
        }
        if (pluginType.equals(PluginType.SINK)) {
            return Optional.ofNullable(sinkDatasourceFeatures.get(datasourceName))
                    .map(
                            cm ->
                                    cm.getSceneMode().stream()
                                            .map(SceneMode::valueOf)
                                            .collect(Collectors.toList()));
        }
        throw new UnsupportedOperationException(
                "pluginType : " + pluginType + " not support SceneMode");
    }

    public Optional<String> findConnectorForDatasourceName(String datasourceName) {
        return connectorDatasourceMappers.entrySet().stream()
                .map(
                        en -> {
                            return en.getValue().getDataSources().stream()
                                            .anyMatch(n -> n.equalsIgnoreCase(datasourceName))
                                    ? en.getKey()
                                    : null;
                        })
                .filter(Objects::nonNull)
                .findFirst();
    }

    public List<DataSourceInfo> findDatasourceNameForConnectors(List<ConnectorInfo> connectors) {

        Map<String, ConnectorInfo> connectorMap =
                connectors.stream()
                        .collect(
                                Collectors.toMap(
                                        connectorInfo ->
                                                connectorInfo.getPluginIdentifier().getPluginName(),
                                        connectorInfo -> connectorInfo));

        return connectorDatasourceMappers.entrySet().stream()
                .filter(en -> connectorMap.containsKey(en.getKey()))
                .flatMap(
                        en ->
                                en.getValue().getDataSources().stream()
                                        .map(
                                                name ->
                                                        new DataSourceInfo(
                                                                connectorMap.get(en.getKey()),
                                                                name)))
                .collect(Collectors.toList());
    }
}
