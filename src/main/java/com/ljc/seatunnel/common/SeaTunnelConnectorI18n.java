package com.ljc.seatunnel.common;

import org.apache.commons.io.IOUtils;
import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigFactory;

import java.nio.charset.StandardCharsets;

public class SeaTunnelConnectorI18n {
    public static Config CONNECTOR_I18N_CONFIG_EN;
    public static Config CONNECTOR_I18N_CONFIG_ZH;

    static {
        try {
            CONNECTOR_I18N_CONFIG_EN =
                    ConfigFactory.parseString(
                            IOUtils.toString(
                                    SeaTunnelConnectorI18n.class.getResourceAsStream(
                                            "/i18n_en.config"),
                                    StandardCharsets.UTF_8));
            CONNECTOR_I18N_CONFIG_ZH =
                    ConfigFactory.parseString(
                            IOUtils.toString(
                                    SeaTunnelConnectorI18n.class.getResourceAsStream(
                                            "/i18n_zh.config"),
                                    StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
