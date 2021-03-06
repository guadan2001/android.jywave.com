package com.jywave.util;

import com.jywave.vo.DownloadItem;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.database.Cursor;

public class NetUtil {
	
	public static DownloadItem queryDownloadItemByRefId(DownloadManager downloadMgr, long refId)
	{
		Query query = new Query();
		query.setFilterById(refId);
		Cursor runningDownloads = downloadMgr.query(query);
		
		int bytesTotalIdx = runningDownloads.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
		int bytesDownloadedIdx = runningDownloads.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
		int downloadStatus = runningDownloads.getColumnIndex(DownloadManager.COLUMN_STATUS);
		int reason = runningDownloads.getColumnIndex(DownloadManager.COLUMN_REASON);
		
		if(runningDownloads.moveToFirst())
		{
			DownloadItem di = new DownloadItem();
			di.bytesDownloaded = runningDownloads.getInt(bytesDownloadedIdx);
			di.bytesTotal = runningDownloads.getInt(bytesTotalIdx);
			di.refId = refId;
			di.status = runningDownloads.getInt(downloadStatus);
			di.reason = runningDownloads.getInt(reason);
			
			runningDownloads.close();
			
			return di;
		}
		else
		{
			runningDownloads.close();
			return null;
		}
	}
}
