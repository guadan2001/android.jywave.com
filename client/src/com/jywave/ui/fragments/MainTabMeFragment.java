package com.jywave.ui.fragments;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.R.id;
import com.jywave.R.layout;
import com.jywave.player.Player;
import com.jywave.ui.activities.PlayerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainTabMeFragment extends Fragment {

	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();

	private ImageButton btnPlaying;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_me, container, false);

		btnPlaying = (ImageButton) view.findViewById(R.id.btnPlaying);

		if (!player.isPlaying) {
			btnPlaying.setVisibility(View.GONE);
		}

		btnPlaying.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("epIndex", player.playingIndexOfEpList);
				intent.setClass(v.getContext(), PlayerActivity.class);
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