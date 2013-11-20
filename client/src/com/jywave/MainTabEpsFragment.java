package com.jywave;

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
	private AppMain app = AppMain.getInstance();
	
	private MainTabEpsListAdapter epsAdapter;
	
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
		
		epsAdapter = new MainTabEpsListAdapter(this.getActivity().getApplicationContext(), app.epsList);
		listEps.setAdapter(epsAdapter);
		
		listEps.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				intent.putExtra("epIndex", arg2);
				intent.setClass(arg1.getContext(), EpDetailActivity.class);
				startActivity(intent);
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		epsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}