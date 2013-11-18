package com.jywave.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.jywave.AppMain;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class LocalStorage {
	
	private AppMain app;
	private final String LOG_TAG = "Local Storage";

	public LocalStorage() {
		this.app = AppMain.getInstance();
	}


	public void saveBmp(Bitmap bmp, String filename) {
		if (bmp == null) {
			Log.w(LOG_TAG, "Trying to save null bitmap.");
			return;
		}
		
		if(!isStorageReady())
		{
			Log.w(LOG_TAG, "Storage device is not ready to write.");
			return;
		}
		if (app.freeSpaceNeededToCache > freeSpace()) 
		{
			Log.w(LOG_TAG, "Low free space to write");
			return;
		}
		
		File file = new File(filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
			Log.i(LOG_TAG, "Bitmap saved");

		} catch (FileNotFoundException e) {
			Log.w(LOG_TAG, "FileNotFoundException");
		} catch (IOException e) {
			Log.w(LOG_TAG, "IOException");
		}

	}

	private int freeSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		int freeMB = (stat.getAvailableBlocks() * stat.getBlockSize())
				/ (1024 * 1024);
		return freeMB;
	}
	
	private boolean isStorageReady()
	{
		if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
