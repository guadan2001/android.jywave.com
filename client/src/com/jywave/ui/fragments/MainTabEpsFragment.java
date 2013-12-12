package com.jywave.ui.fragments;

import java.util.ArrayList;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.player.Player;
import com.jywave.provider.EpProvider;
import com.jywave.ui.activities.EpDetailActivity;
import com.jywave.ui.activities.PlayerActivity;
import com.jywave.ui.adapters.MainTabEpsListAdapter;
import com.jywave.ui.components.XListView;
import com.jywave.ui.components.XListView.IXListViewListener;
import com.jywave.util.imagecache.ImageFetcher;
import com.jywave.vo.Ep;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainTabEpsFragment extends Fragment  implements IXListViewListener{
	
	private static final String TAG = "MainTabepsFragment";

	private XListView listEps;
	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();
	
	private Context thisContext;
	
	private ImageButton btnPlaying;

	private MainTabEpsListAdapter epsAdapter;

	private ImageFetcher imgFetcher;
	
	private boolean isRefreshing = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_eps, container, false);
		thisContext = this.getActivity();

		listEps = (XListView) view.findViewById(R.id.listEps);

		btnPlaying = (ImageButton) view.findViewById(R.id.btnPlaying);
		
		listEps.setPullLoadEnable(true);
		listEps.setPullRefreshEnable(true);
		listEps.setXListViewListener(this);

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
		
		if(app.sumOfEps == 0 && !isRefreshing)
		{
			RefreshEpsTask task = new RefreshEpsTask();
			task.execute(new Void[]{});
		}
		
		listEps.setAdapter(epsAdapter);

		listEps.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.putExtra("epIndex", arg2 - 1);
				
				int status =app.epsList.data.get(arg2 -1).status; 
				
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
	
	@Override
	public void onLoadMore() {
		
		if(app.epsList.size() < app.sumOfEps)
		{
			LoadMoreEpsTask task = new LoadMoreEpsTask();
			task.execute(new Void[]{});
		}
		else
		{
			Toast.makeText(thisContext, "所有的节目都已显示了，亲", Toast.LENGTH_LONG).show();
			listEps.stopLoadMore();
		}
	}
	
	@Override
	public void onRefresh() {
		if(!isRefreshing)
		{
			RefreshEpsTask task = new RefreshEpsTask();
			task.execute(new Void[]{});
		}
	}

	
//	private void refreshEps()
//	{
//		Animation aniCounterclockwiseRotation = AnimationUtils
//				.loadAnimation(this.getActivity(), R.anim.counterclockwise_rotation_infinite);
//		btnRefresh.startAnimation(aniCounterclockwiseRotation);
//		btnRefresh.setClickable(false);
//		
//		RefreshEpsTask task = new RefreshEpsTask();
//		task.execute(new Void[]{});
//	}
	
	class RefreshEpsTask extends AsyncTask<Void, Void, Void>
	{
		
		@Override
		protected void onPreExecute() {
			isRefreshing = true;
		};

		@Override
		protected Void doInBackground(Void... params) {
			EpProvider epProvider = new EpProvider(thisContext);
			epProvider.sync();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			app.initEpList();
			player.refreshPlaylist();
			epsAdapter.notifyDataSetChanged();
			listEps.stopRefresh();
			isRefreshing = false;
		}
	}
	
	class LoadMoreEpsTask extends AsyncTask<Void, Void, Void>
	{
		private ArrayList<Ep> eps;
		@Override
		protected Void doInBackground(Void... params) {
			EpProvider epProvider = new EpProvider(thisContext);
			eps = epProvider.getEps(app.epsList.size(), 10);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			for(int i=0;i<eps.size();i++)
			{
				app.epsList.add(eps.get(i));
			}
			epsAdapter.notifyDataSetChanged();
			listEps.stopLoadMore();
		}
	}

	
}