package me.jluims.velocitywebsync;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public record PluginConfig(String webserverUrl, boolean shouldPingServers, int serverPingTimeout, int updateInterval) {

    private static File getConfigFile() throws URISyntaxException {
        File pluginsPath = new File(PluginConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        File configFile = Paths.get(pluginsPath.getAbsolutePath(), "velocitywebsync", "velocitywebsync.yml").toFile();

        return configFile;
    }

    private static void createDefaultConfig(File configFile) throws IOException {
        configFile.getParentFile().mkdirs();
        if (!configFile.exists()) {
            // Write default config
            Files.copy(Objects.requireNonNull(PluginConfig.class.getClassLoader().getResourceAsStream("config.yml")), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static PluginConfig readFromFile() throws IOException, URISyntaxException {
        File configFile = getConfigFile();
        createDefaultConfig(configFile);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configFile.toPath()).build();
        CommentedConfigurationNode node = loader.load();
        String webserverUrl = node.node("webserver-url").getString();
        boolean shouldPingServers = node.node("should-ping-servers").getBoolean();
        int serverPingTimeout = node.node("server-ping-timeout").getInt();
        int updateInterval = node.node("update-interval").getInt();

        return new PluginConfig(webserverUrl, shouldPingServers, serverPingTimeout, updateInterval);
    }

    public void write() throws IOException, URISyntaxException {
        File configFile = getConfigFile();
        createDefaultConfig(configFile);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configFile.toPath()).build();
        CommentedConfigurationNode node = loader.load();
        node.node("webserver-url").set(this.webserverUrl);
        node.node("should-ping-servers").set(this.shouldPingServers);
        node.node("server-ping-timeout").set(this.serverPingTimeout);
        node.node("update-interval").set(this.updateInterval);

        loader.save(node);
    }
}
