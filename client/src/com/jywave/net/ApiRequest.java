package com.jywave.net;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.jywave.AppMain;

import android.util.Log;

public class ApiRequest {

	private HttpClient httpClient;
	private HttpParams httpParams;
	private BasicHttpContext localContext;

	public ApiRequest() {
		this.httpClient = new DefaultHttpClient();
		this.httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 1000);
		this.localContext = new BasicHttpContext();
	}

	 public ApiResponse get(String url)
	 {
		 try 
			{
				HttpHost targetHost = new HttpHost(AppMain.apiHost, 80, "http");
				
				HttpGet httpget = new HttpGet(url);
				httpget.setHeader("Content-Type", "application/json");

				HttpResponse httpResponse = this.httpClient.execute(targetHost,	httpget, localContext);
				String result = EntityUtils.toString(httpResponse.getEntity());
				int code = httpResponse.getStatusLine().getStatusCode();
				Log.v("http-response", result);
				Log.v("http-code", String.valueOf(code));
				return new ApiResponse(code, result);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
	
	 }
	
	public ApiResponse post(String url, JSONObject json) 
	{
		try 
		{
			HttpHost targetHost = new HttpHost(AppMain.apiHost, 80, "http");

			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Content-Type", "application/json");

			StringEntity se = new StringEntity(json.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httppost.setEntity(se);

			HttpResponse httpResponse = this.httpClient.execute(targetHost,	httppost, localContext);
			String result = EntityUtils.toString(httpResponse.getEntity());
			int code = httpResponse.getStatusLine().getStatusCode();
			Log.v("http-response", result);
			Log.v("http-code", String.valueOf(code));
			return new ApiResponse(code, result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	//
	// public ServiceResponse put(String url, JSONObject json)
	// {
	//
	// }
	//
	// public ServiceResponse delete(String url)
	// {
	//
	// }

}
