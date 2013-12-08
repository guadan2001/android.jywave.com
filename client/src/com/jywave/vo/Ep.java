package com.jywave.vo;

import java.util.Date;

public class Ep {
	public int id;
	public String title;
	public int duration;
	public int status;
	public String description;
	public Date publishDate;
	public int star;
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
}
