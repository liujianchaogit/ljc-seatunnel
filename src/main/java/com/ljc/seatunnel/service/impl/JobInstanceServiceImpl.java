package com.ljc.seatunnel.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljc.seatunnel.bean.connector.ConnectorCache;
import com.ljc.seatunnel.common.CodeGenerateUtils;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.dal.dao.IJobInstanceDao;
import com.ljc.seatunnel.dal.entity.*;
import com.ljc.seatunnel.domain.executor.JobExecutorRes;
import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.connector.SceneMode;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.domain.request.job.DatabaseTableSchemaReq;
import com.ljc.seatunnel.domain.request.job.SelectTableFields;
import com.ljc.seatunnel.domain.request.job.TableSchemaReq;
import com.ljc.seatunnel.domain.request.job.transform.Transform;
import com.ljc.seatunnel.domain.request.job.transform.TransformOptions;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.service.IJobInstanceService;
import com.ljc.seatunnel.utils.SeaTunnelConfigUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.api.common.CommonOptions;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.api.env.ParsingMode;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.common.utils.ExceptionUtils;
import org.apache.seatunnel.shade.com.fasterxml.jackson.core.type.TypeReference;
import org.apache.seatunnel.shade.com.typesafe.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Service
public class JobInstanceServiceImpl implements IJobInstanceService {

    private static final String DAG_PARSING_MODE = "dag-parsing.mode";
    @Autowired
    private IJobInstanceDao jobInstanceDao;
    @Autowired
    private ConnectorCache connectorCache;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public JobExecutorRes createExecuteResource(@NonNull Integer userId, @NonNull Long jobDefineId) {
        log.info(
                "receive createExecuteResource request, userId:{}, jobDefineId:{}",
                userId,
                jobDefineId);
        JobDefinition job = jobDefinitionDao.getJob(jobDefineId);
        JobVersion latestVersion = jobVersionDao.getLatestVersion(job.getId());
        JobInstance jobInstance = new JobInstance();
        String jobConfig = createJobConfig(latestVersion);

        try {
            jobInstance.setId(CodeGenerateUtils.getInstance().genCode());
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            throw new SeatunnelException(SeatunnelErrorEnum.JOB_RUN_GENERATE_UUID_ERROR);
        }
        jobInstance.setJobDefineId(job.getId());
        jobInstance.setEngineName(latestVersion.getEngineName());
        jobInstance.setEngineVersion(latestVersion.getEngineVersion());
        jobInstance.setJobConfig(jobConfig);
        jobInstance.setCreateUserId(userId);
        if (!latestVersion.getJobMode().isEmpty()) {
            jobInstance.setJobType(latestVersion.getJobMode());
        }

        jobInstanceDao.insert(jobInstance);

        return new JobExecutorRes(
                jobInstance.getId(),
                jobInstance.getJobConfig(),
                jobInstance.getEngineName(),
                null,
                null,
                jobInstance.getJobType());
    }

    @Override
    public String generateJobConfig(
            Long jobId, List<JobTask> tasks, List<JobLine> lines, String envStr) {
        checkSceneMode(tasks);
        BusinessMode businessMode =
                BusinessMode.valueOf(jobDefinitionDao.getJob(jobId).getJobType());
        Config envConfig = filterEmptyValue(ConfigFactory.parseString(envStr));

        Map<String, List<Config>> sourceMap = new LinkedHashMap<>();
        Map<String, List<Config>> transformMap = new LinkedHashMap<>();
        Map<String, List<Config>> sinkMap = new LinkedHashMap<>();
        Map<String, JobLine> inputLines =
                lines.stream()
                        .collect(Collectors.toMap(JobLine::getInputPluginId, Function.identity()));
        Map<String, JobLine> targetLines =
                lines.stream()
                        .collect(Collectors.toMap(JobLine::getTargetPluginId, Function.identity()));

        for (JobTask task : tasks) {
            PluginType pluginType = PluginType.valueOf(task.getType().toUpperCase(Locale.ROOT));
            try {
                String pluginId = task.getPluginId();
                OptionRule optionRule =
                        connectorCache.getOptionRule(pluginType.getType(), task.getConnectorType());
                Config config =
                        filterEmptyValue(
                                parseConfigWithOptionRule(
                                        pluginType,
                                        task.getConnectorType(),
                                        task.getConfig(),
                                        optionRule));
                switch (pluginType) {
                    case SOURCE:
                        if (inputLines.containsKey(pluginId)) {
                            config =
                                    addTableName(
                                            CommonOptions.RESULT_TABLE_NAME.key(),
                                            inputLines.get(pluginId),
                                            config);
                            if (!sourceMap.containsKey(task.getConnectorType())) {
                                sourceMap.put(task.getConnectorType(), new ArrayList<>());
                            }

                            if (businessMode.equals(BusinessMode.DATA_REPLICA)) {
                                config =
                                        config.withValue(
                                                DAG_PARSING_MODE,
                                                ConfigValueFactory.fromAnyRef(
                                                        ParsingMode.MULTIPLEX.name()));
                            }

                            if (task.getSceneMode()
                                    .toUpperCase()
                                    .equals(SceneMode.SPLIT_TABLE.name())) {
                                config =
                                        config.withValue(
                                                DAG_PARSING_MODE,
                                                ConfigValueFactory.fromAnyRef(
                                                        ParsingMode.SHARDING.name()));
                            }

                            Config mergeConfig =
                                    mergeTaskConfig(
                                            task,
                                            pluginType,
                                            task.getConnectorType(),
                                            businessMode,
                                            config,
                                            optionRule);

                            sourceMap
                                    .get(task.getConnectorType())
                                    .add(filterEmptyValue(mergeConfig));
                        }
                        break;
                    case TRANSFORM:
                        if (!inputLines.containsKey(pluginId)
                                && !targetLines.containsKey(pluginId)) {
                            break;
                        }
                        if (inputLines.containsKey(pluginId)) {
                            config =
                                    addTableName(
                                            CommonOptions.RESULT_TABLE_NAME.key(),
                                            inputLines.get(pluginId),
                                            config);
                        }
                        if (targetLines.containsKey(pluginId)) {
                            config =
                                    addTableName(
                                            CommonOptions.SOURCE_TABLE_NAME.key(),
                                            targetLines.get(pluginId),
                                            config);
                        }
                        if (!transformMap.containsKey(task.getConnectorType())) {
                            transformMap.put(task.getConnectorType(), new ArrayList<>());
                        }
                        List<TableSchemaReq> inputSchemas = findInputSchemas(tasks, lines, task);
                        Config transformConfig = buildTransformConfig(task, config, inputSchemas);
                        transformMap
                                .get(task.getConnectorType())
                                .add(filterEmptyValue(transformConfig));
                        break;
                    case SINK:
                        if (targetLines.containsKey(pluginId)) {
                            config =
                                    addTableName(
                                            CommonOptions.SOURCE_TABLE_NAME.key(),
                                            targetLines.get(pluginId),
                                            config);
                            if (!sinkMap.containsKey(task.getConnectorType())) {
                                sinkMap.put(task.getConnectorType(), new ArrayList<>());
                            }
                            Config mergeConfig =
                                    mergeTaskConfig(
                                            task,
                                            pluginType,
                                            task.getConnectorType(),
                                            businessMode,
                                            config,
                                            optionRule);

                            sinkMap.get(task.getConnectorType()).add(filterEmptyValue(mergeConfig));
                        }
                        break;
                    default:
                        throw new SeatunnelException(
                                SeatunnelErrorEnum.UNSUPPORTED_CONNECTOR_TYPE,
                                task.getType().toUpperCase());
                }
            } catch (SeatunnelException e) {
                log.error(ExceptionUtils.getMessage(e));
                throw e;
            } catch (Exception e) {
                throw new SeatunnelException(
                        SeatunnelErrorEnum.ERROR_CONFIG,
                        String.format(
                                "Plugin Type: %s, Connector Type: %s, Error Info: %s",
                                pluginType, task.getConnectorType(), ExceptionUtils.getMessage(e)));
            }
        }
        String sources = "";
        if (!sourceMap.isEmpty()) {
            sources = getConnectorConfig(sourceMap);
        }

        String transforms = "";
        if (!transformMap.isEmpty()) {
            transforms = getConnectorConfig(transformMap);
        }

        String sinks = "";
        if (!sinkMap.isEmpty()) {
            sinks = getConnectorConfig(sinkMap);
        }
        String env =
                envConfig
                        .root()
                        .render(
                                ConfigRenderOptions.defaults()
                                        .setJson(false)
                                        .setComments(false)
                                        .setOriginComments(false));
        return SeaTunnelConfigUtil.generateConfig(env, sources, transforms, sinks);
    }

    @Override
    public void complete(@NonNull Integer userId, @NonNull Long jobInstanceId, @NonNull String jobEngineId) {
//        JobInstance jobInstance = jobInstanceDao.getJobInstanceMapper().selectById(jobInstanceId);
//        jobMetricsService.syncJobDataToDb(jobInstance, userId, jobEngineId);
//
//        List<JobPipelineSummaryMetricsRes> status =
//                jobMetricsService.getJobPipelineSummaryMetrics(userId, jobInstanceId);
//
//        String jobStatus;
//        Set<String> statusList =
//                status.stream()
//                        .map(JobPipelineSummaryMetricsRes::getStatus)
//                        .map(String::toUpperCase)
//                        .collect(Collectors.toSet());
//        if (statusList.size() == 1 && statusList.contains("FINISHED")) {
//            jobStatus = JobStatus.FINISHED.name();
//        } else if (statusList.contains("FAILED")) {
//            jobStatus = JobStatus.FAILED.name();
//        } else if (statusList.contains("CANCELED")) {
//            jobStatus = JobStatus.CANCELED.name();
//        } else if (statusList.contains("CANCELLING")) {
//            jobStatus = JobStatus.CANCELLING.name();
//        } else {
//            jobStatus = JobStatus.RUNNING.name();
//        }
//        jobInstance.setJobStatus(jobStatus);
//        jobInstance.setJobEngineId(jobEngineId);
//        jobInstance.setUpdateUserId(userId);
//        jobInstanceDao.update(jobInstance);
    }

    private Config buildTransformConfig(
            JobTask task, Config config, List<TableSchemaReq> inputSchemas) {
        try {
            Transform transform = Transform.valueOf(task.getConnectorType().toUpperCase());
            TransformOptions transformOption =
                    getTransformOption(transform, task.getTransformOptions());
            return TransformConfigSwitcherUtils.mergeTransformConfig(
                    transform, inputSchemas, config, transformOption);
        } catch (IOException e) {
            throw new SeatunnelException(SeatunnelErrorEnum.ILLEGAL_STATE, e.getMessage());
        }
    }

    private List<TableSchemaReq> findInputSchemas(
            List<JobTask> tasks, List<JobLine> lines, JobTask task) {
        ArrayList<String> outputSchemas = new ArrayList<>();
        lines.forEach(
                jobLine -> {
                    if (jobLine.getTargetPluginId().equals(task.getPluginId())) {
                        String inputPluginId = jobLine.getInputPluginId();
                        tasks.forEach(
                                jobTask -> {
                                    if (jobTask.getPluginId().equals(inputPluginId)) {
                                        outputSchemas.add(jobTask.getOutputSchema());
                                    }
                                });
                    }
                });

        checkArgument(outputSchemas.size() == 1, "input schema size must be 1");
        try {
            List<DatabaseTableSchemaReq> databaseTableSchemaReqs =
                    OBJECT_MAPPER.readValue(
                            outputSchemas.get(0),
                            new com.fasterxml.jackson.core.type.TypeReference<
                                    List<DatabaseTableSchemaReq>>() {});
            return databaseTableSchemaReqs.stream()
                    .map(
                            databaseTableSchemaReq -> {
                                TableSchemaReq tableSchemaReq = new TableSchemaReq();
                                tableSchemaReq.setTableName(databaseTableSchemaReq.getTableName());
                                tableSchemaReq.setFields(databaseTableSchemaReq.getFields());
                                return tableSchemaReq;
                            })
                    .collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new SeatunnelException(SeatunnelErrorEnum.ILLEGAL_STATE, e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Config mergeTaskConfig(
            JobTask task,
            PluginType pluginType,
            String connectorType,
            BusinessMode businessMode,
            Config connectorConfig,
            OptionRule optionRule)
            throws JsonProcessingException {

        Long datasourceInstanceId = task.getDataSourceId();
        String pluginName =
                datasourceService
                        .queryDatasourceDetailById(datasourceInstanceId.toString())
                        .getPluginName();
        Config datasourceConf =
                parseConfigWithOptionRule(
                        pluginType,
                        connectorType,
                        datasourceService.queryDatasourceConfigById(
                                datasourceInstanceId.toString()),
                        optionRule);

        DataSourceOption dataSourceOption = null;
        try {
            dataSourceOption =
                    task.getDataSourceOption() == null
                            ? null
                            : new ObjectMapper()
                            .readValue(task.getDataSourceOption(), DataSourceOption.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SelectTableFields selectTableFields = null;
        try {
            selectTableFields =
                    task.getSelectTableFields() == null
                            ? null
                            : new ObjectMapper()
                            .readValue(
                                    task.getSelectTableFields(), SelectTableFields.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SceneMode sceneMode =
                task.getSceneMode() == null ? null : SceneMode.valueOf(task.getSceneMode());
        VirtualTableDetailRes virtualTableDetailRes = null;

        if (!SceneMode.MULTIPLE_TABLE.equals(sceneMode)
                && dataSourceOption != null
                && CollectionUtils.isNotEmpty(dataSourceOption.getTables())) {
            String tableName = dataSourceOption.getTables().get(0);
            if (virtualTableService.containsVirtualTableByTableName(tableName)) {
                virtualTableDetailRes = virtualTableService.queryVirtualTableByTableName(tableName);
            }
        }

        return DataSourceConfigSwitcherUtils.mergeDatasourceConfig(
                pluginName,
                datasourceConf,
                virtualTableDetailRes,
                dataSourceOption,
                selectTableFields,
                businessMode,
                pluginType,
                connectorConfig);
    }

    private String createJobConfig(@NonNull JobVersion jobVersion) {
        List<JobTask> tasks = jobTaskDao.getTasksByVersionId(jobVersion.getId());
        List<JobLine> lines = jobLineDao.getLinesByVersionId(jobVersion.getId());
        return generateJobConfig(jobVersion.getJobId(), tasks, lines, jobVersion.getEnv());
    }

    private String getConnectorConfig(Map<String, List<Config>> connectorMap) {
        List<String> configs = new ArrayList<>();
        ConfigRenderOptions configRenderOptions =
                ConfigRenderOptions.defaults()
                        .setJson(false)
                        .setComments(false)
                        .setOriginComments(false);
        for (Map.Entry<String, List<Config>> entry : connectorMap.entrySet()) {
            for (Config c : entry.getValue()) {
                configs.add(
                        ConfigFactory.empty()
                                .withValue(entry.getKey(), c.root())
                                .root()
                                .render(configRenderOptions));
            }
        }
        return StringUtils.join(configs, "\n");
    }

    private Config addTableName(String tableName, JobLine jobLine, Config config) {
        return config.withValue(
                tableName, ConfigValueFactory.fromAnyRef("Table" + jobLine.getId()));
    }

    private Config filterEmptyValue(Config config) {
        List<String> removeKeys =
                config.entrySet().stream()
                        .filter(entry -> isEmptyValue(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
        for (String removeKey : removeKeys) {
            config = config.withoutPath(removeKey);
        }
        return config;
    }

    private void checkSceneMode(List<JobTask> tasks) {
        Set<String> sceneModes = new HashSet<>();
        Map<PluginType, Set<Long>> dataSourceIds = new HashMap<>();
        tasks.forEach(
                jobTask -> {
                    PluginType pluginType =
                            PluginType.valueOf(jobTask.getType().toUpperCase(Locale.ROOT));
                    if (pluginType.equals(PluginType.SOURCE)
                            || pluginType.equals(PluginType.SINK)) {
                        dataSourceIds
                                .computeIfAbsent(pluginType, n -> new HashSet<>())
                                .add(jobTask.getDataSourceId());
                        if (pluginType.equals(PluginType.SOURCE)) {
                            sceneModes.add(jobTask.getSceneMode());
                        }
                    }
                });

        if (sceneModes.size() != 1) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ERROR_CONFIG,
                    String.format(
                            "Does not support multiple sceneMode in a job, sceneModes: %s",
                            String.join(", ", sceneModes)));
        }
        SceneMode sceneMode =
                SceneMode.valueOf(sceneModes.iterator().next().toUpperCase(Locale.ROOT));
        dataSourceIds.forEach(
                (pluginType, dataSourceIdList) -> {
                    dataSourceIdList.forEach(
                            id -> {
                                String pluginName =
                                        datasourceService
                                                .queryDatasourceDetailById(id.toString())
                                                .getPluginName();

                                List<SceneMode> supportedSceneMode =
                                        dataSourceMapperConfig
                                                .supportedSceneMode(pluginName, pluginType)
                                                .orElseThrow(
                                                        () ->
                                                                new SeatunnelException(
                                                                        SeatunnelErrorEnum
                                                                                .ILLEGAL_STATE,
                                                                        "Unsupported Data connector Name"));
                                if (!supportedSceneMode.contains(sceneMode)) {
                                    throw new SeatunnelException(
                                            SeatunnelErrorEnum.ERROR_CONFIG,
                                            String.format(
                                                    "%s not support %s sceneMode",
                                                    pluginName, sceneMode));
                                }
                            });
                });
    }

    private boolean isEmptyValue(ConfigValue value) {
        return value.unwrapped().toString().isEmpty()
                || value.valueType().equals(ConfigValueType.NULL);
    }

    private Config parseConfigWithOptionRule(
            PluginType pluginType, String connectorType, String config, OptionRule optionRule) {
        return parseConfigWithOptionRule(
                pluginType, connectorType, ConfigFactory.parseString(config), optionRule);
    }

    private Config parseConfigWithOptionRule(
            PluginType pluginType, String connectorType, Config config, OptionRule optionRule) {
        Map<String, TypeReference<?>> typeReferenceMap = new HashMap<>();
        optionRule
                .getOptionalOptions()
                .forEach(option -> typeReferenceMap.put(option.key(), option.typeReference()));
        optionRule
                .getRequiredOptions()
                .forEach(
                        options -> {
                            options.getOptions()
                                    .forEach(
                                            option -> {
                                                typeReferenceMap.put(
                                                        option.key(), option.typeReference());
                                            });
                        });

        Map<String, ConfigObject> needReplaceMap = new HashMap<>();
        Map<String, ConfigValue> needReplaceList = new HashMap<>();

        config.entrySet()
                .forEach(
                        entry -> {
                            String key = entry.getKey();
                            ConfigValue configValue = entry.getValue();
                            try {
                                if (typeReferenceMap.containsKey(key)
                                        && isComplexType(typeReferenceMap.get(key))
                                        && !isEmptyValue(configValue)) {
                                    String valueStr = configValue.unwrapped().toString();
                                    if (typeReferenceMap
                                            .get(key)
                                            .getType()
                                            .getTypeName()
                                            .startsWith("java.util.List")
                                            || typeReferenceMap
                                            .get(key)
                                            .getType()
                                            .getTypeName()
                                            .startsWith(
                                                    "org.apache.seatunnel.api.configuration.Options")) {
                                        String valueWrapper = "{key=" + valueStr + "}";
                                        ConfigValue configList =
                                                ConfigFactory.parseString(valueWrapper)
                                                        .getList("key");
                                        needReplaceList.put(key, configList);
                                    } else {
                                        Config configObject = ConfigFactory.parseString(valueStr);
                                        needReplaceMap.put(key, configObject.root());
                                    }
                                }
                            } catch (Exception e) {
                                throw new SeatunnelException(
                                        SeatunnelErrorEnum.ERROR_CONFIG,
                                        String.format(
                                                "Plugin Type: %s, Connector Type: %s, Key: %s, Error Info: %s",
                                                pluginType, connectorType, key, e.getMessage()));
                            }
                        });
        for (Map.Entry<String, ConfigObject> entry : needReplaceMap.entrySet()) {
            config = config.withValue(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ConfigValue> entry : needReplaceList.entrySet()) {
            config = config.withValue(entry.getKey(), entry.getValue());
        }
        return config;
    }

    private boolean isComplexType(TypeReference<?> typeReference) {
        return typeReference.getType().getTypeName().startsWith("java.util.List")
                || typeReference.getType().getTypeName().startsWith("java.util.Map")
                || typeReference
                .getType()
                .getTypeName()
                .startsWith("org.apache.seatunnel.api.configuration.Options");
    }
}
