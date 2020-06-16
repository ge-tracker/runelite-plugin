package com.getracker.cerebro;

import com.getracker.Transaction;
import com.google.gson.Gson;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;
import static net.runelite.http.api.RuneLiteAPI.JSON;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class CerebroApi
{
	//	private final String API_URL = "https://staging-test.cerebrohub.io/api/";
	private final String API_URL = "http://cerebro.local/api/";
	private final String CLIENT_ID = "eafb7280-e9eb-45f0-9b1c-596bb5633ba4";

	private static final String CEREBRO_CLIENT = "X-CEREBRO-CLIENT-ID";
	private static final String CEREBRO_SESSION = "X-CEREBRO-SESSION-ID";
	private static final String CEREBRO_RSN = "X-CEREBRO-RSN";

	private String rsn;

	private static String sessionId;
	private static final Gson gson = new Gson();

	static
	{
		// TODO: Load `sessionId` from config
		//   if no sessionId, then use machine ID
		//   if we use machine ID, this should be written to config


		MachineUuid machineUuid = new MachineUuid();
		sessionId = machineUuid.getMachineUuid();
	}

	/**
	 * Log a transaction with Cerebro
	 *
	 * @param transaction Class representation of a Grand Exchange transaction
	 */
	public boolean logTransaction(Transaction transaction)
	{
		Gson gson = new Gson();
		System.out.println("Make API request");
		System.out.println(gson.toJson(transaction));

		Response response = makeApiRequest("POST", "transaction", transaction);

		return response != null;
	}

	/**
	 * Create a new session UUID
	 */
	public void createSession()
	{
//		Response response = makeApiRequest("POST", "session");
//		CreateSessionResponse sessionResponse = CerebroApi.gson.fromJson(response.body().string(), CreateSessionResponse.class);
//
//		// TODO: This should be written to config
//		sessionId = sessionResponse.getSessionId();
	}

	private Response makeApiRequest(String method, String endpoint)
	{
		return this.makeApiRequest(method, endpoint, null);
	}

	/**
	 * @param method   Http verb
	 * @param endpoint Endpoint to request
	 * @param body     (optional) POST data to send with request
	 */
	private Response makeApiRequest(String method, String endpoint, Object body)
	{
		String endpointUrl = API_URL + endpoint;

		Request.Builder builder = new Request.Builder()
			.header("Content-Type", "application/json")
			.url(HttpUrl.parse(endpointUrl))
			.header(CerebroApi.CEREBRO_CLIENT, CLIENT_ID);

		if (CerebroApi.sessionId != null)
		{
			builder.header(CerebroApi.CEREBRO_SESSION, CerebroApi.sessionId);
		}

		if (rsn != null)
		{
			builder.header(CerebroApi.CEREBRO_RSN, rsn);
		}

		// Encode the POST body as JSON if specified
		if (method.equals("POST") && body != null)
		{
			Gson gson = new Gson();
			builder.post(RequestBody.create(JSON, gson.toJson(body)));
		}
		else
		{
			builder.method(method, null);
		}

		// Make API request
		try
		{
			Response response = RuneLiteAPI.CLIENT.newCall(builder.build()).execute();

			if (response.isSuccessful())
			{
				return response;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		log.error("Failed making Cerebro API request");
		return null;
	}

	public void setRsn(String rsn)
	{
		this.rsn = Md5Hasher.hash(rsn);
	}
}
