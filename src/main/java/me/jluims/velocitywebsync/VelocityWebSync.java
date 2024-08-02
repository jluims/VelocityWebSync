package me.jluims.velocitywebsync;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "velocitywebsync",
        name = "VelocityWebSync",
        version = BuildConstants.VERSION
)
public class VelocityWebSync {

    @Inject
    private final Logger logger;
    private final ProxyServer proxy;
    private final Gson gson = new Gson();
    private final ServerJsonBuilder jsonBuilder;
    @Nullable
    private ScheduledTask task;
    private PluginConfig config;

    @Inject
    public VelocityWebSync(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        try {
            this.config = PluginConfig.readFromFile();
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException("Failed to load VelocityWebSync config file!", ex);
        }

        this.jsonBuilder = new ServerJsonBuilder(config, logger);
    }

    private void sendInfoToWebserver() throws IOException {
        JsonObject infoJson = jsonBuilder.buildInfoJson(proxy);
        String dataStr = gson.toJson(infoJson);

        HttpURLConnection connection = (HttpURLConnection) URI.create(config.webserverUrl()).toURL().openConnection();
        connection.setRequestProperty("User-Agent", "VelocityWebSync/1.0 (made with <3 by jluims)");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.getOutputStream().write(dataStr.getBytes());
        connection.getOutputStream().flush();
        connection.getOutputStream().close();
        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode > 299) {
            throw new IOException("Webserver returned HTTP response code " + responseCode);
        }

        connection.disconnect();
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("VelocityWebSync loaded!");

        task = proxy.getScheduler().buildTask(this, () -> {
            new Thread(() -> {
                try {
                    sendInfoToWebserver();
                } catch (IOException e) {
                    logger.error("VelocityWebSync error", e);
                }
            }).start();
        }).repeat(config.updateInterval(), TimeUnit.SECONDS).schedule();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (task != null) {
            task.cancel();
        }
    }


}
