package com.jywave.player;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class PlayerService extends Service {

	private static final String TAG = "PlayerService";

	private Player player;
	private MediaPlayer mediaPlayer;

	private Timer sleepTimer = new Timer(true);
	private SleepTimerTask sleepTimerTask;

	private int msg;
	private PlayerServiceReceiver playerServiceReceiver;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (mediaPlayer != null) {
					player.timeElapsed = mediaPlayer.getCurrentPosition() / 1000;
					Intent intent = new Intent();
					intent.setAction(Player.PLAYER_ACTION_CURRENT);
					sendBroadcast(intent);
					handler.sendEmptyMessageDelayed(1, 1000);
				}
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer = new MediaPlayer();
		player = Player.getInstance();

		playerServiceReceiver = new PlayerServiceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Player.PLAYER_ACTION_PLAYBACK_CTRL);
		filter.addAction(Player.PLAYER_ACTION_SLEEP);
		registerReceiver(playerServiceReceiver, filter);

		Intent intent = new Intent();
		intent.setAction(Player.PLAYER_ACTION_SERVICE_READY);
		sendBroadcast(intent);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				Intent intent = new Intent();
				intent.setAction(Player.PLAYER_ACTION_COMPLETE);
				sendBroadcast(intent);
			}
		});
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void play() {
		try {

			mediaPlayer.reset();
			mediaPlayer.setDataSource(player.uri);
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new PreparedListener(0));
			handler.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}

	private void resume() {
		mediaPlayer.start();
	}

	private void ff15s() {
		if (mediaPlayer.getCurrentPosition() + 15000 < mediaPlayer
				.getDuration()) {
			mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 15000);
		} else {
			mediaPlayer.seekTo(mediaPlayer.getDuration() - 5000);
		}
	}

	private void fb15s() {
		if (mediaPlayer.getCurrentPosition() - 15000 > 0) {
			mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 15000);
		} else {
			mediaPlayer.seekTo(0);
		}

	}

	private void seekto(int seconds) {
		mediaPlayer.seekTo(seconds * 1000);
	}

	private void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			try {
				mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	private final class PreparedListener implements OnPreparedListener {

		public PreparedListener(int currentTime) {
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			mediaPlayer.start();
		}
	}

	public class PlayerServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(Player.PLAYER_ACTION_PLAYBACK_CTRL)) {
				int cmd = intent.getIntExtra(Player.PLAYER_PARAMS_CMD, -1);

				switch (cmd) {
				case Player.PLAYER_CMD_PLAY:
					play();
					break;
				case Player.PLAYER_CMD_PAUSE:
					pause();
					break;
				case Player.PLAYER_CMD_RESUME:
					resume();
					break;
				case Player.PLAYER_CMD_FF15S:
					ff15s();
					break;
				case Player.PLAYER_CMD_FB15S:
					fb15s();
					break;
				case Player.PLAYER_CMD_SEEKTO:
					int seconds = intent.getIntExtra(
							Player.PLAYER_PARAMS_SEEKTO, 0);
					seekto(seconds);
					break;
				}
			} else if (intent.getAction().equals(Player.PLAYER_ACTION_SLEEP)) {
				
				if(sleepTimerTask != null)
				{
					sleepTimerTask.cancel();
					
				}
				switch (player.sleepTimerOption) {
				case Player.PLAYER_SLEEP_TIMER_NONE:
					Log.d(TAG, "sleep timer is canceled.");
					break;
				case Player.PLAYER_SLEEP_TIMER_15MINS:
					//sleepTimer = new Timer(true);
					sleepTimerTask = new SleepTimerTask();
					sleepTimer.schedule(sleepTimerTask, 900000);
					Log.d(TAG, "sleep timer is set to 15 mins.");
					break;
				case Player.PLAYER_SLEEP_TIMER_30MINS:
					sleepTimerTask = new SleepTimerTask();
					//sleepTimer = new Timer(true);
					sleepTimer.schedule(sleepTimerTask, 1800000);
					Log.d(TAG, "sleep timer is set to 30 mins.");
					break;
				case Player.PLAYER_SLEEP_TIMER_1HOUR:
					sleepTimerTask = new SleepTimerTask();
					//sleepTimer = new Timer(true);
					sleepTimer.schedule(sleepTimerTask, 3600000);
					Log.d(TAG, "sleep timer is set to 60 mins.");
					break;
				case Player.PLAYER_SLEEP_TIMER_2HOURS:
					//sleepTimer = new Timer(true);
					sleepTimerTask = new SleepTimerTask();
					sleepTimer.schedule(sleepTimerTask, 7200000);
					Log.d(TAG, "sleep timer is set to 120 mins.");
					break;
				case Player.PLAYER_SLEEP_TIMER_STOP_WHEN_EP_ENDS:
					Log.d(TAG, "sleep timer is set to the mode of stoping when this ep ends.");
					break;
				}

			}

		}

	}

	private class SleepTimerTask extends TimerTask{
		public void run() {
			Message message = new Message();
			message.what = 1;
			sleepTimerHandler.sendMessage(message);
		}
	};

	private final Handler sleepTimerHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Intent intent = new Intent();
				intent.setAction(Player.PLAYER_ACTION_SLEEP_DONE);
				sendBroadcast(intent);
				Log.d(TAG, "The player is paused by the sleep timer.");
				break;
			}
			super.handleMessage(msg);
		}
	};
}
