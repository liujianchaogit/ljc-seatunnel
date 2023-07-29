package com.ljc.seatunnel.domain.request.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.seatunnel.datasource.plugin.api.model.TableField;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseTableSchemaReq {

    private String database;
    private String tableName;
    private List<TableField> fields;
}
