package com.getracker.cerebro;

import com.google.gson.Gson;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;

public class SessionManager
{
	@Inject
	private ConfigManager configManager;

	private final Gson GSON = new Gson();
	private static final String CONFIG_GROUP = "CEREBRO";
	private static final String CONFIG_SESSION = "SESSION_ID";

	public CerebroSession loadSession()
	{
		// Attempt to load `sessionId` from disk
		CerebroSession session = loadFromDisk();

		if (session != null)
		{
			return session;
		}

		// If not persisted `sessionId`, generate a new one
		return generateSession();
	}

	private CerebroSession loadFromDisk()
	{
		String session = configManager.getConfiguration(CONFIG_GROUP, CONFIG_SESSION);
		return session == null ? null : GSON.fromJson(session, CerebroSession.class);
	}

	private CerebroSession generateSession()
	{
		CerebroSession session = new CerebroSession();

		MachineUuid machineUuid = new MachineUuid();
		session.setSessionId(machineUuid.getMachineUuid());

		saveSession(session);

		return session;
	}

	private void saveSession(CerebroSession session)
	{
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_SESSION, GSON.toJson(session));
	}
}
