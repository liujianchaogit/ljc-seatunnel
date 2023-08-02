package com.ljc.seatunnel.thirdparty.transform.impl;

import com.google.auto.service.AutoService;
import com.ljc.seatunnel.utils.SeaTunnelOptionRuleWrapper;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import com.ljc.seatunnel.domain.request.job.TableSchemaReq;
import com.ljc.seatunnel.domain.request.job.transform.*;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.transform.TransformConfigSwitcher;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

@AutoService(TransformConfigSwitcher.class)
public class ReplaceTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.REPLACE;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return SeaTunnelOptionRuleWrapper.wrapper(transformOptionRule, this.getTransform().name());
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, TableSchemaReq inputSchema) {

        return transformConfig;
    }
}
