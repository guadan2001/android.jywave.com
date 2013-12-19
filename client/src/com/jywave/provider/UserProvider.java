package com.jywave.provider;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.jywave.AppMain;
import com.jywave.net.ApiRequest;
import com.jywave.net.ApiResponse;
import com.jywave.sql.DatabaseHelper;
import com.jywave.util.CommonUtil;

public class UserProvider {
	public static final String TAG = "UserProvider";
	public AppMain app = AppMain.getInstance();
	private Context context;

	// Database
	public DatabaseHelper database;

	public UserProvider(Context context) {
		database = new DatabaseHelper(context);
		this.context = context;
	}

	public void activeUser() {

		CommonUtil commonUtil = new CommonUtil(context);

		try {
			ApiRequest request = new ApiRequest();

			JSONObject requestBody = new JSONObject();
			requestBody.put("model", commonUtil.getDeviceModel());
			requestBody.put("imei", commonUtil.getDeviceIMEI());
			requestBody.put("category", AppMain.deviceCategory);
			requestBody.put("sysVersion", commonUtil.getSystemVersion());
			requestBody.put("manufacturer", commonUtil.getDeviceManufacturer());
			
			Log.d(TAG, "request body: " + requestBody.toString());

			ApiResponse response = request.post(AppMain.apiLocation
					+ "activeUser", requestBody);

			if (response.httpCode == 200) {
				app.userId = Integer.parseInt(response.json.getJSONObject(
						"userActive").getString("id"));
				Log.d(TAG, "response body: " + response.json.toString());
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
}
