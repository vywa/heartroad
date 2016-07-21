package com.hykj.manager;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.VideoSource;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

import com.hykj.Constant;
import com.hykj.R;

public class RecordVideoManager extends Activity implements OnErrorListener,OnClickListener{

	int position;
	private Button bt_record;
	private Button bt_confirm;

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private com.gc.materialdesign.views.ProgressBarDeterminate mProgressBar;

	private MediaRecorder mMediaRecorder;
	private Camera mCamera;
	private Timer mTimer;// 计时器

	private int mRecordMaxTime = 79;// 一次拍摄最长时间
	private int mTimeCount;// 时间计数
	private String videoPath;
	
	private MediaPlayer player = new MediaPlayer();
	private OnTouchListener touchListener;
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mProgressBar.setProgress(mTimeCount);// 设置进度条
				break;
			case 1:
				mProgressBar.setProgress(mRecordMaxTime);
				break;
			case 2:
				play();
				break;
			case 3:
				bt_record.setOnTouchListener(null);
				bt_record.setOnClickListener(RecordVideoManager.this);
				bt_record.setText("播放中......");
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moive_recorder_view);

		videoPath = getIntent().getStringExtra("videoPath");

		initViews();

		registAndSettings();
	}

	private void initViews() {
		bt_record = (Button) findViewById(R.id.bt_record);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		mSurfaceView.setOnClickListener(this);
		mProgressBar = (com.gc.materialdesign.views.ProgressBarDeterminate) findViewById(R.id.progressBar);
		
		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_back.setOnClickListener(this);
		
		bt_confirm = (Button) findViewById(R.id.bt_subvideo_confirm);
		bt_confirm.setOnClickListener(this);
	}

	void registAndSettings() {
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				bt_record.setText("点击删除");
				bt_record.setOnClickListener(RecordVideoManager.this);
				player.setDisplay(null);
			}
		});
		mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				freeCameraResource();
			}
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				initCamera();
			}
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
		});
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					record();
					bt_record.setText("松开完成录制");
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					if (mMediaRecorder != null) {
						stop();
						bt_record.setText("点击删除");
						bt_record.setOnTouchListener(null);
						bt_record.setOnClickListener(RecordVideoManager.this);
					}
					break;
				}
				return true;
			}
		};
		
		if (TextUtils.isEmpty(videoPath)) {
			bt_record.setOnTouchListener(touchListener);
		}else{
			bt_record.setOnClickListener(this);
			bt_record.setText("点击删除");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			RecordVideoManager.this.finish();
			break;
		case R.id.bt_subvideo_confirm:
			Intent intent = new Intent();
			intent.putExtra("videoPath", videoPath);
			setResult(-22, intent);
			RecordVideoManager.this.finish();
			break;
		case R.id.surfaceview:
			if (videoPath != null && !player.isPlaying()) {
				play();
			}
			break;
		case R.id.bt_record:
			if (!player.isPlaying()) {
				videoPath = null;
				bt_record.setText("按住拍摄");
				bt_record.setOnClickListener(null);
				bt_record.setOnTouchListener(touchListener);
				initCamera();
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		player.release();
		freeCameraResource();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (player.isPlaying()) {
			position = player.getCurrentPosition();
			player.stop();
		}
		if (mMediaRecorder != null) {
			stop();
			bt_record.setText("点击删除");
		}
	}

	private void play() {
		try {
			player.reset();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(videoPath);
			player.setDisplay(mSurfaceHolder);
			player.prepare();
			player.start();
			bt_record.setOnClickListener(null);
			bt_record.setText("播放中......");
		} catch (Exception e) {
			
		}
	}

	private void initCamera() {
		if (mCamera != null) {
			freeCameraResource();
		}
		try {
			mCamera = Camera.open();
		} catch (Exception e) {
			e.printStackTrace();
			freeCameraResource();
		}
		if (mCamera == null)
			return;

		setCameraParams();

		mCamera.setDisplayOrientation(90);
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mCamera.unlock();
	}

	private void setCameraParams() {
		if (mCamera != null) {
			Parameters params = mCamera.getParameters();
			List<Size> supportedPreviewSizes = params.getSupportedPreviewSizes();
			Size size = supportedPreviewSizes.get(supportedPreviewSizes.size() / 2);
			double d = size.width * 1.0 / size.height;

			android.view.ViewGroup.LayoutParams p = mSurfaceView.getLayoutParams();
			p.width = (int) (p.height / d);
			mSurfaceView.setLayoutParams(p);

			params.setPreviewSize(size.width, size.height);
			params.set("jpeg-quality", 100);

			params.set("orientation", "portrait");

			params.setRotation(90);

			mCamera.setParameters(params);
		}
	}

	public void record() {
		try {
			initRecord();
			mTimeCount = 0;// 时间计数器重新赋值
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					mTimeCount++;
					handler.sendEmptyMessage(0);
					if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
						stop();
						handler.sendEmptyMessage(3);
					}
				}
			}, 0, 125);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initRecord() throws IOException {
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.reset();
		if (mCamera != null) {
			mMediaRecorder.setCamera(mCamera);
		}

		mMediaRecorder.setOnErrorListener(this);
		mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		mMediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
		mMediaRecorder.setAudioSource(AudioSource.MIC);// 音频源

		mMediaRecorder.setOrientationHint(90);
		mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

		mMediaRecorder.setOutputFile(Constant.TEMP_FILEPATH + "/temp.mp4");

		mMediaRecorder.prepare();
		try {
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止拍摄
	 * 
	 */
	public void stop() {
		videoPath = Constant.TEMP_FILEPATH + "/temp.mp4";
		stopRecord();
		releaseRecord();
		freeCameraResource();
		handler.sendEmptyMessage(2);
	}

	private void freeCameraResource() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.lock();
			mCamera.release();
			mCamera = null;
		}
	}
	
	/**
	 * 停止录制
	 * 
	 */
	public void stopRecord() {
		handler.sendEmptyMessage(1);
		if (mTimer != null)
			mTimer.cancel();
		if (mMediaRecorder != null) {
			// 设置后不会崩
			mMediaRecorder.setOnErrorListener(null);
			mMediaRecorder.setPreviewDisplay(null);
			try {
				mMediaRecorder.stop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void releaseRecord() {
		if (mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(null);
			try {
				mMediaRecorder.reset();
				mMediaRecorder.release();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mMediaRecorder = null;
	}

	public interface OnRecordFinishListener {
		void onRecordFinish();
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		try {
			if (mr != null)
				mr.reset();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
