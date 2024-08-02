package me.jluims.velocitywebsync;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

@Plugin(
        id = "velocitywebsync",
        name = "VelocityWebSync",
        version = BuildConstants.VERSION
)
public class VelocityWebSync {

    @Inject
    private Logger logger;
    private ProxyServer server;

    @Inject
    public VelocityWebSync(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("VelocityWebSync loaded!");
        server.getScheduler().buildTask(this, () -> {

        }).delay(5L, TimeUnit.SECONDS).schedule();
    }


}
