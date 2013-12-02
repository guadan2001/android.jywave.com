package com.jywave.util;

import java.io.File;

import android.net.Uri;

public class StringUtil {
	public static String getFilenameFromUrl(String url)
	{
		Uri uri = Uri.parse(url);
		String path = uri.getPath();
		File file = new File(path);
		
		return file.getName();
	}

}
