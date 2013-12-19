package com.jywave.provider;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import com.jywave.AppMain;
import com.jywave.net.ApiRequest;
import com.jywave.net.ApiResponse;
import com.jywave.sql.DatabaseHelper;

public class CommonProvider {
	public static final String TAG = "CommonProvider";
	public AppMain app = AppMain.getInstance();
	private Context context;

	// Database
	public DatabaseHelper database;

	public CommonProvider(Context context) {
		database = new DatabaseHelper(context);
		this.context = context;
	}
	
	public boolean feedback(String nickname, String content)
	{
		try {
			ApiRequest request = new ApiRequest();
			
			JSONObject requestBody = new JSONObject();
			requestBody.put("userId", app.userId);
			requestBody.put("nickname", nickname);
			requestBody.put("content", content);
			
			ApiResponse response = request.post(AppMain.apiLocation + "feedback", requestBody);

			if (response.httpCode == 200) {
				
				int feedbackId = response.json.getJSONObject("feedback").getInt("id");
				if(feedbackId > 0)
				{
					return true;
				}
				else
				{
					return false;
				}
				
			} else {
				Log.e(TAG,"feedback submit error, http code:"
								+ String.valueOf(response.httpCode));

			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return false;
	}
}