package com.getracker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "GE-Tracker.com Integration"
)
public class GeTrackerIntegrationPlugin extends Plugin {
    public static final String CONFIG_GROUP = "getracker";

    @Inject
    private Client client;

    @Inject
    private GeTrackerIntegrationConfig config;

    @Override
    protected void startUp() throws Exception {
        log.info("Example started!");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Example stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says hello", null);
        }
    }

    @Provides
    GeTrackerIntegrationConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GeTrackerIntegrationConfig.class);
    }
}
