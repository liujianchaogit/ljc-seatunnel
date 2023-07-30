package com.ljc.seatunnel.dal.dao.impl;

import com.ljc.seatunnel.dal.dao.IDatasourceDao;
import com.ljc.seatunnel.dal.entity.Datasource;
import com.ljc.seatunnel.dal.mapper.DatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DatasourceDaoImpl implements IDatasourceDao {

    @Autowired
    private DatasourceMapper datasourceMapper;

    @Override
    public Datasource selectDatasourceById(Long id) {
        return datasourceMapper.selectById(id);
    }
}
