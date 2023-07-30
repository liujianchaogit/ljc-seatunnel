package com.ljc.seatunnel.utils;

import com.ljc.seatunnel.domain.response.connector.ConnectorFeature;
import com.ljc.seatunnel.domain.response.connector.ConnectorInfo;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import lombok.NonNull;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.api.source.SupportColumnProjection;
import org.apache.seatunnel.api.table.factory.Factory;
import org.apache.seatunnel.api.table.factory.FactoryUtil;
import org.apache.seatunnel.api.table.factory.TableSourceFactory;
import org.apache.seatunnel.common.config.Common;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.common.utils.FileUtils;
import org.apache.seatunnel.plugin.discovery.AbstractPluginDiscovery;
import org.apache.seatunnel.plugin.discovery.PluginIdentifier;
import org.apache.seatunnel.plugin.discovery.seatunnel.SeaTunnelSinkPluginDiscovery;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class PluginDiscoveryUtil {

    public static List<ConnectorInfo> getAllConnectorsFromPluginMapping(PluginType pluginType) {
        Map<PluginIdentifier, String> plugins =
                AbstractPluginDiscovery.getAllSupportedPlugins(pluginType);
        List<ConnectorInfo> connectorInfos = new ArrayList<>();
        plugins.forEach(
                (plugin, artifactId) -> connectorInfos.add(new ConnectorInfo(plugin, artifactId)));
        return connectorInfos;
    }

    public static Map<PluginIdentifier, ConnectorFeature> getConnectorFeatures(
            PluginType pluginType) throws IOException {
        Common.setStarter(true);
        if (!pluginType.equals(PluginType.SOURCE)) {
            throw new UnsupportedOperationException("ONLY support plugin type source");
        }
        Path path = new SeaTunnelSinkPluginDiscovery().getPluginDir();
        List<Factory> factories;
        if (path.toFile().exists()) {
            List<URL> files = FileUtils.searchJarFiles(path);
            factories =
                    FactoryUtil.discoverFactories(new URLClassLoader(files.toArray(new URL[0])));
        } else {
            factories =
                    FactoryUtil.discoverFactories(Thread.currentThread().getContextClassLoader());
        }
        Map<PluginIdentifier, ConnectorFeature> featureMap = new ConcurrentHashMap<>();
        factories.forEach(
                plugin -> {
                    if (TableSourceFactory.class.isAssignableFrom(plugin.getClass())) {
                        TableSourceFactory tableSourceFactory = (TableSourceFactory) plugin;
                        PluginIdentifier info =
                                PluginIdentifier.of(
                                        "seatunnel",
                                        PluginType.SOURCE.getType(),
                                        plugin.factoryIdentifier());
                        featureMap.put(
                                info,
                                new ConnectorFeature(
                                        SupportColumnProjection.class.isAssignableFrom(
                                                tableSourceFactory.getSourceClass())));
                    }
                });
        return featureMap;
    }

    public static List<ConnectorInfo> getDownloadedConnectors(
            @NonNull Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allPlugins,
            @NonNull PluginType pluginType)
            throws IOException {
        LinkedHashMap<PluginIdentifier, OptionRule> pluginIdentifierOptionRuleLinkedHashMap =
                allPlugins.get(pluginType);
        if (pluginIdentifierOptionRuleLinkedHashMap == null) {
            return new ArrayList<>();
        }

        List<ConnectorInfo> connectorInfos = new ArrayList<>();
        pluginIdentifierOptionRuleLinkedHashMap.forEach(
                (plugin, optionRule) -> connectorInfos.add(new ConnectorInfo(plugin, null)));
        return connectorInfos;
    }

    public static List<ConnectorInfo> getTransforms(
            @NonNull Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allPlugins)
            throws IOException {
        LinkedHashMap<PluginIdentifier, OptionRule> pluginIdentifierOptionRuleLinkedHashMap =
                allPlugins.get(PluginType.TRANSFORM);
        if (pluginIdentifierOptionRuleLinkedHashMap == null) {
            return new ArrayList<>();
        }
        return pluginIdentifierOptionRuleLinkedHashMap.keySet().stream()
                .map(t -> new ConnectorInfo(t, null))
                .collect(Collectors.toList());
    }

    public static Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> getAllConnectors()
            throws IOException {
        return new SeaTunnelSinkPluginDiscovery().getAllPlugin();
    }

    public static ConcurrentMap<String, FormStructure> getDownloadedConnectorFormStructures(
            @NonNull Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allPlugins,
            @NonNull PluginType pluginType) {
        LinkedHashMap<PluginIdentifier, OptionRule> pluginIdentifierOptionRuleLinkedHashMap =
                allPlugins.get(pluginType);
        ConcurrentMap<String, FormStructure> result = new ConcurrentHashMap<>();
        if (pluginIdentifierOptionRuleLinkedHashMap == null) {
            return result;
        }

        pluginIdentifierOptionRuleLinkedHashMap.forEach(
                (key, value) ->
                        result.put(
                                key.getPluginName(),
                                SeaTunnelOptionRuleWrapper.wrapper(
                                        value, key.getPluginName(), pluginType)));

        return result;
    }

    public static ConcurrentMap<String, FormStructure> getTransformFormStructures(
            Map<PluginType, LinkedHashMap<PluginIdentifier, OptionRule>> allPlugins) {
        return getDownloadedConnectorFormStructures(allPlugins, PluginType.TRANSFORM);
    }
}
