package com.ljc.seatunnel.service;

import com.ljc.seatunnel.common.CodeGenerateUtils;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.request.datasource.VirtualTableReq;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableRes;

public interface IVirtualTableService {

    String createVirtualTable(Integer userId, VirtualTableReq req)
            throws CodeGenerateUtils.CodeGenerateException;

    boolean containsVirtualTableByTableName(String tableName);

    VirtualTableDetailRes queryVirtualTableByTableName(String tableName);

    String queryTableDynamicTable(String pluginName);

    PageInfo<VirtualTableRes> getVirtualTableList(
            String pluginName, String datasourceName, Integer pageNo, Integer pageSize);

}
