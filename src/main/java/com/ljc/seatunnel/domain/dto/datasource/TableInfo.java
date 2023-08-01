package com.ljc.seatunnel.domain.dto.datasource;

import com.ljc.seatunnel.datasource.plugins.model.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {

    private String tableName;

    private List<TableField> fields;
}
