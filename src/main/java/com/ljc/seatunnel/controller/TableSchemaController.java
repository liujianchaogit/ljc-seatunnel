package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.service.ITableSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seatunnel/api/v1/job/table")
public class TableSchemaController {

    @Autowired
    private ITableSchemaService tableSchemaService;

    @PostMapping("/check")
    public Result<DataSourceOption> checkDatabaseAndTable(
            @RequestParam String datasourceId, @RequestBody DataSourceOption dataSourceOption) {
        return Result.success(
                tableSchemaService.checkDatabaseAndTable(datasourceId, dataSourceOption));
    }

    @GetMapping("/column-projection")
    Result<Boolean> queryColumnProjection(@RequestParam String pluginName) {
        return Result.success(tableSchemaService.getColumnProjection(pluginName));
    }
}
