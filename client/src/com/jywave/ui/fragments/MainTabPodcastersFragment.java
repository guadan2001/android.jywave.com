package com.jywave.ui.fragments;

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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jywave.AppMain;
import com.jywave.Player;
import com.jywave.R;
import com.jywave.provider.PodcasterProvider;
import com.jywave.ui.activities.PlayerActivity;
import com.jywave.ui.components.xlistview.XListView;
import com.jywave.ui.components.xlistview.XListView.IXListViewListener;
import com.jywave.vo.Podcaster;

public class MainTabPodcastersFragment extends Fragment implements
		IXListViewListener {

	private static final String TAG = "MainTabPodcastersFragment";

	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();
	private Context thisContext;

	private ImageButton btnPlaying;

	private MainTabPodcastersListAdapter listPodcastersAdapter;
	private XListView listPodcasters;

	private boolean isRefreshing = false;
	private int clickedIndex;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.main_tab_podcasters, container,
				false);
		thisContext = this.getActivity();

		listPodcasters = (XListView) view.findViewById(R.id.listPodcasters);
		btnPlaying = (ImageButton) view.findViewById(R.id.btnPlaying);

		listPodcasters.setPullLoadEnable(false);
		listPodcasters.setPullRefreshEnable(true);
		listPodcasters.setXListViewListener(this);

		if (!player.isPlaying) {
			btnPlaying.setVisibility(View.GONE);
		}

		this.listPodcastersAdapter = new MainTabPodcastersListAdapter(this
				.getActivity().getApplicationContext(), null);
		listPodcastersAdapter.setImgswtHeartListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < listPodcasters.getChildCount(); i++) {
					if (v == listPodcasters.getChildAt(i).findViewById(
							R.id.imgswtHeart)) {
						clickedIndex = i - 1;
						break;
					}
				}

				Log.d(TAG,
						"clicked item is: "
								+ app.podcastersList.get(clickedIndex).name);

				if (app.podcastersList.get(clickedIndex).iLikeIt) {
					iDontLikePodcaster();
				} else {
					iLikePodcaster();
				}
			}
		});
		listPodcasters.setAdapter(listPodcastersAdapter);

		if (app.podcastersList.size() == 0 && !isRefreshing) {
			RefreshPodcastersTask task = new RefreshPodcastersTask();
			task.execute(new Void[] {});
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

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
		listPodcastersAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh() {
		if (!isRefreshing) {
			RefreshPodcastersTask task = new RefreshPodcastersTask();
			task.execute(new Void[] {});
		}
	}

	@Override
	public void onLoadMore() {

	}

	public void iLikePodcaster() {
		ILikePodcasterTask task = new ILikePodcasterTask();
		task.execute(new Void[] {});
	}

	public void iDontLikePodcaster() {
		IDontLikePodcasterTask task = new IDontLikePodcasterTask();
		task.execute(new Void[] {});
	}

	public void showNetworkErrorMessage() {
		Toast.makeText(
				thisContext,
				getResources().getString(
						R.string.network_conn_error_try_again_later),
				Toast.LENGTH_LONG).show();
	}

	public void showOperationFailedMessage() {
		Toast.makeText(
				thisContext,
				getResources()
						.getString(R.string.service_error_try_again_later),
				Toast.LENGTH_LONG).show();
	}

	class RefreshPodcastersTask extends AsyncTask<Void, Void, Void> {
		private PodcasterProvider.resultCode resultCode;

		@Override
		protected void onPreExecute() {
			isRefreshing = true;
		};

		@Override
		protected Void doInBackground(Void... params) {
			PodcasterProvider podcasterProvider = new PodcasterProvider(
					thisContext);
			resultCode = podcasterProvider.sync();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			switch (resultCode) {
			case DONE:
				app.initPodcastersList();
				break;
			case FAILED:
				showOperationFailedMessage();
				break;
			case NETWORK_ERROR:
				showNetworkErrorMessage();
				break;
			}
			listPodcastersAdapter.notifyDataSetChanged();
			listPodcasters.stopRefresh();
			isRefreshing = false;
		}
	}

	class ILikePodcasterTask extends AsyncTask<Void, Void, Void> {
		private PodcasterProvider.resultCode resultCode;

		@Override
		protected void onPreExecute() {
		};

		@Override
		protected Void doInBackground(Void... params) {
			PodcasterProvider podcasterProvider = new PodcasterProvider(
					thisContext);
			resultCode = podcasterProvider.iLikeIt(app.podcastersList
					.get(clickedIndex).id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			switch (resultCode) {
			case DONE:
				app.podcastersList.get(clickedIndex).iLikeIt();
				break;
			case FAILED:
				showOperationFailedMessage();
				break;
			case NETWORK_ERROR:
				showNetworkErrorMessage();
				break;
			}
			listPodcastersAdapter.notifyDataSetChanged();
		}
	}

	class IDontLikePodcasterTask extends AsyncTask<Void, Void, Void> {
		private PodcasterProvider.resultCode resultCode;

		@Override
		protected void onPreExecute() {
		};

		@Override
		protected Void doInBackground(Void... params) {
			PodcasterProvider podcasterProvider = new PodcasterProvider(
					thisContext);
			resultCode = podcasterProvider.iDontLikeIt(app.podcastersList
					.get(clickedIndex).id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			switch (resultCode) {
			case DONE:
				app.podcastersList.get(clickedIndex).iDontLikeIt();
				break;
			case FAILED:
				showOperationFailedMessage();
				break;
			case NETWORK_ERROR:
				showNetworkErrorMessage();
				break;
			}
			listPodcastersAdapter.notifyDataSetChanged();
		}
	}

	private class MainTabPodcastersListAdapter extends BaseAdapter {

		private static final String TAG = "MainTabPodcastersListAdapter";

		private LayoutInflater listContainer;

		private AppMain app = AppMain.getInstance();
		private Podcaster podcaster;

		private ViewHolder viewHolder;

		public OnClickListener imgswtHeartListener;

		public MainTabPodcastersListAdapter(Context context,
				OnClickListener imgswtHeartListener) {
			listContainer = LayoutInflater.from(context);
			this.imgswtHeartListener = imgswtHeartListener;
		}

		public int getCount() {
			return app.podcastersList.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int i, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = listContainer.inflate(
						R.layout.main_tab_podcasters_list_item, null);

				viewHolder = new ViewHolder();
				viewHolder.txtName = (TextView) convertView
						.findViewById(R.id.txtName);
				viewHolder.txtHeart = (TextView) convertView
						.findViewById(R.id.txtHeart);
				viewHolder.imgswtHeart = (ImageSwitcher) convertView
						.findViewById(R.id.imgswtHeart);
				viewHolder.imgAvatar = (ImageView) convertView
						.findViewById(R.id.imgAvatar);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			podcaster = app.podcastersList.get(i);

			viewHolder.txtName.setText(podcaster.name);
			viewHolder.txtHeart.setText(String.valueOf(podcaster.heart));

			if (podcaster.iLikeIt) {
				viewHolder.imgswtHeart.setDisplayedChild(1);
			} else {
				viewHolder.imgswtHeart.setDisplayedChild(0);
			}

			viewHolder.imgAvatar.setTag(AppMain.apiLocation
					+ podcaster.avatarUrl);
			String url = AppMain.apiLocation + podcaster.avatarUrl;
			app.fb.display(viewHolder.imgAvatar, url);

			if (this.imgswtHeartListener != null) {
				viewHolder.imgswtHeart
						.setOnClickListener(this.imgswtHeartListener);
			}

			return convertView;
		}

		public void setImgswtHeartListener(OnClickListener imgswtHeartListener) {
			this.imgswtHeartListener = imgswtHeartListener;
		}

		private class ViewHolder {
			public TextView txtName;
			public TextView txtHeart;
			public ImageSwitcher imgswtHeart;
			public ImageView imgAvatar;
		}

	}
}