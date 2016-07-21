package com.hykj.activity.subject;

import java.io.File;
import java.util.ArrayList;

import com.hykj.Constant;
import com.hykj.R;
import com.hykj.utils.MyToast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NewSubjectFileActivity extends Activity implements OnClickListener {

	private Button bt_add;
	private Button bt_confirm;
	private TextView tv_path;
	private ImageView iv_file;

	private String filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsubfile);

		bt_add = (Button) findViewById(R.id.bt_subfile_add);
		bt_add.setOnClickListener(this);

		filePath = getIntent().getStringExtra("filePath");

		iv_file = (ImageView) findViewById(R.id.iv_newsubfile);
		tv_path = (TextView) findViewById(R.id.tv_path);
		
		if (!TextUtils.isEmpty(filePath)) {
			iv_file.setVisibility(View.VISIBLE);
			tv_path.setText(filePath);
			bt_add.setText("点击删除");
		}

		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_back.setOnClickListener(this);

		bt_confirm = (Button) findViewById(R.id.bt_subfile_confirm);
		bt_confirm.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			NewSubjectFileActivity.this.finish();
			break;
		case R.id.bt_subfile_add:
			if (iv_file.getVisibility() == View.VISIBLE) {
				// TODO SHANCHU
				filePath = null;
				iv_file.setVisibility(View.GONE);
				tv_path.setText("");
				bt_add.setText("添加附件");
			} else {
				Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
				fileIntent.setType("*/*");
				fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
				try {
					startActivityForResult(Intent.createChooser(fileIntent, "请选择一个要上传的文件"), 0);
				} catch (android.content.ActivityNotFoundException ex) {
					MyToast.show("请安装文件管理器");
				}
			}
			break;
		case R.id.bt_subfile_confirm:
			Intent intent = new Intent();
			intent.putExtra("filePath", filePath);
			setResult(-700, intent);
			NewSubjectFileActivity.this.finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Activity.RESULT_OK:
			Uri uri = data.getData();
			String url = getPath(this, uri);
			if (TextUtils.isEmpty(url)) {
				return;
			}
			for (int i = 0; i < ALLOW_FILE.length; i++) {
				if (url.endsWith(ALLOW_FILE[i])) {
					if (new File(url).length() > 5 * 1024 * 1024) {
						MyToast.show("文件过大，不要超过5M");
					} else {
						filePath = url;
						iv_file.setVisibility(View.VISIBLE);
						tv_path.setText(filePath);
						bt_add.setText("点击删除");
					}
					break;
				}
				if (i == ALLOW_FILE.length - 1 && !url.endsWith(ALLOW_FILE[i])) {
					MyToast.show("不支持文件类型");
				}
			}
			break;
		}
	}

	private String[] ALLOW_FILE = { ".wps", ".rtf", ".doc", ".docx", ".xls", ".txt", ".ppt", ".pdf", ".bmp", ".gif", ".png", ".jpg", ".jpeg", ".rmvb", ".avi", "rm", ".swf", ".wav", ".mpg",
			".mp3", ".mpeg", ".wmv", ".mp4" };

	private String getPath(Context context, Uri uri) {

		if ("content".equalsIgnoreCase(uri.getScheme())) {

			String[] projection = { "_data" };

			Cursor cursor = null;
			try {

				cursor = context.getContentResolver().query(uri, projection, null, null, null);

				int column_index = cursor.getColumnIndexOrThrow("_data");

				if (cursor.moveToFirst()) {

					return cursor.getString(column_index);
				}

			} catch (Exception e) {

			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {

			return uri.getPath();
		}
		return null;
	}
}
