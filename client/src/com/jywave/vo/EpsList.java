package com.jywave.vo;

import java.util.ArrayList;
import java.util.List;

public class EpsList{

	public List<Ep> data;

	public EpsList() {
		this.data = new ArrayList<Ep>();
	}

	public void add(Ep ep) {
		data.add(ep);
	}

	public boolean deleteById(int id)
	{
		int index = findIndexById(id);
		
		if(index >= 0)
		{
			data.remove(index);
			return true;
		}
		else
		{
			return false;
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

}
