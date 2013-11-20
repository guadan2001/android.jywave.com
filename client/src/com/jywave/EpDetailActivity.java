package com.jywave;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jywave.service.ImageService;
import com.jywave.util.NetUtil;
import com.jywave.util.StringUtil;
import com.jywave.vo.Ep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EpDetailActivity extends Activity {

	private TextView epTitle;
	private ImageView epCover;
	private ImageView epStar;
	private TextView epLength;
	private TextView epDescription;
	private ImageButton btnBack;
	private ImageButton btnPlaying;
	private LinearLayout btnDownloadCtrl;
	private LinearLayout btnDownloadCancel;
	private LinearLayout btnClickToPlay;
	private ProgressBar pbDownloadProgress;

	private Context thisContext;

	private DownloadManager downloadMgr;

	private Ep ep;
	private AppMain app = AppMain.getInstance();
	private long downloadMgrRef;
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_detail);

		thisContext = this.getApplicationContext();

		epTitle = (TextView) findViewById(R.id.txtEpTitle);
		epStar = (ImageView) findViewById(R.id.imgEpStar);
		epCover = (ImageView) findViewById(R.id.imgEpCover);
		epLength = (TextView) findViewById(R.id.txtEpLength);
		epDescription = (TextView) findViewById(R.id.txtEpDescription);
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnPlaying = (ImageButton) findViewById(R.id.btnPlaying);
		btnDownloadCtrl = (LinearLayout) findViewById(R.id.btnDownloadCtrl);
		btnDownloadCancel = (LinearLayout) findViewById(R.id.btnDownloadCancel);
		btnClickToPlay = (LinearLayout)findViewById(R.id.btnClickToPlay);
		pbDownloadProgress = (ProgressBar) findViewById(R.id.pbDownloadProgress);

		Intent intent = this.getIntent();
		index = intent.getIntExtra("epIndex", 0);

		ep = app.epsList.data.get(index);

		epTitle.setText(ep.title);
		epLength.setText(ep.getLengthString());
		epDescription.setText(ep.description);
		
		btnClickToPlay.setVisibility(View.GONE);

		if (ep.status == Ep.IN_SERVER) {
			btnDownloadCancel.setVisibility(View.GONE);
			downloadMgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		} else if (ep.status == Ep.DOWNLOADING) {
			pbDownloadProgress.setVisibility(View.VISIBLE);
			btnDownloadCancel.setVisibility(View.VISIBLE);
			btnDownloadCtrl.setVisibility(View.GONE);

			pbDownloadProgress.setProgress(ep.downloadProgress);

			downloadMgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			downloadMgrRef = Long.parseLong(app.downloadList.get(String
					.valueOf(index)));
			
			startListenDownloadComplete();
			downloadProgressUpdater.scheduleAtFixedRate(updateDownloadProgress, 0, 5, TimeUnit.SECONDS);
		} else {
			btnDownloadCtrl.setVisibility(View.GONE);
			btnDownloadCancel.setVisibility(View.GONE);
		}

		switch (ep.star) {
		case 1:
			epStar.setImageResource(R.drawable.star1);
			break;
		case 2:
			epStar.setImageResource(R.drawable.star2);
			break;
		case 3:
			epStar.setImageResource(R.drawable.star3);
			break;
		case 4:
			epStar.setImageResource(R.drawable.star4);
			break;
		case 5:
			epStar.setImageResource(R.drawable.star5);
			break;
		}

		if (!app.isPlaying()) {
			btnPlaying.setVisibility(View.GONE);
		}

		new GetEpCover().execute(ep.coverUrl);

		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnDownloadCtrl.setOnClickListener(downloadCtrlListener);
		btnDownloadCancel.setOnClickListener(downloadCancelListener);
	}

	private OnClickListener downloadCtrlListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			ep.status = Ep.DOWNLOADING;
			app.epsList.data.get(index).status = Ep.DOWNLOADING;

			btnDownloadCancel.setVisibility(View.VISIBLE);
			btnDownloadCtrl.setVisibility(View.GONE);
			pbDownloadProgress.setVisibility(View.VISIBLE);

			Uri uri = Uri.parse(ep.url);
			String filename = StringUtil.getFilenameFromUrl(ep.url);
			DownloadManager.Request request = new Request(uri);
			request.setTitle(ep.title);
			request.setDestinationUri(Uri.fromFile(new File(app.mp3StorageDir
					+ filename)));
			if (app.allowDownloadWithoutWifi) {
				request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
			}
			downloadMgrRef = downloadMgr.enqueue(request);

			app.epsList.data.get(index).downloadProgress = 30;

			app.downloadList.put(String.valueOf(index),
					String.valueOf(downloadMgrRef));
			
			pbDownloadProgress.setProgress(0);
			
			startListenDownloadComplete();
			downloadProgressUpdater = Executors.newScheduledThreadPool(1);
			downloadProgressUpdater.scheduleAtFixedRate(updateDownloadProgress, 0, 5, TimeUnit.SECONDS);
		}
	};
	
	private void startListenDownloadComplete()
	{
		registerReceiver(receiver, filter);
	}
	
	private void stopListenDownloadComplete()
	{
		unregisterReceiver(receiver);
	}
	
	private IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent)
		{
			long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if(reference == downloadMgrRef)
			{
				btnDownloadCancel.setVisibility(View.GONE);
				pbDownloadProgress.setVisibility(View.GONE);
				btnClickToPlay.setVisibility(View.VISIBLE);
				
				ep.status = Ep.IN_LOCAL;
				app.epsList.data.get(index).status = Ep.IN_LOCAL;
				
				app.downloadList.remove(index);
				
				downloadProgressUpdater.shutdown();
			}
		}
	};

	private OnClickListener downloadCancelListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					v.getContext());
			builder.setTitle("您确定要取消下载吗?");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Animation fadeOut = AnimationUtils.loadAnimation(
									thisContext, R.anim.fade_out);
							fadeOut.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									pbDownloadProgress.setVisibility(View.GONE);
								}
							});
							pbDownloadProgress.startAnimation(fadeOut);

							btnDownloadCancel.setVisibility(View.GONE);
							btnDownloadCtrl.setVisibility(View.VISIBLE);
							btnClickToPlay.setVisibility(View.GONE);

							ep.status = Ep.IN_SERVER;
							app.epsList.data.get(index).status = Ep.IN_SERVER;

							downloadMgr.remove(downloadMgrRef);
							app.downloadList.remove(String.valueOf(index));
							
							stopListenDownloadComplete();
							downloadProgressUpdater.shutdown();
						}
					});

			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});

			builder.show();
		}
	};

	public void setEpCover(Bitmap bmp) {
		epCover = (ImageView) findViewById(R.id.imgEpCover);
		epCover.setImageBitmap(bmp);
	}

	private class GetEpCover extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			ImageService imgSrv = new ImageService();
			return imgSrv.loadImage(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				EpDetailActivity.this.setEpCover(result);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
	
	private ScheduledExecutorService downloadProgressUpdater;
	private Runnable updateDownloadProgress = new Runnable() {
		
		@Override
		public void run() {
			int downloadProgress = NetUtil.getDownloadProgressByRefId(downloadMgr, downloadMgrRef);
			pbDownloadProgress.setProgress(downloadProgress);
		}
	};
}
