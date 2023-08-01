package com.ljc.seatunnel.datasource.plugins.kafka;

import org.apache.seatunnel.api.configuration.Option;
import org.apache.seatunnel.api.configuration.Options;
import org.apache.seatunnel.api.configuration.util.OptionRule;

import java.util.Map;

public class KafkaOptionRule {

    public static final Option<String> BOOTSTRAP_SERVERS =
            Options.key("bootstrap.servers")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("Kafka cluster address, separated by \",\".");
    public static final Option<String> TOPIC =
            Options.key("topic")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Kafka topic name. If there are multiple topics, use , to split, for example: \"tpc1,tpc2\".");

    public static final Option<Boolean> PATTERN =
            Options.key("pattern")
                    .booleanType()
                    .defaultValue(false)
                    .withDescription(
                            "If pattern is set to true,the regular expression for a pattern of topic names to read from."
                                    + " All topics in clients with names that match the specified regular expression will be subscribed by the consumer.");

    public static final Option<Map<String, String>> KAFKA_CONFIG =
            Options.key("kafka.config")
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
        return OptionRule.builder().required(BOOTSTRAP_SERVERS).optional(KAFKA_CONFIG).build();
    }

    public static OptionRule metadataRule() {
        return OptionRule.builder().required(TOPIC).optional(PATTERN).build();
    }
}
