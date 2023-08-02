package com.ljc.seatunnel.thirdparty.transform.impl;

import com.google.auto.service.AutoService;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import com.ljc.seatunnel.domain.request.job.TableSchemaReq;
import com.ljc.seatunnel.domain.request.job.transform.*;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.transform.TransformConfigSwitcher;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

@AutoService(TransformConfigSwitcher.class)
public class FiledEventTypeTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.FILTERROWKIND;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, TableSchemaReq inputSchema) {
        return transformConfig;
    }
}
