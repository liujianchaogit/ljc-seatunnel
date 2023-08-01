package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.dal.entity.JobDefinition;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.response.job.JobDefinitionRes;
import com.ljc.seatunnel.service.IJobDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seatunnel/api/v1/job/definition")
public class JobDefinitionController {

    @Autowired
    private IJobDefinitionService jobService;

    @GetMapping
    Result<PageInfo<JobDefinitionRes>> getJobDefinition(
            @RequestParam(required = false) String searchName,
            @RequestParam Integer pageNo,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String jobMode) {
        return Result.success(jobService.getJob(searchName, pageNo, pageSize, jobMode));
    }

    @GetMapping("/{jobId}")
    Result<JobDefinition> getJobDefinition(@PathVariable long jobId) {
        return Result.success(jobService.getJobDefinitionByJobId(jobId));
    }

}
