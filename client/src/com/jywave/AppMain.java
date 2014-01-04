package com.jywave;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import com.jywave.provider.EpProvider;
import com.jywave.provider.PodcasterProvider;
import com.jywave.vo.DownloadItem;
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
	public static final String packageName = "com.jywave";
	
	public static final String APP_KEY_SINA_WEIBO = "2761262264";
	public static final String SECRET_KEY_SINA_WEIBO = "345ffa1e87c6a294909af9e525a10499";
	public static final String REDIRECT_URL_SINA_WEIBO = "https://api.weibo.com/oauth2/default.html";
	public static final String APP_ID_WEIXIN = "wx169a0cc3eabb8767";
	public static final String APP_KEY_WEIXIN = "d284957d81f3d3e4fc82c25bb2898f81";
	public static final String APP_ID_RENREN = "245410";
	public static final String APP_KEY_RENREN = "56f5df8ef4bd4de0b3067d618b3e8918";
	public static final String SECRET_KEY_RENREN = "389d49462e1948a4b9c515064a1cb26e";
	
	public int userId = 0;
	
	// Music Player
	public Player player;

	public EpsList epsList;
	public EpsList downloadedEpsList;
	public int sumOfEps;
	
	public int latestClickedEpIndex;
	
	public PodcastersList podcastersList;

	public Map<Long, Integer> downloadList; //Map<RefId, EpId>

	// LocalStorage
	public static final String localStorageDir = Environment.getExternalStorageDirectory().toString() + "/jywave.com/";
	public static final String mp3StorageDir = localStorageDir + "mp3/";
	public static final String imagesCacheDir = localStorageDir + "images/";

	// Network
	public static final String apiHost = "android.jywave.com";
	public static final String apiLocation = "http://android.jywave.com/";
//	public static final String apiHost = "192.168.0.100";
//	public static final String apiLocation = "http://192.168.0.100/api.jywave.com/";
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
	
	public FinalBitmap fb;
	
	//Websites
	public String urlOfficialSite = "http://www.jywave.com/";
	public String urlSinaWeibo = "http://weibo.com/jiangyouwave";
	public String urlRenrenSite = "http://zhan.renren.com/jiangyouwave";
	public String urlDoubanSite = "http://site.douban.com/136913/";
	
	//Social Networks
	public static final String uidSinaWeibo = "2443459233";
	


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
		
		downloadList = new HashMap<Long, Integer>();
		
		latestClickedEpIndex = -1;
		
		fb = FinalBitmap.create(this);
		fb.configLoadingImage(R.drawable.picture);
		fb.configDiskCachePath(AppMain.imagesCacheDir);
		
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
			epsList.setData(epProvider.getEps(0, 10));
		}
		else
		{
			epsList.setData(new ArrayList<Ep>());
		}
		sumOfEps = epProvider.getEpCount();
	}
	
	public void initDownloadedEpsList()
	{
		EpProvider epProvider = new EpProvider(this);
		downloadedEpsList = epProvider.getDownloadedEpsList();
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
	}
	
	public void refreshDownloadedList() {
		if (downloadedEpsList.size() > 0) {
			for (int i = 0; i < downloadedEpsList.size(); i++) {
				String filename = downloadedEpsList.get(i).getEpFilename();
				File f = new File(AppMain.mp3StorageDir + filename);
				if (!f.exists()) {
					downloadedEpsList.deleteByIndex(i);
				}

				if (player.playingId == downloadedEpsList.get(i).id) {
					downloadedEpsList.get(i).status = Ep.PLAYING;
					player.playingIndex = i;
				}
				else
				{
					downloadedEpsList.get(i).status = Ep.IN_LOCAL;
				}
			}
		}
		
		downloadedEpsList.sortById();
		
		player.playingIndexOfEpsList = epsList.findIndexById(player.playingId);
	}
}
