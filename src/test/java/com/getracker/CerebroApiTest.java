package com.getracker;

import com.getracker.cerebro.CerebroApi;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class CerebroApiTest
{
	private CerebroApi api;

	@Before
	public void setUp()
	{
		this.api = new CerebroApi();
	}

	@Test
	public void canRequestCerebroApi()
	{
		Transaction transaction = new Transaction();
		transaction.setItemId(13576);
		transaction.setDate(1592310541);
		transaction.setPrice(55000000);
		transaction.setQty(3);
		transaction.setBuying(true);

		assertTrue(api.logTransaction(transaction));
	}
}
