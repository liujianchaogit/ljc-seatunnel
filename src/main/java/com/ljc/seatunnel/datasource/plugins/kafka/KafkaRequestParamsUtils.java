package com.ljc.seatunnel.datasource.plugins.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigFactory;

import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;

public class KafkaRequestParamsUtils {

    public static Properties parsePropertiesFromRequestParams(Map<String, String> requestParams) {
        checkArgument(
                requestParams.containsKey(KafkaOptionRule.BOOTSTRAP_SERVERS.key()),
                String.format(
                        "Missing %s in requestParams", KafkaOptionRule.BOOTSTRAP_SERVERS.key()));
        final Properties properties = new Properties();
        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                requestParams.get(KafkaOptionRule.BOOTSTRAP_SERVERS.key()));
        if (requestParams.containsKey(KafkaOptionRule.KAFKA_CONFIG.key())) {
            Config configObject =
                    ConfigFactory.parseString(
                            requestParams.get(KafkaOptionRule.KAFKA_CONFIG.key()));
            configObject
                    .entrySet()
                    .forEach(
                            entry -> {
                                properties.put(
                                        entry.getKey(), entry.getValue().unwrapped().toString());
                            });
        }
        return properties;
    }
}
