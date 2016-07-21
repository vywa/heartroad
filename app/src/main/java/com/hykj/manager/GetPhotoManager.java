package com.hykj.manager;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.hykj.Constant;
import com.hykj.R;
import com.hykj.utils.BitmapUtil;

public class GetPhotoManager extends Activity {

	final int REQUEST_CODE_CAMERA = 1;
	final int REQUEST_CODE_PICKPHOTO = 2;
	final int REQUEST_CODE_CROP = 3;

	final String FILE_NAME = "camtemp.jpg";
	private boolean isCrop;
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getphotomanager);

		isCrop = getIntent().getBooleanExtra("isCrop", false); // 如果需要截取就开启这个activity的时候传入true!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		fileName = getIntent().getStringExtra("fileName");

		findViewById(R.id.bt_gpm_cam).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCrop) {
					gotoCameraView();
				}else{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constant.TEMP_FILEPATH, FILE_NAME)));
					startActivityForResult(intent, REQUEST_CODE_CAMERA);
				}
			}
		});
		findViewById(R.id.bt_gpm_pho).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCrop) {
					gotoPickphotoView();
				}else{
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, REQUEST_CODE_PICKPHOTO);
				}
			}
		});
		findViewById(R.id.bt_gpm_cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GetPhotoManager.this.finish();
			}
		});
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Log.wtf("xxxxxxxxxx",resultCode+ "");
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_CAMERA) {
				if (isCrop) {
					resizeImage(Uri.fromFile(new File(Constant.TEMP_FILEPATH, FILE_NAME)));
				} else {
					//BitmapUtil.compressImage(Constant.TEMP_FILEPATH + "/" + FILE_NAME, Constant.TEMP_FILEPATH + "/temp.jpg");
					if (TextUtils.isEmpty(fileName)) {
						BitmapUtil.compressImage(Constant.TEMP_FILEPATH + "/" + FILE_NAME, Constant.TEMP_FILEPATH + "/temp.jpg");
					}else{
						BitmapUtil.compressImage(Constant.TEMP_FILEPATH + "/" + FILE_NAME, Constant.IMAGE_FILEPATH + "/"+fileName);
					}
					done();
				}
			} else if (requestCode == REQUEST_CODE_PICKPHOTO) {
				if (isCrop) {
					resizeImage(data.getData());
				} else {
					Uri selectedImage = data.getData();
					String[] filePathColumns = { MediaStore.Images.Media.DATA };
					Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
					c.moveToFirst();
					int columnIndex = c.getColumnIndex(filePathColumns[0]);
					String picturePath = c.getString(columnIndex);
					c.close();
					//BitmapUtil.compressImage(picturePath, Constant.TEMP_FILEPATH + "/temp.jpg");
					if (TextUtils.isEmpty(fileName)) {
						BitmapUtil.compressImage(picturePath, Constant.TEMP_FILEPATH + "/temp.jpg");
					}else{
						BitmapUtil.compressImage(picturePath, Constant.IMAGE_FILEPATH + "/"+fileName);
					}
					done();
				}
			} else if (requestCode == REQUEST_CODE_CROP) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					String s = BitmapUtil.writeBitmap(photo, Constant.TEMP_FILEPATH + "/" + FILE_NAME);
					if (TextUtils.isEmpty(fileName)) {
						BitmapUtil.compressImage(s, Constant.TEMP_FILEPATH + "/temp.jpg");
					}else{
						BitmapUtil.compressImage(s, Constant.IMAGE_FILEPATH + "/"+fileName);
					}
					done();
				}else{
					finish();
				}
			}
		}
	}
	
	void done(){
		File file = new File(Constant.TEMP_FILEPATH , FILE_NAME);
		if (file.exists()) {
			file.delete();
		}
		setResult(-21);
		finish();
	}

	private void resizeImage(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUEST_CODE_CROP);
	}

	private void gotoPickphotoView() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");// 相片类型
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUEST_CODE_PICKPHOTO);
	}

	private void gotoCameraView() {
		Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constant.TEMP_FILEPATH, FILE_NAME)));
		cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
	}
}
