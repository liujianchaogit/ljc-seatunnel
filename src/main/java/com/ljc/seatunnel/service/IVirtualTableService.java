package com.ljc.seatunnel.service;

import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;

public interface IVirtualTableService {

    boolean containsVirtualTableByTableName(String tableName);

    VirtualTableDetailRes queryVirtualTableByTableName(String tableName);
}
