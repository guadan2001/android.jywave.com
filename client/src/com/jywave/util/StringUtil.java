package com.jywave.util;

public class StringUtil {
	public static String getFilenameFromUrl(String url) {
		int i = url.lastIndexOf("/");
		return url.substring(i + 1, url.length());
	}
	
	public static String convertSecondsToTimeString(int seconds)
	{
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
		
		return result;
	}

}
