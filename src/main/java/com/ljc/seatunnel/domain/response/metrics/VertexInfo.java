package com.ljc.seatunnel.domain.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.seatunnel.common.constants.PluginType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VertexInfo {
    private long vertexId;
    private PluginType type;
    private String connectorType;
}
