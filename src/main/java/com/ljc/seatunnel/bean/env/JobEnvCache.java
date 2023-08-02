package com.ljc.seatunnel.bean.env;

import com.ljc.seatunnel.dynamicforms.AbstractFormOption;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.utils.SeaTunnelOptionRuleWrapper;
import lombok.Data;
import lombok.Getter;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.api.env.EnvOptionRule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class JobEnvCache {

    @Getter private final FormStructure envFormStructure;

    public JobEnvCache() {
        OptionRule envOptionRules = EnvOptionRule.getEnvOptionRules();
        envFormStructure =
                SeaTunnelOptionRuleWrapper.wrapper(
                        envOptionRules.getOptionalOptions(),
                        envOptionRules.getRequiredOptions(),
                        "Env");
        List<AbstractFormOption> collect =
                envFormStructure.getForms().stream()
                        .filter(form -> !"parallelism".equalsIgnoreCase(form.getField()))
                        .collect(Collectors.toList());
        envFormStructure.setForms(collect);
    }
}
