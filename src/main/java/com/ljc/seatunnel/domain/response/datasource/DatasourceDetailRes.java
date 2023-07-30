package com.ljc.seatunnel.domain.response.datasource;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class DatasourceDetailRes {

    private String id;

    private String datasourceName;

    private String pluginName;

    private String pluginVersion;

    // todo check configuration
    private Map<String, String> datasourceConfig;

    private String description;

    private String createUserName;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;
}
