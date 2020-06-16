package com.getracker.cerebro;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Calculate unique machine UUID that will be used to tag transactions in Cerebro
 * This allows for a global grand exchange history to be loaded for a user
 */
public class Md5Hasher
{
	/**
	 * Return MD5 sum of a string
	 *
	 * @param string String to be hashed
	 * @return Md5 sum
	 */
	public static String hash(String string)
	{
		MessageDigest md = null;

		try
		{
			md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(string.getBytes(StandardCharsets.UTF_8));
			BigInteger number = new BigInteger(1, digest);
			return number.toString(16);
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
	}
}
