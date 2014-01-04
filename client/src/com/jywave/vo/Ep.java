package com.jywave.vo;

import java.io.File;

import com.jywave.AppMain;
import com.jywave.util.StringUtil;

public class Ep {
	public int id;
	public String title;
	public int duration;
	public int sn;
	public String category;
	public int status;
	public String description;
	public long publishDate;
	public int star;
	public int rating;
	public String url;
	public String coverUrl;
	public String coverThumbnailUrl;
	public boolean isNew;
	public int downloadProgress;
	
	private String lengthString = "";
	
	public final static int IN_SERVER = 0;
	public final static int DOWNLOADING = 1;
	public final static int IN_LOCAL = 2;
	public final static int PLAYING = 3;
	
	public static final String CAT_JYHADOUKEN = "jyhadouken";
	public static final String CAT_JYSHOCK = "jyshock";
	public static final String CAT_JYTECH = "jytech";
	public static final String CAT_JYREADING = "jyreading";
	
	//convert seconds to string with "HH:mm:ss" format
	public String getLengthString() {
		
		if(this.lengthString != "")
		{
			return lengthString;
		}
		else
		{
			int seconds = this.duration;
			String result = "";
			int hour = seconds / 3600;
			seconds %= 3600;
			int min = seconds / 60;
			seconds %= 60;
			
			if(hour > 0)
			{
				result += String.valueOf(hour);
				result += ":";
			}
			
			if(min < 10 && hour > 0)
			{
				result += "0";
				
			}
			
			result += String.valueOf(min);
			result += ":";
			
			if(seconds < 10)
			{
				result += "0";
			}
			
			result += String.valueOf(seconds);
			
			lengthString = result;
			
			return result;
		}
	}
	
	public String getEpFilename()
	{
		int i = url.lastIndexOf("/");
		return url.substring(i+1, url.length());
	}
	
	public void checkStatus()
	{
		if(url != null)
		{
			File f = new File(AppMain.mp3StorageDir + getEpFilename());
			if(f.exists())
			{
				status = Ep.IN_LOCAL;
			}
			else
			{
				status = Ep.IN_SERVER;
			}
		}
	}
	
	private boolean isInLocal(String url)
	{
		String filename = AppMain.mp3StorageDir + StringUtil.getFilenameFromUrl(url);
		File f = new File(filename);
		if(f.exists())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private long getSizeByUrl(String url)
	{
		String filename = AppMain.mp3StorageDir + StringUtil.getFilenameFromUrl(url);
		File f = new File(filename);
		if(f.exists())
		{
			return f.length();
		}
		else
		{
			return 0;
		}
	}
}
