package com.ljc.seatunnel.domain.request.job;

import com.ljc.seatunnel.datasource.plugins.model.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseTableSchemaReq {

    private String database;
    private String tableName;
    private List<TableField> fields;
}
