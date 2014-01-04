package com.jywave.service;

import com.jywave.AppMain;
import com.jywave.Constants;
import com.jywave.Player;
import com.jywave.R;
import com.jywave.provider.EpProvider;
import com.jywave.ui.activities.PlayerActivity;
import com.jywave.util.NetUtil;
import com.jywave.vo.DownloadItem;
import com.jywave.vo.Ep;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.Notification.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class DownloadService extends Service {

	private static final String TAG = "DownloadService";
	private Context thisContext;
	
	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();

	private IntentFilter filterDownloadComplete;
	private BroadcastReceiver receiver;
	private DownloadManager downloadManager;
	
	private NotificationManager notificationMgr;
	private Notification.Builder notificationBuilder;

	@Override
	public void onCreate() {
		super.onCreate();
		thisContext = this;
		
		notificationMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationBuilder = new Builder(thisContext);

		filterDownloadComplete = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				long refId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);

				int epId = app.downloadList.get(refId);
				DownloadItem di = NetUtil.queryDownloadItemByRefId(downloadManager,	refId);
				int index = app.epsList.findIndexById(epId);
				
				if (di.status == DownloadManager.STATUS_SUCCESSFUL) {
					
					if(index >= 0)
					{
						app.epsList.get(index).status = Ep.IN_LOCAL;
					}
					
					EpProvider epProvider = new EpProvider(thisContext);
					Ep ep = epProvider.getEp(epId);
					
					app.downloadedEpsList.add(ep);
					app.refreshDownloadedList();

					app.downloadList.remove(refId);
					
					//---------------------------------------------------------------------------------------------------
					//	Notification
					//---------------------------------------------------------------------------------------------------
					
//					PendingIntent intentPlay = new PendingIntent();
//					int idx = app.downloadedEpsList.findIndexById(ep.id);
//					intent.putExtra("epIndex", idx);
//					intent.setClass(thisContext, PlayerActivity.class);
					
					notificationBuilder.setSmallIcon(R.drawable.ico_checkmark);
					notificationBuilder.setTicker(ep.title + " 下载完成");
					notificationBuilder.setAutoCancel(true);
					notificationBuilder.setContentTitle(getString(R.string.app_name));
					notificationBuilder.setContentText(ep.title + "下载完成");
//					notificationBuilder.setContentIntent(intentPlay);
					notificationBuilder.setWhen(System.currentTimeMillis());
					Notification n = notificationBuilder.getNotification();
					notificationMgr.notify(Constants.NOTIFICATION_ID_DOWNLOAD_COMPLETE_BASE + ep.id, n);
					
				} else if (di.status == DownloadManager.STATUS_FAILED) {

					if(index >= 0)
					{
						app.epsList.get(index).status = Ep.IN_SERVER;
						app.epsList.get(index).downloadProgress = 0;
					}

					app.downloadList.remove(refId);
				}
				
				if(app.downloadList.size() == 0)
				{
					unregisterReceiver(receiver);
					stopSelf();
				}
			}
		};
		
		registerReceiver(receiver, filterDownloadComplete);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
