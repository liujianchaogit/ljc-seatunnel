package com.ljc.seatunnel.service;

import com.ljc.seatunnel.domain.request.connector.ConnectorStatus;
import com.ljc.seatunnel.domain.request.connector.SceneMode;
import com.ljc.seatunnel.domain.response.connector.ConnectorInfo;
import com.ljc.seatunnel.domain.response.connector.DataSourceInstance;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import lombok.NonNull;

import java.util.List;

public interface IConnectorService {

    List<ConnectorInfo> listSources(ConnectorStatus status);
    List<DataSourceInstance> listSourceDataSourceInstances(Long jobId, SceneMode sceneMode, ConnectorStatus status);

    List<ConnectorInfo> listTransformsForJob(Long jobId);

    List<ConnectorInfo> listSinks(ConnectorStatus status);

    List<DataSourceInstance> listSinkDataSourcesInstances(Long jobId, ConnectorStatus status);

    FormStructure getTransformFormStructure(
            @NonNull String pluginType, @NonNull String connectorName);

    FormStructure getDatasourceFormStructure(
            @NonNull Long jobId, @NonNull Long dataSourceId, @NonNull String pluginType);

}
