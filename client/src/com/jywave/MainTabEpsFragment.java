package com.jywave;

import com.jywave.util.imagecache.ImageFetcher;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainTabEpsFragment extends Fragment {

	private static final String TAG = "MainTabEpsFragment";

	private ListView listEps;
	private AppMain app = AppMain.getInstance();

	private MainTabEpsListAdapter epsAdapter;

	private ImageFetcher imgFetcher;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_eps, container, false);

		listEps = (ListView) view.findViewById(R.id.listEps);

		ImageButton btnPlaying = (ImageButton) view
				.findViewById(R.id.btnPlaying);
		AppMain appMain = AppMain.getInstance();

		if (!appMain.isPlaying()) {
			btnPlaying.setVisibility(View.GONE);
		}

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		imgFetcher = new ImageFetcher(getActivity(), getResources()
				.getDimensionPixelSize(R.dimen.ep_cover_thumbnail));
		imgFetcher.setLoadingImage(R.drawable.ep_cover);
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
				intent.setClass(arg1.getContext(), EpDetailActivity.class);
				startActivity(intent);
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