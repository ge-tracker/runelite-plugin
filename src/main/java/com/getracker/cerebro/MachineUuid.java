package com.getracker.cerebro;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.net.NetworkInterface;
import java.util.Enumeration;
import net.runelite.client.util.OSType;

/**
 * Calculate unique machine UUID that will be used to tag transactions in Cerebro
 * This allows for a global grand exchange history to be loaded for a user
 */
public class MachineUuid
{
	private String machineUuid;

	public String getMachineUuid()
	{
		return (machineUuid != null) ? machineUuid : calculate();
	}

	@SuppressWarnings("UnstableApiUsage")
	private String calculate()
	{
		if (machineUuid != null)
		{
			return machineUuid;
		}

		try
		{
			Hasher hasher = Hashing.sha256().newHasher();
			Runtime runtime = Runtime.getRuntime();

			hasher.putByte((byte) OSType.getOSType().ordinal());
			hasher.putByte((byte) runtime.availableProcessors());
			hasher.putUnencodedChars(System.getProperty("os.arch", ""));
			hasher.putUnencodedChars(System.getProperty("os.version", ""));
			hasher.putUnencodedChars(System.getProperty("user.name", ""));

			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements())
			{
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				byte[] hardwareAddress = networkInterface.getHardwareAddress();

				if (hardwareAddress != null)
				{
					hasher.putBytes(hardwareAddress);
				}
			}

			machineUuid = hasher.hash().toString();
			return machineUuid;
		}
		catch (Exception ex)
		{
			// unable to generate machine id
		}

		return null;
	}
}
