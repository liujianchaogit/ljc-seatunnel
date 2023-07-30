package com.ljc.seatunnel.datasource.plugins.api;

import lombok.Builder;
import lombok.Data;

import static com.google.common.base.Preconditions.checkNotNull;

@Data
@Builder
public class DataSourcePluginInfo {

    public String name;

    public String icon;

    public String version;

    private Integer type;

    /** whether support virtual tables, default false */
    private Boolean supportVirtualTables;

    public DataSourcePluginInfo(
            String name, String icon, String version, Integer type, Boolean supportVirtualTables) {
        this.name = checkNotNull(name, "name can not be null");
        this.icon = checkNotNull(icon, "icon can not be null");
        this.version = checkNotNull(version, "version can not be null");
        this.type = checkNotNull(type, "type can not be null");
        this.supportVirtualTables = supportVirtualTables != null && supportVirtualTables;
    }
}
