package com.jywave.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ApiResponse {
	public int httpCode;
	public JSONObject json;
	
	public ApiResponse(int code, String data)
	{
		this.httpCode = code;
		try {
			this.json = new JSONObject(data);
		} catch (JSONException e) {
			Log.e("json", e.toString());
		}
	}
	

}
