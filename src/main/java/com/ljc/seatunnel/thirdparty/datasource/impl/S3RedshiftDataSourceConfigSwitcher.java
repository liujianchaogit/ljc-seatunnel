/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ljc.seatunnel.thirdparty.datasource.impl;

import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import com.ljc.seatunnel.domain.request.job.DataSourceOption;
import com.ljc.seatunnel.domain.request.job.SelectTableFields;
import com.ljc.seatunnel.domain.response.datasource.VirtualTableDetailRes;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.datasource.AbstractDataSourceConfigSwitcher;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

import java.util.List;

public class S3RedshiftDataSourceConfigSwitcher extends AbstractDataSourceConfigSwitcher {

    private S3RedshiftDataSourceConfigSwitcher() {}

    private static final S3RedshiftDataSourceConfigSwitcher INSTANCE =
            new S3RedshiftDataSourceConfigSwitcher();

    @Override
    public FormStructure filterOptionRule(
            String connectorName,
            OptionRule dataSourceOptionRule,
            OptionRule virtualTableOptionRule,
            BusinessMode businessMode,
            PluginType pluginType,
            OptionRule connectorOptionRule,
            List<String> excludedKeys) {
        excludedKeys.add("access_key");
        excludedKeys.add("secret_key");
        return super.filterOptionRule(
                connectorName,
                dataSourceOptionRule,
                virtualTableOptionRule,
                businessMode,
                pluginType,
                connectorOptionRule,
                excludedKeys);
    }

    @Override
    public Config mergeDatasourceConfig(
            Config dataSourceInstanceConfig,
            VirtualTableDetailRes virtualTableDetail,
            DataSourceOption dataSourceOption,
            SelectTableFields selectTableFields,
            BusinessMode businessMode,
            PluginType pluginType,
            Config connectorConfig) {
        return super.mergeDatasourceConfig(
                dataSourceInstanceConfig,
                virtualTableDetail,
                dataSourceOption,
                selectTableFields,
                businessMode,
                pluginType,
                connectorConfig);
    }

    public static S3RedshiftDataSourceConfigSwitcher getInstance() {
        return INSTANCE;
    }
}
