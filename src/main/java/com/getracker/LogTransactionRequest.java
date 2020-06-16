package com.getracker;

import lombok.Getter;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;

public class LogTransactionRequest
{
	@Getter
	private final String clientId;

	@Getter
	private final String sessionId;

	@Getter
	private final String rsn;

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

	public LogTransactionRequest(GrandExchangeOffer offer, String clientId, String sessionId, String rsn)
	{
		this.itemId = offer.getItemId();
		this.date = 0;
		this.price = offer.getPrice();
		this.qty = offer.getTotalQuantity();
		this.buying = buying(offer);

		this.clientId = clientId;
		this.sessionId = sessionId;
		this.rsn = rsn;
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
