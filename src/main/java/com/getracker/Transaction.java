package com.getracker;

import com.google.gson.Gson;
import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;

@RequiredArgsConstructor
public class Transaction implements Serializable
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

	public static Transaction fromOffer(GrandExchangeOffer offer)
	{
		final GrandExchangeOfferState state = offer.getState();
		final boolean buying = state == GrandExchangeOfferState.BUYING || state == GrandExchangeOfferState.BOUGHT;

		return new Transaction(
			offer.getItemId(),
			0,
			offer.getPrice(),
			offer.getTotalQuantity(),
			buying
		);
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

	public String toString()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
