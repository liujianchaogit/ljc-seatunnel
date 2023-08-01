package com.ljc.seatunnel.dal.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.seatunnel.dal.dao.IDatasourceDao;
import com.ljc.seatunnel.dal.entity.Datasource;
import com.ljc.seatunnel.dal.mapper.DatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DatasourceDaoImpl implements IDatasourceDao {

    @Autowired
    private DatasourceMapper datasourceMapper;

    @Override
    public Datasource selectDatasourceById(Long id) {
        return datasourceMapper.selectById(id);
    }

    @Override
    public Datasource queryDatasourceByName(String name) {
        return datasourceMapper.selectOne(
                new QueryWrapper<Datasource>().eq("datasource_name", name));
    }

    @Override
    public List<Datasource> selectDatasourceByPluginName(String pluginName, String pluginVersion) {
        return datasourceMapper.selectList(
                new QueryWrapper<Datasource>()
                        .eq("plugin_name", pluginName)
                        .eq("plugin_version", pluginVersion));
    }

    @Override
    public IPage<Datasource> selectDatasourceByParam(Page<Datasource> page, List<Long> availableDatasourceIds, String searchVal, String pluginName) {
        QueryWrapper<Datasource> datasourceQueryWrapper = new QueryWrapper<>();
//        datasourceQueryWrapper.in("id", availableDatasourceIds);
        if (searchVal != null
                && !searchVal.isEmpty()
                && pluginName != null
                && !pluginName.isEmpty()) {
            return datasourceMapper.selectPage(
                    page,
                    datasourceQueryWrapper
                            .eq("plugin_name", pluginName)
                            .like("datasource_name", searchVal));
        }
        if (searchVal != null && !searchVal.isEmpty()) {
            return datasourceMapper.selectPage(
                    page, datasourceQueryWrapper.like("datasource_name", searchVal));
        }
        if (pluginName != null && !pluginName.isEmpty()) {
            return datasourceMapper.selectPage(
                    page, datasourceQueryWrapper.eq("plugin_name", pluginName));
        }
        return datasourceMapper.selectPage(page, datasourceQueryWrapper);

    }
}
