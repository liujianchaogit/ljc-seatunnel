package com.ljc.seatunnel.dynamicforms;

import lombok.Getter;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractFormSelectOption extends AbstractFormOption {

    @JsonProperty("type")
    @Getter
    private final FormType formType = FormType.SELECT;

    public AbstractFormSelectOption(@NonNull String label, @NonNull String field) {
        super(label, field);
    }

    public static class SelectOption {
        @JsonProperty @Getter private String label;

        @JsonProperty @Getter private Object value;

        public SelectOption(@NonNull String label, @NonNull Object value) {
            this.label = label;
            this.value = value;
        }
    }
}
