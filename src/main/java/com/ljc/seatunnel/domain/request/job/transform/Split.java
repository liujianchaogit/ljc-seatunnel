package com.ljc.seatunnel.domain.request.job.transform;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Split extends TransformOption {

    private String separator;

    private List<String> outputFields;
}
