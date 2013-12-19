package com.jywave;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.jywave.player.Player;
import com.jywave.provider.EpProvider;
import com.jywave.provider.PodcasterProvider;
import com.jywave.vo.Ep;
import com.jywave.vo.EpsList;
import com.jywave.vo.Podcaster;
import com.jywave.vo.PodcastersList;

import android.app.Application;
import android.app.DownloadManager;
import android.os.Environment;

public class AppMain extends Application {

	public static AppMain singleton;
	public static final String TAG = "AppMain";
	public static final boolean DEBUG = true;
	public static final String deviceCategory = "android";
	
	public int userId = 0;
	
	// Music Player
	public Player player;

	public EpsList epsList;
	public int sumOfEps;
	
	public int latestClickedEpIndex;
	
	public PodcastersList podcastersList;

	// Download Manager Reference Id to EP's index in epList
	public Map<String, String> downloadList;

	// LocalStorage
	public static final String localStorageDir = Environment.getExternalStorageDirectory().toString() + "/jywave.com/";
	public static final String mp3StorageDir = localStorageDir + "mp3/";
	public static final String imagesCacheDir = localStorageDir + "images/";

	// Network
	public static final String apiHost = "192.168.0.100";
	public static final String apiLocation = "http://192.168.0.100/api.jywave.com/";
	public DownloadManager downloadManager;
	
	// Configuration
	public boolean allowDownloadWithoutWifi;
	
	//Screen Parameters
	public int screenHeight;
	public int screenWidth;
	
	//Debug Server
	public static final String debugServer = "http://192.168.0.100/";
	
	//UI
	public int listEpsScrollPosition = 0;
	
	//Websites
	public String urlOfficialSite = "http://www.jywave.com/";
	public String urlSinaWeibo = "http://weibo.com/jiangyouwave";
	public String urlRenrenSite = "http://zhan.renren.com/jiangyouwave";
	public String urlDoubanSite = "http://site.douban.com/136913/";
	


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
		
		downloadList = new HashMap<String, String>();
		
		latestClickedEpIndex = -1;
		
		initLocalStorage();
		initConfiguration();
	}
	
	public void initEpList()
	{
		epsList = new EpsList();
		
		EpProvider epProvider = new EpProvider(this);
		ArrayList<Ep> result = epProvider.getEps(0, 10);
		if(result != null)
		{
			epsList.data = epProvider.getEps(0, 10);
		}
		else
		{
			epsList.data = new ArrayList<Ep>();
		}
		sumOfEps = epProvider.getEpCount();
	}
	
	public void initPodcastersList()
	{
		podcastersList = new PodcastersList();
		
		PodcasterProvider podcasterProvider = new PodcasterProvider(this);
		ArrayList<Podcaster> result = podcasterProvider.getAllPodcasters();
		
		if(result != null)
		{
			podcastersList.setData(result);
		}
	}
	
	private void initConfiguration()
	{
		// Initialize Configuration
		allowDownloadWithoutWifi = false;
	}
	
	private void initLocalStorage()
	{
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
	}
	
	public void initPlayer()
	{
		player = Player.getInstance();
		player.refreshPlaylist();
	}
}
