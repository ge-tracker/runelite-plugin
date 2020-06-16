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
		Transaction transaction = new Transaction(13576, 1592310541, 55000000, 3, true);
		assertTrue(api.logTransaction(transaction));
	}
}
