package com.ljc.seatunnel.service.impl;

import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.dal.dao.IDatasourceDao;
import com.ljc.seatunnel.dal.dao.IVirtualTableDao;
import com.ljc.seatunnel.dal.entity.Datasource;
import com.ljc.seatunnel.dal.entity.VirtualTable;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableFieldRes;
import com.ljc.seatunnel.service.IVirtualTableService;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VirtualTableServiceImpl implements IVirtualTableService {

    @Resource(name = "virtualTableDaoImpl")
    IVirtualTableDao virtualTableDao;
    @Autowired
    private IDatasourceDao datasourceDao;

    @Override
    public boolean containsVirtualTableByTableName(String tableName) {
        return null != virtualTableDao.selectVirtualTableByTableName(tableName);
    }

    @Override
    public VirtualTableDetailRes queryVirtualTableByTableName(String tableName) {
        VirtualTable virtualTable = virtualTableDao.selectVirtualTableByTableName(tableName);
        return buildVirtualTableDetailRes(virtualTable);
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
