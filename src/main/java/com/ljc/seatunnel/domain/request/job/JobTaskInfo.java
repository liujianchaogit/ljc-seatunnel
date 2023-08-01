package com.ljc.seatunnel.domain.request.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTaskInfo {

    private List<Edge> edges;

    private List<PluginConfig> plugins;
}
