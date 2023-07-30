package com.ljc.seatunnel.dal.dao.impl;

import com.ljc.seatunnel.dal.dao.IJobDefinitionDao;
import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.dal.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobDefinitionDaoImpl implements IJobDefinitionDao {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public JobDefinition getJob(long id) {
        return jobMapper.selectById(id);
    }
}
