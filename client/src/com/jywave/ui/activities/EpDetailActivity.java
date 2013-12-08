package com.jywave.ui.activities;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.R.anim;
import com.jywave.R.dimen;
import com.jywave.R.drawable;
import com.jywave.R.id;
import com.jywave.R.layout;
import com.jywave.player.Player;
import com.jywave.util.NetUtil;
import com.jywave.util.StringUtil;
import com.jywave.util.imagecache.ImageFetcher;
import com.jywave.util.imagecache.ImageWorker;
import com.jywave.vo.DownloadItem;
import com.jywave.vo.Ep;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EpDetailActivity extends FragmentActivity implements
		OnGestureListener, OnTouchListener {

	private LinearLayout pageEpDetail;
	private TextView txtEpTitle;
	private ImageView imgEpCover;
	private ImageView imgEpStar;
	private TextView txtEpLength;
	private TextView txtEpDescription;
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
	private Player player = Player.getInstance();
	private long downloadMgrRef;
	private int index;

	private GestureDetector gestureDetector;

	private ImageFetcher imgFetcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_detail);

		// Get UI elements
		thisContext = this.getApplicationContext();

		pageEpDetail = (LinearLayout) findViewById(R.id.pageEpDetail);
		txtEpTitle = (TextView) findViewById(R.id.txtEpTitle);
		imgEpStar = (ImageView) findViewById(R.id.imgEpStar);
		imgEpCover = (ImageView) findViewById(R.id.imgEpCover);
		txtEpLength = (TextView) findViewById(R.id.txtEpLength);
		txtEpDescription = (TextView) findViewById(R.id.txtEpDescription);
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnPlaying = (ImageButton) findViewById(R.id.btnPlaying);
		btnDownloadCtrl = (LinearLayout) findViewById(R.id.btnDownloadCtrl);
		btnDownloadCancel = (LinearLayout) findViewById(R.id.btnDownloadCancel);
		btnClickToPlay = (LinearLayout) findViewById(R.id.btnClickToPlay);
		pbDownloadProgress = (ProgressBar) findViewById(R.id.pbDownloadProgress);

		// Get EP index
		Intent intent = this.getIntent();
		index = intent.getIntExtra("epIndex", 0);

		// Set EP Data
		ep = app.epsList.data.get(index);

		app.latestClickedEpIndex = index;

		// Set Texts
		txtEpTitle.setText(ep.title);
		txtEpLength.setText(ep.getLengthString());
		txtEpDescription.setText(ep.description);

		// hide the "click to play" button
		btnClickToPlay.setVisibility(View.GONE);

		// Load EP Cover
		imgFetcher = new ImageFetcher(this, app.screenWidth
				- getResources().getDimensionPixelSize(
						R.dimen.page_ep_detail_margin) * 2);
		imgFetcher.addImageCache(this.getSupportFragmentManager(),
				app.cacheParams);
		imgFetcher.setImageFadeIn(true);
		imgEpCover.setTag(ep.coverUrl);
		imgFetcher.loadImage(ep.coverUrl, imgEpCover);

		// Ep rank
		switch (ep.star) {
		case 1:
			imgEpStar.setImageResource(R.drawable.star1);
			break;
		case 2:
			imgEpStar.setImageResource(R.drawable.star2);
			break;
		case 3:
			imgEpStar.setImageResource(R.drawable.star3);
			break;
		case 4:
			imgEpStar.setImageResource(R.drawable.star4);
			break;
		case 5:
			imgEpStar.setImageResource(R.drawable.star5);
			break;
		}

		// Set download control
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
			downloadProgressUpdater = Executors.newScheduledThreadPool(1);
			downloadProgressUpdater.scheduleAtFixedRate(updateDownloadStatus,
					0, 5, TimeUnit.SECONDS);
		} else {
			btnDownloadCtrl.setVisibility(View.GONE);
			btnDownloadCancel.setVisibility(View.GONE);
		}

		if (!player.isPlaying) {
			btnPlaying.setVisibility(View.GONE);
		}

		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnPlaying.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("epIndex", player.playingIndexOfEpList);
				intent.setClass(v.getContext(), PlayerActivity.class);
				startActivity(intent);
			}
		});

		btnDownloadCtrl.setOnClickListener(downloadCtrlListener);
		btnDownloadCancel.setOnClickListener(downloadCancelListener);

		// gesture implementation
		gestureDetector = new GestureDetector(this, this);
		pageEpDetail.setOnTouchListener(this);
		pageEpDetail.setLongClickable(true);

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
			request.setDestinationUri(Uri.fromFile(new File(
					AppMain.mp3StorageDir + filename)));
			if (app.allowDownloadWithoutWifi) {
				request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
			}
			downloadMgrRef = downloadMgr.enqueue(request);

			app.downloadList.put(String.valueOf(index),
					String.valueOf(downloadMgrRef));

			pbDownloadProgress.setProgress(0);

			startListenDownloadComplete();
			downloadProgressUpdater = Executors.newScheduledThreadPool(1);
			downloadProgressUpdater.scheduleAtFixedRate(updateDownloadStatus,
					0, 3, TimeUnit.SECONDS);
		}
	};

	private void startListenDownloadComplete() {
		registerReceiver(receiver, filter);
	}

	private void stopListenDownloadComplete() {
		unregisterReceiver(receiver);
	}

	private IntentFilter filter = new IntentFilter(
			DownloadManager.ACTION_DOWNLOAD_COMPLETE);
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long reference = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (reference == downloadMgrRef) {
				DownloadItem di = NetUtil.queryDownloadItemByRefId(downloadMgr,
						downloadMgrRef);
				if (di.status == DownloadManager.STATUS_SUCCESSFUL) {
					btnDownloadCancel.setVisibility(View.GONE);
					pbDownloadProgress.setVisibility(View.GONE);
					btnClickToPlay.setVisibility(View.VISIBLE);

					ep.status = Ep.IN_LOCAL;
					app.epsList.data.get(index).status = Ep.IN_LOCAL;

					player.refreshPlaylist();

					app.downloadList.remove(index);
				} else if (di.status == DownloadManager.STATUS_FAILED) {
					btnDownloadCancel.setVisibility(View.GONE);
					pbDownloadProgress.setVisibility(View.GONE);
					btnDownloadCtrl.setVisibility(View.VISIBLE);

					ep.status = Ep.IN_SERVER;
					app.epsList.data.get(index).status = Ep.IN_SERVER;

					ep.downloadProgress = 0;
					app.epsList.data.get(index).downloadProgress = 0;

					app.downloadList.remove(index);

					Toast.makeText(thisContext,
							"下载失败，错误代码：" + String.valueOf(di.reason),
							Toast.LENGTH_LONG).show();
				}

				downloadProgressUpdater.shutdownNow();
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
							downloadProgressUpdater.shutdownNow();
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

	private ScheduledExecutorService downloadProgressUpdater;
	private Runnable updateDownloadStatus = new Runnable() {

		@Override
		public void run() {
			DownloadItem di = NetUtil.queryDownloadItemByRefId(downloadMgr,
					downloadMgrRef);

			if (di.status == DownloadManager.STATUS_RUNNING) {
				pbDownloadProgress.setProgress(di.progress);
				ep.downloadProgress = di.progress;
				app.epsList.data.get(index).downloadProgress = di.progress;
			}
		}
	};

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() < -120) {
			finish();
			return true;
		}
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (imgEpCover != null) {
			// Cancel any pending image work
			ImageWorker.cancelWork(imgEpCover);
			imgEpCover.setImageDrawable(null);
		}

		imgFetcher.closeCache();

		if (ep.status == Ep.DOWNLOADING) {
			unregisterReceiver(receiver);
			downloadProgressUpdater.shutdownNow();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		imgFetcher.setExitTasksEarly(true);
		imgFetcher.flushCache();
	}

	@Override
	public void onResume() {
		super.onResume();
		imgFetcher.setExitTasksEarly(false);
	}
}
