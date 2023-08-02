package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.request.job.JobDAG;
import com.ljc.seatunnel.domain.request.job.JobTaskInfo;
import com.ljc.seatunnel.domain.request.job.PluginConfig;
import com.ljc.seatunnel.domain.response.job.JobTaskCheckRes;
import com.ljc.seatunnel.service.IJobTaskService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seatunnel/api/v1/job/")
public class JobTaskController {

    @Autowired
    private IJobTaskService jobTaskService;

    @PostMapping("/dag/{jobVersionId}")
    @ApiOperation(value = "save job dag", httpMethod = "POST")
    Result<JobTaskCheckRes> saveJobDAG(
             @PathVariable long jobVersionId,
             @RequestBody JobDAG jobDAG) {
        return Result.success(jobTaskService.saveJobDAG(jobVersionId, jobDAG));
    }

    @GetMapping("/{jobVersionId}")
    Result<JobTaskInfo> getJob(@PathVariable long jobVersionId) {
        return Result.success(jobTaskService.getTaskConfig(jobVersionId));
    }

    @PostMapping("/task/{jobVersionId}")
    Result<Void> saveSingleTask(
            @ApiParam(value = "job version id", required = true) @PathVariable long jobVersionId,
            @ApiParam(value = "task info", required = true) @RequestBody
            PluginConfig pluginConfig) {
        jobTaskService.saveSingleTask(jobVersionId, pluginConfig);
        return Result.success();
    }

    @GetMapping("/task/{jobVersionId}")
    Result<PluginConfig> getSingleTask(
            @ApiParam(value = "job version id", required = true) @PathVariable long jobVersionId,
            @ApiParam(value = "task plugin id", required = true) @RequestParam String pluginId) {
        return Result.success(jobTaskService.getSingleTask(jobVersionId, pluginId));
    }
}
