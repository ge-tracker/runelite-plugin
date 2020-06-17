package com.getracker;

import lombok.Data;
import net.runelite.api.GrandExchangeOfferState;

@Data
class SavedOffer
{
	private int itemId;
	private int quantitySold;
	private int totalQuantity;
	private int price;
	private int spent;
	private GrandExchangeOfferState state;
}
