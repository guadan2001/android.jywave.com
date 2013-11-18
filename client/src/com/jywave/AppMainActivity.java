package com.jywave;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class AppMainActivity extends FragmentActivity {

	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_main);

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

		RadioGroup tabGroup = (RadioGroup) this
				.findViewById(R.id.main_tab_group);
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
