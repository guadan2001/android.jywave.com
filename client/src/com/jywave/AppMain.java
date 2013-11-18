package com.jywave;

import com.jywave.vo.EpsList;

import android.app.Application;
import android.app.DownloadManager;
import android.os.Environment;

public class AppMain extends Application {
	
	public static AppMain singleton;
	
	private boolean isPlaying;
	
	public int freeSpaceNeededToCache = 10;
	
	public EpsList downloadList;
	
	//LocalStorage
	public String localStorageDir = Environment.getExternalStorageDirectory().toString()+"/jywave.com/";
	
	//Network
	public String apiLocation = "http://android.jywave.com/";
	public DownloadManager downloadManager;
	
	
	public static AppMain getInstance()
	{
		return singleton;
	}
	
	@Override
	public final void onCreate()
	{
		super.onCreate();
		singleton = this;
		
		downloadList = new EpsList();
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
