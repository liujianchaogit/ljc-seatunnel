package com.ljc.seatunnel.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTaskCheckRes {

    private boolean global;

    private String pluginId;

    private SchemaError schemaError;

    private String normalError;
}
