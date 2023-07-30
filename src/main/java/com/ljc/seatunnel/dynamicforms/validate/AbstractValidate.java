package com.ljc.seatunnel.dynamicforms.validate;

import com.ljc.seatunnel.dynamicforms.FormLocale;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.apache.seatunnel.shade.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

@Data
public class AbstractValidate<T extends AbstractValidate> {
    private final List<String> trigger = Arrays.asList("input", "blur");

    // support i18n
    private String message = "required";

    public enum RequiredType {
        @JsonProperty("non-empty")
        NON_EMPTY("non-empty"),

        @JsonProperty("union-non-empty")
        UNION_NON_EMPTY("union-non-empty"),

        @JsonProperty("mutually-exclusive")
        MUTUALLY_EXCLUSIVE("mutually-exclusive");

        @Getter private String type;

        RequiredType(String type) {
            this.type = type;
        }
    }

    public T withMessage(@NonNull String message) {
        this.message = message;
        return (T) this;
    }

    public T withI18nMessage(@NonNull String message) {
        this.message = FormLocale.I18N_PREFIX + message;
        return (T) this;
    }
}
