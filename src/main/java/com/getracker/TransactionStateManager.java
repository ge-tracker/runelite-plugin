package com.getracker;

import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.client.config.ConfigManager;
import static net.runelite.http.api.RuneLiteAPI.GSON;

public class TransactionStateManager
{
	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	private GrandExchangeOffer offer;
	private GrandExchangeOfferState state;
	private SavedOffer savedOffer;

	@Getter
	private int qty;

	@Getter
	private int dSpent;

	private static final String GROUP_NAME = "gestate";

	public TransactionStateManager with(int slot, GrandExchangeOffer offer)
	{
		this.offer = offer;
		this.state = offer.getState();
		this.savedOffer = getOffer(slot);
		return this;
	}

	/**
	 * Decides the state of the transaction. States are taken from RuneLite's GrandExchangePlugin
	 *
	 * @return State of the transaction
	 */
	public TransactionState getState()
	{
		if (offer.getItemId() == 0 || (state == GrandExchangeOfferState.EMPTY && client.getGameState() != GameState.LOGGED_IN))
		{
			return TransactionState.SKIP;
		}

		if (savedOffer == null && (state == GrandExchangeOfferState.BUYING || state == GrandExchangeOfferState.SELLING) && offer.getQuantitySold() == 0)
		{
			return TransactionState.NEW;
		}

		if (savedOffer == null || savedOffer.getItemId() != offer.getItemId() || savedOffer.getPrice() != offer.getPrice() || savedOffer.getTotalQuantity() != offer.getTotalQuantity())
		{
			return TransactionState.NO_CHANGE;
		}

		if (savedOffer.getState() == offer.getState() && savedOffer.getQuantitySold() == offer.getQuantitySold())
		{
			return TransactionState.NO_CHANGE;
		}

		if (state == GrandExchangeOfferState.CANCELLED_BUY || state == GrandExchangeOfferState.CANCELLED_SELL)
		{
			return TransactionState.CANCELLED;
		}

		// Calculate the difference in the amount sold since the transaction was last cached
		qty = offer.getQuantitySold() - savedOffer.getQuantitySold();
		dSpent = offer.getSpent() - savedOffer.getSpent();

		if (qty <= 0 || dSpent <= 0)
		{
			return TransactionState.COMPLETED;
		}

		return TransactionState.UPDATED;
	}

	public SavedOffer getOffer(int slot)
	{
		String offer = configManager.getConfiguration(GROUP_NAME + "." + client.getUsername().toLowerCase(), Integer.toString(slot));
		return offer == null ? null : GSON.fromJson(offer, SavedOffer.class);
	}

	public void setOffer(int slot, SavedOffer offer)
	{
		configManager.setConfiguration(GROUP_NAME + "." + client.getUsername().toLowerCase(), Integer.toString(slot), GSON.toJson(offer));
	}

	public void deleteOffer(int slot)
	{
		configManager.unsetConfiguration(GROUP_NAME + "." + client.getUsername().toLowerCase(), Integer.toString(slot));
	}

	public void updateConfig(int slot, GrandExchangeOffer offer)
	{
		if (offer.getState() == GrandExchangeOfferState.EMPTY)
		{
			deleteOffer(slot);
			return;
		}

		SavedOffer savedOffer = new SavedOffer();
		savedOffer.setItemId(offer.getItemId());
		savedOffer.setQuantitySold(offer.getQuantitySold());
		savedOffer.setTotalQuantity(offer.getTotalQuantity());
		savedOffer.setPrice(offer.getPrice());
		savedOffer.setSpent(offer.getSpent());
		savedOffer.setState(offer.getState());
		setOffer(slot, savedOffer);
	}
}
