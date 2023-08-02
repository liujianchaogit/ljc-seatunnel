package com.ljc.seatunnel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.request.job.JobConfig;
import com.ljc.seatunnel.domain.response.job.JobConfigRes;
import com.ljc.seatunnel.service.IJobConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seatunnel/api/v1/job/config")
public class JobConfigController {

    @Autowired
    private IJobConfigService jobConfigService;

    @PutMapping("/{jobVersionId}")
    Result<Void> updateJobConfig(
            @PathVariable long jobVersionId,
            @RequestBody JobConfig jobConfig)
            throws JsonProcessingException {
        jobConfigService.updateJobConfig(1, jobVersionId, jobConfig);
        return Result.success();
    }

    @GetMapping("/{jobVersionId}")
    Result<JobConfigRes> getJobConfig(@PathVariable long jobVersionId)
            throws JsonProcessingException {
        return Result.success(jobConfigService.getJobConfig(jobVersionId));
    }
}
