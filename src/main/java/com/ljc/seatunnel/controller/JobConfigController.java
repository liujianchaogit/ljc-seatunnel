package com.ljc.seatunnel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.response.job.JobConfigRes;
import com.ljc.seatunnel.service.IJobConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seatunnel/api/v1/job/config")
public class JobConfigController {

    @Autowired
    private IJobConfigService jobConfigService;

    @GetMapping("/{jobVersionId}")
    Result<JobConfigRes> getJobConfig(@PathVariable long jobVersionId)
            throws JsonProcessingException {
        return Result.success(jobConfigService.getJobConfig(jobVersionId));
    }
}
