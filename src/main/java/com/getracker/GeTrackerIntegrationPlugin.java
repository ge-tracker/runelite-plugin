package com.getracker;

import com.getracker.cerebro.CerebroApi;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.client.callback.ClientThread;
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
	private ClientThread clientThread;

	@Inject
	private GeTrackerIntegrationConfig config;

	@Inject
	private TransactionStateManager transactionStateManager;

	@Inject
	private CerebroApi api;

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			onLoggedInGameState();
		}
	}

	@Provides
	GeTrackerIntegrationConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GeTrackerIntegrationConfig.class);
	}

	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged offerEvent)
	{
		final int slot = offerEvent.getSlot();
		final GrandExchangeOffer offer = offerEvent.getOffer();
		final GrandExchangeOfferState state = offer.getState();
		final TransactionState txnState = transactionStateManager.getState(slot, offer);

		System.out.println(String.format("Slot: %s - %s - %s %sx%s (%s)", offer.getItemId(), slot, state.toString(), offer.getSpent(), offer.getQuantitySold(), offer.getTotalQuantity()));

		if (txnState == TransactionState.SKIP)
		{
			return;
		}

		transactionStateManager.updateConfig(slot, offer);

		Transaction transaction = Transaction.fromOffer(offer);
		System.out.println(String.format("Slot: %s - %s %sx%s (%s)", slot, state.toString(), offer.getSpent(), offer.getQuantitySold(), offer.getTotalQuantity()));
		System.out.println(transaction);
		System.out.println(txnState.toString());
		System.out.println("---");

//		api.logTransaction(transaction);
	}

	/**
	 * Handle a user logging in
	 * <p>
	 * Set the RSN to be sent to the Cerebro API
	 * The RSN is one-way hashed so that it is not visible to the API, but is used as a method of tagging if for
	 * some reason this client's `sessionId` is missing from config
	 */
	private void onLoggedInGameState()
	{
		//keep scheduling this task until it returns true (when we have access to a display name)
		clientThread.invokeLater(() ->
		{
			//we return true in this case as something went wrong and somehow the state isn't logged in, so we don't
			//want to keep scheduling this task.
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				return true;
			}

			final Player player = client.getLocalPlayer();

			//player is null, so we can't get the display name so, return false, which will schedule
			//the task on the client thread again.
			if (player == null)
			{
				return false;
			}

			final String name = player.getName();

			if (name == null || name.equals(""))
			{
				return false;
			}

			// Set RSN in Cerebro API
			api.setRsn(name);

			return true;
		});
	}
}
