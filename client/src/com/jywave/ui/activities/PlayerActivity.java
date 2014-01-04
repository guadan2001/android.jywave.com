package com.jywave.ui.activities;

import java.io.File;

import com.jywave.AppMain;
import com.jywave.Player;
import com.jywave.R;
import com.jywave.service.PlayerService;
import com.jywave.util.StringUtil;
import com.jywave.vo.Ep;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutFeedParam;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class PlayerActivity extends FragmentActivity implements
		IWeiboHandler.Response {

	@SuppressWarnings("unused")
	private static final String TAG = "PlayerActivity";

	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();
	private Ep ep;
	private int indexInDownloadedList;
	private int indexInEpsList;
	private Context thisContext;

	// UI elements
	private ImageButton btnBack;
	private ImageButton btnPlaybackFF15s;
	private ImageButton btnPlaybackFB15s;
	private ImageButton btnPlaybackPlay;
	private ImageButton btnPlaybackNext;
	private ImageButton btnPlaybackPrev;
	private ImageButton btnShare;
	private ImageButton btnSleepTimer;
	private TextView txtEpTitle;
	private TextView txtEpDescription;
	private TextView txtTimeElsped;
	private TextView txtTimeRemaining;
	private TextView txtPlaylistPosition;
	private ImageView imgEpCover;
	private ViewFlipper vfEpInfo;
	private LinearLayout llEpDescription;
	private SeekBar seekbarPlaybackProgress;
	private PopupWindow menuSleepTimer;
	private PopupWindow menuShare;
	private View viewMenuSleepTimer;
	private View viewMenuShare;
	private ProgressDialog progressDialog;

	// player service
	private PlayerActivityReceiver playerActivityReceiver;

	// Social Networks
	private IWeiboShareAPI apiSinaWeibo = null; // Sina Weibo
	private IWXAPI apiWeixin = null; // weixin
	private RennClient apiRenren = null; // renren.com

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		thisContext = this;

		// get UI elements
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnPlaybackPlay = (ImageButton) findViewById(R.id.btnPlaybackPlay);
		btnPlaybackFF15s = (ImageButton) findViewById(R.id.btnPlaybackFF15s);
		btnPlaybackFB15s = (ImageButton) findViewById(R.id.btnPlaybackFB15s);
		btnPlaybackNext = (ImageButton) findViewById(R.id.btnPlaybackNext);
		btnPlaybackPrev = (ImageButton) findViewById(R.id.btnPlaybackPrev);
		btnShare = (ImageButton) findViewById(R.id.btnShare);
		btnSleepTimer = (ImageButton) findViewById(R.id.btnSleepTimer);

		txtEpTitle = (TextView) findViewById(R.id.txtEpTitle);
		txtEpDescription = (TextView) findViewById(R.id.txtEpDescription);
		txtTimeElsped = (TextView) findViewById(R.id.txtTimeElapsed);
		txtTimeRemaining = (TextView) findViewById(R.id.txtTimeRemaining);
		txtPlaylistPosition = (TextView) findViewById(R.id.txtPlaylistPosition);
		vfEpInfo = (ViewFlipper) findViewById(R.id.vflipperEpInfo);
		imgEpCover = (ImageView) findViewById(R.id.imgEpCover);
		llEpDescription = (LinearLayout) findViewById(R.id.llEpDescription);
		seekbarPlaybackProgress = (SeekBar) findViewById(R.id.seekbarPlaybackProgress);

		// Get EP index
		Intent intent = this.getIntent();
		indexInDownloadedList = intent.getIntExtra("epIndex", -1);

		// Get EP Data
		ep = app.downloadedEpsList.get(indexInDownloadedList);
		indexInEpsList = app.epsList.findIndexById(ep.id);

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

		// --------------------------------------------------------------------------------------------------------
		// Sina Weibo
		// --------------------------------------------------------------------------------------------------------

		apiSinaWeibo = WeiboShareSDK.createWeiboAPI(this,
				AppMain.APP_KEY_SINA_WEIBO);

		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!apiSinaWeibo.isWeiboAppInstalled()) {
			apiSinaWeibo
					.registerWeiboDownloadListener(new IWeiboDownloadListener() {
						@Override
						public void onCancel() {
							Toast.makeText(thisContext, "新浪微博客户端未安装",
									Toast.LENGTH_SHORT).show();
						}
					});
		}

		if (savedInstanceState != null) {
			apiSinaWeibo.handleWeiboResponse(getIntent(), this);
		}

		// --------------------------------------------------------------------------------------------------------
		// Tencent Weixin
		// --------------------------------------------------------------------------------------------------------

		apiWeixin = WXAPIFactory.createWXAPI(thisContext,
				AppMain.APP_ID_WEIXIN, true);

		apiWeixin.registerApp(AppMain.APP_ID_WEIXIN);

		// --------------------------------------------------------------------------------------------------------
		// renren.com
		// --------------------------------------------------------------------------------------------------------
		apiRenren = RennClient.getInstance(this); // renren.com
		apiRenren.init(AppMain.APP_ID_RENREN, AppMain.APP_KEY_RENREN,
				AppMain.SECRET_KEY_RENREN);
		apiRenren
				.setScope("read_user_status publish_share publish_feed");
		apiRenren.setTokenType("bearer");

		// --------------------------------------------------------------------------------------------------------
		// UI Elements Handlers
		// --------------------------------------------------------------------------------------------------------

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

				initPlayerService();

				play();

			} else {
				Toast.makeText(this, "您要播放的节目未下载，请重新下载", Toast.LENGTH_LONG)
						.show();
				app.downloadedEpsList.deleteByIndex(indexInDownloadedList);

				if (indexInEpsList >= 0) {
					app.epsList.get(indexInEpsList).status = Ep.IN_SERVER;
				}
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

		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showShareMenu();
			}
		});
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(playerActivityReceiver);
	};

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	};

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
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

		if (player.isPlaying) {
			btnPlaybackPlay.setImageResource(R.drawable.btn_playback_pause);
		} else {
			btnPlaybackPlay.setImageResource(R.drawable.btn_playback_play);
		}

		// TODO: set rating bar progress

		txtPlaylistPosition.setText(String.valueOf(indexInDownloadedList + 1) + '/' + String.valueOf(app.downloadedEpsList.size()));
		
		txtEpDescription.setText(ep.description);

		imgEpCover.setTag(ep.coverUrl);
		app.fb.display(imgEpCover, AppMain.apiLocation + ep.coverUrl);
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

		if (app.downloadedEpsList.size() > 1) {
			player.next();

			ep = app.downloadedEpsList.get(player.playingIndex);
			indexInDownloadedList = player.playingIndex;
			indexInEpsList = player.playingIndexOfEpsList;

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

		if (app.downloadedEpsList.size() > 1) {
			player.prev();

			ep = app.downloadedEpsList.get(player.playingIndex);
			indexInDownloadedList = player.playingIndex;
			indexInEpsList = player.playingIndexOfEpsList;

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
				if (player.sleepTimerOption == Player.PLAYER_SLEEP_TIMER_STOP_WHEN_EP_ENDS) {
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_NONE;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock);
					pause();

				} else {
					if (app.downloadedEpsList.size() > 1) {
						next();
					} else {
						seekto(0);
					}
				}
			} else if (action.equals(Player.PLAYER_ACTION_SLEEP_DONE)) {
				player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_NONE;
				btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock);
				pause();
			}
		}
	}

	private void showShareMenu() {

		viewMenuShare = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.menu_ep_share, null);

		ImageButton btnSinaWeibo = (ImageButton) viewMenuShare
				.findViewById(R.id.btnSinaWeibo);
		ImageButton btnWeixin = (ImageButton) viewMenuShare
				.findViewById(R.id.btnWeixin);
		ImageButton btnWeixinMoment = (ImageButton) viewMenuShare
				.findViewById(R.id.btnWeixinMoment);
		ImageButton btnRenren = (ImageButton) viewMenuShare
				.findViewById(R.id.btnRenren);
		Button btnCancel = (Button) viewMenuShare.findViewById(R.id.btnCancel);

		if (menuShare == null) {

			menuShare = new PopupWindow(this);

			menuShare.setFocusable(true);
			menuShare.setTouchable(true);
			menuShare.setOutsideTouchable(false);

			menuShare.setContentView(viewMenuShare);

			menuShare.setWidth(LayoutParams.MATCH_PARENT);
			menuShare.setHeight(LayoutParams.WRAP_CONTENT);

			menuShare.setAnimationStyle(R.style.popup_menu);
		}

		menuShare.showAtLocation(this.findViewById(R.id.btnShare),
				Gravity.BOTTOM, 0, 0);

		menuShare.update();

		btnSinaWeibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareToSinaWeibo();
			}
		});

		btnWeixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareToWeixin();
			}
		});

		btnWeixinMoment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareToWeixinMoment();
			}
		});

		btnRenren.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (apiRenren.isLogin()) {
					shareToRenren();
				} else {
					apiRenren.login(PlayerActivity.this);
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menuShare.dismiss();
			}
		});

		apiRenren.setLoginListener(new LoginListener() {

			@Override
			public void onLoginSuccess() {
				shareToRenren();
			}

			@Override
			public void onLoginCanceled() {
				Toast.makeText(thisContext, "登录失败", Toast.LENGTH_LONG).show();

			}
		});
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

		Button btnSleepTimerMenuOK = (Button) viewMenuSleepTimer
				.findViewById(R.id.btnOK);

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
				RadioGroup rgSleepTimerOptions = (RadioGroup) viewMenuSleepTimer
						.findViewById(R.id.rgSleepTimerOptions);
				switch (rgSleepTimerOptions.getCheckedRadioButtonId()) {
				case R.id.rbSleepTimerNone:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_NONE;
					btnSleepTimer.setImageResource(R.drawable.btn_alarm_clock);
					break;
				case R.id.rbSleepTimer15mins:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_15MINS;
					btnSleepTimer
							.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				case R.id.rbSleepTimer30mins:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_30MINS;
					btnSleepTimer
							.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				case R.id.rbSleepTimer1hour:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_1HOUR;
					btnSleepTimer
							.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				case R.id.rbSleepTimer2hours:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_2HOURS;
					btnSleepTimer
							.setImageResource(R.drawable.btn_alarm_clock_activated);
					break;
				case R.id.rbSleepTimerStopWhenEpEnds:
					player.sleepTimerOption = Player.PLAYER_SLEEP_TIMER_STOP_WHEN_EP_ENDS;
					btnSleepTimer
							.setImageResource(R.drawable.btn_alarm_clock_activated);
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

	// --------------------------------------------------------------------------------------------------------
	// Tencent Wechat
	// --------------------------------------------------------------------------------------------------------

	private void shareToWeixin() {
		if (apiWeixin.isWXAppInstalled()) {

			// WXTextObject textObj = new WXTextObject();
			// textObj.text = "测试";
			//
			// // 用WXTextObject对象初始化一个WXMediaMessage对象
			// WXMediaMessage msg = new WXMediaMessage();
			// msg.mediaObject = textObj;
			// // 发送文本类型的消息时，title字段不起作用
			// // msg.title = "Will be ignored";
			// msg.description = "测试一下";
			//
			// // 构造一个Req
			// SendMessageToWX.Req req = new SendMessageToWX.Req();
			// req.transaction = String.valueOf(System.currentTimeMillis());
			// // transaction字段用于唯一标识一个请求
			// req.message = msg;
			// req.scene = SendMessageToWX.Req.WXSceneSession;
			//
			// // 调用api接口发送数据到微信
			// apiWeixin.sendReq(req);
			WXMusicObject music = new WXMusicObject();
			music.musicUrl = ep.url;

			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = music;
			msg.title = ep.title;
			msg.description = ep.description;

			msg.setThumbImage(app.fb.getBitmapFromCache(AppMain.apiLocation
					+ ep.coverThumbnailUrl));

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			apiWeixin.sendReq(req);

			menuShare.dismiss();

			finish();
		} else {
			Toast.makeText(thisContext,
					getResources().getString(R.string.weixin_is_not_installed),
					Toast.LENGTH_LONG).show();
		}
	}

	private void shareToWeixinMoment() {
		if (apiWeixin.isWXAppInstalled()) {

			WXMusicObject music = new WXMusicObject();
			music.musicUrl = ep.url;

			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = music;
			msg.title = ep.title;
			msg.description = ep.description;

			msg.setThumbImage(app.fb.getBitmapFromCache(AppMain.apiLocation
					+ ep.coverThumbnailUrl));

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			apiWeixin.sendReq(req);

			menuShare.dismiss();

			finish();
		} else {
			Toast.makeText(thisContext,
					getResources().getString(R.string.weixin_is_not_installed),
					Toast.LENGTH_LONG).show();
		}
	}

	// --------------------------------------------------------------------------------------------------------
	// Renren
	// --------------------------------------------------------------------------------------------------------

	private void shareToRenren() {
		PutFeedParam param = new PutFeedParam();
		param.setTitle(ep.title);
		param.setMessage("我正在用酱油微播Android客户端听《" + ep.title + "》这期节目，你也来听听吧~~");
		param.setDescription("我正在用酱油微播Android客户端听《" + ep.title + "》这期节目，你也来听听吧~~");
		param.setActionTargetUrl(ep.url);
		param.setImageUrl(AppMain.apiLocation + ep.coverUrl);
		param.setTargetUrl(ep.url);

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(PlayerActivity.this);
			progressDialog.setCancelable(true);
			progressDialog.setTitle("请等待");
			progressDialog.setIcon(android.R.drawable.ic_dialog_info);
			progressDialog.setMessage("正在分享");
			progressDialog.show();
		}
		try {
			apiRenren.getRennService().sendAsynRequest(param, new CallBack() {

				@Override
				public void onSuccess(RennResponse response) {
					Toast.makeText(PlayerActivity.this, "发布成功",
							Toast.LENGTH_SHORT).show();
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
						menuShare.dismiss();
					}
				}

				@Override
				public void onFailed(String errorCode, String errorMessage) {
					Toast.makeText(PlayerActivity.this, "发布失败" + errorCode + " " + errorMessage,
							Toast.LENGTH_SHORT).show();
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
						menuShare.dismiss();
					}
				}
			});
		} catch (RennException e) {
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------------------------------
	// Sina Weibo
	// --------------------------------------------------------------------------------------------------------

	private void shareToSinaWeibo() {

		try {
			if (apiSinaWeibo.checkEnvironment(true)) {

				apiSinaWeibo.registerApp();
				WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

				TextObject text = new TextObject();
				text.text = "我正在用酱油微播Android客户端听《" + ep.title
						+ "》这期节目，你也来听听吧~~" + ep.url;

				ImageObject image = new ImageObject();
				image.setImageObject(app.fb
						.getBitmapFromCache(AppMain.apiLocation + ep.coverUrl));

				weiboMessage.textObject = text;
				weiboMessage.imageObject = image;

				SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
				request.transaction = String
						.valueOf(System.currentTimeMillis());
				request.multiMessage = weiboMessage;

				apiSinaWeibo.sendRequest(request);

				menuShare.dismiss();
			}

		} catch (WeiboShareException e) {
			e.printStackTrace();
			Toast.makeText(PlayerActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
			menuShare.dismiss();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");
		super.onNewIntent(intent);

		apiSinaWeibo.handleWeiboResponse(intent, this);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		Log.d(TAG, "onResponse");
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, "分享取消", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, "分享失败" + "Error Message: " + baseResp.errMsg,
					Toast.LENGTH_LONG).show();
			break;
		}
	}
}
