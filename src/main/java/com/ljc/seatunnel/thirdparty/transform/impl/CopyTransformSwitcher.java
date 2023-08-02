package com.ljc.seatunnel.thirdparty.transform.impl;

import com.google.auto.service.AutoService;
import com.ljc.seatunnel.domain.request.job.TableSchemaReq;
import com.ljc.seatunnel.domain.request.job.transform.*;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.transform.TransformConfigSwitcher;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

import java.util.LinkedHashMap;

import static com.ljc.seatunnel.thirdparty.transform.TransformConfigSwitcherUtils.getOrderedConfigForLinkedHashMap;

@AutoService(TransformConfigSwitcher.class)
public class CopyTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.COPY;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, TableSchemaReq inputSchema) {

        CopyTransformOptions copyTransformOptions = (CopyTransformOptions) transformOption;

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        for (Copy copy : copyTransformOptions.getCopyList()) {
            fields.put(copy.getTargetFieldName(), copy.getSourceFieldName());
        }

        return transformConfig.withValue("fields", getOrderedConfigForLinkedHashMap(fields).root());
    }
}
