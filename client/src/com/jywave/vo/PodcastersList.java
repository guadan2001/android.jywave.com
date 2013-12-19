package com.jywave.vo;

import java.util.ArrayList;
import java.util.List;

public class PodcastersList {
	
	private List<Podcaster> data;
	
	public PodcastersList()
	{
		this.data = new ArrayList<Podcaster>();
	}
	
	public void add(Podcaster podcaster)
	{
		this.data.add(podcaster);
	}
	
	public int size()
	{
		return data.size();
	}
	
	public void set(Podcaster podcaster)
	{
		int index = getIndexById(podcaster.id);
		
		if(index >= 0)
		{
			data.set(index, podcaster);
		}
	}
	
	public void setData(ArrayList<Podcaster> podcasterList)
	{
		this.data = podcasterList;
	}
	
	
	public Podcaster get(int index)
	{
		return data.get(index);
	}
	
	public Podcaster getPodcasterById(int id)
	{
		int index = getIndexById(id);
		if(index >= 0)
		{
			return data.get(index);
		}
		else
		{
			return null;
		}
	}
	
	public int getIndexById(int id)
	{
		for(int i=0;i<data.size();i++)
		{
			if(data.get(i).id == id)
			{
				return i;
			}
		}
		
		return -1;
	}

}
