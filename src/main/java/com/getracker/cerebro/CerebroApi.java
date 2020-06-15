package com.getracker.cerebro;

import com.getracker.Transaction;
import com.google.gson.Gson;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class CerebroApi
{
	private final String API_URL = "https://staging-test.cerebrohub.io";
	private final String CLIENT_ID = "eafb7280-e9eb-45f0-9b1c-596bb5633ba4";

	public void logTransaction(Transaction transaction)
	{
		Gson gson = new Gson();
		System.out.println("Make API request");
		System.out.println(gson.toJson(transaction));
		System.out.println(transaction.toString());
//		Request request = buildRequest("/api/transaction")
//			.post(RequestBody.create(JSON, gson.toJson(transaction)))
//			.build();
//
//		RuneLiteAPI.CLIENT.newCall(request).enqueue(new Callback()
//		{
//			@Override
//			public void onFailure(Call call, IOException e)
//			{
//				onError.accept(e);
//			}
//
//			@Override
//			public void onResponse(Call call, Response response)
//			{
//				onResponse.accept(response);
//				response.close();
//			}
//		});
	}

	private Request.Builder buildRequest(String endpoint)
	{
		String endpointUrl = API_URL + endpoint;

		return new Request.Builder()
			.header("Content-Type", "application/json")
			.url(HttpUrl.parse(endpointUrl));
	}
}
