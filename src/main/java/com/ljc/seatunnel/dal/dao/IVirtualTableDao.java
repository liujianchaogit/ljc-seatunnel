package com.ljc.seatunnel.dal.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.seatunnel.dal.entity.VirtualTable;

import java.util.List;

public interface IVirtualTableDao {

    boolean insertVirtualTable(VirtualTable virtualTable);

    boolean updateVirtualTable(VirtualTable virtualTable);

    VirtualTable selectVirtualTableById(Long id);

    VirtualTable selectVirtualTableByTableName(String tableName);

    boolean checkVirtualTableNameUnique(String virtualTableName, String databaseName, Long tableId);

    List<String> getVirtualTableNames(String databaseName, Long datasourceId);

    IPage<VirtualTable> selectVirtualTablePage(
            Page<VirtualTable> page, String pluginName, String datasourceName);

    boolean checkHasVirtualTable(Long datasourceId);
}
