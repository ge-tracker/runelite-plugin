package com.getracker;

import com.getracker.cerebro.Md5Hasher;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Md5HasherTest
{
	@Test
	public void canHashValues()
	{
		String expected = "51fce500deec3c741538e81fe1806131";
		assertEquals(expected, Md5Hasher.hash("https://www.ge-tracker.com"));
	}
}
