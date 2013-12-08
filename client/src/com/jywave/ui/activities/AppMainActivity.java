package com.jywave.ui.activities;


import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.R.drawable;
import com.jywave.R.id;
import com.jywave.R.layout;
import com.jywave.R.menu;
import com.jywave.ui.DummyTabContent;
import com.jywave.ui.fragments.MainTabEpsFragment;
import com.jywave.ui.fragments.MainTabMeFragment;
import com.jywave.ui.fragments.MainTabMoreFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class AppMainActivity extends FragmentActivity {

	private TabHost tabHost;
	private RadioGroup tabGroup;
	private AppMain app = AppMain.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_main);
		
		//get screen's height and width
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        app.screenHeight = displayMetrics.heightPixels;
        app.screenWidth = displayMetrics.widthPixels;
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
				MainTabEpsFragment tabEpsFragment = (MainTabEpsFragment) fm
						.findFragmentByTag("tabEps");
				MainTabMeFragment tabMeFragment = (MainTabMeFragment) fm
						.findFragmentByTag("tabMe");
				MainTabMoreFragment tabMoreFragment = (MainTabMoreFragment) fm
						.findFragmentByTag("tabMore");

				android.support.v4.app.FragmentTransaction ft = fm
						.beginTransaction();

				if (tabEpsFragment != null)
					ft.detach(tabEpsFragment);

				if (tabMeFragment != null)
					ft.detach(tabMeFragment);

				if (tabMoreFragment != null)
					ft.detach(tabMoreFragment);

				/** If current tab is android */
				if (tabId.equalsIgnoreCase("tabEps")) {

					if (tabEpsFragment == null) {
						ft.add(R.id.realtabcontent, new MainTabEpsFragment(),
								"tabEps");
					} else {
						ft.attach(tabEpsFragment);
					}

				} else if (tabId.equalsIgnoreCase("tabMe")) {
					/** If current tab is apple */
					if (tabMeFragment == null) {
						ft.add(R.id.realtabcontent, new MainTabMeFragment(),
								"tabMe");
					} else {
						ft.attach(tabMeFragment);
					}
				} else {
					if (tabMoreFragment == null) {
						ft.add(R.id.realtabcontent, new MainTabMoreFragment(),
								"tabMore");
					} else {
						ft.attach(tabMoreFragment);
					}
				}
				ft.commit();
			}
		};

		/** Setting tabchangelistener for the tab */
		tabHost.setOnTabChangedListener(tabChangeListener);

		/** Defining tab builder for Andriod tab */
		TabHost.TabSpec tSpecEps = tabHost.newTabSpec("tabEps");
		tSpecEps.setIndicator("tabEps",
				getResources().getDrawable(R.drawable.ico_tab_eps));
		tSpecEps.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecEps);

		TabHost.TabSpec tSpecMe = tabHost.newTabSpec("tabMe");
		tSpecMe.setIndicator("tabMe",
				getResources().getDrawable(R.drawable.ico_tab_me));
		tSpecMe.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecMe);

		TabHost.TabSpec tSpecMore = tabHost.newTabSpec("tabMore");
		tSpecMore.setIndicator("tabMore",
				getResources().getDrawable(R.drawable.ico_tab_more));
		tSpecMore.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecMore);

		tabGroup = (RadioGroup) findViewById(R.id.main_tab_group);
		tabGroup.check(R.id.main_tab_eps);

		tabGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.main_tab_eps:
					tabHost.setCurrentTabByTag("tabEps");
					break;
				case R.id.main_tab_me:
					tabHost.setCurrentTabByTag("tabMe");
					break;
				case R.id.main_tab_more:
					tabHost.setCurrentTabByTag("tabMore");
					break;
				default:
					tabHost.setCurrentTabByTag("tabEps");
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_main, menu);
		return true;
	}
}
