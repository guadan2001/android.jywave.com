package com.jywave;

import com.jywave.R;
import com.jywave.R.id;
import com.jywave.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MainTabMeFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.main_tab_me, container, false);
		
		ImageButton btnPlaying = (ImageButton)view.findViewById(R.id.btnPlaying);
		AppMain appMain = AppMain.getInstance();
		
		if(!appMain.isPlaying())
		{
			btnPlaying.setVisibility(View.GONE);
		}
		
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}