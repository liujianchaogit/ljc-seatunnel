package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.request.connector.ConnectorStatus;
import com.ljc.seatunnel.domain.request.connector.SceneMode;
import com.ljc.seatunnel.domain.response.connector.ConnectorInfo;
import com.ljc.seatunnel.domain.response.connector.DataSourceInstance;
import com.ljc.seatunnel.service.IConnectorService;
import io.swagger.annotations.ApiParam;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/seatunnel/api/v1/datasource")
@RestController
public class ConnectorDataSourceController {

    @Autowired
    private IConnectorService connectorService;

    @GetMapping("/sources")
    public Result<List<DataSourceInstance>> listSource(
            @RequestParam Long jobId,
            @RequestParam SceneMode sceneMode,
            @RequestParam ConnectorStatus status) {
        return Result.success(
                connectorService.listSourceDataSourceInstances(jobId, sceneMode, status));
    }

    @GetMapping("/sinks")
    public Result<List<DataSourceInstance>> listSink(
            @ApiParam(value = "jobCode", required = true) @RequestParam Long jobId,
            @ApiParam(value = "ConnectorStatus", required = true) @RequestParam
            ConnectorStatus status) {
        return Result.success(connectorService.listSinkDataSourcesInstances(jobId, status));
    }

    @GetMapping("/transforms")
    public Result<List<ConnectorInfo>> listAllTransform(@RequestParam Long jobId) {
        return Result.success(connectorService.listTransformsForJob(jobId));
    }

    @GetMapping("/form")
    public Result<String> getDatasourceInstanceFormStructure(
            @RequestParam(required = false, value = "jobCode")
            Long jobId,
            @RequestParam(required = true, value = "connectorType")
            String connectorType,
            @RequestParam(required = false, value = "connectorName")
            String connectorName,
            @RequestParam(required = false, value = "dataSourceInstanceId")
            Long dataSourceInstanceId) {
        if (PluginType.TRANSFORM.getType().equals(connectorType)) {
            return Result.success(
                    JsonUtils.toJsonString(
                            connectorService.getTransformFormStructure(
                                    connectorType, connectorName)));
        }
        return Result.success(
                JsonUtils.toJsonString(
                        connectorService.getDatasourceFormStructure(
                                jobId, dataSourceInstanceId, connectorType)));
    }
}
