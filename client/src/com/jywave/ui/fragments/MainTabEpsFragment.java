package com.jywave.ui.fragments;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.player.Player;
import com.jywave.provider.EpProvider;
import com.jywave.ui.activities.EpDetailActivity;
import com.jywave.ui.activities.PlayerActivity;
import com.jywave.ui.components.XListView;
import com.jywave.ui.components.XListView.IXListViewListener;
import com.jywave.util.CommonUtil;
import com.jywave.vo.Ep;

public class MainTabEpsFragment extends Fragment  implements IXListViewListener{
	
	private static final String TAG = "MainTabepsFragment";

	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();
	
	private Context thisContext;
	
	private ImageButton btnPlaying;

	private MainTabEpsListAdapter epsAdapter;
	private XListView listEps;
	
	private FinalBitmap fb;
	
	private boolean isRefreshing = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
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
		
		fb = FinalBitmap.create(this.getActivity());
		fb.configLoadingImage(R.drawable.picture);
		fb.configDiskCachePath(AppMain.imagesCacheDir);

		epsAdapter = new MainTabEpsListAdapter(this.getActivity().getApplicationContext());
		
		if(app.sumOfEps == 0 && !isRefreshing)
		{
			if(CommonUtil.checkNetState(thisContext))
			{
				RefreshEpsTask task = new RefreshEpsTask();
				task.execute(new Void[]{});
			}
			else
			{
				listEps.stopRefresh();
				Toast.makeText(thisContext, "亲，你的网络貌似没有连接，请检查一下再来", Toast.LENGTH_LONG).show();
			}
			
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
		fb.onResume();

		epsAdapter.notifyDataSetChanged();

		listEps.setSelection(app.listEpsScrollPosition);
		
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
		fb.onPause();
	}

	@Override
	public void onDestroy() {
		try {
			super.onDestroy();
			fb.exitTasksEarly(true);
			fb.closeCache();
			fb.onDestroy();
			fb = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
			if(CommonUtil.checkNetState(thisContext))
			{
				RefreshEpsTask task = new RefreshEpsTask();
				task.execute(new Void[]{});
			}
			else
			{
				listEps.stopRefresh();
				Toast.makeText(thisContext, "亲，你的网络貌似没有连接，请检查一下再来", Toast.LENGTH_LONG).show();
			}
		}
	}
	
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

	private class MainTabEpsListAdapter extends BaseAdapter {

		private static final String TAG = "MainTabEpsListAdapter";

		private LayoutInflater listContainer;

		private AppMain app = AppMain.getInstance();
		private Ep ep;

		private ViewHolder viewHolder;

		public MainTabEpsListAdapter(Context context) {
			listContainer = LayoutInflater.from(context);
		}

		public int getCount() {
			return app.epsList.data.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int i, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = listContainer.inflate(R.layout.main_tab_eps_list_item, null);
				
				viewHolder = new ViewHolder();
				viewHolder.txtEpTitle = (TextView) convertView.findViewById(R.id.txtEpTitle);
				viewHolder.txtEpLength = (TextView) convertView.findViewById(R.id.txtEpLength);
				viewHolder.txtEpStatus = (TextView) convertView.findViewById(R.id.txtEpStatus);
				viewHolder.imgEpIsNew = (ImageView) convertView.findViewById(R.id.imgEpNewMark);
				viewHolder.imgEpStar = (ImageView) convertView.findViewById(R.id.imgEpStar);
				viewHolder.imgEpCover = (ImageView) convertView.findViewById(R.id.imgEpCover);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			ep = app.epsList.data.get(i);

			viewHolder.txtEpTitle.setText(ep.title);
			viewHolder.txtEpLength.setText(ep.getLengthString());

			if (ep.isNew) {
				viewHolder.imgEpIsNew.setVisibility(View.VISIBLE);
			} else {
				viewHolder.imgEpIsNew.setVisibility(View.GONE);
			}

			switch (ep.status) {
			case Ep.IN_SERVER:
				viewHolder.txtEpStatus.setVisibility(View.GONE);
				break;
			case Ep.IN_LOCAL:
				viewHolder.txtEpStatus.setText("已下载");
				break;
			case Ep.DOWNLOADING:
				viewHolder.txtEpStatus.setText("已下载" + String.valueOf(ep.downloadProgress)
						+ "%");
				break;
			case Ep.PLAYING:
				viewHolder.txtEpStatus.setText("正在播放");
				break;
			}

			switch (ep.star) {
			case 1:
				viewHolder.imgEpStar.setImageResource(R.drawable.star1);
				break;
			case 2:
				viewHolder.imgEpStar.setImageResource(R.drawable.star2);
				break;
			case 3:
				viewHolder.imgEpStar.setImageResource(R.drawable.star3);
				break;
			case 4:
				viewHolder.imgEpStar.setImageResource(R.drawable.star4);
				break;
			case 5:
				viewHolder.imgEpStar.setImageResource(R.drawable.star5);
				break;
			}

			viewHolder.imgEpCover.setTag(ep.coverThumbnailUrl);
			fb.display(viewHolder.imgEpCover, ep.coverThumbnailUrl);

			return convertView;
		}

		private class ViewHolder {
			public TextView txtEpTitle;
			public TextView txtEpLength;
			public TextView txtEpStatus;
			public ImageView imgEpCover;
			public ImageView imgEpIsNew;
			public ImageView imgEpStar;
		}

	}
}