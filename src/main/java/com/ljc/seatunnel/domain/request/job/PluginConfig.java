package com.ljc.seatunnel.domain.request.job;

import com.ljc.seatunnel.domain.request.connector.SceneMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.seatunnel.common.constants.PluginType;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PluginConfig {

    private String pluginId;

    private String name;

    private PluginType type;

    private String connectorType;

    private DataSourceOption tableOption;

    private SelectTableFields selectTableFields;

    private Map<String, Object> transformOptions;

    private List<DatabaseTableSchemaReq> outputSchema;

    private Long dataSourceId;

    private SceneMode sceneMode;

    private String config;
}
