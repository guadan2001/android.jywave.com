package com.jywave.ui.fragments;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.R.dimen;
import com.jywave.R.drawable;
import com.jywave.R.id;
import com.jywave.R.layout;
import com.jywave.player.Player;
import com.jywave.ui.activities.EpDetailActivity;
import com.jywave.ui.activities.PlayerActivity;
import com.jywave.ui.adapters.MainTabEpsListAdapter;
import com.jywave.util.imagecache.ImageFetcher;
import com.jywave.vo.Ep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainTabEpsFragment extends Fragment {

	private ListView listEps;
	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();
	
	private ImageButton btnPlaying;

	private MainTabEpsListAdapter epsAdapter;

	private ImageFetcher imgFetcher;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_eps, container, false);

		listEps = (ListView) view.findViewById(R.id.listEps);

		btnPlaying = (ImageButton) view.findViewById(R.id.btnPlaying);

		if (!player.isPlaying) {
			btnPlaying.setVisibility(View.GONE);
		}

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		imgFetcher = new ImageFetcher(getActivity(), getResources()
				.getDimensionPixelSize(R.dimen.ep_cover_thumbnail));
		imgFetcher.setLoadingImage(R.drawable.picture);
		imgFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				app.cacheParams);

		epsAdapter = new MainTabEpsListAdapter(this.getActivity()
				.getApplicationContext(), imgFetcher);
		listEps.setAdapter(epsAdapter);

		listEps.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.putExtra("epIndex", arg2);
				
				int status =app.epsList.data.get(arg2).status; 
				
				if(status == Ep.IN_SERVER || status == Ep.DOWNLOADING)
				{
					intent.setClass(arg1.getContext(), EpDetailActivity.class);
					startActivity(intent);
				}
				else
				{
					intent.setClass(arg1.getContext(), PlayerActivity.class);
					startActivity(intent);
				}
			}
		});

		listEps.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					app.listEpsScrollPosition = listEps.getFirstVisiblePosition();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		
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
	public void onResume() {
		super.onResume();

		epsAdapter.notifyDataSetChanged();

		imgFetcher.setExitTasksEarly(false);
		
		listEps.setSelection(app.listEpsScrollPosition);
		
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
		imgFetcher.setPauseWork(false);
		imgFetcher.setExitTasksEarly(true);
		imgFetcher.flushCache();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		imgFetcher.closeCache();
	}

//	private void updateView() {
//		Log.d(TAG,
//				"getFirstVisiblePosition(): "
//						+ String.valueOf(listEps.getFirstVisiblePosition()));
//		Log.d(TAG,
//				"getLastVisiblePosition(): "
//						+ String.valueOf(listEps.getLastVisiblePosition()));
//		View v = listEps.getChildAt(app.latestClickedEpIndex
//				- listEps.getFirstVisiblePosition());
//		// View v = listEps.getChildAt(app.latestClickedEpIndex);
//		int status = app.epsList.data.get(app.latestClickedEpIndex).status;
//		int downloadProgress = app.epsList.data.get(app.latestClickedEpIndex).downloadProgress;
//		TextView txtEpStatus = (TextView) v.findViewById(R.id.txtEpStatus);
//
//		switch (status) {
//		case Ep.IN_SERVER:
//			txtEpStatus.setVisibility(View.GONE);
//			break;
//		case Ep.IN_LOCAL:
//			txtEpStatus.setText("已下载");
//			break;
//		case Ep.DOWNLOADING:
//			txtEpStatus.setText("已下载" + String.valueOf(downloadProgress) + "%");
//			break;
//		case Ep.PLAYING:
//			txtEpStatus.setText("正在播放");
//			break;
//		}
//	}

}