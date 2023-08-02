package com.ljc.seatunnel.dal.dao;

import com.ljc.seatunnel.dal.entity.JobLine;

import java.util.List;

public interface IJobLineDao {

    void deleteLinesByVersionId(long jobVersionId);

    void insertLines(List<JobLine> lines);
    List<JobLine> getLinesByVersionId(long jobVersionId);
}
