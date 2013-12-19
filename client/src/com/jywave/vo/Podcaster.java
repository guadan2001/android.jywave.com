package com.jywave.vo;

public class Podcaster {
	public int id;
	public String name;
	public String description;
	public int heart;
	public String avatarUrl;
	public long updateTime;
	public boolean iLikeIt;
	
	public void iLikeIt()
	{
		iLikeIt = true;
		heart++;
	}
	
	public void iDontLikeIt()
	{
		iLikeIt = false;
		heart--;
	}

}
