package com.hykj.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import com.hykj.activity.subject.SubjectDetailActivity;
import com.hykj.activity.usermanagement.PushInfoDetialActivity;
import com.hykj.entity.PushDataEntity;
import com.hykj.entity.Subject;
import com.hykj.fragment.subject.SubjectFragment;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.MyToast;
import com.hykj.utils.ScreenUtils;
import com.hykj.utils.TimeUtil;
import com.tb.emoji.EmojiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月12日 下午5:32:51 类说明：收藏adapter
 */
public class InfoCollectAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	private List<PushDataEntity> datas;
	private LinkedList<Subject> subjects;

	public InfoCollectAdapter(Context context, List<PushDataEntity> datas, LinkedList<Subject> subjects) {
		super();
		this.context = context;
		this.datas = datas;
		this.subjects = subjects;
	}

	@Override
	public int getCount() {
		if (datas != null && subjects != null) {
			return datas.size() + subjects.size();
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
	public int getItemViewType(int position) {
		if (position < datas.size()) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		ViewHolder vh = null;
		SubjectViewHolder subjectViewHolder = null;

		if (convertView == null) {
			if (viewType == 0) {
				convertView = View.inflate(context, R.layout.item_info_collect, null);
				vh = new ViewHolder();
				vh.ll = (LinearLayout) convertView.findViewById(R.id.item_info_collect_ll);
				vh.img_url = (SimpleDraweeView) convertView.findViewById(R.id.item_info_collect_imgv);
				vh.img_forward = (ImageView) convertView.findViewById(R.id.item_info_collect_forward);
				vh.img_del = (ImageView) convertView.findViewById(R.id.item_info_collect_del);
				vh.tv_title = (TextView) convertView.findViewById(R.id.item_info_collect_title);
				vh.tv_time = (TextView) convertView.findViewById(R.id.item_info_collect_time);
				vh.tv_content = (TextView) convertView.findViewById(R.id.item_info_collect_content);
				convertView.setTag(vh);
			} else {
				convertView = View.inflate(App.getContext(), R.layout.item_fragment_subject, null);
				subjectViewHolder = new SubjectViewHolder(convertView);
				convertView.setTag(subjectViewHolder);
			}

		} else {
			if (viewType == 0) {

				vh = (ViewHolder) convertView.getTag();
			} else {
				subjectViewHolder = (SubjectViewHolder) convertView.getTag();
			}
		}

		if (viewType == 0) {
			PushDataEntity entity = datas.get(position);
//			Picasso.with(context).load(entity.getUrl()).into(vh.img_url);
			vh.img_url.setImageURI(Uri.parse(entity.getUrl()));
			vh.tv_title.setText(datas.get(position).getTitle());
			vh.tv_time.setText(datas.get(position).getTime());
			vh.tv_content.setText(datas.get(position).getContents());
			vh.img_forward.setOnClickListener(this);
			vh.ll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String content_url = datas.get(position).getContent_url();
					Intent intent = new Intent(context, PushInfoDetialActivity.class);
					intent.putExtra("url", content_url);
					context.startActivity(intent);
				}
			});

			vh.img_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO 删除服务器收藏数据
					final Dialog dialog = new Dialog(context);
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.dialog_del_collection);
					Window dialogWindow = dialog.getWindow();
					WindowManager.LayoutParams lp = dialogWindow.getAttributes();
					dialogWindow.setGravity(Gravity.CENTER);
					WindowManager m = dialogWindow.getWindowManager();
					Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
					lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
					lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
					dialogWindow.setAttributes(lp);
					Button btn_true = (Button) dialog.findViewById(R.id.dialog_btn_collect_true);
					Button btn_cancel = (Button) dialog.findViewById(R.id.dialog_btn_collect_cancel);
					dialog.show();
					btn_cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.cancel();
						}
					});
					btn_true.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.cancel();
							delData(position);

						}

					});
				}
			});
		} else {

			final Subject subject = subjects.get(position - datas.size());
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(App.getContext(), SubjectDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("subject", subject);
					App.getContext().startActivity(intent);
				}
			});
			subjectViewHolder.coll.setVisibility(View.VISIBLE);
			subjectViewHolder.coll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO 删除服务器收藏数据
					final Dialog dialog = new Dialog(context);
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.dialog_del_collection);
					Window dialogWindow = dialog.getWindow();
					WindowManager.LayoutParams lp = dialogWindow.getAttributes();
					dialogWindow.setGravity(Gravity.CENTER);
					WindowManager m = dialogWindow.getWindowManager();
					Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
					lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
					lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
					dialogWindow.setAttributes(lp);
					Button btn_true = (Button) dialog.findViewById(R.id.dialog_btn_collect_true);
					Button btn_cancel = (Button) dialog.findViewById(R.id.dialog_btn_collect_cancel);
					dialog.show();
					btn_cancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.cancel();
						}
					});
					btn_true.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							dialog.cancel();
							subjects.remove(position - datas.size());
							try {
								SubjectFragment.setSubjectCollection(subject.getSubjectId(),false);
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							notifyDataSetChanged();
							StringBuilder url = new StringBuilder(App.BASE + Constant.SUBJECT_DELCOLL + "tocken=" + App.TOKEN);
							url.append("&data=");
							JSONObject object = new JSONObject();
							try {
								object.put("id", subject.getSubjectId());
							} catch (JSONException e) {
								e.printStackTrace();
							}
							url.append(object.toString());
							// Log.wtf("删除收藏帖子的url", url.toString());
							App.getRequestQueue().add(new StringRequest(Request.Method.GET, url.toString(), new Listener<String>() {
								@Override
								public void onResponse(String response) {
									// Log.wtf("删除收藏帖子返回信息", response);
									try {
										JSONObject jsonObject = new JSONObject(response);
										Message message = Message.obtain();
										message.what = Constant.GET_DATA_SUCCESS;
										message.obj = jsonObject.optString("message");
										mHandler.sendMessage(message);
									} catch (JSONException e) {
										mHandler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
										e.printStackTrace();
									}
								}
							}, new ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									mHandler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
								}
							}));
						}
					});
				}
			});

			subjectViewHolder.author.setText(subject.getAuthor());
			if (subject.isDoctor()) {
				subjectViewHolder.author.setTextColor(Color.RED);
			} else {
				subjectViewHolder.author.setTextColor(Color.BLACK);
			}
			String publishTime = subject.getPublishTime();
			String stringTime = TimeUtil.getStringTime(Long.parseLong(publishTime));
			subjectViewHolder.time.setText(stringTime);

			String authorPhotoImgUrl = subject.getAuthorPhotoImgUrl();
//			Picasso.with(App.getContext()).load(TextUtils.isEmpty(authorPhotoImgUrl) ? App.DEFULT_PHOTO : authorPhotoImgUrl).into(subjectViewHolder.civ);
			subjectViewHolder.civ.setImageURI(Uri.parse(TextUtils.isEmpty(authorPhotoImgUrl) ? App.DEFULT_PHOTO : authorPhotoImgUrl));
			int subjectType = subject.getSubjectType();
			if (subjectType == 1) {
				subjectViewHolder.title.setCompoundDrawablesWithIntrinsicBounds(App.getContext().getResources().getDrawable(R.drawable.best), null, null, null);
			} else {
				subjectViewHolder.title.setCompoundDrawables(null, null, null, null);
			}
			subjectViewHolder.title.setText(subject.getTitle());
			EmojiUtil.displayTextView(subjectViewHolder.title);

			subjectViewHolder.replynum.setText(subject.getReplyNum() + "");

			android.view.ViewGroup.LayoutParams params = subjectViewHolder.contentimg.getLayoutParams();

			if (subject.getImgUrls().size() > 6) {
				params.height = w * 3+ DensityUtils.dp2px(App.getContext(),3);
			} else if (subject.getImgUrls().size() > 3) {
				params.height = w * 2+ DensityUtils.dp2px(App.getContext(),2);
			} else if (subject.getImgUrls().size() > 0) {
				params.height = w+ DensityUtils.dp2px(App.getContext(),1);
			} else {
				params.height = 0;
			}
			subjectViewHolder.contentimg.setLayoutParams(params);

			if (subject.getImgUrls().size() == 0) {
				subjectViewHolder.contentimg.setVisibility(View.INVISIBLE);
			} else {
				subjectViewHolder.contentimg.setVisibility(View.VISIBLE);
				subjectViewHolder.contentimg.setFocusable(false);
				subjectViewHolder.contentimg.setClickable(false);
				subjectViewHolder.contentimg.setEnabled(false);
				BaseAdapter baseAdapter = new BaseAdapter() {
					@SuppressLint("NewApi")
					@Override
					public View getView(int arg0, View arg1, ViewGroup arg2) {
						String img = subject.getImgUrls().get(arg0);
						if (arg1 == null) {
							SimpleDraweeView imageView = new SimpleDraweeView(App.getContext());
							imageView.setPadding(5, 5, 5, 5);
							imageView.setScaleType(ScaleType.CENTER_CROP);
							GridView.LayoutParams params = new GridView.LayoutParams(w, w);
							imageView.setLayoutParams(params);
//							Picasso.with(App.getContext()).load(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO : img).into(imageView);
							imageView.setImageURI(Uri.parse(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO : img));
							imageView.setTag(img);
							return imageView;
						} else if (img.equals(arg1.getTag())) {
							return arg1;
						} else {
//							Picasso.with(App.getContext()).load(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO : img).into((ImageView) arg1);
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
						return subject.getImgUrls().size();
					}
				};
				// 配置适配器
				subjectViewHolder.contentimg.setAdapter(baseAdapter);
			}
		}
		return convertView;
	}

	private void delData(final int position) {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE + Constant.DSUBJECT);
		JSONObject json = new JSONObject();
		url.append("data=");
		try {
			json.put("id", datas.get(position).getPoint());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		url.append(json.toString());
		url.append("&tocken=" + App.TOKEN);
		// Log.wtf("删除饮食", url.toString());

		rq.add(new StringRequest(Request.Method.GET, url.toString(), new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject o = new JSONObject(response);
					if ("206".equals(o.getString("code"))) {
						datas.remove(position);
						notifyDataSetChanged();
						mHandler.sendEmptyMessage(0);
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

				MyToast.show("删除成功");
				break;
			case 1:
				MyToast.show("网络错误");
				break;
			case Constant.GET_DATA_SUCCESS:
				MyToast.show((String) msg.obj);
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				MyToast.show("网络错误");
				break;
			}
		}
	};

	class ViewHolder {
		private LinearLayout ll;
		private ImageView  img_del, img_forward;
		private TextView tv_title, tv_content, tv_time;
		private SimpleDraweeView img_url;
	}

	class SubjectViewHolder {
		public TextView author, title, replynum, time;
		public GridView contentimg;
		public SimpleDraweeView civ;
		public ImageView coll;

		public SubjectViewHolder(View itemView) {
			title = (TextView) itemView.findViewById(R.id.tv_question_title);
			replynum = (TextView) itemView.findViewById(R.id.tv_question_replynum);
			time = (TextView) itemView.findViewById(R.id.tv_subjectpreview_publishtime);
			civ = (SimpleDraweeView) itemView.findViewById(R.id.niv_subjectpreview_photo);
			contentimg = (GridView) itemView.findViewById(R.id.gv_question_contentimg);
			author = (TextView) itemView.findViewById(R.id.tv_subjectpreview_authorname);
			coll = (ImageView) itemView.findViewById(R.id.iv_subject_coll);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.item_info_collect_forward:

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

	private int w = (ScreenUtils.getScreenWidth(App.getContext()) - DensityUtils.dp2px(App.getContext(),20))/3;

}
