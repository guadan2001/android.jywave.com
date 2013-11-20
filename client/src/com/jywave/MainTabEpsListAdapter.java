package com.jywave;

import com.jywave.service.ImageService;
import com.jywave.vo.Ep;
import com.jywave.vo.EpsList;
import com.jywave.vo.EpsListItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainTabEpsListAdapter extends BaseAdapter {
	private LayoutInflater listContainer;
	private AppMain app = AppMain.getInstance();
	
	private ImageView epCover;

	public MainTabEpsListAdapter(Context context, EpsList epsList) {
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

	/**
	 * 显示物品详情
	 * 
	 * @param clickID
	 */
	// private void showDetailInfo(int clickID) {
	// new AlertDialog.Builder(context)
	// .setTitle("物品详情：" + listItems.get(clickID).get("info"))
	// .setMessage(listItems.get(clickID).get("detail").toString())
	// .setPositiveButton("确定", null)
	// .show();
	// }

	/**
	 * ListView Item设置
	 */
	public View getView(int i, View convertView, ViewGroup parent) {

		convertView = listContainer.inflate(R.layout.main_tab_eps_list_item,
				null);

		TextView title = (TextView) convertView.findViewById(R.id.txtEpTitle);
		TextView length = (TextView) convertView.findViewById(R.id.txtEpLength);
		TextView status = (TextView) convertView.findViewById(R.id.txtEpStatus);
		ImageView isNew = (ImageView) convertView
				.findViewById(R.id.imgEpNewMark);
		ImageView star = (ImageView) convertView.findViewById(R.id.imgEpStar);
		epCover = (ImageView) convertView.findViewById(R.id.imgEpCover);

		Ep ep = app.epsList.data.get(i);
		
		title.setText(ep.title);
		length.setText(ep.getLengthString());

		switch (ep.status) {
		case EpsListItem.IN_SERVER:
			status.setVisibility(View.GONE);
			break;
		case EpsListItem.IN_LOCAL:
			status.setText("已下载");
			break;
		case EpsListItem.DOWNLOADING:
			status.setText("已下载"
					+ String.valueOf(ep.downloadProgress) + "%");
			break;
		case EpsListItem.PLAYING:
			status.setText("正在播放");
			break;
		}
		
		if(ep.isNew)
		{
			isNew.setVisibility(View.VISIBLE);
		}
		else
		{
			isNew.setVisibility(View.GONE);
		}
		
		switch(ep.star)
		{
		case 1:
			star.setImageResource(R.drawable.star1);
			break;
		case 2:
			star.setImageResource(R.drawable.star2);
			break;
		case 3:
			star.setImageResource(R.drawable.star3);
			break;
		case 4:
			star.setImageResource(R.drawable.star4);
			break;
		case 5:
			star.setImageResource(R.drawable.star5);
			break;
		}
		
		new GetEpCover().execute(ep.coverUrl);
		
		return convertView;
	}
	
	public void setEpCover(Bitmap bmp) {
		epCover.setImageBitmap(bmp);
	}

	private class GetEpCover extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			ImageService imgSrv = new ImageService();
			return imgSrv.loadImage(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				MainTabEpsListAdapter.this.setEpCover(result);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
}
