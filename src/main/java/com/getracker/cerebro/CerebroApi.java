package com.getracker.cerebro;

import com.getracker.Transaction;
import com.google.gson.Gson;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;
import static net.runelite.http.api.RuneLiteAPI.JSON;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class CerebroApi
{
	//	private final String API_URL = "https://staging-test.cerebrohub.io/api/";
//	private final String CLIENT_ID = "61834a65-6326-486c-a7a5-ddadbb99005a";
	private final String API_URL = "http://cerebro.local/api/";
	private final String CLIENT_ID = "60e0ce7b-443c-4173-a17d-45f2be4148d9";

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
		makeApiRequest("POST", "transaction", transaction);
		return true;
	}

	private Request buildRequest(String method, String endpoint, Object body)
	{
		String endpointUrl = API_URL + endpoint;

		Request.Builder builder = new Request.Builder()
			.header("Content-Type", "application/json")
			.url(HttpUrl.parse(endpointUrl))
			.header(CerebroApi.CEREBRO_CLIENT, CLIENT_ID)
			.header("X-CLIENT-IP", "1.1.1.1");

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

		return builder.build();
	}

	/**
	 * @param method   Http verb
	 * @param endpoint Endpoint to request
	 * @param body     (optional) POST data to send with request
	 */
	private void makeApiRequest(String method, String endpoint, Object body)
	{
		// Build HTTP request
		final Request request = buildRequest(method, endpoint, body);

		// Make API request
		RuneLiteAPI.CLIENT.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.error("Encountered an error when connecting to the API");
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				log.debug("GE transaction logged");
			}
		});
	}

	private void makeApiRequest(String method, String endpoint)
	{
		this.makeApiRequest(method, endpoint, null);
	}

	public void setRsn(String rsn)
	{
		this.rsn = Md5Hasher.hash(rsn);
	}
}
