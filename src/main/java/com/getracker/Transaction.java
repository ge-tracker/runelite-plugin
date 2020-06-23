package com.getracker;

import com.google.gson.Gson;
import java.io.Serializable;
import lombok.Data;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;

@Data
public class Transaction implements Serializable
{
	private int itemId;
	private int date;
	private int price;
	private int qty;
	private boolean buying;
	private TransactionWorldType worldType;

	public static Transaction fromOffer(GrandExchangeOffer offer)
	{
		final GrandExchangeOfferState state = offer.getState();
		final boolean buying = state == GrandExchangeOfferState.BUYING || state == GrandExchangeOfferState.BOUGHT;
		final int price = offer.getSpent() / offer.getQuantitySold();

		Transaction transaction = new Transaction();
		transaction.setItemId(offer.getItemId());
		transaction.setDate(0);
		transaction.setPrice(price);
		transaction.setQty(offer.getTotalQuantity());
		transaction.setBuying(buying);

		return transaction;
	}

	public String toString()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
