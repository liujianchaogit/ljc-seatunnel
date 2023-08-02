package com.ljc.seatunnel.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchemaError {

    private String database;

    private String tableName;

    private String fieldName;

    private SchemaErrorType errorType;
}
