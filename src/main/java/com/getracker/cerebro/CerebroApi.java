package com.getracker.cerebro;

import com.getracker.Transaction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.Setter;
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
	private final String API_URL = "https://cerebrohub.io/api/";
	private final String CLIENT_ID = "72f5104e-5b9c-435d-8978-ca76c2a6aa3d";

	private static final String CEREBRO_CLIENT = "X-CEREBRO-CLIENT-ID";
	private static final String CEREBRO_SESSION = "X-CEREBRO-SESSION-ID";
	private static final String CEREBRO_RSN = "X-CEREBRO-RSN";

	private static final Gson gson = new Gson();

	private String rsn;

	@Setter
	private CerebroSession session;

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
			.header(CerebroApi.CEREBRO_CLIENT, CLIENT_ID);

		if (session != null)
		{
			builder.header(CerebroApi.CEREBRO_SESSION, session.getSessionId());
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
				response.close();
			}
		});
	}

	private void makeApiRequest(String method, String endpoint)
	{
		this.makeApiRequest(method, endpoint, null);
	}

	@SuppressWarnings("UnstableApiUsage")
	public void setRsn(String rsn)
	{
		this.rsn = Hashing.sha256()
			.hashString(rsn, StandardCharsets.UTF_8)
			.toString();
	}
}
