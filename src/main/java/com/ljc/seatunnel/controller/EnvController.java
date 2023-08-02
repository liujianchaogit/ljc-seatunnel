package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.service.IJobEnvService;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/seatunnel/api/v1/job/env")
@RestController
public class EnvController {

    @Resource private IJobEnvService jobEnvService;

    @GetMapping("")
    public Result<String> getJobEnvFormStructure() {
        return Result.success(JsonUtils.toJsonString(jobEnvService.getJobEnvFormStructure()));
    }
}
