package com.hykj.activity.subject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.manager.GetPhotoManager;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.MyToast;
import com.hykj.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;

public class NewSubjectPicActivity extends Activity implements OnClickListener {

	private ArrayList<String> imagePath;
	private GridView gv_pic;
	private Button bt_add;
	private Button bt_confirm;
	private BaseAdapter baseAdapter;
	private int w = (ScreenUtils.getScreenWidth(App.getContext()) - DensityUtils
			.dp2px(App.getContext(), 50)) / 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_newsubpic);
		imagePath = (ArrayList<String>) getIntent().getSerializableExtra(
				"imagePath");

		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_back.setOnClickListener(this);

		gv_pic = (GridView) findViewById(R.id.gv_newsubpic);
		bt_add = (Button) findViewById(R.id.bt_subpic_add);
		bt_add.setOnClickListener(this);

		bt_confirm = (Button) findViewById(R.id.bt_subpic_confirm);
		bt_confirm.setOnClickListener(this);

		baseAdapter = new BaseAdapter() {
			@SuppressLint("NewApi")
			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				String img = imagePath.get(arg0);
				if (arg1 == null) {
					SimpleDraweeView imageView = new SimpleDraweeView(App.getContext());
					imageView.setPadding(5, 5, 5, 5);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					GridView.LayoutParams params = new GridView.LayoutParams(w,w);
					imageView.setLayoutParams(params);
					imageView.setImageURI(Uri.parse("file://"+img));

					imageView.setTag(img);
					return imageView;
				} else if (img.equals(arg1.getTag())) {
					return arg1;
				} else {
					((SimpleDraweeView) arg1).setImageURI(Uri.parse("file://"+img));
					arg1.setTag(img);
					return arg1;
				}
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				return imagePath.size();
			}
		};
		gv_pic.setAdapter(baseAdapter);
		gv_pic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				imagePath.remove(position);
				baseAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_title_back:
				finish();
				break;
			case R.id.bt_subpic_confirm:
				Intent intent = new Intent();
				intent.putExtra("imagePath", imagePath);
				setResult(-800, intent);
				NewSubjectPicActivity.this.finish();
				break;

			case R.id.bt_subpic_add:
				if (imagePath.size() < 9) {
					Intent getPicIntent = new Intent(this, GetPhotoManager.class);
					getPicIntent.putExtra("isCrop", false);
					startActivityForResult(getPicIntent, 0);
				} else {
					MyToast.show("最多上传9张图片");
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case -21:
				boolean b = new File(Constant.TEMP_FILEPATH + "/temp.jpg")
						.renameTo(new File(Constant.TEMP_FILEPATH + "/"
								+ imagePath.size() + ".jpg"));
				if (b) {
					imagePath.add(Constant.TEMP_FILEPATH + "/" + imagePath.size()
							+ ".jpg");
				}
				baseAdapter.notifyDataSetChanged();
				break;
		}
	}
}
