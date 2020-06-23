package com.getracker;

import com.getracker.cerebro.CerebroApi;
import com.getracker.cerebro.SessionManager;
import com.google.inject.Provides;
import java.util.EnumSet;
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
	private SessionManager sessionManager;

	@Inject
	private CerebroApi api;

	@Override
	protected void startUp() throws Exception
	{
		api.setSession(sessionManager.loadSession());
	}

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
		final TransactionState txnState = transactionStateManager.with(slot, offer).getState();

		if (txnState == TransactionState.SKIP)
		{
			return;
		}

		transactionStateManager.updateConfig(slot, offer);

		if (txnState == TransactionState.UPDATED)
		{
			Transaction transaction = Transaction.fromOffer(offer);
			transaction.setQty(transactionStateManager.getQty());
			transaction.setPrice(transactionStateManager.getDSpent() / transactionStateManager.getQty());
			transaction.setWorldType(getGeWorldType());

			api.logTransaction(transaction);
		}
	}

	private TransactionWorldType getGeWorldType()
	{
		EnumSet<net.runelite.api.WorldType> worldTypes = client.getWorldType();

		if (worldTypes.contains(net.runelite.api.WorldType.DEADMAN))
		{
			return TransactionWorldType.DEADMAN;
		}

		if (worldTypes.contains(net.runelite.api.WorldType.DEADMAN_TOURNAMENT))
		{
			return TransactionWorldType.DEADMAN_TOURNAMENT;
		}

		return TransactionWorldType.REGULAR;
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
		// Wait until a username is available
		clientThread.invokeLater(() ->
		{
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				return true;
			}

			final Player player = client.getLocalPlayer();

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
