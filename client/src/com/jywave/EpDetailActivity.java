package com.jywave;

import com.jywave.service.EpService;
import com.jywave.service.ImageService;
import com.jywave.vo.Ep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EpDetailActivity extends Activity{
	
	private TextView epTitle;
	private ImageView epCover;
	private ImageView epStar;
	private TextView epLength;
	private TextView epDescription;
	private LinearLayout llStartDownload;
	private LinearLayout llDownloading;
	private ImageButton btnBack;
	private ImageButton btnPlaying;
	private ImageButton btnDownloadCtrl;
	private Button btnDownload;
	
	private Ep ep;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_detail);
		
		epTitle = (TextView)findViewById(R.id.txtEpTitle);
		epStar = (ImageView)findViewById(R.id.imgEpStar);
		epLength = (TextView)findViewById(R.id.txtEpLength);
		epDescription = (TextView)findViewById(R.id.txtEpDescription);
		llDownloading = (LinearLayout)findViewById(R.id.llDownloading);
		llStartDownload = (LinearLayout)findViewById(R.id.llStartDownload);
		btnBack = (ImageButton)findViewById(R.id.btnBack);
		btnPlaying = (ImageButton)findViewById(R.id.btnPlaying);
		btnDownload = (Button)findViewById(R.id.btnDownload);
		btnDownloadCtrl = (ImageButton)findViewById(R.id.btnDownloadCtrl);
		
		AppMain app = AppMain.getInstance();		
		
		EpService epSrv = new EpService();
		Intent intent = this.getIntent();
		int epId = intent.getIntExtra("epId", 1);
		ep = epSrv.getEp(epId);
		
		epTitle.setText(ep.title);
		epLength.setText(ep.getLengthString());
		epDescription.setText(ep.description);
		
		if(ep.status == Ep.IN_SERVER)
		{
			llDownloading.setVisibility(View.GONE);
		}
		else if(ep.status == Ep.DOWNLOADING)
		{
			llStartDownload.setVisibility(View.GONE);
			btnDownloadCtrl.setImageResource(R.drawable.ico_download_pause);
			btnDownloadCtrl.setOnClickListener(downloadCtrlListener);
			
			app.downloadList.add(ep);
		}
		else if(ep.status == Ep.DOWNLOADING_PAUSED)
		{
			llStartDownload.setVisibility(View.GONE);
			btnDownloadCtrl.setImageResource(R.drawable.ico_download_resume);
			btnDownloadCtrl.setOnClickListener(downloadCtrlListener);
			
			app.downloadList.add(ep);
		}
		else
		{
			llDownloading.setVisibility(View.GONE);
			llStartDownload.setVisibility(View.GONE);
		}
		
		switch(ep.star)
		{
		case 1:
			epStar.setImageResource(R.drawable.star1);
			break;
		case 2:
			epStar.setImageResource(R.drawable.star2);
			break;
		case 3:
			epStar.setImageResource(R.drawable.star3);
			break;
		case 4:
			epStar.setImageResource(R.drawable.star4);
			break;
		case 5:
			epStar.setImageResource(R.drawable.star5);
			break;
		}
		
		if(!app.isPlaying())
		{
			btnPlaying.setVisibility(View.GONE);
		}
		
		new GetEpCover().execute(ep.coverUrl);
		
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnDownloadCtrl.setOnClickListener(downloadCtrlListener);
	}
	
	private OnClickListener downloadCtrlListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			AppMain app = AppMain.getInstance();
			int i = app.downloadList.findIndexById(ep.id);
			
			if(ep.status == Ep.DOWNLOADING)
			{
				btnDownloadCtrl.setImageResource(R.drawable.ico_download_resume);
				ep.status = Ep.DOWNLOADING_PAUSED;
				app.downloadList.data.get(i).status = Ep.DOWNLOADING_PAUSED;
			}
			else if(ep.status == Ep.DOWNLOADING_PAUSED)
			{
				btnDownloadCtrl.setImageResource(R.drawable.ico_download_pause);
				ep.status = Ep.DOWNLOADING;
				app.downloadList.data.get(i).status = Ep.DOWNLOADING;
			}
			
		}
	};
	
	public void setEpCover(Bitmap bmp)
	{
		epCover = (ImageView)findViewById(R.id.imgEpCover);
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
				EpDetailActivity.this.setEpCover(result);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

}
