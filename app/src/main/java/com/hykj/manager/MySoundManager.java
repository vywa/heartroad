package com.hykj.manager;

import java.io.File;
import java.io.IOException;

import com.hykj.App;
import com.hykj.Constant;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

public class MySoundManager {

	private SoundMeter mSensor;
	private int POLL_INTERVAL = 300;
	private String fileName;
	private Handler mHandler = new Handler();

	public MySoundManager() {
		mSensor = new SoundMeter();
	}

	public MySoundManager(String fileName) {
		mSensor = new SoundMeter();
		this.fileName = fileName;
	}

	public String start() {
		String soundPath;
		if (TextUtils.isEmpty(fileName)) {
			soundPath = Constant.TEMP_FILEPATH + "/temp.amr";
		} else {
			soundPath = Constant.SOUND_FILEPATH + "/" + fileName;
		}
		File file = new File(soundPath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mSensor.start(soundPath);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
		return soundPath;
	}

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};

	public void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		try {
			mSensor.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public void cancel() { mHandler.removeCallbacks(mSleepTask);
	 * mHandler.removeCallbacks(mPollTask); mSensor=null; // mSensor.stop(); }
	 */

	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			// updateDisplay(amp);
			if (listener != null) {
				listener.levelChange(amp);
			}
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);
		}
	};

	public interface SoundLevelChangeListener {
		void levelChange(double signalEMA);
	}

	private SoundLevelChangeListener listener;

	public void setOnSoundLevelChangeListener(SoundLevelChangeListener listener) {
		this.listener = listener;
	}

	MediaPlayer mMediaPlayer = null;

	public void playMusic(String path) {
		if (mMediaPlayer == null) {
			// MediaPlayer.create(this,Uri.parse(Environment.getDataDirectory()+"/"+voiceName));
			// mMediaPlayer = MediaPlayer.create(App.getContext(),
			// Uri.fromFile(new File(path)));
			mMediaPlayer = new MediaPlayer();
		}
		try {
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					/*mp.stop();
					mp.reset();
					mp.release();
					mp = null;*/
				}
			});
			mMediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mMediaPlayer.reset();
					return false;
				}
			});
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					mMediaPlayer.start();
				}
			});

			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopPlayMusic() {
		if (mMediaPlayer == null) {
			return;
		}
		mMediaPlayer.reset();
		mMediaPlayer.release();
		mMediaPlayer = null;
	}
}
