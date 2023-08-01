package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.VirtualTable;

import java.util.List;

public interface IVirtualTableDao {

    VirtualTable selectVirtualTableByTableName(String tableName);

    List<String> getVirtualTableNames(String databaseName, Long datasourceId);

    boolean checkHasVirtualTable(Long datasourceId);
}
