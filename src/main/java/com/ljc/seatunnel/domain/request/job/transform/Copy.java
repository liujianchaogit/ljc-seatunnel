package com.ljc.seatunnel.domain.request.job.transform;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Copy extends TransformOption {

    private String targetFieldName;
}
