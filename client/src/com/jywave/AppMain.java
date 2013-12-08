package com.jywave;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.jywave.player.Player;
import com.jywave.provider.EpProvider;
import com.jywave.util.imagecache.ImageCache.ImageCacheParams;
import com.jywave.vo.Ep;
import com.jywave.vo.EpsList;

import android.R.integer;
import android.app.Application;
import android.app.DownloadManager;
import android.os.Environment;
import android.util.Log;

public class AppMain extends Application {

	public static AppMain singleton;
	public static final String TAG = "App Main";
	public static final boolean DEBUG = false;
	
	// Music Player
	public Player player;

	public EpsList epsList;
	
	public int latestClickedEpIndex;

	// Download Manager Reference Id to EP's index in epList
	public Map<String, String> downloadList;

	// LocalStorage
	public static final String localStorageDir = Environment.getExternalStorageDirectory().toString() + "/jywave.com/";
	public static final String mp3StorageDir = localStorageDir + "mp3/";
	public static final String imagesCacheDir = localStorageDir + "images/";

	// Network
	public String apiLocation = "http://android.jywave.com/";
	public DownloadManager downloadManager;
	
	//Image Cache
	public ImageCacheParams cacheParams; // Set memory cache to 25% of app memory

	// Configuration
	public boolean allowDownloadWithoutWifi;
	
	//Screen Parameters
	public int screenHeight;
	public int screenWidth;
	
	//Debug Server
	public static final String debugServer = "http://192.168.0.100/";
	
	//UI
	public int listEpsScrollPosition = 0;
	
	

	public static AppMain getInstance() {
		return singleton;
	}

	@Override
	public final void onCreate() {
		super.onCreate();
		singleton = this;

		init();
	}

	public void init() {
		epsList = new EpsList();
		downloadList = new HashMap<String, String>();
		
		latestClickedEpIndex = -1;
		
		EpProvider epSrv = new EpProvider();
		
		for(int i=0;i<12;i++)
		{
			epsList.add(epSrv.getEp(i));
		}

		File defaultDir = new File(localStorageDir);
		if (!defaultDir.exists()) {
			defaultDir.mkdir();
		}

		File mp3Dir = new File(mp3StorageDir);
		if (!mp3Dir.exists()) {
			mp3Dir.mkdir();
		}

		File imagesCacheDir = new File(AppMain.imagesCacheDir);
		if (!imagesCacheDir.exists()) {
			imagesCacheDir.mkdir();
		}

		if (AppMain.DEBUG) {
			Log.d(TAG, "Local Storage DIR: " + AppMain.localStorageDir);
		}

		// Initialize Configuration
		allowDownloadWithoutWifi = false;
		
		//set image cache parameters
		cacheParams = new ImageCacheParams(this, "images");
		cacheParams.setMemCacheSizePercent(0.25f);
		
		player = Player.getInstance();
		player.refreshPlaylist();
	}
}
