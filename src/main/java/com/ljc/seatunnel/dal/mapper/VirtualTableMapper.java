package com.ljc.seatunnel.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ljc.seatunnel.dal.entity.VirtualTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VirtualTableMapper extends BaseMapper<VirtualTable> {
    IPage<VirtualTable> selectVirtualTablePageByParam(
            IPage<VirtualTable> page,
            @Param("pluginName") String pluginName,
            @Param("datasourceName") String datasourceName);

    int checkVirtualTableNameUnique(
            @Param("tableId") Long tableId,
            @Param("virtualDatabaseName") String databaseName,
            @Param("virtualTableName") String virtualTableName);
}
