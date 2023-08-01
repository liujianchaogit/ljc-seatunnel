package com.ljc.seatunnel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.seatunnel.common.CodeGenerateUtils;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.config.ConnectorDataSourceMapperConfig;
import com.ljc.seatunnel.dal.dao.IDatasourceDao;
import com.ljc.seatunnel.dal.dao.IVirtualTableDao;
import com.ljc.seatunnel.dal.entity.Datasource;
import com.ljc.seatunnel.dal.entity.VirtualTable;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.request.datasource.VirtualTableFieldReq;
import com.ljc.seatunnel.domain.request.datasource.VirtualTableReq;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableFieldRes;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableRes;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.service.IVirtualTableService;
import com.ljc.seatunnel.thirdparty.datasource.DataSourceClientFactory;
import com.ljc.seatunnel.utils.SeaTunnelOptionRuleWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VirtualTableServiceImpl implements IVirtualTableService {

    @Resource(name = "virtualTableDaoImpl")
    IVirtualTableDao virtualTableDao;
    @Autowired
    private IDatasourceDao datasourceDao;
    @Autowired
    private ConnectorDataSourceMapperConfig dataSourceMapperConfig;

    @Override
    public String createVirtualTable(Integer userId, VirtualTableReq req) throws CodeGenerateUtils.CodeGenerateException {
        long uuid = CodeGenerateUtils.getInstance().genCode();
        Long datasourceId = Long.valueOf(req.getDatasourceId());
        boolean isUnique =
                virtualTableDao.checkVirtualTableNameUnique(
                        req.getTableName(), req.getDatabaseName(), 0L);
        if (!isUnique) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.VIRTUAL_TABLE_ALREADY_EXISTS, req.getTableName());
        }

        VirtualTable virtualTable =
                VirtualTable.builder()
                        .id(uuid)
                        .datasourceId(datasourceId)
                        .virtualDatabaseName(req.getDatabaseName())
                        .virtualTableName(req.getTableName())
                        .description(req.getDescription())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .createUserId(userId)
                        .updateUserId(userId)
                        .build();
        if (CollectionUtils.isEmpty(req.getTableFields())) {
            throw new SeatunnelException(SeatunnelErrorEnum.VIRTUAL_TABLE_FIELD_EMPTY);
        }
        String fieldJson = convertTableFields(req.getTableFields());
        virtualTable.setTableFields(fieldJson);
        virtualTable.setVirtualTableConfig(JsonUtils.toJsonString(req.getDatabaseProperties()));

        boolean success = virtualTableDao.insertVirtualTable(virtualTable);
        if (!success) {
            throw new SeatunnelException(SeatunnelErrorEnum.VIRTUAL_TABLE_CREATE_FAILED);
        }
        return String.valueOf(uuid);
    }

    private String convertTableFields(List<VirtualTableFieldReq> tableFields) {
        List<VirtualTableFieldRes> fieldList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tableFields)) {
            for (VirtualTableFieldReq field : tableFields) {
                VirtualTableFieldRes fieldRes =
                        VirtualTableFieldRes.builder()
                                .fieldName(field.getFieldName())
                                .fieldType(field.getFieldType())
                                .fieldExtra(field.getFieldExtra())
                                .defaultValue(field.getDefaultValue())
                                .primaryKey(field.getPrimaryKey())
                                .nullable(field.getNullable())
                                .fieldComment(field.getFieldComment())
                                .build();
                fieldList.add(fieldRes);
            }
        }
        return JsonUtils.toJsonString(fieldList);
    }

    @Override
    public boolean containsVirtualTableByTableName(String tableName) {
        return null != virtualTableDao.selectVirtualTableByTableName(tableName);
    }

    @Override
    public VirtualTableDetailRes queryVirtualTableByTableName(String tableName) {
        VirtualTable virtualTable = virtualTableDao.selectVirtualTableByTableName(tableName);
        return buildVirtualTableDetailRes(virtualTable);
    }

    @Override
    public String queryTableDynamicTable(String pluginName) {
        OptionRule rule =
                DataSourceClientFactory.getDataSourceClient().queryMetadataFieldByName(pluginName);
        if (null == rule) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_EXISTS);
        }
        String connectorForDatasourceName =
                dataSourceMapperConfig
                        .findConnectorForDatasourceName(pluginName)
                        .orElseThrow(
                                () ->
                                        new SeatunnelException(
                                                SeatunnelErrorEnum
                                                        .CAN_NOT_FOUND_CONNECTOR_FOR_DATASOURCE,
                                                pluginName));
        FormStructure form = SeaTunnelOptionRuleWrapper.wrapper(rule, connectorForDatasourceName);
        return JsonUtils.toJsonString(form);
    }

    @Override
    public PageInfo<VirtualTableRes> getVirtualTableList(String pluginName, String datasourceName, Integer pageNo, Integer pageSize) {
        Page<VirtualTable> page = new Page<>(pageNo, pageSize);
        IPage<VirtualTable> iPage =
                virtualTableDao.selectVirtualTablePage(page, pluginName, datasourceName);

        PageInfo<VirtualTableRes> pageInfo = new PageInfo<>();
        pageInfo.setPageNo((int) iPage.getPages());
        pageInfo.setPageSize((int) iPage.getSize());
        pageInfo.setTotalCount((int) iPage.getTotal());

        List<VirtualTableRes> resList = new ArrayList<>();
        List<Long> datasourceIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(iPage.getRecords())) {
            pageInfo.setData(resList);
            return pageInfo;
        }
        iPage.getRecords()
                .forEach(
                        virtualTable -> {
                            datasourceIds.add(virtualTable.getDatasourceId());
                        });
        List<Datasource> datasourceList = datasourceDao.selectDatasourceByIds(datasourceIds);
        Map<Long, String> datasourceMap =
                datasourceList.stream()
                        .collect(
                                Collectors.toMap(Datasource::getId, Datasource::getDatasourceName));
        Map<Long, String> datasourcePluginNameMap =
                datasourceList.stream()
                        .collect(Collectors.toMap(Datasource::getId, Datasource::getPluginName));
        iPage.getRecords()
                .forEach(
                        virtualTable -> {
                            VirtualTableRes res = new VirtualTableRes();
                            res.setTableId(String.valueOf(virtualTable.getId()));
                            res.setTableName(virtualTable.getVirtualTableName());
                            res.setDatabaseName(virtualTable.getVirtualDatabaseName());
                            res.setDescription(virtualTable.getDescription());
                            res.setDatasourceId(String.valueOf(virtualTable.getDatasourceId()));
                            res.setCreateUserId(virtualTable.getCreateUserId());
                            res.setUpdateUserId(virtualTable.getUpdateUserId());
                            res.setCreateTime(virtualTable.getCreateTime());
                            res.setUpdateTime(virtualTable.getUpdateTime());

                            res.setDatasourceName(
                                    datasourceMap.get(virtualTable.getDatasourceId()));
                            res.setPluginName(
                                    datasourcePluginNameMap.get(virtualTable.getDatasourceId()));
                            resList.add(res);
                        });
        pageInfo.setData(resList);
        return pageInfo;
    }

    private VirtualTableDetailRes buildVirtualTableDetailRes(VirtualTable virtualTable) {
        if (null == virtualTable) {
            throw new SeatunnelException(SeatunnelErrorEnum.VIRTUAL_TABLE_NOT_EXISTS);
        }
        Datasource datasource = datasourceDao.selectDatasourceById(virtualTable.getDatasourceId());
        if (null == datasource) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_EXISTS);
        }

        VirtualTableDetailRes res = new VirtualTableDetailRes();
        res.setTableId(String.valueOf(virtualTable.getId()));
        res.setTableName(virtualTable.getVirtualTableName());
        res.setDatabaseName(virtualTable.getVirtualDatabaseName());
        res.setDescription(virtualTable.getDescription());
        res.setDatasourceId(String.valueOf(virtualTable.getDatasourceId()));
        res.setPluginName(datasource.getPluginName());
        res.setCreateTime(virtualTable.getCreateTime());
        res.setUpdateTime(virtualTable.getUpdateTime());
        res.setDatasourceName(datasource.getDatasourceName());
        res.setDatasourceProperties(JsonUtils.toMap(virtualTable.getVirtualTableConfig()));

        List<VirtualTableFieldRes> fields =
                JsonUtils.toList(virtualTable.getTableFields(), VirtualTableFieldRes.class);
        res.setFields(fields);
        return res;
    }
}
