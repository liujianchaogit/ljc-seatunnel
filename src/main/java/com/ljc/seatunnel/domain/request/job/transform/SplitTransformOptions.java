package com.ljc.seatunnel.domain.request.job.transform;

import lombok.Data;

import java.util.List;

@Data
public class SplitTransformOptions implements TransformOptions {

    List<Split> splits;
}
