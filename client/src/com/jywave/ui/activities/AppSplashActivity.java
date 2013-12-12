package com.jywave.ui.activities;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.provider.EpProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class AppSplashActivity extends Activity {
	
	private static final String TAG = "AppSplashActivity";
	private AppMain app = AppMain.getInstance();
	
	private Context thisContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_splash);
		
		thisContext = this;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		AppInitializeTask task = new AppInitializeTask();
		task.execute(new Void[]{});
	}
	
	private void startApp()
	{
		new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            	Intent intent = new Intent();
        		intent.setClass(AppSplashActivity.this, AppMainActivity.class);
        		startActivity(intent);
        		finish();
            }
        }, 2000);
		
	}

	class AppInitializeTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			app.initEpList();
			app.initPlayer();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			startApp();
		}
		
	}
}
