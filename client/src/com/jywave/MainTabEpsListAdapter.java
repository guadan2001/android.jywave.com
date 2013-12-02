package com.jywave;

import com.jywave.util.imagecache.ImageFetcher;
import com.jywave.vo.Ep;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainTabEpsListAdapter extends BaseAdapter {

	private static final String TAG = "MainTabEpsListAdapter";

	private LayoutInflater listContainer;

	private AppMain app = AppMain.getInstance();
	private Ep ep;

	private ImageFetcher imgFetcher;
	
	private ViewHolder viewHolder;

	public MainTabEpsListAdapter(Context context, ImageFetcher imgFetcher) {
		listContainer = LayoutInflater.from(context);
		this.imgFetcher = imgFetcher;
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

		viewHolder.imgEpCover = (ImageView) convertView.findViewById(R.id.imgEpCover);
		imgFetcher.setImageFadeIn(true);
		imgFetcher.loadImage(ep.coverThumbnailUrl, viewHolder.imgEpCover);

		return convertView;
	}

	private static class ViewHolder {
		public TextView txtEpTitle;
		public TextView txtEpLength;
		public TextView txtEpStatus;
		public ImageView imgEpCover;
		public ImageView imgEpIsNew;
		public ImageView imgEpStar;
	}

}
