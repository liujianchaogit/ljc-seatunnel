package com.ljc.seatunnel.thirdparty.transform;

import com.ljc.seatunnel.domain.request.job.TableSchemaReq;
import com.ljc.seatunnel.domain.request.job.transform.Transform;
import com.ljc.seatunnel.domain.request.job.transform.TransformOptions;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

public interface TransformConfigSwitcher {

    Transform getTransform();

    FormStructure getFormStructure(OptionRule transformOptionRule);

    Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, TableSchemaReq inputSchema);
}
