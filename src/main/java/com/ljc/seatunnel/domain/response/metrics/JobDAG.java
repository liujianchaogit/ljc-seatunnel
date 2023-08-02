package com.ljc.seatunnel.domain.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDAG {

    private long jobId;

    private Map<Integer, List<Edge>> pipelineEdges;

    private Map<Integer, VertexInfo> vertexInfoMap;
}
