package me.jluims.velocitywebsync;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.PingOptions;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class ServerJsonBuilder {
    private final PluginConfig config;
    private final Logger logger;

    public ServerJsonBuilder(PluginConfig config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    private JsonArray getPlayersJson(RegisteredServer server) {
        JsonArray playersJson = new JsonArray();
        Collection<Player> players = server.getPlayersConnected();
        for (Player player : players) {
            JsonObject playerJson = new JsonObject();
            playerJson.addProperty("username", player.getUsername());
            playerJson.addProperty("uuid", player.getUniqueId().toString());
            playersJson.add(playerJson);
        }
        return playersJson;
    }

    private JsonArray getServersJson(ProxyServer proxy) {
        JsonArray serversJson = new JsonArray();
        Collection<RegisteredServer> servers = proxy.getAllServers();

        for (RegisteredServer server : servers) {
            try {
                ServerInfo info = server.getServerInfo();

                JsonObject serverJson = new JsonObject();
                serverJson.addProperty("name", info.getName());
                serverJson.addProperty("address", info.getAddress().getHostString());

                if (config.shouldPingServers()) {
                    try {
                        long pingStart = System.currentTimeMillis();
                        PingOptions pingOptions = PingOptions
                                .builder()
                                .timeout(this.config.serverPingTimeout(), TimeUnit.SECONDS)
                                .build();
                        server.ping(pingOptions).join();
                        long ping = System.currentTimeMillis() - pingStart;

                        serverJson.addProperty("ping", (int) ping);
                        serverJson.addProperty("online", true);
                    } catch (CompletionException e) {
                        logger.warn("Failed to query server: Timeout exceeded!");
                        serverJson.addProperty("ping", -1);
                        serverJson.addProperty("online", false);
                    }

                }

                JsonArray playersJson = getPlayersJson(server);
                serverJson.add("players", playersJson);
                serversJson.add(serverJson);
            } catch (Exception ex) {
                logger.warn("Failed to query server:", ex);
            }
        }

        return serversJson;
    }

    public JsonObject buildInfoJson(ProxyServer proxy) {
        JsonObject infoJson = new JsonObject();
        JsonArray serversJson = getServersJson(proxy);
        infoJson.add("servers", serversJson);
        return infoJson;
    }


}
