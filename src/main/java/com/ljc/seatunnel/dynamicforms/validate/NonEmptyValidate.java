package com.ljc.seatunnel.dynamicforms.validate;

import lombok.Data;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class NonEmptyValidate extends AbstractValidate {
    private final boolean required = true;

    @JsonProperty("type")
    private final RequiredType requiredType = RequiredType.NON_EMPTY;
}
