package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.datasource.plugins.api.DataSourcePluginInfo;
import com.ljc.seatunnel.datasource.plugins.model.TableField;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.dto.datasource.DatabaseTableFields;
import com.ljc.seatunnel.domain.dto.datasource.DatabaseTables;
import com.ljc.seatunnel.domain.dto.datasource.TableInfo;
import com.ljc.seatunnel.domain.request.datasource.DatasourceCheckReq;
import com.ljc.seatunnel.domain.response.datasource.DatasourceDetailRes;
import com.ljc.seatunnel.domain.response.datasource.DatasourceRes;
import com.ljc.seatunnel.service.IDatasourceService;
import com.ljc.seatunnel.utils.CartesianProductUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seatunnel/api/v1/datasource")
public class SeatunnelDatasourceController {

    @Autowired
    private IDatasourceService datasourceService;
//    private static final String WS_SOURCE = "WS";
//    private static final List<String> wsSupportDatasources =
//            PropertyUtils.getList("ws.support.datasources", ",");

    @PostMapping("/check/connect")
    Result<Boolean> testConnect(
            @RequestBody DatasourceCheckReq req) {

        // Map<String, String> stringStringMap = JSONUtils.toMap(req.getDatasourceConfig());
        return Result.success(
                datasourceService.testDatasourceConnectionAble(
                        1,
                        req.getPluginName(),
                        "1.0.0",
                        req.getDatasourceConfig()));
    }

    @GetMapping("/{id}")
    Result<DatasourceDetailRes> getDatasource(@PathVariable("id") String id) {
        return Result.success(datasourceService.queryDatasourceDetailById(id));
    }

    @GetMapping("/list")
    Result<PageInfo<DatasourceRes>> getDatasourceList(
            @RequestParam("searchVal") String searchVal,
            @RequestParam("pluginName") String pluginName,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        PageInfo<DatasourceRes> datasourceResPageInfo =
                datasourceService.queryDatasourceList(
                        1, searchVal, pluginName, pageNo, pageSize);
        if (CollectionUtils.isNotEmpty(datasourceResPageInfo.getData())) {
            Map<Integer, String> userIdNameMap = new HashMap<>();
            datasourceResPageInfo
                    .getData()
                    .forEach(
                            datasourceRes -> {
                                Map<String, String> datasourceConfig =
                                        datasourceRes.getDatasourceConfig();
                                Optional.ofNullable(
                                                MapUtils.getString(
                                                        datasourceConfig, "password"))
                                        .ifPresent(
                                                password -> {
                                                    datasourceConfig.put(
                                                            "password",
                                                            CartesianProductUtils.maskPassword(
                                                                    password));
                                                });
                                datasourceRes.setDatasourceConfig(datasourceConfig);
                                datasourceRes.setCreateUserName(
                                        userIdNameMap.getOrDefault(
                                                datasourceRes.getCreateUserId(), ""));
                                datasourceRes.setUpdateUserName(
                                        userIdNameMap.getOrDefault(
                                                datasourceRes.getUpdateUserId(), ""));
                            });
        }
        return Result.success(datasourceResPageInfo);
    }

    @GetMapping("/support-datasources")
    Result<Map<Integer, List<DataSourcePluginInfo>>> getSupportDatasources(
            @RequestParam("showVirtualDataSource") Boolean showVirtualDataSource,
            @RequestParam(value = "source", required = false) String source) {
        Map<Integer, List<DataSourcePluginInfo>> allDatasources =
                datasourceService.queryAllDatasourcesGroupByType(showVirtualDataSource);
        // default source is WS
//        if (StringUtils.isEmpty(source) || source.equals(WS_SOURCE)) {
//            allDatasources.forEach(
//                    (k, typeList) -> {
//                        typeList =
//                                typeList.stream()
//                                        .filter(
//                                                plugin ->
//                                                        wsSupportDatasources.contains(
//                                                                plugin.getName()))
//                                        .collect(Collectors.toList());
//                        allDatasources.put(k, typeList);
//                    });
//        }
        return Result.success(allDatasources);
    }

    @GetMapping("/dynamic-form")
    Result<String> getDynamicForm(@RequestParam("pluginName") String pluginName) {
        return Result.success(datasourceService.getDynamicForm(pluginName));
    }

    @GetMapping("/databases")
    Result<List<String>> getDatabases(
            @RequestParam("datasourceName") String datasourceName) {
        return Result.success(datasourceService.queryDatabaseByDatasourceName(datasourceName));
    }

    @GetMapping("/tables")
    Result<List<String>> getTableNames(
            @RequestParam("datasourceName") String datasourceName,
            @RequestParam("databaseName") String databaseName) {
        return Result.success(datasourceService.queryTableNames(datasourceName, databaseName));
    }

    @PostMapping("/schemas")
    Result<List<DatabaseTableFields>> getMultiTableFields(
            @RequestParam("datasourceId") String datasourceId,
            @RequestBody List<DatabaseTables> tableNames) {
        DatasourceDetailRes res = datasourceService.queryDatasourceDetailById(datasourceId);
        List<DatabaseTableFields> tableFields = new ArrayList<>();
        tableNames.forEach(
                database -> {
                    List<TableInfo> tableInfos = new ArrayList<>();
                    database.getTables()
                            .forEach(
                                    tableName -> {
                                        List<TableField> tableField =
                                                datasourceService.queryTableSchema(
                                                        res.getDatasourceName(),
                                                        database.getDatabase(),
                                                        tableName);
                                        tableInfos.add(new TableInfo(tableName, tableField));
                                    });
                    tableFields.add(new DatabaseTableFields(database.getDatabase(), tableInfos));
                });
        return Result.success(tableFields);
    }
}
