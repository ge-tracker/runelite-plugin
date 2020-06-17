package com.getracker;

import javax.inject.Inject;
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

	private static final String GROUP_NAME = "gestate";

	/**
	 * Decides the state of the transaction. States are taken from RuneLite's GrandExchangePlugin
	 *
	 * @param slot  Slot number
	 * @param offer Grand exchange offer
	 * @return State of the transaction
	 */
	public TransactionState getState(int slot, GrandExchangeOffer offer)
	{
		final GrandExchangeOfferState state = offer.getState();
		final SavedOffer savedOffer = getOffer(slot);

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

		final int qty = offer.getQuantitySold() - savedOffer.getQuantitySold();
		final int dspent = offer.getSpent() - savedOffer.getSpent();

		if (qty <= 0 || dspent <= 0)
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
