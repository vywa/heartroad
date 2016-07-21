package com.hykj.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hykj.App;
import com.hykj.R;
import com.hykj.entity.Subject;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.ScreenUtils;
import com.hykj.utils.TimeUtil;
import com.tb.emoji.EmojiUtil;

import java.util.LinkedList;

public class SubjectAdapter extends BaseAdapter {

	int i = (ScreenUtils.getScreenWidth(App.getContext()) - DensityUtils.dp2px(App.getContext(),20))/3;
	//所有信息集合
	LinkedList<Subject> subjects = new LinkedList<Subject>();

	public SubjectAdapter(LinkedList<Subject> subjects) {
		this.subjects = subjects;
	}

	@Override
	public int getCount() {
		return subjects.size();
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

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(App.getContext(), R.layout.item_fragment_subject, null);

			holder = new ViewHolder(convertView);
			//作为标签保存
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Subject subject = subjects.get(position);

		holder.author.setText(subject.getAuthor());
		if (subject.isDoctor()) {
			holder.author.setTextColor(Color.RED);
		}else{
			holder.author.setTextColor(Color.BLACK);
		}
		String publishTime = subject.getPublishTime();
		String stringTime = TimeUtil.getStringTime(Long.parseLong(publishTime));
		holder.time.setText(stringTime);

		String authorPhotoImgUrl = subject.getAuthorPhotoImgUrl();
//		Picasso.with(App.getContext()).load(TextUtils.isEmpty(authorPhotoImgUrl) ? App.DEFULT_PHOTO:authorPhotoImgUrl).into(holder.civ);
		holder.civ.setImageURI(Uri.parse(TextUtils.isEmpty(authorPhotoImgUrl) ? App.DEFULT_PHOTO:authorPhotoImgUrl));
		int subjectType = subject.getSubjectType();
		if (subjectType == 1) {
			holder.title.setCompoundDrawablesWithIntrinsicBounds(App.getContext().getResources().getDrawable(R.drawable.best), null, null, null);
		}else{
			holder.title.setCompoundDrawables(null, null, null, null);
		}
		holder.title.setText(subject.getTitle());
		EmojiUtil.displayTextView(holder.title);

		holder.replynum.setText(subject.getReplyNum()+"");

		final android.view.ViewGroup.LayoutParams params = holder.contentimg.getLayoutParams();

		if (subject.getImgUrls().size() > 6) {
			params.height = i * 3+ DensityUtils.dp2px(App.getContext(),3);
		} else if (subject.getImgUrls().size() > 3) {
			params.height = i * 2+ DensityUtils.dp2px(App.getContext(),2);
		} else if (subject.getImgUrls().size() > 0) {
			params.height = i + DensityUtils.dp2px(App.getContext(),1);
		} else {
			params.height = 0;
		}
		holder.contentimg.setLayoutParams(params);

		if (subject.getImgUrls().size() == 0) {
			holder.contentimg.setVisibility(View.INVISIBLE);
		} else {
			holder.contentimg.setVisibility(View.VISIBLE);
			holder.contentimg.setFocusable(false);
			holder.contentimg.setClickable(false);
			holder.contentimg.setEnabled(false);

			BaseAdapter baseAdapter = new BaseAdapter() {
				@SuppressLint("NewApi")
				@Override
				public View getView(int arg0, View arg1, ViewGroup arg2) {
					String img = subject.getImgUrls().get(arg0);
					if (arg1 == null) {
						SimpleDraweeView imageView = new SimpleDraweeView(App.getContext());
						imageView.setPadding(5, 5, 5, 5);
						imageView.setScaleType(ScaleType.CENTER_CROP);

						GridView.LayoutParams p = new GridView.LayoutParams(i, i);
						imageView.setLayoutParams(p);

//						Picasso.with(App.getContext()).load(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO:img).into(imageView);

						imageView.setController(Fresco.newDraweeControllerBuilder()
								.setImageRequest(ImageRequestBuilder.newBuilderWithSource(Uri.parse(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO:img))
										.setResizeOptions(new ResizeOptions(100,100)).build())
								.build());

						imageView.setTag(img);
						return imageView;
					}else if(img.equals((String)arg1.getTag())){
						return arg1;
					}else{
//						Picasso.with(App.getContext()).load(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO:img).into((ImageView)arg1);
//						((SimpleDraweeView)arg1).setImageURI(Uri.parse(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO:img));
						((SimpleDraweeView)arg1).setController(Fresco.newDraweeControllerBuilder()
								.setImageRequest(ImageRequestBuilder.newBuilderWithSource(Uri.parse(TextUtils.isEmpty(img) ? App.DEFULT_PHOTO:img))
										.setResizeOptions(new ResizeOptions(100,100)).build())
								.build());
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
			holder.contentimg.setAdapter(baseAdapter);
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView author, title, replynum,time;
		public GridView contentimg;
		public SimpleDraweeView civ;

		public ViewHolder(View itemView) {
			title = (TextView) itemView.findViewById(R.id.tv_question_title);
			replynum = (TextView) itemView.findViewById(R.id.tv_question_replynum);
			time = (TextView) itemView.findViewById(R.id.tv_subjectpreview_publishtime);
			civ = (SimpleDraweeView) itemView.findViewById(R.id.niv_subjectpreview_photo);
			contentimg = (GridView) itemView.findViewById(R.id.gv_question_contentimg);
			author = (TextView) itemView.findViewById(R.id.tv_subjectpreview_authorname);
		}
	}
}