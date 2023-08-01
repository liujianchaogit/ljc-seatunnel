package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.request.job.JobTaskInfo;
import com.ljc.seatunnel.service.IJobTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seatunnel/api/v1/job/")
public class JobTaskController {

    @Autowired
    private IJobTaskService jobTaskService;

    @GetMapping("/{jobVersionId}")
    Result<JobTaskInfo> getJob(@PathVariable long jobVersionId) {
        return Result.success(jobTaskService.getTaskConfig(jobVersionId));
    }
}
