package com.jywave.ui.activities;

import java.io.File;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.R.anim;
import com.jywave.R.id;
import com.jywave.R.layout;
import com.jywave.player.Player;
import com.jywave.player.PlayerService;
import com.jywave.util.StringUtil;
import com.jywave.util.imagecache.ImageFetcher;
import com.jywave.vo.Ep;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class PlayerActivity extends FragmentActivity {

	private static final String TAG = "PlayerActivity";

	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();
	private Ep ep;
	private int index;

	// UI elements
	private ImageButton btnBack;
	private ImageButton btnPlaybackFF15s;
	private ImageButton btnPlaybackFB15s;
	private ImageButton btnPlaybackPlay;
	private ImageButton btnPlaybackNext;
	private ImageButton btnPlaybackPrev;
	private ImageButton btnActionMenu;
	private ImageButton btnSleepTimer;
	private TextView txtEpTitle;
	private TextView txtEpDescription;
	private TextView txtTimeElsped;
	private TextView txtTimeRemaining;
	private ImageView imgEpCover;
	private ViewFlipper vfEpInfo;
	private LinearLayout llEpDescription;
	private SeekBar seekbarPlaybackProgress;
	private PopupWindow menuSleepTimer;
	private View viewMenuSleepTimer;

	private ImageFetcher imgFetcher;

	// player service
	private PlayerActivityReceiver playerActivityReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		// get UI elements
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnPlaybackPlay = (ImageButton) findViewById(R.id.btnPlaybackPlay);
		btnPlaybackFF15s = (ImageButton) findViewById(R.id.btnPlaybackFF15s);
		btnPlaybackFB15s = (ImageButton) findViewById(R.id.btnPlaybackFB15s);
		btnPlaybackNext = (ImageButton) findViewById(R.id.btnPlaybackNext);
		btnPlaybackPrev = (ImageButton) findViewById(R.id.btnPlaybackPrev);
		btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
		btnSleepTimer = (ImageButton) findViewById(R.id.btnSleepTimer);

		txtEpTitle = (TextView) findViewById(R.id.txtEpTitle);
		txtEpDescription = (TextView) findViewById(R.id.txtEpDescription);
		txtTimeElsped = (TextView) findViewById(R.id.txtTimeElapsed);
		txtTimeRemaining = (TextView) findViewById(R.id.txtTimeRemaining);
		vfEpInfo = (ViewFlipper) findViewById(R.id.vflipperEpInfo);
		imgEpCover = (ImageView) findViewById(R.id.imgEpCover);
		llEpDescription = (LinearLayout) findViewById(R.id.llEpDescription);
		seekbarPlaybackProgress = (SeekBar) findViewById(R.id.seekbarPlaybackProgress);

		// Get EP index
		Intent intent = this.getIntent();
		index = intent.getIntExtra("epIndex", 0);

		// Get EP Data
		ep = app.epsList.data.get(index);

		app.latestClickedEpIndex = index;

		// Load EP Cover
		imgFetcher = new ImageFetcher(this, app.screenWidth);
		imgFetcher.addImageCache(this.getSupportFragmentManager(),
				app.cacheParams);
		imgFetcher.setImageFadeIn(true);

		// setup all UI elements
		refreshUI();

		// set ep information area's layout parameters
		LayoutParams lpEpCover = imgEpCover.getLayoutParams();
		lpEpCover.width = app.screenWidth;
		lpEpCover.height = app.screenWidth;
		imgEpCover.setLayoutParams(lpEpCover);

		LayoutParams lpVfEpInfo = vfEpInfo.getLayoutParams();
		lpVfEpInfo.width = app.screenWidth;
		lpVfEpInfo.height = app.screenWidth;
		vfEpInfo.setLayoutParams(lpVfEpInfo);

		// click to back to previous page
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// click to switch pages of EP Info ViewFlipper
		imgEpCover.setOnClickListener(listenerSwitchEpInfo);
		llEpDescription.setOnClickListener(listenerSwitchEpInfo);

		// playback fast forward 15s
		btnPlaybackFF15s.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Animation aniClockwiseRotation = AnimationUtils.loadAnimation(
						v.getContext(), R.anim.clockwise_rotation);
				aniClockwiseRotation
						.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationRepeat(Animation animation) {

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								playbackFF15s();
							}
						});
				btnPlaybackFF15s.startAnimation(aniClockwiseRotation);
			}
		});

		// playback fast backward 15s
		btnPlaybackFB15s.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Animation aniCounterclockwiseRotation = AnimationUtils
						.loadAnimation(v.getContext(),
								R.anim.counterclockwise_rotation);
				aniCounterclockwiseRotation
						.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationRepeat(Animation animation) {

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								playbackFB15s();
							}
						});
				btnPlaybackFB15s.startAnimation(aniCounterclockwiseRotation);
			}
		});

		seekbarPlaybackProgress
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						seekto(seekBar.getProgress());
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						int timeElapsed = progress;
						int timeRemaining = ep.duration - timeElapsed;

						txtTimeElsped.setText(StringUtil
								.convertSecondsToTimeString(timeElapsed));
						txtTimeRemaining.setText("-"
								+ StringUtil
										.convertSecondsToTimeString(timeRemaining));
					}
				});

		if (ep.status == Ep.PLAYING && ep.id == player.playingId) {
			seekbarPlaybackProgress.setMax(ep.duration);
		} else if (ep.status == Ep.IN_LOCAL) {
			File f = new File(AppMain.mp3StorageDir + ep.getEpFilename());
			if (f.exists()) {
				txtTimeElsped.setText("00:00");
				txtTimeRemaining.setText("-" + ep.getLengthString());

				seekbarPlaybackProgress.setProgress(0);
				seekbarPlaybackProgress.setMax(ep.duration);

				ep.status = Ep.PLAYING;
				app.epsList.data.get(index).status = Ep.PLAYING;

				initPlayerService();

				play();

			} else {
				Toast.makeText(this, "您要播放的节目未下载，请重新下载", Toast.LENGTH_LONG)
						.show();
				app.epsList.data.get(index).status = Ep.IN_SERVER;
				finish();
			}
		}

		// player activity receiver: receive messages from player service
		if (playerActivityReceiver == null) {
			playerActivityReceiver = new PlayerActivityReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Player.PLAYER_ACTION_CURRENT);
			filter.addAction(Player.PLAYER_ACTION_COMPLETE);
			filter.addAction(Player.PLAYER_ACTION_SERVICE_READY);
			filter.addAction(Player.PLAYER_ACTION_SLEEP_DONE);
			registerReceiver(playerActivityReceiver, filter);
		}

		btnPlaybackPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (player.isPlaying) {
					pause();
				} else {
					resume();
				}
			}
		});

		btnPlaybackNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				next();
			}
		});

		btnPlaybackPrev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (player.timeElapsed < 5) {
					prev();
				} else {
					seekto(0);
				}
			}
		});

		btnSleepTimer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSleepTimerMenu();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(playerActivityReceiver);
	};

	private OnClickListener listenerSwitchEpInfo = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Animation aniFadeIn = AnimationUtils.loadAnimation(v.getContext(),
					R.anim.fade_in);
			Animation aniFadeOut = AnimationUtils.loadAnimation(v.getContext(),
					R.anim.fade_out);
			vfEpInfo.setInAnimation(aniFadeIn);
			vfEpInfo.setOutAnimation(aniFadeOut);
			vfEpInfo.showNext();
		}
	};

	private void initPlayerService() {
		startService(new Intent(this, PlayerService.class));
	}

	private void refreshUI() {
		txtEpTitle.setText(ep.title);
		txtTimeElsped.setText("0:00");
		txtTimeRemaining.setText("-" + ep.getLengthString());

		seekbarPlaybackProgress.setProgress(0);
		seekbarPlaybackProgress.setMax(ep.duration);
		
		if(player.isPlaying)
		{
			btnPlaybackPlay.setImageResource(R.drawable.btn_playback_pause);
		}
		else
		{
			btnPlaybackPlay.setImageResource(R.drawable.btn_playback_play);
		}

		// TODO: set rating bar progress

		txtEpDescription.setText(ep.description);

		imgEpCover.setTag(ep.coverUrl);
		imgFetcher.loadImage(ep.coverUrl, imgEpCover);
	}

	private void play() {
		btnPlaybackPlay.setImageResource(R.drawable.btn_playback_pause);

		player.play(ep);

		Intent intent = new Intent();
		intent.setAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
		intent.putExtra(Player.PLAYER_PARAMS_CMD, Player.PLAYER_CMD_PLAY);
		sendBroadcast(intent);
	}

	private void pause() {
		btnPlaybackPlay.setImageResource(R.drawable.btn_playback_play);

		player.pause();

		Intent intent = new Intent();
		intent.setAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
		intent.putExtra(Player.PLAYER_PARAMS_CMD, Player.PLAYER_CMD_PAUSE);
		sendBroadcast(intent);
	}

	private void resume() {
		btnPlaybackPlay.setImageResource(R.drawable.btn_playback_pause);

		player.isPlaying = true;

		Intent intent = new Intent();
		intent.setAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
		intent.putExtra(Player.PLAYER_PARAMS_CMD, Player.PLAYER_CMD_RESUME);
		sendBroadcast(intent);
	}

	private void playbackFF15s() {
		Intent intent = new Intent();
		intent.setAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
		intent.putExtra(Player.PLAYER_PARAMS_CMD, Player.PLAYER_CMD_FF15S);
		sendBroadcast(intent);
	}

	private void playbackFB15s() {
		Intent intent = new Intent();
		intent.setAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
		intent.putExtra(Player.PLAYER_PARAMS_CMD, Player.PLAYER_CMD_FB15S);
		sendBroadcast(intent);
	}

	private void seekto(int seconds) {
		Intent intent = new Intent();
		intent.setAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
		intent.putExtra(Player.PLAYER_PARAMS_CMD, Player.PLAYER_CMD_SEEKTO);
		intent.putExtra(Player.PLAYER_PARAMS_SEEKTO, seconds);
		sendBroadcast(intent);
	}

	private void next() {
		btnPlaybackPlay.setImageResource(R.drawable.btn_playback_pause);

		if (player.playlist.size() > 1) {
			player.next();

			ep = app.epsList.data.get(player.playingIndexOfEpList);
			index = player.playingIndexOfEpList;

			refreshUI();

			Intent intent = new Intent();
			intent.setAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
			intent.putExtra(Player.PLAYER_PARAMS_CMD, Player.PLAYER_CMD_PLAY);
			sendBroadcast(intent);
		} else {
			Toast.makeText(this, "亲，你只下载了这一期节目哦", Toast.LENGTH_LONG).show();
		}

	}

	private void prev() {
		btnPlaybackPlay.setImageResource(R.drawable.btn_playback_pause);

		if (player.playlist.size() > 1) {
			player.prev();

			ep = app.epsList.data.get(player.playingIndexOfEpList);
			index = player.playingIndexOfEpList;

			refreshUI();

			Intent intent = new Intent();
			intent.setAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
			intent.putExtra(Player.PLAYER_PARAMS_CMD, Player.PLAYER_CMD_PLAY);
			sendBroadcast(intent);
		} else {
			Toast.makeText(this, "亲，你只下载了这一期节目哦", Toast.LENGTH_LONG).show();
		}

	}

	public class PlayerActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Player.PLAYER_ACTION_SERVICE_READY)) {
				play();
			} else if (action.equals(Player.PLAYER_ACTION_CURRENT)) {
				txtTimeElsped.setText(StringUtil
						.convertSecondsToTimeString(player.timeElapsed));
				txtTimeRemaining.setText("-"
						+ StringUtil.convertSecondsToTimeString(player.duration
								- player.timeElapsed));
				seekbarPlaybackProgress.setProgress(player.timeElapsed);
			} else if (action.equals(Player.PLAYER_ACTION_COMPLETE)) {
				if(player.sleepTimerOption == Player.PLAYER_SLEEP_TIMER_STOP_WHEN_EP_ENDS)
				{
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_NONE;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock);
					pause();
					
				}
				else
				{
					if (player.playlist.size() > 1) {
						next();
					} else {
						seekto(0);
					}
				}
			} else if(action.equals(Player.PLAYER_ACTION_SLEEP_DONE))
			{
				player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_NONE;
				btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock);
				pause();
			}
		}
	}

	private void showSleepTimerMenu() {

		viewMenuSleepTimer = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.menu_sleep_timer, null);
		
		RadioButton rbSleepTimerNone = (RadioButton) viewMenuSleepTimer
				.findViewById(R.id.rbSleepTimerNone);
		RadioButton rbSleepTimer15mins = (RadioButton) viewMenuSleepTimer
				.findViewById(R.id.rbSleepTimer15mins);
		RadioButton rbSleepTimer30mins = (RadioButton) viewMenuSleepTimer
				.findViewById(R.id.rbSleepTimer30mins);
		RadioButton rbSleepTimer1hour = (RadioButton) viewMenuSleepTimer
				.findViewById(R.id.rbSleepTimer1hour);
		RadioButton rbSleepTimer2hours = (RadioButton) viewMenuSleepTimer
				.findViewById(R.id.rbSleepTimer2hours);
		RadioButton rbSleepTimerStopWhenEpEnds = (RadioButton) viewMenuSleepTimer
				.findViewById(R.id.rbSleepTimerStopWhenEpEnds);

		Button btnSleepTimerMenuOK = (Button) viewMenuSleepTimer.findViewById(R.id.btnOK);

		if (menuSleepTimer == null) {

			menuSleepTimer = new PopupWindow(this);

			menuSleepTimer.setFocusable(true);
			menuSleepTimer.setTouchable(true);
			menuSleepTimer.setOutsideTouchable(false);

			menuSleepTimer.setContentView(viewMenuSleepTimer);

			menuSleepTimer.setWidth(LayoutParams.MATCH_PARENT);
			menuSleepTimer.setHeight(LayoutParams.WRAP_CONTENT);

			menuSleepTimer.setAnimationStyle(R.style.popup_menu);

			switch (player.sleepTimerOption) {
			case Player.PLAYER_SLEEP_TIMER_NONE:
				rbSleepTimerNone.setChecked(true);
				break;
			case Player.PLAYER_SLEEP_TIMER_15MINS:
				rbSleepTimer15mins.setChecked(true);
				break;
			case Player.PLAYER_SLEEP_TIMER_30MINS:
				rbSleepTimer30mins.setChecked(true);
				break;
			case Player.PLAYER_SLEEP_TIMER_1HOUR:
				rbSleepTimer1hour.setChecked(true);
				break;
			case Player.PLAYER_SLEEP_TIMER_2HOURS:
				rbSleepTimer2hours.setChecked(true);
				break;
			case Player.PLAYER_SLEEP_TIMER_STOP_WHEN_EP_ENDS:
				rbSleepTimerStopWhenEpEnds.setChecked(true);
				break;
			}
		}

		menuSleepTimer.showAtLocation(this.findViewById(R.id.btnSleepTimer),
				Gravity.BOTTOM, 0, 0);

		menuSleepTimer.update();

		btnSleepTimerMenuOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RadioGroup rgSleepTimerOptions = (RadioGroup) viewMenuSleepTimer.findViewById(R.id.rgSleepTimerOptions);
				switch (rgSleepTimerOptions.getCheckedRadioButtonId()) {
				case R.id.rbSleepTimerNone:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_NONE;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock);
					break;
				case R.id.rbSleepTimer15mins:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_15MINS;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				case R.id.rbSleepTimer30mins:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_30MINS;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				case R.id.rbSleepTimer1hour:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_1HOUR;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				case R.id.rbSleepTimer2hours:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_2HOURS;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				case R.id.rbSleepTimerStopWhenEpEnds:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_STOP_WHEN_EP_ENDS;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				}

				Intent intent = new Intent();
				intent.setAction(Player.PLAYER_ACTION_SLEEP);
				sendBroadcast(intent);

				menuSleepTimer.dismiss();
				menuSleepTimer = null;
			}
		});
	}

}
