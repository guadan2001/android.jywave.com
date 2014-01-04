package com.jywave;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jywave.vo.Ep;

public class Player {
	public static Player singleton;

	private AppMain app = AppMain.getInstance();

	public int timeElapsed;
	public int duration;

	// Index of playing Ep in app.downloadedEpsList
	public int playingIndex;
	// Index of playing Ep in app.epsList, if the playing Ep is not exist in
	// app.epsList, the playingIndexOfEpsList = -1
	public int playingIndexOfEpsList;
	// Id of playing Ep
	public int playingId;

	public String uri;
	public boolean isPlaying;

	public int sleepTimerOption;

	public static final String PLAYER_ACTION_PLAYBACK_CTRL = "PLAYER_ACTION_PLAYBACK_CTRL";
	public static final String PLAYER_ACTION_COMPLETE = "PLAYER_ACTION_COMPLETE";
	public static final String PLAYER_ACTION_CURRENT = "PLAYER_ACTION_CURRENT";
	public static final String PLAYER_ACTION_SERVICE_READY = "PLAYER_ACTION_SERVICE_READY";
	public static final String PLAYER_ACTION_SLEEP = "PLAYER_ACTION_SLEEP";
	public static final String PLAYER_ACTION_SLEEP_DONE = "PLAYER_ACTION_SLEEP_DONE";

	public static final int PLAYER_CMD_PLAY = 0;
	public static final int PLAYER_CMD_PAUSE = 1;
	public static final int PLAYER_CMD_RESUME = 2;
	public static final int PLAYER_CMD_NEXT = 3;
	public static final int PLAYER_CMD_PREV = 4;
	public static final int PLAYER_CMD_FF15S = 5;
	public static final int PLAYER_CMD_FB15S = 6;
	public static final int PLAYER_CMD_SEEKTO = 7;

	public static final String PLAYER_PARAMS_CMD = "PLAYER_PARAMS_CMD";
	public static final String PLAYER_PARAMS_SEEKTO = "PLAYER_PARAMS_SEEKTO";
	public static final String PLAYER_PARAMS_DURATION = "PLAYER_PARAMS_DURATION";
	public static final String PLAYER_PARAMS_CURRENT_POSITION = "PLAYER_PARAMS_CURRENT_POSITION";

	public static final int PLAYER_SLEEP_TIMER_NONE = 0;
	public static final int PLAYER_SLEEP_TIMER_15MINS = 1;
	public static final int PLAYER_SLEEP_TIMER_30MINS = 2;
	public static final int PLAYER_SLEEP_TIMER_1HOUR = 3;
	public static final int PLAYER_SLEEP_TIMER_2HOURS = 4;
	public static final int PLAYER_SLEEP_TIMER_STOP_WHEN_EP_ENDS = 5;

	public static Player getInstance() {
		if (singleton != null) {
			return singleton;
		} else {
			return new Player();
		}
	}

	private Player() {
		sleepTimerOption = 0;
		singleton = this;
	}

	public void play(Ep ep) {

		int indexInDownloadedList = app.downloadedEpsList.findIndexById(ep.id);
		int indexInEpsList = app.epsList.findIndexById(ep.id);

		if (playingId > 0) {
			app.downloadedEpsList.get(playingIndex).status = Ep.IN_LOCAL;
			app.downloadedEpsList.get(indexInDownloadedList).status = Ep.PLAYING;
			
			if(indexInEpsList >= 0)
			{
				app.epsList.get(indexInEpsList).status = Ep.PLAYING;
			}
			
			if(playingIndexOfEpsList >= 0)
			{
				app.epsList.get(playingIndexOfEpsList).status = Ep.IN_LOCAL;
			}
		}

		playingId = ep.id;
		playingIndex = indexInDownloadedList;
		playingIndexOfEpsList = indexInEpsList;
		duration = ep.duration;
		timeElapsed = 0;

		isPlaying = true;
		uri = AppMain.mp3StorageDir + ep.getEpFilename();
	}

	public void pause() {
		isPlaying = false;
	}

	public void next() {
		if (app.downloadedEpsList.size() > 1) {
			if (playingIndex < app.downloadedEpsList.size() - 1) {
				playingIndex++;
			} else {
				playingIndex = 0;
			}
			play(app.downloadedEpsList.get(playingIndex));
		}
	}

	public void prev() {
		if (app.downloadedEpsList.size() > 1) {
			if (playingIndex > 0) {
				playingIndex--;
			} else {
				playingIndex = app.downloadedEpsList.size() - 1;
			}
			
			play(app.downloadedEpsList.get(playingIndex));
		}
	}
}
