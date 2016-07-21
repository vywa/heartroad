package com.hykj.activity.usermanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.R;
import com.hykj.entity.MedicalRecord;
import com.hykj.manager.SubjectPhotoView;
import com.hykj.utils.DensityUtils;

public class MedicalRecordDetailActivity extends Activity {
	private ImageView mImg_back;
	MedicalRecord record;
	int w = DensityUtils.dp2px(App.getContext(), 100);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_medicalrecordetail);

		record = (MedicalRecord) getIntent().getSerializableExtra("record");

		findViews();

	}

	private void findViews() {
		gv_imgs = (GridView) findViewById(R.id.gv_mrdetail_imgs);
		mImg_back=(ImageView) findViewById(R.id.mrdetail_imgv_back);
		mImg_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					MedicalRecordDetailActivity.this.finish();
			}
		});
		BaseAdapter baseAdapter = new BaseAdapter() {
			@SuppressLint("NewApi")
			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				String img = record.getImgList().get(arg0);
				if (arg1 == null) {
					SimpleDraweeView imageView = new SimpleDraweeView(App.getContext());
					imageView.setPadding(5, 10, 5, 10);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					GridView.LayoutParams params = new GridView.LayoutParams(w,
							w);
					imageView.setLayoutParams(params);
					imageView.setImageURI(Uri.parse(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO: img));
					imageView.setTag(img);
					final int y = arg0;
					imageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									MedicalRecordDetailActivity.this,
									SubjectPhotoView.class);
							intent.putExtra("imageUrls", record.getImgList());
							intent.putExtra("currentItem", y);
							startActivity(intent);
						}
					});
					return imageView;
				} else if (img.equals(arg1.getTag())) {
					return arg1;
				} else {
					((SimpleDraweeView) arg1).setImageURI(Uri.parse(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO : img));
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
				return record.getImgList().size();
			}
		};
		gv_imgs.setAdapter(baseAdapter);

		tv_type = (TextView) findViewById(R.id.tv_mrcordetail_type);
		tv_time = (TextView) findViewById(R.id.tv_mrcordetail_time);
		tv_content = (TextView) findViewById(R.id.tv_mrcordetail_content);

		if (record != null) {
			if (!TextUtils.isEmpty(record.getType())) {
				tv_type.setText("类型:"+record.getType());
			} else {
				tv_type.setText("");
			}

			if (!TextUtils.isEmpty(record.getRecordTime())) {
				tv_time.setText("上传时间:"+record.getRecordTime());
			} else {
				tv_time.setText("");
			}

			if (!TextUtils.isEmpty(record.getContent())) {
				tv_content.setText("	"+record.getContent());
			} else {
				tv_content.setText("");
			}
		}
	}

	private GridView gv_imgs;
	private TextView tv_type;
	private TextView tv_time;
	private TextView tv_content;

	public void setRecord(MedicalRecord record) {
		this.record = record;
	}

}
