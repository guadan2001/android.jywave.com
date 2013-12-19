package com.jywave.ui.activities;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.player.Player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;

public class AboutActivity extends Activity {
	private static final String TAG = "AboutActivity";
	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();

	private Context thisContext;

	private ImageButton btnBack;
	private ImageButton btnPlaying;
	private ImageSwitcher imgswtLogo;
	private TextSwitcher txtswtQQGroup;
	private TextSwitcher txtswtWeixin;

	private RelativeLayout linkOfficialSite;
	private RelativeLayout linkSinaWeibo;
	private RelativeLayout linkRenrenSite;
	private RelativeLayout linkDoubanSite;
	private RelativeLayout linkQQGroup;
	private RelativeLayout linkWeixin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		thisContext = this;

		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnPlaying = (ImageButton) findViewById(R.id.btnPlaying);
		imgswtLogo = (ImageSwitcher) findViewById(R.id.imgswtLogo);
		txtswtQQGroup = (TextSwitcher) findViewById(R.id.txtswtQQGroup);
		txtswtWeixin = (TextSwitcher) findViewById(R.id.txtswtWeixin);

		linkOfficialSite = (RelativeLayout) findViewById(R.id.linkOfficialSite);
		linkSinaWeibo = (RelativeLayout) findViewById(R.id.linkSinaWeibo);
		linkRenrenSite = (RelativeLayout) findViewById(R.id.linkRenrenSite);
		linkDoubanSite = (RelativeLayout) findViewById(R.id.linkDoubanSite);
		linkQQGroup = (RelativeLayout) findViewById(R.id.linkQQGroup);
		linkWeixin = (RelativeLayout) findViewById(R.id.linkWeixin);

		if (!player.isPlaying) {
			btnPlaying.setVisibility(View.GONE);
		}

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
				intent.putExtra("epIndex", player.playingIndexOfEpList);
				intent.setClass(v.getContext(), PlayerActivity.class);
				startActivity(intent);
			}
		});

		imgswtLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imgswtLogo.showNext();
			}
		});

		linkOfficialSite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setData(Uri.parse(app.urlOfficialSite));
				startActivity(intent);
			}
		});
		
		linkSinaWeibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setData(Uri.parse(app.urlSinaWeibo));
				startActivity(intent);
			}
		});

		linkRenrenSite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setData(Uri.parse(app.urlRenrenSite));
				startActivity(intent);
			}
		});

		linkDoubanSite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setData(Uri.parse(app.urlDoubanSite));
				startActivity(intent);
			}
		});
		
		linkQQGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				txtswtQQGroup.showNext();
			}
		});
		
		linkWeixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				txtswtWeixin.showNext();
			}
		});

	}
}
