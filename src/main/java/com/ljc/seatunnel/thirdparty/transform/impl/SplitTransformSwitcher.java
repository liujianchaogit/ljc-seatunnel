package com.ljc.seatunnel.thirdparty.transform.impl;

import com.google.auto.service.AutoService;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import com.ljc.seatunnel.domain.request.job.TableSchemaReq;
import com.ljc.seatunnel.domain.request.job.transform.*;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.transform.TransformConfigSwitcher;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@AutoService(TransformConfigSwitcher.class)
public class SplitTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.MULTIFIELDSPLIT;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, TableSchemaReq inputSchema) {

        SplitTransformOptions splitTransformOptions = (SplitTransformOptions) transformOption;

        checkArgument(
                splitTransformOptions.getSplits().size() > 0,
                "SplitTransformSwitcher splits must be greater than 0");

        List<Map<String, Object>> splitOPs = new ArrayList<>();

        for (Split split : splitTransformOptions.getSplits()) {
            Map<String, Object> splitOP = new HashMap<>();
            splitOP.put("separator", split.getSeparator());
            splitOP.put("split_field", split.getSourceFieldName());
            splitOP.put("output_fields", ConfigValueFactory.fromIterable(split.getOutputFields()));
            splitOPs.add(splitOP);
        }

        return transformConfig.withValue("splitOPs", ConfigValueFactory.fromIterable(splitOPs));
    }
}
