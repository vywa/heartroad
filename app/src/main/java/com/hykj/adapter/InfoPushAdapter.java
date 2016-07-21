package com.hykj.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.usermanagement.PushInfoDetialActivity;
import com.hykj.entity.PushDataEntity;
import com.hykj.utils.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月12日 上午10:46:46 类说明：今日推送adapter
 */
public class InfoPushAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	private List<PushDataEntity> datas;

	public InfoPushAdapter(Context context, List<PushDataEntity> pushDatas) {
		super();
		this.context = context;
		this.datas = pushDatas;
	}

	@Override
	public int getCount() {
		if (datas != null) {
			return datas.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	ViewHolder vh = null;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_info_push, null);
			vh = new ViewHolder();
			vh.imgv = (SimpleDraweeView) convertView.findViewById(R.id.item_info_push_imgv);
			vh.btn_collect = (ImageView) convertView
					.findViewById(R.id.item_info_push_collect);
			vh.btn_forward = (ImageView) convertView
					.findViewById(R.id.item_info_push_forward);
			vh.tv_time = (TextView) convertView
					.findViewById(R.id.item_info_push_time);
			vh.tv_title = (TextView) convertView
					.findViewById(R.id.item_info_push_title);
			vh.tv_content = (TextView) convertView
					.findViewById(R.id.item_info_push_content);
			vh.ll = (LinearLayout) convertView
					.findViewById(R.id.item_info_push_ll);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		PushDataEntity entity = (PushDataEntity) getItem(position);
//		Picasso.with(context).load(entity.getUrl()).into(vh.imgv);
		vh.imgv.setImageURI(Uri.parse(entity.getUrl()));
		vh.btn_forward.setOnClickListener(this);
		
		vh.ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content_url = datas.get(position).getContent_url();
				Intent intent = new Intent(context, PushInfoDetialActivity.class);
				intent.putExtra("url", content_url);
				context.startActivity(intent);
			}
		});
		if(datas.get(position).isCollected()){
			vh.btn_collect.setImageResource(R.drawable.collect_fill);
		}else{
			vh.btn_collect.setImageResource(R.drawable.collect);
		}
		vh.btn_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PushDataEntity data = datas.get(position);
				if(data.isCollected()){
					mHandler.sendEmptyMessage(2);
					return;
				}
				go2Server(data);
			}
		});
		vh.tv_time.setText(datas.get(position).getTime());
		vh.tv_title.setText(datas.get(position).getTitle());
		vh.tv_content.setText(datas.get(position).getContents());
		return convertView;
	}

	protected void go2Server(final PushDataEntity entity) {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE + Constant.COLLECTION
				+ "data=");

		JSONObject json = new JSONObject();
		try {
			json.put("id",entity.getPoint());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		url.append("&tocken=" + App.TOKEN);
//		Log.wtf("咨询请求url", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject o = new JSONObject(response);
//							Log.wtf("咨询请求url", o.getString("code"));
							if ("206".equals(o.getString("code"))) {
								entity.setCollected(true);
								InfoPushAdapter.this.notifyDataSetChanged();
								mHandler.sendEmptyMessage(0);
							} else if ("110".equals(o.getString("code"))) {
								mHandler.sendEmptyMessage(2);
							} else {
								mHandler.sendEmptyMessage(1);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(1);
					}
				}));
		rq.start();

	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				
				MyToast.show("收藏成功");
				break;
			case 1:
				MyToast.show("收藏失败");
				break;
			case 2:
				MyToast.show("您已收藏过，无需再次收藏");
				break;
			default:
				break;
			}
		}
	};

	class ViewHolder {
		private LinearLayout ll;
		private SimpleDraweeView imgv;
		private ImageView btn_collect, btn_forward;
		private TextView tv_title, tv_time, tv_content;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_info_push_forward:
			ShareSDK.initSDK(context);
			OnekeyShare oks = new OnekeyShare();
			// 关闭sso授权
			oks.disableSSOWhenAuthorize();
			// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
			oks.setTitle("新浪微博"); // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
			oks.setTitleUrl("http://sharesdk.cn"); // text是分享文本，所有平台都需要这个字段
			oks.setText("我是分享文本"); // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
			oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
			// url仅在微信（包括好友和朋友圈）中使用 oks.setUrl("http://sharesdk.cn");
			// comment是我对这条分享的评论，仅在人人网和QQ空间使用
			oks.setComment("我是测试评论文本"); //
			// site是分享此内容的网站名称，仅在QQ空间使用
			// oks.setSite(getString(R.string.app_name)); //
			// siteUrl是分享此内容的网站地址，仅在QQ空间使用
			oks.setSiteUrl("http://sharesdk.cn");
			// 启动分享GUI
			oks.show(context);

			break;

		default:
			break;
		}
	}
}
