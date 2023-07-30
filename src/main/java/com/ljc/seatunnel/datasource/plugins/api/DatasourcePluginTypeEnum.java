package com.ljc.seatunnel.datasource.plugins.api;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("checkstyle:RegexpSingleline")
public enum DatasourcePluginTypeEnum {
    DATABASE(1, "database", "传统数据库"),
    FILE(2, "file", "文件"),
    NO_STRUCTURED(3, "no_structured", "非结构化数据（NoSQLs）"),
    STORAGE(4, "storage", "存储"),
    REMOTE_CONNECTION(5, "remote_connection", "远程连接");

    private final Integer code;

    private final String name;

    private final String chineseName;

    DatasourcePluginTypeEnum(Integer code, String name, String chineseName) {
        this.code = checkNotNull(code);
        this.name = checkNotNull(name);
        this.chineseName = checkNotNull(chineseName);
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getChineseName() {
        return chineseName;
    }
}
