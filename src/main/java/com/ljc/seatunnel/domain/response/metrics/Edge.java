package com.ljc.seatunnel.domain.response.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge {
    private Long inputVertexId;
    private Long targetVertexId;
}
