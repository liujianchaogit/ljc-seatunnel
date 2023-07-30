package com.ljc.seatunnel.datasource.plugins.model;

import lombok.Data;

import java.util.Map;

@Data
public class TableField {

    private String type;

    private String name;

    private String comment;

    private Boolean primaryKey;

    private String defaultValue;

    private Boolean nullable;

    private Map<String, String> properties;

    private Boolean unSupport;

    private String outputDataType;
}
