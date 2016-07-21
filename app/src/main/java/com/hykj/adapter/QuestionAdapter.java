package com.hykj.adapter;

import java.util.List;

import com.hykj.R;
import com.hykj.entity.QuestionEntity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月22日 下午3:12:26
 * 类说明：常见问题适配器
 */
public class QuestionAdapter extends BaseAdapter {
	private List<QuestionEntity> data;
	private Context context;
	
	/**
	 * @param data
	 * @param context
	 */
	public QuestionAdapter(List<QuestionEntity> data, Context context) {
		super();
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		if(data!=null){
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
		ViewHolder vh=null;
		if(convertView==null){
			convertView=View.inflate(context, R.layout.item_lv_question, null);
			vh=new ViewHolder();
			vh.title=(TextView) convertView.findViewById(R.id.question_tv_title);
			vh.contents=(TextView) convertView.findViewById(R.id.question_tv_contents);
			convertView.setTag(vh);
		}else{
			vh=(ViewHolder) convertView.getTag();
		}
		vh.title.setText(data.get(position).getTitle());
		vh.contents.setText(data.get(position).getContents());
		return convertView;
	}
	
	public class ViewHolder{
		TextView title,contents;
	}
}
