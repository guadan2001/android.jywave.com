package com.jywave.vo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jywave.AppMain;
import com.jywave.util.StringUtil;

import android.util.Log;

public class EpsList{
	private static final String TAG = "EpsList";

	private ArrayList<Ep> data;

	public EpsList() {
		this.data = new ArrayList<Ep>();
	}

	public void add(Ep ep) {
		data.add(ep);
	}
	
	public Ep get(int i)
	{
		return this.data.get(i);
	}
	
	public void set(int i, Ep ep)
	{
		data.set(i, ep);
	}
	
	public void setData(ArrayList<Ep> list)
	{
		this.data = list;
	}

	public void deleteById(int id)
	{
		int index = findIndexById(id);
		
		deleteByIndex(index);
	}
	
	public void deleteByIndex(int index)
	{
		if(index >= 0)
		{
			data.remove(index);
		}
	}

	public Ep getEpById(int id) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).id == id) {
				return data.get(i);
			}

		}

		return null;
	}
	
	public int findIndexById(int id) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).id == id) {
				return i;
			}
		}
		return -1;
	}
	
	public int size()
	{
		return data.size();
	}
	
	public void sortById()
	{
		if(data.size() > 1)
		{
			Collections.sort(data, new Comparator<Ep>() {

				@Override
				public int compare(Ep lhs, Ep rhs) {
					if(lhs.id < rhs.id)
					{
						return 1;
					}
					else
					{
						return -1;
					}
				}
			});
		}
	}
}
