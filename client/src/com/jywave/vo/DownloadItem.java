package com.jywave.vo;

public class DownloadItem {
	public long refId;
	public int status;
	public int reason;
	public long bytesTotal;
	public long bytesDownloaded;
	
	public static enum downloadStatus{DOWNLOADING, DONE};
		
	public DownloadItem()
	{
		
	}
	
	public int getDownloadProgress()
	{
		return (int)(bytesDownloaded * 100 / bytesTotal);
	}
}
