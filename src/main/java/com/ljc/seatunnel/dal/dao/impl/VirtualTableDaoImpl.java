package com.ljc.seatunnel.dal.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.seatunnel.dal.dao.IVirtualTableDao;
import com.ljc.seatunnel.dal.entity.VirtualTable;
import com.ljc.seatunnel.dal.mapper.VirtualTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class VirtualTableDaoImpl implements IVirtualTableDao {

    @Autowired
    private VirtualTableMapper virtualTableMapper;

    @Override
    public boolean insertVirtualTable(VirtualTable virtualTable) {
        return virtualTableMapper.insert(virtualTable) > 0;
    }

    @Override
    public VirtualTable selectVirtualTableByTableName(String tableName) {
        return virtualTableMapper.selectOne(
                new QueryWrapper<VirtualTable>().eq("virtual_table_name", tableName));
    }

    @Override
    public boolean checkVirtualTableNameUnique(String virtualTableName, String databaseName, Long tableId) {
        return virtualTableMapper.checkVirtualTableNameUnique(
                tableId, databaseName, virtualTableName)
                <= 0;
    }

    @Override
    public List<String> getVirtualTableNames(String databaseName, Long datasourceId) {
        List<VirtualTable> result =
                virtualTableMapper.selectList(
                        new QueryWrapper<VirtualTable>()
                                .select("virtual_table_name")
                                .eq("datasource_id", datasourceId)
                                .eq("virtual_database_name", databaseName));
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result.stream().map(VirtualTable::getVirtualTableName).collect(Collectors.toList());
    }

    @Override
    public IPage<VirtualTable> selectVirtualTablePage(Page<VirtualTable> page, String pluginName, String datasourceName) {
        log.debug(
                "======================pluginName:{}, datasourceName:{}",
                pluginName,
                datasourceName);
        if (StringUtils.isBlank(pluginName) && StringUtils.isBlank(datasourceName)) {
            return virtualTableMapper.selectPage(
                    page, new QueryWrapper<VirtualTable>().orderByDesc("create_time"));
        }
        return virtualTableMapper.selectVirtualTablePageByParam(page, pluginName, datasourceName);
    }

    @Override
    public boolean checkHasVirtualTable(Long datasourceId) {
        return virtualTableMapper.selectCount(
                new QueryWrapper<VirtualTable>().eq("datasource_id", datasourceId))
                > 0;
    }
}
