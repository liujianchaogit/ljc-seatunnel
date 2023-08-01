package com.ljc.seatunnel.domain.response.datasource;

import com.ljc.seatunnel.domain.response.BaseInfo;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class DatasourceRes extends BaseInfo {

    private String id;

    private String datasourceName;

    private String pluginName;

    private String pluginVersion;

    private String description;

    private String createUserName;

    private String updateUserName;

    private Map<String, String> datasourceConfig;

    private int createUserId;

    private int updateUserId;

    private Date createTime;

    private Date updateTime;
}
