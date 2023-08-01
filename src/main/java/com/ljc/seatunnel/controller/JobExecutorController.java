package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.service.IJobExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/seatunnel/api/v1/job/executor")
@RestController
public class JobExecutorController {
    @Autowired
    IJobExecutorService jobExecutorService;

    @GetMapping("/execute")
    public Result<Long> jobExecutor(@RequestParam("jobDefineId")
            Long jobDefineId) {
        return jobExecutorService.jobExecute(1, jobDefineId);
    }
}
