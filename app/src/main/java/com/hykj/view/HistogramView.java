package com.hykj.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.hykj.R;

public class HistogramView extends View {

	private Paint xLinePaint;// 坐标轴 轴线 画笔：
	private Paint hLinePaint;// 坐标轴水平内部 虚线画笔
	private Paint titlePaint;// 绘制文本的画笔
	private Paint paint;// 矩形画笔 柱状图的样式信息
	private int[] progress;// 2 条
	private int[] aniProgress;// 实现动画的值
	private final int TRUE = 1;// 在柱状图上显示数字
	private int[] text;
	// 坐标轴左侧的数标
	private String[] ySteps;
	// 坐标轴底部数据
	private String[] xWeeks;
	private HistogramAnimation ani;
	public HistogramView(Context context) {
		super(context);
		init(context, null);
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		ySteps = new String[] { "120", "80", "", "0" };
		xWeeks = new String[] { "低血压", "高血压" };
		text = new int[] { 0, 0 };
		aniProgress = new int[] { 0, 0 };
		ani = new HistogramAnimation();
		ani.setDuration(1000);
		xLinePaint = new Paint();// 坐标轴 轴线 画笔：
		xLinePaint.setStrokeWidth(5);
		xLinePaint.setStyle(Style.STROKE);
		hLinePaint = new Paint();
		hLinePaint.setStrokeWidth(2);
		hLinePaint.setStyle(Style.STROKE);
		hLinePaint.setColor(Color.parseColor("#30d85c"));
		PathEffect effects = new DashPathEffect(new float[] { 20, 5, 20, 5 }, 0);// 虚线
		hLinePaint.setPathEffect(effects);

		titlePaint = new Paint();// 绘制文本
		paint = new Paint();// 绘制矩形

		xLinePaint.setColor(Color.parseColor("#616060"));

		titlePaint.setColor(Color.parseColor("#616060"));
	}

	public void setText(int[] text) {

		this.text = text;

		this.postInvalidate();// 可以子线程 更新视图的方法调用。
	}

	public void setProgress(int[] progress) {
		this.progress = progress;
		// this.invalidate(); //失效的意思。
		// this.postInvalidate(); // 可以子线程 更新视图的方法调用。
		 this.startAnimation(ani);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	int startY;
	int stopY;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float width = getWidth();
		float height = getHeight() - 100;

		// 1 绘制坐标线：startX, startY, stopX, stopY, paint
		int startX = dip2px(getContext(), 100);
		startY = dip2px(getContext(), 100);
		int stopX = dip2px(getContext(), 100);
		stopY = dip2px(getContext(), 500);
		canvas.drawLine(120, height / 5, 120, height * 4 / 5, xLinePaint);//纵坐标

		canvas.drawLine(120, height * 4 / 5, width - 50, height * 4 / 5,//横坐标
				xLinePaint);
		canvas.drawLine(120,  height / 5,100, height / 5+20, xLinePaint);//画纵坐标箭头
		canvas.drawLine(120,  height / 5,140, height / 5+20, xLinePaint);//画纵坐标箭头
		canvas.drawLine(width-50,  height*4 / 5,width-50-20, height*4 / 5+20, xLinePaint);//画横坐标箭头
		canvas.drawLine(width-50,  height *4/ 5,width-50-20, height*4 / 5-20, xLinePaint);//画横坐标箭头
		

		// 2 绘制坐标内部的水平线

		float hPerHeight = height * 3 / 25;// 左侧外周的 需要划分的高度：

		hLinePaint.setTextAlign(Align.CENTER);
		for (int i = 1; i < 3; i++) {
			canvas.drawLine(120, i* hPerHeight +hPerHeight+ height / 5, width - 50, i
					* hPerHeight +hPerHeight+ height / 5, hLinePaint);
		}

		// 3 绘制 Y 周坐标

		titlePaint.setTextAlign(Align.RIGHT);
		titlePaint.setTextSize(40);
		titlePaint.setAntiAlias(true);
		titlePaint.setStyle(Paint.Style.FILL);
		for (int i = 0; i < ySteps.length; i++) {
			canvas.drawText(ySteps[i], 110, height / 5 + (i + 2) * hPerHeight,
					titlePaint);
		}
		
		// 4 绘制 X 轴坐标

		float step = (width - 120 - 50) / 3;
		canvas.drawText(xWeeks[0], step * 2, height * 4 / 5 + 50, titlePaint);
		canvas.drawText(xWeeks[1], 3 * step, height * 4 / 5 + 50, titlePaint);
		titlePaint.setTextSize(25);
		canvas.drawText("(mmHg)",100, height / 5 , titlePaint);
		titlePaint.setTextSize(30);
		titlePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		canvas.drawText("低血压",width*1/3, 50 , titlePaint);//标注文字
		canvas.drawText("高血压",width*1/3,100 , titlePaint);
		canvas.drawText("正常血压",width*3/4, 50 , titlePaint);
		
		// 5 绘制矩形

		if (aniProgress != null && aniProgress.length > 0) {
			int value = aniProgress[0];// 值
			paint.setAntiAlias(true);// 抗锯齿效果
			paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(20);// 字体大小
			paint.setColor(Color.parseColor("#77d1f7"));// 蓝色
			canvas.drawRect(3 * step - 80, height * 4 / 5 - value * height * 3
					/ 1000, 3 * step - 80 + 40, height * 4 / 5-2, paint);

			int value1 = aniProgress[1];// 值
			Paint paint1 = new Paint();
			paint1.setAntiAlias(true);// 抗锯齿效果
			paint1.setStyle(Paint.Style.FILL);
			paint1.setTextSize(20);// 字体大小
			paint1.setColor(Color.parseColor("#f5d761"));// 黄色颜色
			canvas.drawRect(2 * step - 80, height * 4 / 5 - value1 * height * 3
					/ 1000, 40 + step * 2 - 80, height * 4 / 5-2, paint1);
			
			Paint valuePaint=new Paint();
			valuePaint.setColor(Color.parseColor("#616060"));
			valuePaint.setTextSize(30);
			valuePaint.setAntiAlias(true);
			canvas.drawText(value+"", 3 * step - 80, (height * 4 / 5 - value * height * 3
					/ 1000)-20, valuePaint);
			Paint valuePaint1=new Paint();
			valuePaint1.setColor(Color.parseColor("#616060"));
			valuePaint1.setTextSize(30);
			valuePaint1.setAntiAlias(true);
			
			canvas.drawText(value1+"", 2 * step - 80, (height * 4 / 5 - value1 * height * 3
					/ 1000)-20, valuePaint1);
			
			canvas.drawRect(width*1/3+20, 30, width*1/3+120, 50, paint1);//标注图形
			canvas.drawRect(width*1/3+20, 80, width*1/3+120, 100, paint);
			canvas.drawLine(width*3/4+20, 40, width*3/4+20+100, 40, hLinePaint);
			
			
		}
	}
	
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	private class HistogramAnimation extends Animation {
		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				for (int i = 0; i < aniProgress.length; i++) {
					aniProgress[i] = (int) (progress[i] * interpolatedTime);
				}
			} else {
				for (int i = 0; i < aniProgress.length; i++) {
					aniProgress[i] = progress[i];
				}
			}
			postInvalidate();
		}
	}
}