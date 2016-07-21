package com.hykj.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.entity.SubjectRepeatReply;
import com.hykj.entity.SubjectReply;
import com.hykj.manager.SubjectPhotoView;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.MyToast;
import com.hykj.utils.ScreenUtils;
import com.hykj.utils.TimeUtil;
import com.tb.emoji.EmojiUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class SubjectReplyAdapter extends BaseAdapter implements OnClickListener{

	private ArrayList<SubjectReply> subjectReplys = new ArrayList<SubjectReply>();
	private int subjectId;
	private Activity activity;

	int screenWidth = ScreenUtils.getScreenWidth(App.getContext());
	int ivSize = screenWidth - DensityUtils.dp2px(App.getContext(), 200);

	public SubjectReplyAdapter(ArrayList<SubjectReply> subjectReplys, int subjectId, Activity activity) {
		this.subjectReplys = subjectReplys;
		this.subjectId = subjectId;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return subjectReplys.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		SubjectReplyViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(App.getContext(), R.layout.activity_subjectdetail_reply_item, null);
			holder = new SubjectReplyViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (SubjectReplyViewHolder) convertView.getTag();
		}
		final SubjectReply subjectReply = subjectReplys.get(position);

		holder.tv_author.setText(subjectReply.getReplyAuthor());
		if (subjectReply.isDoctor()) {
			holder.tv_author.setTextColor(Color.RED);
		}else{
			holder.tv_author.setTextColor(Color.BLACK);
		}

		holder.tv_publishTime.setText(position+1+"楼\t"+TimeUtil.getStringTime(Long.parseLong(subjectReply.getReplyTime())));
		holder.tv_content.setText(subjectReply.getReplyContent());
		
		holder.tv_content.setText(subjectReply.getReplyContent());
		EmojiUtil.displayTextView(holder.tv_content);
		
		if (!TextUtils.isEmpty(subjectReply.getReplyLocInfo())) {
			holder.tv_loc.setVisibility(View.VISIBLE);
			holder.tv_loc.setText(subjectReply.getReplyLocInfo());
		} else {
			holder.tv_loc.setVisibility(View.INVISIBLE);
		}

//		Picasso.with(App.getContext()).load(TextUtils.isEmpty(subjectReply.getReplyAuthorPhotoUrl()) ? App.DEFULT_PHOTO:subjectReply.getReplyAuthorPhotoUrl()).into(holder.niv_photo);
		holder.niv_photo.setImageURI(Uri.parse(TextUtils.isEmpty(subjectReply.getReplyAuthorPhotoUrl()) ? App.DEFULT_PHOTO:subjectReply.getReplyAuthorPhotoUrl()));
		holder.ll_imgcontainer.removeAllViews();
		if (!(subjectReply.getReplyImg().size() == 0 && TextUtils.isEmpty(subjectReply.getReplySoundUrl()))) {

			for (int i = 0; i < subjectReply.getReplyImg().size(); i++) {
				SimpleDraweeView imageView = new SimpleDraweeView(App.getContext());
				imageView.setScaleType(ScaleType.FIT_CENTER);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ivSize, ivSize);
				imageView.setLayoutParams(params);
				String imgUrl = subjectReply.getReplyImg().get(i);
//				Picasso.with(App.getContext()).load(TextUtils.isEmpty(imgUrl) ? App.DEFULT_PHOTO:imgUrl).into(imageView);
				imageView.setImageURI(Uri.parse(imgUrl));
				holder.ll_imgcontainer.addView(imageView);

				final int y = i;
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(App.getContext(), SubjectPhotoView.class);
						intent.putExtra("imageUrls", subjectReply.getReplyImg());
						intent.putExtra("currentItem", y);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						App.getContext().startActivity(intent);
					}
				});

			}
			if (!TextUtils.isEmpty(subjectReply.getReplySoundUrl())) {
				ImageView imageView = new ImageView(App.getContext());
				imageView.setImageResource(R.drawable.sound);
				holder.ll_imgcontainer.addView(imageView);
				imageView.setOnClickListener(new SoundPlayListener(subjectReply.getReplySoundUrl()));
			}
		}

		holder.ll_repeatreplycontainer.removeAllViews();
		if (subjectReply.getRepeatReply().size() != 0) {
			ArrayList<SubjectRepeatReply> list = subjectReply.getRepeatReply();
			for (SubjectRepeatReply subjectRepeatReply : list) {
				View view = View.inflate(App.getContext(), R.layout.item_repeatreply, null);
				TextView tv_repeat = (TextView) view.findViewById(R.id.tv_rr_repeat);
				TextView tv_repeatTo = (TextView) view.findViewById(R.id.tv_rr_repeatto);
				TextView tv_repeatContent = (TextView) view.findViewById(R.id.tv_rr_repeatvcontent);
				tv_repeat.setText(subjectRepeatReply.getRepeat());
				tv_repeatTo.setText(subjectRepeatReply.getRepeatTo());
				tv_repeatContent.setText(subjectRepeatReply.getRepeatContent());
				holder.ll_repeatreplycontainer.addView(view);
				tv_repeat.setOnClickListener(this);
				tv_repeatTo.setOnClickListener(this);
			}
		}
		
		return convertView;
	}

	MediaPlayer mediaPlayer = null;

	class SubjectReplyViewHolder {

		private SimpleDraweeView niv_photo;
		private TextView tv_author;
		private TextView tv_publishTime;
		private TextView tv_loc;
		private TextView tv_content;
		private LinearLayout ll_imgcontainer;
		private LinearLayout ll_repeatreplycontainer;

		SubjectReplyViewHolder(View view) {
			niv_photo = (SimpleDraweeView) view.findViewById(R.id.niv_sdreply_photo);
			tv_author = (TextView) view.findViewById(R.id.tv_sdreply_authorname);
			tv_publishTime = (TextView) view.findViewById(R.id.tv_sdreply_publishtime);
			tv_loc = (TextView) view.findViewById(R.id.tv_sdreply_loc);
			tv_content = (TextView) view.findViewById(R.id.tv_sdreply_content);
			ll_imgcontainer = (LinearLayout) view.findViewById(R.id.ll_sdreply_imgcontainer);
			ll_repeatreplycontainer = (LinearLayout) view.findViewById(R.id.ll_sdreply_repeatreplycontainer);
		}
	}

	class SoundPlayListener implements OnClickListener {

		public SoundPlayListener(String url) {
			this.url = url;
		}

		String url;

		@Override
		public void onClick(final View v) {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				return;
			}
			if (mediaPlayer == null) {
				mediaPlayer = MediaPlayer.create(App.getContext(), Uri.parse(url));
			} else {
				mediaPlayer.start();
			}
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {

				}
			});
		}
	}

	StringBuilder url;

	int responseCode = -1;
	String description = null;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.GET_DATA_SUCCESS:
				// TODO
				subjectReplys.remove(subjectReplys.get((Integer) msg.obj));
				SubjectReplyAdapter.this.notifyDataSetChanged();
				break;
			case Constant.GET_DATA_SERVER_ERROR:
				MyToast.show(description);
				break;
			case Constant.GET_DATA_ANALYZE_ERROR:
				MyToast.show("解析数据失败");
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				MyToast.show("网络错误");
				break;
			}
		}
	};

	private Request getOperatorReq(final int position) {

		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
				try {
					JSONObject response = new JSONObject(res);

					responseCode = response.getInt("code");
					description = response.getString("message");
					if (responseCode != 206) {
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						return;
					}
					Message message = Message.obtain();
					message.obj = position;
					message.what = Constant.GET_DATA_SUCCESS;
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_rr_repeat:
			repeatListener.setRepeatNickName(((TextView)v).getText().toString());
			break;
		case R.id.tv_rr_repeatto:
			repeatListener.setRepeatNickName(((TextView)v).getText().toString());
			break;
		}
	}
	
	public interface RepeatListener{
		void setRepeatNickName(String name);
	}
	private RepeatListener repeatListener;
	
	public void setOnRepeatListener(RepeatListener repeatListener){
		this.repeatListener = repeatListener;
	}
}





















