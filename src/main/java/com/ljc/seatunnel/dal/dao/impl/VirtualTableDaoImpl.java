package com.ljc.seatunnel.dal.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ljc.seatunnel.dal.dao.IVirtualTableDao;
import com.ljc.seatunnel.dal.entity.VirtualTable;
import com.ljc.seatunnel.dal.mapper.VirtualTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class VirtualTableDaoImpl implements IVirtualTableDao {

    @Autowired
    private VirtualTableMapper virtualTableMapper;

    @Override
    public VirtualTable selectVirtualTableByTableName(String tableName) {
        return virtualTableMapper.selectOne(
                new QueryWrapper<VirtualTable>().eq("virtual_table_name", tableName));
    }
}
