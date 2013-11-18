package com.jywave;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.jywave.vo.EpsListItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainTabEpsListAdapter extends BaseAdapter {
	private Context context;
	private List<EpsListItem> listItems;
	private LayoutInflater listContainer;

	public MainTabEpsListAdapter(Context context, List<EpsListItem> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context);
		this.listItems = listItems;
	}

	public int getCount() {
		return listItems.size();
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
		ImageView epCover = (ImageView) convertView
				.findViewById(R.id.imgEpCover);
		ImageView star = (ImageView) convertView.findViewById(R.id.imgEpStar);

		title.setText(listItems.get(i).title);
		length.setText(secondsToString(listItems.get(i).length));

		switch (listItems.get(i).status) {
		case EpsListItem.IN_SERVER:
			status.setVisibility(View.GONE);
			break;
		case EpsListItem.IN_LOCAL:
			status.setText("已下载");
			break;
		case EpsListItem.DOWNLOADING:
			status.setText("已下载"
					+ String.valueOf(listItems.get(i).downloadProgress) + "%");
			break;
		case EpsListItem.PLAYING:
			status.setText("正在播放");
			break;
		}
		
		if(listItems.get(i).isNew)
		{
			isNew.setVisibility(View.VISIBLE);
		}
		else
		{
			isNew.setVisibility(View.GONE);
		}
		
		switch(listItems.get(i).star)
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
		
		epCover.setImageResource(R.drawable.ep_cover);

		return convertView;
	}

	private String secondsToString(int seconds) {
		String result = "";
		int hour = seconds / 3600;
		seconds %= 3600;
		int min = seconds / 60;
		seconds %= 60;
		
		if(hour > 0)
		{
			result += String.valueOf(hour);
			result += ":";
		}
		
		if(min < 10 && hour > 0)
		{
			result += "0";
			
		}
		
		result += String.valueOf(min);
		result += ":";
		
		if(seconds < 10 && min > 0)
		{
			result += "0";
		}
		
		result += String.valueOf(seconds);
		
		return result;
		
	}
}
