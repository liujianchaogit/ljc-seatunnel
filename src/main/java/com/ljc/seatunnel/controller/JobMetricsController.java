package com.ljc.seatunnel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.response.metrics.JobDAG;
import com.ljc.seatunnel.domain.response.metrics.JobPipelineDetailMetricsRes;
import com.ljc.seatunnel.domain.response.metrics.JobPipelineSummaryMetricsRes;
import com.ljc.seatunnel.service.IJobMetricsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RequestMapping("/seatunnel/api/v1/job/metrics")
@RestController
public class JobMetricsController {

    @Resource private IJobMetricsService jobMetricsService;

    @GetMapping("/detail")
    @ApiOperation(value = "get the job pipeline detail metrics", httpMethod = "GET")
    public Result<List<JobPipelineDetailMetricsRes>> detail(@RequestParam Long jobInstanceId)
            throws IOException {
        return Result.success(
                jobMetricsService.getJobPipelineDetailMetricsRes(2, jobInstanceId));
    }

    @GetMapping("/dag")
    @ApiOperation(value = "get the job pipeline dag", httpMethod = "GET")
    public Result<JobDAG> getJobDAG(@RequestParam Long jobInstanceId)
            throws JsonProcessingException {

        return Result.success(jobMetricsService.getJobDAG(2, jobInstanceId));
    }

    @GetMapping("/summary")
    @ApiOperation(value = "get the job pipeline summary metrics", httpMethod = "GET")
    public Result<List<JobPipelineSummaryMetricsRes>> summary(@RequestParam Long jobInstanceId)
            throws IOException {
        return Result.success(
                jobMetricsService.getJobPipelineSummaryMetrics(2, jobInstanceId));
    }
}
