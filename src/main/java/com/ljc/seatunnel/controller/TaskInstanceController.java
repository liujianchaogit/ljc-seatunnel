package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.dto.job.SeaTunnelJobInstanceDto;
import com.ljc.seatunnel.service.ITaskInstanceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/seatunnel/api/v1/task")
@RestController
public class TaskInstanceController {

    @Autowired
    ITaskInstanceService taskInstanceService;

    @GetMapping("/jobMetrics")
    @ApiOperation(value = "get the jobMetrics list ", httpMethod = "GET")
    public Result<PageInfo<SeaTunnelJobInstanceDto>> getTaskInstanceList(
            @RequestParam(name = "jobDefineName", required = false) String jobDefineName,
            @RequestParam(name = "executorName", required = false) String executorName,
            @RequestParam(name = "stateType", required = false) String stateType,
            @RequestParam(name = "startDate", required = false) String startTime,
            @RequestParam(name = "endDate", required = false) String endTime,
            @RequestParam("syncTaskType") String syncTaskType,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        Result<PageInfo<SeaTunnelJobInstanceDto>> result =
                taskInstanceService.getSyncTaskInstancePaging(
                        2,
                        jobDefineName,
                        executorName,
                        stateType,
                        startTime,
                        endTime,
                        syncTaskType,
                        pageNo,
                        pageSize);

        return result;
    }
}
