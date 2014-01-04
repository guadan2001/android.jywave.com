package com.jywave.ui.activities;

import java.io.File;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jywave.AppMain;
import com.jywave.Player;
import com.jywave.R;
import com.jywave.provider.EpProvider;
import com.jywave.ui.components.swipelistview.BaseSwipeListViewListener;
import com.jywave.ui.components.swipelistview.SwipeListView;
import com.jywave.util.StringUtil;
import com.jywave.vo.Ep;

public class MyDownloadsActivity extends Activity {
	private static final String TAG = "DownloadListActivity";

	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();
	private Context thisContext;

	private ImageButton btnPlaying;
	private ImageButton btnBack;

	private DownloadListAdapter listviewDownloadsAdapter;
	private SwipeListView listviewDownloads;

	private int clickedIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUIElements();

		initUIListeners();

	}

	private void initUIElements() {
		setContentView(R.layout.my_downloads);

		thisContext = this;

		listviewDownloads = (SwipeListView) findViewById(R.id.listDownloads);
		btnPlaying = (ImageButton) findViewById(R.id.btnPlaying);
		btnBack = (ImageButton) findViewById(R.id.btnBack);

		if (!player.isPlaying) {
			btnPlaying.setVisibility(View.GONE);
		}

		listviewDownloadsAdapter = new DownloadListAdapter(thisContext, null);
		listviewDownloads.setAdapter(listviewDownloadsAdapter);

	}

	private void initUIListeners() {
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnPlaying.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("epIndex", player.playingIndex);
				intent.setClass(v.getContext(), PlayerActivity.class);
				startActivity(intent);
			}
		});

		listviewDownloadsAdapter.setBtnDeleteListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < listviewDownloads.getChildCount(); i++) {
					if (v == listviewDownloads.getChildAt(i).findViewById(
							R.id.btnDelete)) {
						clickedIndex = i;
						break;
					}
				}

				delete();
			}
		});
		
		listviewDownloads.setSwipeListViewListener(new BaseSwipeListViewListener(){
			@Override
			public void onOpened(int position, boolean toRight)
			{
			}

			@Override
			public void onClosed(int position, boolean fromRight)
			{
			}

			@Override
			public void onListChanged()
			{
			}

			@Override
			public void onMove(int position, float x)
			{
			}

			@Override
			public void onStartOpen(int position, int action, boolean right)
			{
				// L.d("swipe", String.format(
				// "onStartOpen %d - action %d", position, action));
			}

			@Override
			public void onStartClose(int position, boolean right)
			{
			}

			@Override
			public void onClickFrontView(int position)
			{
				Log.d(TAG, "clicked index: " + String.valueOf(position));
				Intent intent = new Intent();
				intent.setClass(thisContext, PlayerActivity.class);
				intent.putExtra("epIndex", position);
				startActivity(intent);
			}

			@Override
			public void onClickBackView(int position)
			{
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions)
			{
			}
		});
		
		listviewDownloads.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
			}
		});
	}

	private void delete() {
		EpProvider epProvider = new EpProvider(thisContext);
		int id = app.downloadedEpsList.get(clickedIndex).id;
		epProvider.deleteDownloadedEp(id);
		listviewDownloadsAdapter.notifyDataSetChanged();
		listviewDownloads.closeOpenedItems();
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

	private class DownloadListAdapter extends BaseAdapter {

		private static final String TAG = "DownloadListAdapter";

		private LayoutInflater listContainer;

		private AppMain app = AppMain.getInstance();
		private Ep ep;

		private ViewHolder vh;

		public OnClickListener btnDeleteListener;

		public DownloadListAdapter(Context context,
				OnClickListener btnDeleteListener) {
			listContainer = LayoutInflater.from(context);
			this.btnDeleteListener = btnDeleteListener;
		}

		public int getCount() {
			return app.downloadedEpsList.size();
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
						R.layout.my_downloads_list_item, null);

				vh = new ViewHolder();
				vh.txtTitle = (TextView) convertView
						.findViewById(R.id.txtEpTitle);
				vh.txtLength = (TextView) convertView
						.findViewById(R.id.txtEpLength);
				vh.txtDuration = (TextView) convertView
						.findViewById(R.id.txtEpDuration);
				vh.imgEpCover = (ImageView) convertView
						.findViewById(R.id.imgEpCover);
				vh.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			ep = app.downloadedEpsList.get(i);

			vh.txtTitle.setText(ep.title);
			vh.txtDuration.setText(StringUtil
					.convertSecondsToTimeString(ep.duration));
			File f = new File(AppMain.mp3StorageDir + ep.getEpFilename());
			vh.txtLength.setText(StringUtil.convertBytesToMBString(f.length()));

			vh.imgEpCover.setTag(ep.coverThumbnailUrl);

			app.fb.display(vh.imgEpCover, AppMain.apiLocation
					+ ep.coverThumbnailUrl);

			if (this.btnDeleteListener != null) {
				vh.btnDelete.setOnClickListener(this.btnDeleteListener);
			}

			return convertView;
		}

		public void setBtnDeleteListener(OnClickListener btnDeleteListener) {
			this.btnDeleteListener = btnDeleteListener;
		}

		private class ViewHolder {
			public TextView txtTitle;
			public TextView txtLength;
			public TextView txtDuration;
			public Button btnDelete;
			public ImageView imgEpCover;
		}

	}
}
