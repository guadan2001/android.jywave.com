package com.jywave;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.jywave.service.EpService;
import com.jywave.vo.EpsList;

import android.app.Application;
import android.app.DownloadManager;
import android.os.Environment;

public class AppMain extends Application {
	
	public static AppMain singleton;
	
	private boolean isPlaying;
	
	public int freeSpaceNeededToCache = 10;
	
	//public EpsList downloadList;
	public EpsList epsList;
	
	//Download Manager Reference Id to EP's index in epList
	public Map<String, String> downloadList;
	
	//LocalStorage
	public String localStorageDir = Environment.getExternalStorageDirectory().toString()+"/jywave.com/";
	public String mp3StorageDir = localStorageDir + "mp3/";
	
	//Network
	public String apiLocation = "http://android.jywave.com/";
	public DownloadManager downloadManager;
	
	//Configuration
	public boolean allowDownloadWithoutWifi;	
	
	public static AppMain getInstance()
	{
		return singleton;
	}
	
	@Override
	public final void onCreate()
	{
		super.onCreate();
		singleton = this;
		
		init();
	}
	
	public void init()
	{
		epsList = new EpsList();
		downloadList = new HashMap<String, String>();
		EpService epSrv = new EpService();
		epsList.add(epSrv.getEp(1));
		epsList.add(epSrv.getEp(2));
		epsList.add(epSrv.getEp(3));
		epsList.add(epSrv.getEp(4));
		
		File defaultDir = new File(localStorageDir);
		if(!defaultDir.exists())
		{
			defaultDir.mkdir();
		}
		
		File mp3Dir = new File(mp3StorageDir);
		if(!mp3Dir.exists())
		{
			mp3Dir.mkdir();
		}
		
		//Initialize Configuration
		allowDownloadWithoutWifi = false;
	}
	
	public boolean isPlaying()
	{
		return this.isPlaying;
	}
	
	public void playing(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
	}
}
