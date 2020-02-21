package com.getracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GeTrackerIntegrationPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GeTrackerIntegrationPlugin.class);
		RuneLite.main(args);
	}
}