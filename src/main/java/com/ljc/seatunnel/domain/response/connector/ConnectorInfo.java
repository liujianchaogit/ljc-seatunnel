package com.ljc.seatunnel.domain.response.connector;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.seatunnel.plugin.discovery.PluginIdentifier;

@Data
@AllArgsConstructor
public class ConnectorInfo {
    private PluginIdentifier pluginIdentifier;
    private String artifactId;
}
