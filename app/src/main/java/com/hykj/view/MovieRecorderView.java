package com.hykj.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.VideoSource;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.hykj.App;
import com.hykj.R;
import com.hykj.utils.ScreenUtils;

public class MovieRecorderView extends LinearLayout implements OnErrorListener {

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private com.gc.materialdesign.views.ProgressBarDeterminate mProgressBar;

	private MediaRecorder mMediaRecorder;
	private Camera mCamera;
	private Timer mTimer;// 计时器
	private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口

	private int mWidth;// 视频分辨率宽度
	private int mHeight;// 视频分辨率高度
	private boolean isOpenCamera;// 是否一开始就打开摄像头
	private int mRecordMaxTime;// 一次拍摄最长时间
	private int mTimeCount;// 时间计数
	private File mVecordFile = null;// 文件

	public MovieRecorderView(Context context) {
		this(context, null);
	}

	public MovieRecorderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mWidth = 320;
		mHeight = 240;
		isOpenCamera = true;
		mRecordMaxTime = 79;

		LayoutInflater.from(context).inflate(R.layout.view_recorderview, this);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		android.view.ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
		params.height = (int) (ScreenUtils.getScreenWidth(App.getContext())*(1.78)); //TODO
		params.width = ScreenUtils.getScreenWidth(getContext());
		mSurfaceView.setLayoutParams(params);
		
		mProgressBar = (com.gc.materialdesign.views.ProgressBarDeterminate) findViewById(R.id.progressBar);
		mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(new CustomCallBack());
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	
	public SurfaceView getSurfaceView(){
		return mSurfaceView;
	}

	private class CustomCallBack implements Callback {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (!isOpenCamera)
				return;
			try {
				initCamera();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (!isOpenCamera)
				return;
			freeCameraResource();
		}
	}

	private void initCamera() throws IOException {
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
		mCamera.setPreviewDisplay(mSurfaceHolder);
		mCamera.startPreview();
		mCamera.unlock();
	}

	private void setCameraParams() {
		if (mCamera != null) {
			
			Parameters params = mCamera.getParameters();
			
			params.set("jpeg-quality",100);
			
			params.set("orientation", "portrait");
			
			params.setRotation(90);
			
			mCamera.setParameters(params);
		}
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

	private void createRecordDir() {
		// File sampleDir = new File(Environment.getExternalStorageDirectory() +
		// File.separator + "im/video/");
		File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "RecordVideo/");
		// File sampleDir = new File("/video/");
		if (!sampleDir.exists()) {
			sampleDir.mkdirs();
		}
		File vecordDir = sampleDir;
		// 创建文件
		try {
			mVecordFile = File.createTempFile("recording", ".mp4", vecordDir);// mp4格式
			// LogUtils.i(mVecordFile.getAbsolutePath());
			Log.d("Path:", mVecordFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initRecord() throws IOException {
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.reset();
		if (mCamera != null)
			mMediaRecorder.setCamera(mCamera);
		
		mMediaRecorder.setOnErrorListener(this);
		mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		mMediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
		mMediaRecorder.setAudioSource(AudioSource.MIC);// 音频源
		
		/*mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);// 视频输出格式 --
		mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB);// 音频格式
		mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率：--
		mMediaRecorder.setVideoFrameRate(30);// 感觉没什么用--
		mMediaRecorder.setVideoEncodingBitRate(5 * 1920 * 1080);// 设置帧频率，然后就清晰了--
		mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
		mMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);// 视频录制格式--
		// mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);
*/
		mMediaRecorder.setOrientationHint(90);
		mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
		
		mMediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());
		
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
	 * 开始录制视频
	 * 
	 * // * @param fileName // * 视频储存位置
	 * 
	 * @param onRecordFinishListener
	 *            达到指定时间之后回调接口
	 */
	public void record(final OnRecordFinishListener onRecordFinishListener) {
		this.mOnRecordFinishListener = onRecordFinishListener;
		createRecordDir();
		try {
			if (!isOpenCamera)// 如果未打开摄像头，则打开
				initCamera();
			initRecord();
			mTimeCount = 0;// 时间计数器重新赋值
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mTimeCount++;
					//mProgressBar.setProgress(mTimeCount);// 设置进度条
					handler.sendEmptyMessage(0);
					if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
						stop();
					}
				}
			}, 0, 125);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mProgressBar.setProgress(mTimeCount);// 设置进度条
				break;
			case 1:
				mProgressBar.setProgress(mRecordMaxTime);
				break;
			case 2:
				if (mOnRecordFinishListener != null){
					mOnRecordFinishListener.onRecordFinish();
				}
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 停止拍摄
	 * 
	 */
	public void stop() {
		stopRecord();
		releaseRecord();
		freeCameraResource();
		handler.sendEmptyMessage(2);
		
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

	/**
	 * 释放资源
	 * 
	 */
	private void releaseRecord() {
		if (mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(null);
			try {
				mMediaRecorder.release();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mMediaRecorder = null;
	}

	public int getTimeCount() {
		return mTimeCount;
	}

	/**
	 * @return the mVecordFile
	 */
	public File getmVecordFile() {
		return mVecordFile;
	}

	/**
	 * 录制完成回调接口
	 * 
	 */
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
	
	/**
	 * 旋转数据
	 * 
	 * @param dst
	 *            目标数据
	 * @param src
	 *            源数据
	 * @param srcWidth
	 *            源数据宽
	 * @param srcHeight
	 *            源数据高
	 */
	private void YV12RotateNegative90(byte[] dst, byte[] src, int srcWidth,
	        int srcHeight) {
	    int t = 0;
	    int i, j;
	 
	    int wh = srcWidth * srcHeight;
	 
	    for (i = srcWidth - 1; i >= 0; i--) {
	        for (j = srcHeight - 1; j >= 0; j--) {
	            dst[t++] = src[j * srcWidth + i];
	        }
	    }
	 
	    for (i = srcWidth / 2 - 1; i >= 0; i--) {
	        for (j = srcHeight / 2 - 1; j >= 0; j--) {
	            dst[t++] = src[wh + j * srcWidth / 2 + i];
	        }
	    }
	 
	    for (i = srcWidth / 2 - 1; i >= 0; i--) {
	        for (j = srcHeight / 2 - 1; j >= 0; j--) {
	            dst[t++] = src[wh * 5 / 4 + j * srcWidth / 2 + i];
	        }
	    }
	 
	}
	
}
