package com.ljc.seatunnel.datasource.plugins.pulsar;

import org.apache.seatunnel.api.configuration.Option;
import org.apache.seatunnel.api.configuration.Options;
import org.apache.seatunnel.api.configuration.util.OptionRule;

import java.util.Map;

public class PulsarOptionRule {
    public static final Option<String> TOPIC =
            Options.key("topic")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Kafka topic name. If there are multiple topics, use , to split, for example: \"tpc1,tpc2\".");
    public static final Option<String> SUB =
            Options.key("subscription.name")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("HAHA");
    public static final Option<String> ADMIN_URL =
            Options.key("admin.service-url")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("HAHA");
    public static final Option<String> CLIENT_URL =
            Options.key("client.service-url")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("HAHA");

    public static final Option<Map<String, String>> PULSAR_CONFIG =
            Options.key("pulsar.config")
                    .mapType()
                    .noDefaultValue()
                    .withDescription(
                            "{\n"
                                    + "client.id=client_1\n"
                                    + "max.poll.records=500\n"
                                    + "auto.offset.reset=earliest\n"
                                    + "enable.auto.commit=false\n"
                                    + "}");

    public static OptionRule optionRule() {
        return OptionRule.builder()
                .required(ADMIN_URL).required(CLIENT_URL).required(SUB)
                .optional(PULSAR_CONFIG).build();
    }

    public static OptionRule metadataRule() {
        return OptionRule.builder().required(TOPIC).build();
    }
}
