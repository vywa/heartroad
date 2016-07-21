package com.hykj.activity.subject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.Constant;
import com.hykj.R;
import com.hykj.manager.MySoundManager;
import com.hykj.manager.MySoundManager.SoundLevelChangeListener;
import com.hykj.utils.MyToast;

public class NewSubjectSoundActivity extends Activity implements OnClickListener{
	
	String soundPath;
	
	private ImageView iv_sound;
	Button bt_add;
	Button bt_confirm;
	long startTime;
	private MySoundManager mySoundManager;
	private MySoundManager mySoundPlayer = new MySoundManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsubsound);
		
		tv_level = (TextView) findViewById(R.id.tv_level);
		
		onTouchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mySoundManager == null) {
						mySoundManager = new MySoundManager("lyl.arm");
						mySoundManager.setOnSoundLevelChangeListener(levelChangeListener);
					}
					mySoundManager.start();
					bt_add.setText("....................");
					startTime = System.currentTimeMillis();
					// soundPath = Constant.SOUND_FILEPATH + "/lyl.arm";
				} else if (event.getAction() == MotionEvent.ACTION_UP && startTime != 0) {// 松开手势时执行录制完成
					if (mySoundManager != null) {
						mySoundManager.stop();
						mySoundManager = null;
					}
					if (System.currentTimeMillis() - startTime < 1000) {
						MyToast.show("录音时间太短");
					} else {
						soundPath = Constant.SOUND_FILEPATH + "/lyl.arm";
						iv_sound.setVisibility(View.VISIBLE);
						bt_add.setText("点击删除");
						bt_add.setOnTouchListener(null);
					}
					startTime = 0L;
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL && startTime != 0) {
					if (mySoundManager != null) {
						mySoundManager.stop();
						// mySoundManager.cancel();
						mySoundManager = null;
					}
					if (System.currentTimeMillis() - startTime < 1000) {
						MyToast.show("录音时间太短");
					} else {
						soundPath = Constant.SOUND_FILEPATH + "/lyl.arm";
						iv_sound.setVisibility(View.VISIBLE);
						bt_add.setText("点击删除");
						bt_add.setOnTouchListener(null);
					}
					startTime = 0L;
				}
				return true;
			}
		};
		
		bt_add = (Button) findViewById(R.id.bt_subsound_add);
		bt_add.setOnClickListener(this);
		
		soundPath = getIntent().getStringExtra("soundPath");
		iv_sound = (ImageView) findViewById(R.id.iv_newsubsound);
		if (!TextUtils.isEmpty(soundPath)) {
			iv_sound.setVisibility(View.VISIBLE);
			bt_add.setText("点击删除");
		}else{
			bt_add.setOnTouchListener(onTouchListener);
		}
		
		iv_sound.setOnClickListener(this);
		
		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_back.setOnClickListener(this);
		
		
		bt_confirm = (Button) findViewById(R.id.bt_subsound_confirm);
		bt_confirm.setOnClickListener(this);
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mySoundPlayer.stopPlayMusic();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			NewSubjectSoundActivity.this.finish();
			break;
		case R.id.bt_subsound_add:
			if (iv_sound.getVisibility() == View.VISIBLE) {
				//TODO SHANCHU
				bt_add.setOnTouchListener(onTouchListener);
				soundPath = null;
				iv_sound.setVisibility(View.GONE);
				bt_add.setText("录制声音");
			}
			break;
		case R.id.bt_subsound_confirm:
			Intent intent = new Intent();
			intent.putExtra("soundPath", soundPath);
			setResult(-900, intent);
			NewSubjectSoundActivity.this.finish();
			break;
		case R.id.iv_newsubsound:
			if (!TextUtils.isEmpty(soundPath)) {
				mySoundPlayer.playMusic(soundPath);
			}
			break;
		}
	}
	
	SoundLevelChangeListener levelChangeListener = new MySoundManager.SoundLevelChangeListener() {
		@Override
		public void levelChange(double signalEMA) {
			switch ((int) signalEMA) {
			case 0:
			case 1:
//				Log.wtf("XXXXXXXXXXXXX", "111111111");
				break;
			case 2:
			case 3:
//				Log.wtf("XXXXXXXXXXXXX", "222222222222");
				break;
			case 4:
			case 5:
//				Log.wtf("XXXXXXXXXXXXX", "33333333333");
				break;
			case 6:
			case 7:
//				Log.wtf("XXXXXXXXXXXXX", "444444444444");
				break;
			case 8:
			case 9:
//				Log.wtf("XXXXXXXXXXXXX", "5555555555555");
				break;
			case 10:
			case 11:
//				Log.wtf("XXXXXXXXXXXXX", "666666666666");
				break;
			default:
				break;
			}
		}
	};

	private OnTouchListener onTouchListener;

	private TextView tv_level;
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (mySoundManager != null) {
//			mySoundManager.cancel();
			mySoundManager.stop();
			mySoundManager = null;
			if (System.currentTimeMillis() - startTime < 1000) {
				MyToast.show("录音时间太短");
			} else {
				soundPath = Constant.SOUND_FILEPATH + "/lyl.arm";
				iv_sound.setVisibility(View.VISIBLE);
				bt_add.setText("点击删除");
				bt_add.setOnTouchListener(null);
			}
			startTime = 0L;
		}
	}
}





















