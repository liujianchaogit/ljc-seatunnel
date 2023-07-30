package com.ljc.seatunnel.thirdparty.transform;

import com.ljc.seatunnel.domain.request.job.TableSchemaReq;
import com.ljc.seatunnel.domain.request.job.transform.Transform;
import com.ljc.seatunnel.domain.request.job.transform.TransformOptions;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class TransformConfigSwitcherUtils {

    public static FormStructure getFormStructure(
            Transform transform, OptionRule transformOptionRule) {

        return TransformConfigSwitcherProvider.INSTANCE
                .getTransformConfigSwitcher(transform)
                .getFormStructure(transformOptionRule);
    }

    public static Config mergeTransformConfig(
            Transform transform,
            List<TableSchemaReq> inputSchemas,
            Config TransformConfig,
            TransformOptions transformOption) {

        checkArgument(inputSchemas.size() == 1, "transformSwitcher only support one input table");

        return TransformConfigSwitcherProvider.INSTANCE
                .getTransformConfigSwitcher(transform)
                .mergeTransformConfig(TransformConfig, transformOption, inputSchemas.get(0));
    }

    public static Config getOrderedConfigForLinkedHashMap(LinkedHashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append(" {\n");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("    ")
                    .append("\"")
                    .append(entry.getKey())
                    .append("\"")
                    .append("=")
                    .append("\"")
                    .append(entry.getValue())
                    .append("\"")
                    .append("\n");
        }
        sb.append("}");

        return ConfigFactory.parseString(sb.toString());
    }
}
