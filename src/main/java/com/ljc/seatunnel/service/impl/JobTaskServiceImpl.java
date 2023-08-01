package com.ljc.seatunnel.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.dal.dao.IJobLineDao;
import com.ljc.seatunnel.dal.dao.IJobTaskDao;
import com.ljc.seatunnel.dal.entity.JobLine;
import com.ljc.seatunnel.dal.entity.JobTask;
import com.ljc.seatunnel.domain.request.connector.SceneMode;
import com.ljc.seatunnel.domain.request.job.*;
import com.ljc.seatunnel.service.IJobTaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.common.constants.PluginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobTaskServiceImpl implements IJobTaskService {

    @Autowired
    private IJobTaskDao jobTaskDao;
    @Autowired
    private IJobLineDao jobLineDao;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public JobTaskInfo getTaskConfig(long jobVersionId) {
        List<JobTask> tasks = jobTaskDao.getTasksByVersionId(jobVersionId);
        if (tasks.isEmpty()) {
            return null;
        }
        List<JobLine> lines = jobLineDao.getLinesByVersionId(jobVersionId);
        return new JobTaskInfo(
                lines.stream()
                        .map(l -> new Edge(l.getInputPluginId(), l.getTargetPluginId()))
                        .collect(Collectors.toList()),
                tasks.stream()
                        .map(
                                t -> {
                                    try {
                                        return getPluginConfigFromJobTask(t);
                                    } catch (Exception e) {
                                        throw new SeatunnelException(
                                                SeatunnelErrorEnum.UNKNOWN, e.getMessage());
                                    }
                                })
                        .collect(Collectors.toList()));
    }

    private static PluginConfig getPluginConfigFromJobTask(JobTask jobTask) {
        try {
            return PluginConfig.builder()
                    .pluginId(jobTask.getPluginId())
                    .name(jobTask.getName())
                    .type(PluginType.valueOf(jobTask.getType().toUpperCase()))
                    .dataSourceId(jobTask.getDataSourceId())
                    .config(jobTask.getConfig())
                    .connectorType(jobTask.getConnectorType())
                    .sceneMode(
                            StringUtils.isEmpty(jobTask.getSceneMode())
                                    ? null
                                    : SceneMode.valueOf(jobTask.getSceneMode()))
                    .tableOption(
                            StringUtils.isEmpty(jobTask.getDataSourceOption())
                                    ? null
                                    : OBJECT_MAPPER.readValue(
                                    jobTask.getDataSourceOption(), DataSourceOption.class))
                    .selectTableFields(
                            StringUtils.isEmpty(jobTask.getSelectTableFields())
                                    ? null
                                    : OBJECT_MAPPER.readValue(
                                    jobTask.getSelectTableFields(),
                                    SelectTableFields.class))
                    .outputSchema(
                            StringUtils.isEmpty(jobTask.getOutputSchema())
                                    ? null
                                    : OBJECT_MAPPER.readValue(
                                    jobTask.getOutputSchema(),
                                    new TypeReference<List<DatabaseTableSchemaReq>>() {}))
                    .transformOptions(
                            StringUtils.isEmpty(jobTask.getTransformOptions())
                                    ? null
                                    : OBJECT_MAPPER.readValue(
                                    jobTask.getTransformOptions(),
                                    new TypeReference<Map<String, Object>>() {}))
                    .config(jobTask.getConfig())
                    .build();
        } catch (Exception e) {
            throw new SeatunnelException(SeatunnelErrorEnum.UNKNOWN, e.getMessage());
        }
    }
}
