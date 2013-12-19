package com.jywave.ui.activities;


import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.ui.DummyTabContent;
import com.jywave.ui.fragments.MainTabEpsFragment;
import com.jywave.ui.fragments.MainTabMeFragment;
import com.jywave.ui.fragments.MainTabMoreFragment;
import com.jywave.ui.fragments.MainTabPodcastersFragment;
import com.jywave.vo.Ep;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class AppMainActivity extends FragmentActivity {

	private TabHost tabHost;
	private RadioGroup tabGroup;
	private AppMain app = AppMain.getInstance();
	
	private static final String TAG_TAB_EPS = "tabEps";
	private static final String TAG_TAB_PODCASTERS = "tabPodcasters";
	private static final String TAG_TAB_ME = "tabMe";
	private static final String TAG_TAB_MORE = "tabMore";
	

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
				MainTabEpsFragment tabEpsFragment = (MainTabEpsFragment) fm.findFragmentByTag(TAG_TAB_EPS);
				MainTabPodcastersFragment tabPodcastersFragment = (MainTabPodcastersFragment) fm.findFragmentByTag(TAG_TAB_PODCASTERS);
				MainTabMeFragment tabMeFragment = (MainTabMeFragment) fm.findFragmentByTag(TAG_TAB_ME);
				MainTabMoreFragment tabMoreFragment = (MainTabMoreFragment) fm.findFragmentByTag(TAG_TAB_MORE);

				android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

				if (tabEpsFragment != null)
					ft.detach(tabEpsFragment);
				
				if (tabPodcastersFragment != null)
					ft.detach(tabPodcastersFragment);

				if (tabMeFragment != null)
					ft.detach(tabMeFragment);

				if (tabMoreFragment != null)
					ft.detach(tabMoreFragment);

				if (tabId.equalsIgnoreCase(TAG_TAB_EPS)) {

					if (tabEpsFragment == null) {
						ft.add(R.id.realtabcontent, new MainTabEpsFragment(),
								TAG_TAB_EPS);
					} else {
						ft.attach(tabEpsFragment);
					}
				} else if (tabId.equalsIgnoreCase(TAG_TAB_PODCASTERS)) {
					if (tabPodcastersFragment == null) {
						ft.add(R.id.realtabcontent, new MainTabPodcastersFragment(),
								TAG_TAB_PODCASTERS);
					} else {
						ft.attach(tabPodcastersFragment);
					}
				} else if (tabId.equalsIgnoreCase(TAG_TAB_ME)) {
					if (tabMeFragment == null) {
						ft.add(R.id.realtabcontent, new MainTabMeFragment(),
								TAG_TAB_ME);
					} else {
						ft.attach(tabMeFragment);
					}
				} else if (tabId.equalsIgnoreCase(TAG_TAB_MORE)){
					if (tabMoreFragment == null) {
						ft.add(R.id.realtabcontent, new MainTabMoreFragment(),
								TAG_TAB_MORE);
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
		TabHost.TabSpec tSpecEps = tabHost.newTabSpec(TAG_TAB_EPS);
		tSpecEps.setIndicator(TAG_TAB_EPS,
				getResources().getDrawable(R.drawable.ico_tab_eps));
		tSpecEps.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecEps);
		
		TabHost.TabSpec tSpecPodcasters = tabHost.newTabSpec(TAG_TAB_PODCASTERS);
		tSpecPodcasters.setIndicator(TAG_TAB_PODCASTERS,
				getResources().getDrawable(R.drawable.ico_tab_me));
		tSpecPodcasters.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecPodcasters);

		TabHost.TabSpec tSpecMe = tabHost.newTabSpec(TAG_TAB_ME);
		tSpecMe.setIndicator(TAG_TAB_ME,
				getResources().getDrawable(R.drawable.ico_tab_me));
		tSpecMe.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecMe);

		TabHost.TabSpec tSpecMore = tabHost.newTabSpec(TAG_TAB_MORE);
		tSpecMore.setIndicator(TAG_TAB_MORE,
				getResources().getDrawable(R.drawable.ico_tab_more));
		tSpecMore.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecMore);

		tabGroup = (RadioGroup) findViewById(R.id.main_tab_group);
		
		tabGroup.check(R.id.main_tab_eps);
		tabHost.setCurrentTabByTag(TAG_TAB_EPS);

		tabGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.main_tab_eps:
					tabHost.setCurrentTabByTag(TAG_TAB_EPS);
					break;
				case R.id.main_tab_podcasters:
					tabHost.setCurrentTabByTag(TAG_TAB_PODCASTERS);
					break;
				case R.id.main_tab_me:
					tabHost.setCurrentTabByTag(TAG_TAB_ME);
					break;
				case R.id.main_tab_more:
					tabHost.setCurrentTabByTag(TAG_TAB_MORE);
					break;
				default:
					tabHost.setCurrentTabByTag(TAG_TAB_EPS);
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.app_main, menu);
		return true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}


