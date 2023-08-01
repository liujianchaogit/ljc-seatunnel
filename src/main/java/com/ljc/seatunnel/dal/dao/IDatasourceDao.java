package com.ljc.seatunnel.dal.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.seatunnel.dal.entity.Datasource;

import java.util.List;

public interface IDatasourceDao {

    boolean insertDatasource(Datasource datasource);

    Datasource selectDatasourceById(Long id);

    Datasource queryDatasourceByName(String name);

    boolean updateDatasourceById(Datasource datasource);

    boolean checkDatasourceNameUnique(String dataSourceName, Long dataSourceId);

    List<Datasource> selectDatasourceByPluginName(String pluginName, String pluginVersion);

    IPage<Datasource> selectDatasourceByParam(
            Page<Datasource> page,
            List<Long> availableDatasourceIds,
            String searchVal,
            String pluginName);

    List<Datasource> selectDatasourceByIds(List<Long> ids);
}
