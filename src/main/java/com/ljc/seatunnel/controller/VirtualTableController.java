package com.ljc.seatunnel.controller;

import com.ljc.seatunnel.common.Result;
import com.ljc.seatunnel.domain.PageInfo;
import com.ljc.seatunnel.domain.request.datasource.VirtualTableReq;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableRes;
import com.ljc.seatunnel.service.IVirtualTableService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/seatunnel/api/v1/virtual_table")
public class VirtualTableController {

    @Autowired
    private IVirtualTableService virtualTableService;

    @PostMapping("/create")
    Result<String> createVirtualTable(
            @RequestBody VirtualTableReq tableReq) {
        return Result.success(virtualTableService.createVirtualTable(1, tableReq));
    }

    @PutMapping("/{id}")
    Result<Boolean> updateVirtualTable(
            @PathVariable("id") String id,
            @RequestBody VirtualTableReq tableReq) {
        return Result.success(
                virtualTableService.updateVirtualTable(1, id, tableReq));
    }

    @GetMapping("/{id}")
    Result<VirtualTableDetailRes> queryVirtualTable(
            @PathVariable("id") String id) {
        return Result.success(virtualTableService.queryVirtualTable(id));
    }

    @GetMapping("/list")
    Result<PageInfo<VirtualTableRes>> getVirtualTableList(
            @RequestParam("pluginName") String pluginName,
            @RequestParam("datasourceName") String datasourceName,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        PageInfo<VirtualTableRes> virtualTableList =
                virtualTableService.getVirtualTableList(
                        pluginName, datasourceName, pageNo, pageSize);
        if (virtualTableList.getTotalCount() == 0
                || CollectionUtils.isEmpty(virtualTableList.getData())) {
            return Result.success(virtualTableList);
        }
        Map<Integer, String> userIdNameMap = new HashMap<>();
        virtualTableList
                .getData()
                .forEach(
                        virtualTableRes -> {
                            virtualTableRes.setCreateUserName(
                                    userIdNameMap.getOrDefault(
                                            virtualTableRes.getCreateUserId(), ""));
                            virtualTableRes.setUpdateUserName(
                                    userIdNameMap.getOrDefault(
                                            virtualTableRes.getUpdateUserId(), ""));
                        });
        return Result.success(virtualTableList);
    }

    @GetMapping("/dynamic_config")
    Result<String> getDynamicConfig(
            @RequestParam("pluginName") String pluginName,
            @RequestParam("datasourceName") String datasourceName) {
        return Result.success(virtualTableService.queryTableDynamicTable(pluginName));
    }

}
