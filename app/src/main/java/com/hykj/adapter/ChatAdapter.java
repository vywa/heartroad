package com.hykj.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.chat.ChatActivity;
import com.hykj.db.ChatProvider;
import com.hykj.db.ChatProvider.ChatConstants;
import com.hykj.manager.ChatPhotoView;
import com.hykj.manager.MySoundManager;
import com.hykj.utils.BitmapUtil;
import com.hykj.utils.TimeUtil;
import com.tb.emoji.EmojiUtil;

import java.io.File;
import java.io.IOException;

public class ChatAdapter extends SimpleCursorAdapter {

	private Context mContext;
	File file = null;
	MySoundManager manager = new MySoundManager();
	
	public ChatAdapter(Context context, Cursor cursor, String[] from) {
		super(context, 0, cursor, from, null);
		mContext = context;
	}

	class ChatViewHolder {
		TextView tv_ichat_time, tv_ichat_content, tv_ichat__me_content;
		LinearLayout ll_ichat_me, ll_ichat;
		SimpleDraweeView niv_ichat_photo, niv_ichat_me_photo,iv_ichat_pic,iv_ichat_me_pic;
		ProgressBar pb_ichat_me;
		ImageView iv_ichat_sendfailure,iv_ichat_sound,iv_ichat_me_sound ;
	}

	private void displayTextView(TextView tv) {
		try {
			EmojiUtil.handlerEmojiText(tv, tv.getText().toString(), mContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopPlay(){
		manager.stopPlayMusic();
	}

	/**
	 * 标记为已读消息
	 * 
	 * @param id
	 */
	private void markAsRead(int id) {
		Uri rowuri = Uri.parse("content://" + ChatProvider.AUTHORITY + "/" + ChatProvider.TABLE_NAME + "/" + id);
		ContentValues values = new ContentValues();
		values.put(ChatConstants.DELIVERY_STATUS, ChatConstants.DS_SENT_OR_READ);
		mContext.getContentResolver().update(rowuri, values, null, null);
	}

	private void markAsReadDelayed(final int id, int delay) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				markAsRead(id);
			}
		}, delay);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_chat, null);
			holder = new ChatViewHolder();
			holder.ll_ichat = (LinearLayout) convertView.findViewById(R.id.ll_ichat);
			holder.ll_ichat_me = (LinearLayout) convertView.findViewById(R.id.ll_ichat_me);
			holder.tv_ichat_time = (TextView) convertView.findViewById(R.id.tv_ichat_time);
			holder.tv_ichat_content = (TextView) convertView.findViewById(R.id.tv_ichat_content);
			holder.tv_ichat__me_content = (TextView) convertView.findViewById(R.id.tv_ichat__me_content);
			holder.niv_ichat_photo = (SimpleDraweeView) convertView.findViewById(R.id.niv_ichat_photo);
			holder.niv_ichat_me_photo = (SimpleDraweeView) convertView.findViewById(R.id.niv_ichat_me_photo);
			holder.pb_ichat_me = (ProgressBar) convertView.findViewById(R.id.pb_ichat_me);
			holder.iv_ichat_sendfailure = (ImageView) convertView.findViewById(R.id.iv_ichat_sendfailure);
			holder.iv_ichat_pic = (SimpleDraweeView) convertView.findViewById(R.id.iv_ichat_pic);
			holder.iv_ichat_sound = (ImageView) convertView.findViewById(R.id.iv_ichat_sound);
			holder.iv_ichat_me_pic = (SimpleDraweeView) convertView.findViewById(R.id.iv_ichat_me_pic);
			holder.iv_ichat_me_sound = (ImageView) convertView.findViewById(R.id.iv_ichat_me_sound);
			convertView.setTag(R.id.tag_first, holder);
		} else {
			holder = (ChatViewHolder) convertView.getTag(R.id.tag_first);
		}

		Cursor cursor = this.getCursor();
		
		cursor.moveToPosition(cursor.getCount() - position - 1);

		long dateMilliseconds = cursor.getLong(cursor.getColumnIndex(ChatProvider.ChatConstants.DATE));

		int _id = cursor.getInt(cursor.getColumnIndex(ChatProvider.ChatConstants._ID));
		String message = cursor.getString(cursor.getColumnIndex(ChatProvider.ChatConstants.MESSAGE));
		
		if (message.length() > 150) {
			String pid = cursor.getString(cursor.getColumnIndex(ChatProvider.ChatConstants.PACKET_ID));

			if (new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".jpg").exists()) {
				file = new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".jpg");
			} else if (new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".arm").exists()) {
				file = new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".arm");
			} else if (new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".png").exists()) {
				file = new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".png");
			} else if (new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".gif").exists()) {
				file = new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".gif");
			} else if (new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".bmp").exists()) {
				file = new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".bmp");
			} else if (new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".wav").exists()) {
				file = new File(Constant.DOWNLOAD_FILEPATH + "/" + pid + ".wav");
			} else {
				file = BitmapUtil.byte2file(BitmapUtil.hexToBytes(message), pid);
			}
			message = pid;
		}else{
			file = null;
		}
		convertView.setTag(R.id.tag_second, file);
		
		int come = cursor.getInt(cursor.getColumnIndex(ChatProvider.ChatConstants.DIRECTION));// 消息来自
		boolean from_me = (come == ChatConstants.OUTGOING);
		String jid = cursor.getString(cursor.getColumnIndex(ChatProvider.ChatConstants.JID));
		int delivery_status = cursor.getInt(cursor.getColumnIndex(ChatProvider.ChatConstants.DELIVERY_STATUS));

		final File myFile = (File) convertView.getTag(R.id.tag_second);

		if (cursor.moveToNext()) {
			if (dateMilliseconds - cursor.getLong(cursor.getColumnIndex(ChatProvider.ChatConstants.DATE))> 2 * 60 * 1000) {
				holder.tv_ichat_time.setVisibility(View.VISIBLE);
				holder.tv_ichat_time.setText(TimeUtil.getStringTime(dateMilliseconds));
			} else {
				holder.tv_ichat_time.setVisibility(View.GONE);
			}
		}

		if (from_me) {
			holder.ll_ichat.setVisibility(View.GONE);
			holder.ll_ichat_me.setVisibility(View.VISIBLE);

			if (myFile != null && myFile.exists()) {
				holder.tv_ichat__me_content.setVisibility(View.GONE);
				if (myFile.getName().endsWith("arm")) {
					holder.iv_ichat_me_sound.setVisibility(View.VISIBLE);
					holder.iv_ichat_me_pic.setVisibility(View.GONE);
					holder.iv_ichat_me_sound.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							manager.playMusic(myFile.getAbsolutePath());
						}
					});
				}else{
					holder.iv_ichat_me_sound.setVisibility(View.GONE);
					holder.iv_ichat_me_pic.setVisibility(View.VISIBLE);
					String imgUrl = "file://"+myFile.getAbsolutePath();
//					Picasso.with(App.getContext()).load(imgUrl).resize(100, 100).centerCrop().into(holder.iv_ichat_me_pic);
					holder.iv_ichat_me_pic.setImageURI(Uri.parse(imgUrl));
					holder.iv_ichat_me_pic.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(App.getContext(), ChatPhotoView.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("photoPath", myFile.getAbsolutePath());
							App.getContext().startActivity(intent);
						}
					});
				}
			}else{
				holder.tv_ichat__me_content.setVisibility(View.VISIBLE);
				holder.iv_ichat_me_pic.setVisibility(View.GONE);
				holder.iv_ichat_me_sound.setVisibility(View.GONE);
				holder.tv_ichat__me_content.setText(message);
				displayTextView(holder.tv_ichat__me_content);
			}
			holder.niv_ichat_me_photo.setImageURI(Uri.parse(App.HEAD_IMAGE_URL));
//			Picasso.with(App.getContext()).load(App.HEAD_IMAGE_URL).into(holder.niv_ichat_me_photo);
		} else {
			holder.ll_ichat_me.setVisibility(View.GONE);
			holder.ll_ichat.setVisibility(View.VISIBLE);

			if (myFile != null && myFile.exists()) {
				holder.tv_ichat_content.setVisibility(View.GONE);
				if (myFile.getName().endsWith("arm")) {
					holder.iv_ichat_sound.setVisibility(View.VISIBLE);
					holder.iv_ichat_pic.setVisibility(View.GONE);
					holder.iv_ichat_sound.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							manager.playMusic(myFile.getAbsolutePath());
						}
					});
				}else{
					holder.iv_ichat_sound.setVisibility(View.GONE);
					holder.iv_ichat_pic.setVisibility(View.VISIBLE);
					String imgUrl = "file://"+myFile.getAbsolutePath();
					holder.iv_ichat_pic.setImageURI(Uri.parse(imgUrl));
//					Picasso.with(App.getContext()).load(imgUrl).resize(100, 100).centerCrop().into(holder.iv_ichat_pic);
					
					holder.iv_ichat_pic.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(App.getContext(), ChatPhotoView.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("photoPath", myFile.getAbsolutePath());
							App.getContext().startActivity(intent);
						}
					});
				}
			}else{
				holder.tv_ichat_content.setVisibility(View.VISIBLE);
				holder.iv_ichat_pic.setVisibility(View.GONE);
				holder.iv_ichat_sound.setVisibility(View.GONE);
				
				holder.tv_ichat_content.setText(message);
				displayTextView(holder.tv_ichat_content);
			}
			holder.niv_ichat_photo.setImageURI(Uri.parse(ChatActivity.doctor.getIconUrl() == null ? App.DEFULT_PHOTO:ChatActivity.doctor.getIconUrl()));
//			Picasso.with(App.getContext()).load(ChatActivity.doctor.getIconUrl() == null ? App.DEFULT_PHOTO:ChatActivity.doctor.getIconUrl()).into(holder.niv_ichat_photo);
		}

		if (!from_me && delivery_status == ChatConstants.DS_NEW) {
			markAsReadDelayed(_id, 1000);
		}

		if (from_me) {
			switch (delivery_status) {
			case ChatConstants.DS_SENT_OR_READ:
				holder.pb_ichat_me.setVisibility(View.VISIBLE);
				holder.iv_ichat_sendfailure.setVisibility(View.INVISIBLE);
				break;
			case ChatConstants.DS_NEW:
				holder.pb_ichat_me.setVisibility(View.INVISIBLE);
				holder.iv_ichat_sendfailure.setVisibility(View.VISIBLE);
				break;
			case ChatConstants.DS_ACKED:
				holder.pb_ichat_me.setVisibility(View.INVISIBLE);
				holder.iv_ichat_sendfailure.setVisibility(View.INVISIBLE);
				break;
			}
		}

		return convertView;
	}
}
