package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.bean.engine.EngineDataType;
import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.service.IEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/seatunnel/api/v1/engine")
@RestController
public class EngineController {

    @Autowired
    private IEngineService engineService;

    @GetMapping("/type")
    public Result<List<String>> listSupportDataTypes() {
        return Result.success(
                Arrays.stream(engineService.listSupportDataTypes())
                        .map(EngineDataType.DataType::getName)
                        .collect(Collectors.toList()));
    }

}
