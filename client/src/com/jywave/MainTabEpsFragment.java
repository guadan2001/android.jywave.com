package com.jywave;

import java.util.ArrayList;
import java.util.List;

import com.jywave.vo.EpsListItem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainTabEpsFragment extends Fragment {
	
	private ListView listEps;
	private List<EpsListItem> listItems;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.main_tab_eps, container, false);
		
		listEps = (ListView)view.findViewById(R.id.listEps);
		
		ImageButton btnPlaying = (ImageButton)view.findViewById(R.id.btnPlaying);
		AppMain appMain = AppMain.getInstance();
		
		if(!appMain.isPlaying())
		{
			btnPlaying.setVisibility(View.GONE);
		}
		
		//prepare some test data
		listItems = new ArrayList<EpsListItem>();
		EpsListItem ep1 = new EpsListItem();
		ep1.id = 1;
		ep1.title = "酱油冲击播-VOL.037-13-11-11";
		ep1.star = 5;
		ep1.length = 2539;
		ep1.status = EpsListItem.IN_SERVER;
		ep1.isNew = true;
		ep1.isLocalEpCover = true;
		ep1.epCoverUrl = "/ep_covers/jyshock037-128.jpg";
		
		EpsListItem ep2 = new EpsListItem();
		ep2.id = 2;
		ep2.title = "酱油冲击播-VOL.036-清晨醒来RELOAD";
		ep2.star = 4;
		ep2.length = 2419;
		ep2.status = EpsListItem.DOWNLOADING;
		ep2.isNew = true;
		ep2.isLocalEpCover = true;
		ep2.epCoverUrl = "/ep_covers/jyshock036-128.jpg";
		
		EpsListItem ep3 = new EpsListItem();
		ep3.id = 3;
		ep3.title = "酱油播动拳-VOL.028-城市-西安";
		ep3.star = 3;
		ep3.length = 5722;
		ep3.status = EpsListItem.IN_LOCAL;
		ep3.isNew = false;
		ep3.isLocalEpCover = true;
		ep3.epCoverUrl = "/ep_covers/jyhadouken028-128.jpg";
		
		EpsListItem ep4 = new EpsListItem();
		ep4.id = 4;
		ep4.title = "酱油冲击播-VOL.035-RONALD JENKEES";
		ep4.star = 2;
		ep4.length = 2360;
		ep4.status = EpsListItem.PLAYING;
		ep4.isNew = false;
		ep4.isLocalEpCover = true;
		ep4.epCoverUrl = "/ep_covers/jyshock035-128.jpg";
		
		listItems.add(ep1);
		listItems.add(ep2);
		listItems.add(ep3);
		listItems.add(ep4);
		
		MainTabEpsListAdapter epsAdapter = new MainTabEpsListAdapter(this.getActivity().getApplicationContext(), listItems);
		listEps.setAdapter(epsAdapter);
		
		listEps.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				intent.putExtra("epId", listItems.get(arg2).id);
				intent.setClass(arg1.getContext(), EpDetailActivity.class);
				startActivity(intent);
			}
		});
		
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}