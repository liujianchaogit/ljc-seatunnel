package com.ljc.seatunnel.domain.response.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VirtualTableFieldRes {

    private String fieldName;

    private String fieldType;

    private Boolean nullable;

    private String defaultValue;

    private Boolean primaryKey;

    private String fieldComment;

    private String fieldExtra;
}
