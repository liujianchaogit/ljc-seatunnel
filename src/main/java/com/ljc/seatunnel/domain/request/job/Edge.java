package com.ljc.seatunnel.domain.request.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge {

    private String inputPluginId;

    private String targetPluginId;
}
