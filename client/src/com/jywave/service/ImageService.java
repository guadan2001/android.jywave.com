package com.jywave.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.jywave.AppMain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageService {

	private String LOG_TAG = "Image Service";

	public Bitmap loadRemoteImage(String url) {
		Bitmap bmp = null;
		try {
			URL urlObj = new URL(url);
			URLConnection conn = urlObj.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bmp = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (IOException e) {
			Log.e(LOG_TAG, "Load Remote Image Failed, Info:" + e.toString());
		}
		return bmp;
	}

	public Bitmap loadLocalImage(String uri) {
		Bitmap bmp = null;
		try {
			File file = new File(uri);
			if (file.exists()) {
				bmp = BitmapFactory.decodeFile(uri);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Image File does not exist, Info: " + e.toString());
		}

		return bmp;

	}

	public void saveImageToLocal(Bitmap bmp, String filename) {
		try {
			File file = new File(filename);
			file.createNewFile();
			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Saving image to local failed, info:" + e.toString());
		}
	}

	public Bitmap loadImage(String url) {
		Bitmap bmp = null;

		try {
			URL urlObj = new URL(url);
			String filename = urlObj.getFile();
			AppMain app = AppMain.getInstance();
			File img = new File(app.localStorageDir + "/" + filename);
			if (img.exists()) {
				bmp = loadLocalImage(app.localStorageDir + "/" + filename);
			} else {
				bmp = loadRemoteImage(url);
				saveImageToLocal(bmp, filename);
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, "URL object creation failed, info:" + e.toString());

		}
		return bmp;
	}

}
