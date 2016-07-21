package com.hykj.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.R;
import com.hykj.entity.MedicalRecord;

import java.util.List;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月15日 下午7:02:20 类说明
 */
public class MymedicalRecordAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<MedicalRecord> data;
	private Context context;

	public MymedicalRecordAdapter(List<MedicalRecord> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (data != null) {
			return data.size();
		}
		return 0;

	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lv_mymedicalrecord,
					null);
			vh = new ViewHolder();
			vh.img = (SimpleDraweeView) convertView
					.findViewById(R.id.item_img_mymedical_medicalrecord);
			vh.tv_type = (TextView) convertView
					.findViewById(R.id.item_img_mymedical_type);
			vh.tv_time = (TextView) convertView
					.findViewById(R.id.item_img_mymedical_time);
			vh.tv_describe = (TextView) convertView
					.findViewById(R.id.item_img_mymedical_describe);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		MedicalRecord mr = data.get(position);
		String url =null;
		if (mr.getImgList().size()>0 && !TextUtils.isEmpty(mr.getImgList().get(0)) ) {
			url=mr.getImgList().get(0);
		}else{
			url = App.DEFULT_PHOTO;
		}
//		Picasso.with(context).load(url).into(vh.img);
		vh.img.setImageURI(Uri.parse(url));
		vh.tv_type.setText("类型:"+mr.getType());
		vh.tv_time.setText("上传时间:"+mr.getRecordTime());
		vh.tv_describe.setText("	"+mr.getContent());
		return convertView;
	}

	public class ViewHolder {
		SimpleDraweeView img;
		TextView tv_type, tv_time, tv_describe;

	}

}
