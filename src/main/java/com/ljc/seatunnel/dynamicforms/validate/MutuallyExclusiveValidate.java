package com.ljc.seatunnel.dynamicforms.validate;

import lombok.Data;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public class MutuallyExclusiveValidate extends AbstractValidate {
    private final boolean required = false;
    private List<String> fields;

    @JsonProperty("type")
    private final RequiredType requiredType = RequiredType.MUTUALLY_EXCLUSIVE;

    public MutuallyExclusiveValidate(@NonNull List<String> fields) {
        checkArgument(fields.size() > 0);
        this.fields = fields;
        this.withMessage("parameters:" + fields + ", only one can be set");
    }
}
