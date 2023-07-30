package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.VirtualTable;

public interface IVirtualTableDao {

    VirtualTable selectVirtualTableByTableName(String tableName);

}
