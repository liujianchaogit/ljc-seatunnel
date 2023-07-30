package com.ljc.seatunnel.service;

import com.ljc.seatunnel.domain.response.datasource.DatasourceDetailRes;

import java.util.Map;

public interface IDatasourceService {
    DatasourceDetailRes queryDatasourceDetailById(Integer userId, String datasourceId);

    DatasourceDetailRes queryDatasourceDetailById(String datasourceId);

    Map<String, String> queryDatasourceConfigById(String datasourceId);

}