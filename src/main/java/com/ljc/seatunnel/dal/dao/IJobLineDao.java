package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobLine;

import java.util.List;

public interface IJobLineDao {
    List<JobLine> getLinesByVersionId(long jobVersionId);
}
