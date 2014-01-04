package com.jywave.ui.fragments;

import com.jywave.AppMain;
import com.jywave.Player;
import com.jywave.R;
import com.jywave.ui.activities.AboutActivity;
import com.jywave.ui.activities.FeedbackActivity;
import com.jywave.ui.activities.PlayerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class MainTabMoreFragment extends Fragment {

	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();

	private ImageButton btnPlaying;
	private RelativeLayout optAbout;
	private RelativeLayout optFeedback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_more, container, false);

		btnPlaying = (ImageButton) view.findViewById(R.id.btnPlaying);
		optAbout = (RelativeLayout) view.findViewById(R.id.optAbout);
		optFeedback = (RelativeLayout) view.findViewById(R.id.optFeedback);

		if (!player.isPlaying) {
			btnPlaying.setVisibility(View.GONE);
		}

		btnPlaying.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("epIndex", player.playingIndex);
				intent.setClass(v.getContext(), PlayerActivity.class);
				startActivity(intent);
			}
		});

		optAbout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), AboutActivity.class);
				startActivity(intent);
			}
		});

		optFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), FeedbackActivity.class);
				startActivity(intent);
			}
		});

		return view;
	}

}