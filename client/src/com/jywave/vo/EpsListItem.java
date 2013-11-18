package com.jywave.vo;

public class EpsListItem {
	
	public int id;
	public String title;
	public boolean isNew;
	public boolean isLocalEpCover;
	public int star;
	public int length;
	public int downloadProgress;
	public int status;
	public String epCoverUrl;
	
	final public static int IN_SERVER = 0;
	final public static int DOWNLOADING = 1;
	final public static int IN_LOCAL = 2;
	final public static int PLAYING = 3;
}
