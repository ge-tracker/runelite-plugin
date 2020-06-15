package com.getracker;

import lombok.Getter;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;

public class Transaction
{
	@Getter
	private final int itemId;

	@Getter
	private final int date;

	@Getter
	private final int price;

	@Getter
	private final int qty;

	@Getter
	private final boolean buying;

	public Transaction(GrandExchangeOffer offer)
	{
		this.itemId = offer.getItemId();
		this.date = 0;
		this.price = offer.getPrice();
		this.qty = offer.getTotalQuantity();
		this.buying = buying(offer);
	}

	private boolean buying(GrandExchangeOffer offer)
	{
		GrandExchangeOfferState state = offer.getState();
		return state == GrandExchangeOfferState.BUYING || state == GrandExchangeOfferState.BOUGHT;
	}

	/**
	 * Returns `true` if the transaction is in a completed state
	 *
	 * @return boolean
	 */
	public static boolean complete(GrandExchangeOffer offer)
	{
		GrandExchangeOfferState state = offer.getState();
		return state == GrandExchangeOfferState.BOUGHT
			|| state == GrandExchangeOfferState.SOLD
			|| state == GrandExchangeOfferState.CANCELLED_BUY
			|| state == GrandExchangeOfferState.CANCELLED_SELL;
	}
}
