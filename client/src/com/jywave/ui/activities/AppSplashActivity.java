package com.jywave.ui.activities;

import com.jywave.AppMain;
import com.jywave.Player;
import com.jywave.R;
import com.jywave.provider.EpProvider;
import com.jywave.provider.UserProvider;
import com.jywave.util.CommonUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class AppSplashActivity extends Activity {

	@SuppressWarnings("unused")
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
	public void onStart() {
		super.onStart();
		AppInitializeTask task = new AppInitializeTask();
		task.execute(new Void[] {});
	}

	private void startApp() {

		Intent intent = new Intent();
		intent.setClass(AppSplashActivity.this, AppMainActivity.class);
		startActivity(intent);
		finish();

	}

	class AppInitializeTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			app.initEpList();
			EpProvider epProvider = new EpProvider(thisContext);
			int epCount = epProvider.getEpCount();
			Log.d(TAG, "ep count: " + String.valueOf(epCount));
			if (epCount == 0 && CommonUtil.checkNetState(thisContext)) {
				epProvider.sync();
			}
			app.initEpList();
			
			app.initDownloadedEpsList();

			app.initPlayer();
			app.initPodcastersList();

			if (CommonUtil.checkNetState(thisContext)) {
				UserProvider userProvider = new UserProvider(thisContext);
				userProvider.activeUser();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			startApp();
		}

	}
}
