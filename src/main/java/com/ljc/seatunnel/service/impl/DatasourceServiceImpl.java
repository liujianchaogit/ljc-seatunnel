package com.ljc.seatunnel.service.impl;

import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.dal.dao.IDatasourceDao;
import com.ljc.seatunnel.dal.entity.Datasource;
import com.ljc.seatunnel.domain.response.datasource.DatasourceDetailRes;
import com.ljc.seatunnel.service.IDatasourceService;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DatasourceServiceImpl implements IDatasourceService {

    @Autowired
    private IDatasourceDao datasourceDao;

    @Override
    public DatasourceDetailRes queryDatasourceDetailById(Integer userId, String datasourceId) {
        return this.queryDatasourceDetailById(datasourceId);
    }

    @Override
    public DatasourceDetailRes queryDatasourceDetailById(String datasourceId) {
        long datasourceIdLong = Long.parseLong(datasourceId);
        Datasource datasource = datasourceDao.selectDatasourceById(datasourceIdLong);
        if (null == datasource) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_FOUND, datasourceId);
        }
        DatasourceDetailRes datasourceDetailRes = new DatasourceDetailRes();
        datasourceDetailRes.setId(datasource.getId().toString());
        datasourceDetailRes.setDatasourceName(datasource.getDatasourceName());
        datasourceDetailRes.setPluginName(datasource.getPluginName());
        datasourceDetailRes.setPluginVersion(datasource.getPluginVersion());
        datasourceDetailRes.setDescription(datasource.getDescription());
        datasourceDetailRes.setCreateTime(datasource.getCreateTime());
        datasourceDetailRes.setUpdateTime(datasource.getUpdateTime());

        Map<String, String> datasourceConfig =
                JsonUtils.toMap(datasource.getDatasourceConfig(), String.class, String.class);
        // convert option rule
        datasourceDetailRes.setDatasourceConfig(datasourceConfig);

        return datasourceDetailRes;
    }

    @Override
    public Map<String, String> queryDatasourceConfigById(String datasourceId) {
        long datasourceIdLong = Long.parseLong(datasourceId);
        Datasource datasource = datasourceDao.selectDatasourceById(datasourceIdLong);
        if (null == datasource) {
            throw new SeatunnelException(SeatunnelErrorEnum.DATASOURCE_NOT_FOUND, datasourceId);
        }
        String configJson = datasource.getDatasourceConfig();
        return JsonUtils.toMap(configJson, String.class, String.class);
    }
}
