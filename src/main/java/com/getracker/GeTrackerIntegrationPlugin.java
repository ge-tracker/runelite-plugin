package com.getracker;

import com.getracker.cerebro.CerebroApi;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "GE-Tracker.com Integration"
)
public class GeTrackerIntegrationPlugin extends Plugin
{
	public static final String CONFIG_GROUP = "getracker";

	@Inject
	private Client client;

	@Inject
	private GeTrackerIntegrationConfig config;

	@Inject
	private CerebroApi api;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		System.out.println(String.format("Game state: %s", gameStateChanged.getGameState().toString()));

		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says hello", null);
		}
	}

	@Provides
	GeTrackerIntegrationConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GeTrackerIntegrationConfig.class);
	}

	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged newOfferEvent)
	{
		GrandExchangeOfferState state = newOfferEvent.getOffer().getState();

		// Ignore the game's initialisation when logging in
		if (client.getGameState() != GameState.LOGGED_IN || state == GrandExchangeOfferState.EMPTY)
		{
			return;
		}

		// Exit here if the transaction is not in completed state
		if (!Transaction.complete(newOfferEvent.getOffer()))
		{
			return;
		}

		Transaction transaction = new Transaction(newOfferEvent.getOffer());

		System.out.println(String.format("Slot: %s - %s", newOfferEvent.getSlot(), newOfferEvent.getOffer().getState().toString()));

		api.logTransaction(transaction);

//		System.out.println(String.format(
//			"Offer - itemId: %s, price: %s, qty: %s (%s)",
//			offer.getItemId(), offer.getPrice(), offer.getTotalQuantity(), offer.getQuantitySold()
//		));
	}
}
