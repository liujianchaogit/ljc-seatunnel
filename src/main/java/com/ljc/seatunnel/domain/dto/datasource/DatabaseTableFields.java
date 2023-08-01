package com.ljc.seatunnel.domain.dto.datasource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseTableFields {

    private String database;

    private List<TableInfo> tableInfos;
}
