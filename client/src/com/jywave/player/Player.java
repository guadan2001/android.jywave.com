package com.jywave.player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

import com.jywave.AppMain;
import com.jywave.vo.Ep;

public class Player {
	public static Player singleton;

	private AppMain app = AppMain.getInstance();

	public int timeElapsed;
	public int duration;
	public int playingIndexOfPlaylist;
	public int playingIndexOfEpList;
	public int playingId;

	public String uri;
	public boolean isPlaying;

	public List<Integer> playlist; // item: Ep.id
	
	public int sleepTimerOption;

	public static final String PLAYER_ACTION_PLAYBACK_CTRL = "PLAYER_ACTION_PLAYBACK_CTRL";
	//public static final String PLAYER_ACTION_PLAYING = "PLAYER_ACTION_PLAYING";
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
	//public static final String PLAYER_PARAMS_PATH = "PLAYER_PARAMS_PATH";
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
		playlist = new ArrayList<Integer>();
		refreshPlaylist();
		sleepTimerOption = 0;
		singleton = this;
	}

	public void refreshPlaylist() {
		if (app.epsList.size() > 0) {
			for (int i = 0; i < app.epsList.size(); i++) {
				String filename = app.epsList.data.get(i).getEpFilename();
				File f = new File(AppMain.mp3StorageDir + filename);
				if (f.exists()) {
					if (app.epsList.data.get(i).status == Ep.IN_LOCAL) {
						playlist.add(app.epsList.data.get(i).id);
					} else {
						app.epsList.data.get(i).status = Ep.IN_LOCAL;
					}
				}
				
				if(isPlaying)
				{
					if(playingId == app.epsList.data.get(i).id)
					{
						app.epsList.data.get(i).status = Ep.PLAYING;
					}
				}
			}
		}

		playingIndexOfEpList = app.epsList.findIndexById(playingId);
		playingIndexOfPlaylist = playlist.indexOf(playingId);
	}

	public void play(Ep ep) {
		
		int epIndex = app.epsList.findIndexById(ep.id);
		
		if(playingId > 0)
		{
			app.epsList.data.get(playingIndexOfEpList).status = Ep.IN_LOCAL;
			app.epsList.data.get(epIndex).status = Ep.PLAYING;
		}
		
		playingId = ep.id;
		playingIndexOfPlaylist = playlist.indexOf(playingId);
		playingIndexOfEpList = epIndex;
		duration = ep.duration;
		timeElapsed = 0;

		isPlaying = true;
		uri = AppMain.mp3StorageDir + ep.getEpFilename();
	}
	
	public void pause()
	{
		isPlaying = false;
	}
	
	public void next()
	{
		if(playlist.size() > 1)
		{
			if(playingIndexOfPlaylist < playlist.size() - 1)
			{
				playingIndexOfPlaylist++;
			}
			else
			{
				playingIndexOfPlaylist = 0;
			}
			int epId = playlist.get(playingIndexOfPlaylist);
			
			play(app.epsList.getEpById(epId));
		}
	}
	
	public void prev()
	{
		if(playlist.size() > 1)
		{
			if(playingIndexOfPlaylist > 0)
			{
				playingIndexOfPlaylist--;
			}
			else
			{
				playingIndexOfPlaylist = playlist.size() - 1;
			}
			
			int epId = playlist.get(playingIndexOfPlaylist);
			play(app.epsList.getEpById(epId));
		}
	}
}
