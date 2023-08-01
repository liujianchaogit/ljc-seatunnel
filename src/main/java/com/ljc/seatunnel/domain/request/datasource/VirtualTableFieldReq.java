package com.ljc.seatunnel.domain.request.datasource;

import lombok.Data;

@Data
public class VirtualTableFieldReq {

    private String fieldName;

    private String fieldType;
    private Boolean nullable;

    private String defaultValue;
    private Boolean primaryKey;

    private String fieldComment;

    private String fieldExtra;
}
