package com.ljc.seatunnel.domain.request.job;

import lombok.Data;

import java.util.List;

@Data
public class JobDAG {

    private List<Edge> edges;
}
