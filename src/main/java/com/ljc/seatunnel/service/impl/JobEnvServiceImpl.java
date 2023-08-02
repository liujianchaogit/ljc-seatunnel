package com.ljc.seatunnel.service.impl;

import com.ljc.seatunnel.bean.env.JobEnvCache;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.service.IJobEnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobEnvServiceImpl implements IJobEnvService {

    @Autowired
    private JobEnvCache jobEnvCache;

    @Override
    public FormStructure getJobEnvFormStructure() {
        return this.jobEnvCache.getEnvFormStructure();
    }

}
