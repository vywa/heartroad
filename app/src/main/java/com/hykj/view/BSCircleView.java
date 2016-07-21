package com.hykj.view;

import java.math.BigDecimal;

import org.xbill.DNS.MRRecord;

import com.hykj.App;
import com.hykj.entity.BloodSugarInfo;
import com.hykj.entity.TargetBloodSugar;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.ScreenUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BSCircleView extends View {

	public BSCircleView(Context context) {
		super(context);
		width = ScreenUtils.getScreenWidth(App.getContext());

	}

	public BSCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		width = ScreenUtils.getScreenWidth(App.getContext());
	}

	public BSCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		width = ScreenUtils.getScreenWidth(App.getContext()) * 3 / 4;
	}

	private TargetBloodSugar target;

	public void setTargetValue(BloodSugarInfo info, TargetBloodSugar target) {
		if (info != null) {
			this.targetValue = new BigDecimal(info.getBsValue());
			kind = info.getMeasureType();
//			Log.wtf("aaa", target.toString());

			this.target = target;

		}
		invalidate();
	}

	private int width;
	private BigDecimal value = new BigDecimal(0);
	private BigDecimal targetValue = new BigDecimal(0);
	private int kind = -1;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int center = ScreenUtils.getScreenWidth(App.getContext()) / 2;

		int marginTop = DensityUtils.dp2px(App.getContext(), 80);

		int outCircle = (int) (width / 3); // 外圆半径
		int innerCircle = width / 8;// 内圆半径
		int arc = (int) (width / 4);// 刻度弧形半径

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(30);
		paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		paint.setColor(0Xff616060);
		canvas.drawText("偏低", 50, 50, paint);
		canvas.drawText("正常", getWidth() / 2, 50, paint);
		canvas.drawText("偏高", 50, 100, paint);
		canvas.drawText("目标", getWidth() / 2, 100, paint);
		canvas.drawText("极高", 50, 150, paint);
		paint.setColor(Color.parseColor("#fdbd59"));
		paint.setStyle(Style.FILL_AND_STROKE);
		canvas.drawRect(120, 25, 220, 45, paint);
		paint.setColor(Color.parseColor("#b4f7fd"));
		canvas.drawRect(getWidth() / 2 + 70, 25, getWidth() / 2 + 170, 45,
				paint);
		paint.setColor(0XFFf1aab2);
		canvas.drawRect(120, 75, 220, 95, paint);
		paint.setColor(Color.parseColor("#30d85c"));
		canvas.drawRect(getWidth() / 2 + 70, 75, getWidth() / 2 + 170, 95,
				paint);
		paint.setColor(Color.parseColor("#de4559"));
		canvas.drawRect(120, 125, 220, 145, paint);
		// 阴影
		paint.setColor(0xFFBBBBBB);
		paint.setMaskFilter(new BlurMaskFilter(35, BlurMaskFilter.Blur.NORMAL));
		canvas.drawCircle(center, outCircle + marginTop, outCircle, paint);

		paint.setColor(0XFFfdbd59);
		paint.setMaskFilter(null);

		int more = DensityUtils.dp2px(App.getContext(), 7);
		// 设置个新的长方形，扫描测量 // 左上右下
		RectF oval = new RectF(center - outCircle - more, marginTop - more,
				center + outCircle + more, marginTop + more + outCircle * 2);

		// 画低血糖扇形
		if (value.doubleValue() < 3.9) {
			canvas.drawArc(oval, 270, (float) (value.doubleValue() * 9), true,
					paint); // 起始角度
		} else {
			canvas.drawArc(oval, 270, 35.1F, true, paint); // 起始角度 圆心角度数
			// 画正常血糖扇形
			paint.setColor(0XFFc9f4f8);
			if (value.doubleValue() < 11.1) {
				canvas.drawArc(oval, 305.1F,
						(float) ((value.doubleValue() - 3.9) * 9), true, paint);
			} else {
				canvas.drawArc(oval, 305.1F, 64.8F, true, paint);
				// 画稍高血糖扇形
				paint.setColor(0XFFf1aab2);
				if (value.doubleValue() < 17) {
					canvas.drawArc(oval, 9.9F,
							(float) ((value.doubleValue() - 11.1) * 9), true,
							paint);
				} else {
					canvas.drawArc(oval, 9.9F, 53.1F, true, paint);
					// 画最高血糖扇形
					paint.setColor(0XFFeb6f7d);
					if (value.doubleValue() <= 33.3) {
						canvas.drawArc(oval, 63F,
								(float) ((value.doubleValue() - 17) * 9), true,
								paint);
					} else {
						canvas.drawArc(oval, 63F, 146.7F, true, paint);
					}
				}
			}
		}

		// 圆
		paint.setColor(Color.WHITE);
		canvas.drawCircle(center, marginTop + outCircle, outCircle, paint);
		paint.setTextAlign(Align.CENTER);

		int padding = DensityUtils.dp2px(App.getContext(), 10);// 刻度的padding
		int bigLenth = DensityUtils.dp2px(App.getContext(), 8);// 刻度的长度
		// 刻度
		paint.setTypeface(Typeface.DEFAULT);
		paint.setColor(0XFF333333);
		paint.setStrokeWidth(DensityUtils.px2dp(App.getContext(), 3.5f));

		canvas.drawLine(center, marginTop + padding, center, marginTop
				+ padding + bigLenth, paint);
		canvas.drawLine(center - outCircle + padding, marginTop + outCircle,
				center - outCircle + padding + bigLenth, marginTop + outCircle,
				paint);
		canvas.drawLine(center, marginTop + outCircle * 2 - padding, center,
				marginTop + outCircle * 2 - padding - bigLenth, paint);
		canvas.drawLine(center + outCircle - padding, marginTop + outCircle,
				center + outCircle - padding - bigLenth, marginTop + outCircle,
				paint);

		// 刻度的数字
		paint.setTextSize(30);
		paint.setColor(0Xff616060);
		canvas.drawText("0", center, marginTop + padding + bigLenth * 3, paint);
		canvas.drawText(
				"30",
				center - outCircle + padding + bigLenth * 2,
				marginTop + outCircle + DensityUtils.dp2px(App.getContext(), 5),
				paint);
		canvas.drawText("20", center, marginTop + outCircle * 2 - bigLenth * 3,
				paint);

		canvas.drawText(
				"10",
				center + outCircle - padding - bigLenth * 2,
				marginTop + outCircle + DensityUtils.dp2px(App.getContext(), 5),
				paint);
		paint.setTextSize(40);
		paint.setColor(0XFFac98fe);
		canvas.drawText(
				"R",
				(float) (center + (outCircle - padding - bigLenth)
						* Math.sin(Math.toRadians(34 * 9))),
				(float) (marginTop + outCircle - (outCircle - padding - bigLenth)
						* Math.cos(Math.toRadians(34 * 9))), paint);
		// 画 指针
		paint.setColor(0XFFEE6CAC);
		paint.setStrokeWidth(DensityUtils.dp2px(App.getContext(), 1));
		double d = marginTop + outCircle - (outCircle)
				* Math.cos(Math.toRadians(value.doubleValue() * 9));
		double d1 = center + (outCircle)
				* Math.sin(Math.toRadians(value.doubleValue() * 9));
		canvas.drawLine(center, marginTop + outCircle, (float) d1, (float) d,
				paint);

		paint.setColor(0xFFBBBBBB);
		paint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));
		canvas.drawLine(center - DensityUtils.dp2px(App.getContext(), 3.5f),
				marginTop + outCircle,
				(float) d1 - DensityUtils.dp2px(App.getContext(), 3.5f),
				(float) d, paint);
		paint.setMaskFilter(null);
		// 小刻度
		paint.setColor(0XFF888888);
		paint.setStrokeWidth(1f);
		for (int i = 1; i < 34; i++) {
			if (i % 10 == 0) {
				continue;
			}
			canvas.drawLine(
					(float) (center + arc
							* Math.cos(Math.toRadians(90 - i * 9))),
					(float) (marginTop + outCircle - arc
							* Math.cos(Math.toRadians(i * 9))),
					(float) (center + (arc + 10)
							* Math.cos(Math.toRadians(90 - i * 9))),
					(float) (marginTop + outCircle - (arc + 10)
							* Math.cos(Math.toRadians(i * 9))), paint);
		}
		paint.setColor(0Xaa30d85c);
		// 画目标扇形
		// TODO
		float startangle;
		float centerangle;

		switch (kind) {

			case 0:

				if ("0.0".equals(target.getZero())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getZero().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}

				RectF oval1 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval1, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数
				break;
			case 1:
				if ("0.0".equals(target.getBeforeBreakfast())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getBeforeBreakfast().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}
				RectF oval2 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval2, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数
				break;
			case 2:
				if ("0.0".equals(target.getAfterBreakfast())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getAfterBreakfast().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}
				RectF oval3 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval3, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数
				break;
			case 3:
				if ("0.0".equals(target.getBeforeLunch())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getBeforeLunch().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}
				RectF oval4 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval4, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数
				break;
			case 4:
				if ("0.0".equals(target.getAfterLunch())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getAfterLunch().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}
				RectF oval5 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval5, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数
				break;
			case 5:
				if ("0.0".equals(target.getBeforeSupper())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getBeforeSupper().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}
				RectF oval6 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval6, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数
				break;
			case 6:
				if ("0.0".equals(target.getAfterSupper())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getAfterSupper().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}
				RectF oval7 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval7, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数
				break;
			case 7:
				if ("0.0".equals(target.getBeforeSleep())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getBeforeSleep().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}
				RectF oval8 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval8, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数
				break;
			default:
				if (target == null) {
					break;
				}
				if ("0.0".equals(target.getRandom())) {
					startangle = 305.1f;
					centerangle = 64.8f;
				} else {
					String[] str = target.getRandom().split("~");
					startangle = 270f + Float.parseFloat(str[0]) * 9;
					centerangle = Float.parseFloat(str[1]) * 9
							- Float.parseFloat(str[0]) * 9;
				}
				RectF oval0 = new RectF(center - arc, marginTop + outCircle - arc,
						center + arc, marginTop + outCircle + arc);// 设置个新的长方形，扫描测量
				canvas.drawArc(oval0, startangle, centerangle, true, paint); // 起始角度
				// 圆心角度数

				break;

		}

		// 小圆阴影
		paint.setColor(0xFFDDDDDD);
		paint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
		canvas.drawCircle(center, marginTop + outCircle, innerCircle, paint);// 小圆
		// 小圆
		paint.setColor(Color.WHITE);
		paint.setMaskFilter(null);
		canvas.drawCircle(center, marginTop + outCircle, innerCircle, paint);// 小圆

		// 写数值
		paint.setColor(0xFF555555);
		paint.setTextSize(width / 25);
		String range = "";
		switch (kind) {
			case 0:
				range = target.getZero().equals("0.0") ? "3.9~11.1" : target
						.getZero();
				break;
			case 1:
				range = target.getBeforeBreakfast().equals("0.0") ? "3.9~11.1"
						: target.getBeforeBreakfast();
				break;
			case 2:
				range = target.getAfterBreakfast().equals("0.0") ? "3.9~11.1"
						: target.getAfterBreakfast();
				break;
			case 3:
				range = target.getBeforeLunch().equals("0.0") ? "3.9~11.1" : target
						.getBeforeLunch();
				break;
			case 4:
				range = target.getAfterLunch().equals("0.0") ? "3.9~11.1" : target
						.getAfterLunch();
				break;
			case 5:
				range = target.getBeforeSupper().equals("0.0") ? "3.9~11.1"
						: target.getBeforeSupper();
				break;
			case 6:
				range = target.getAfterSupper().equals("0.0") ? "3.9~11.1" : target
						.getAfterSupper();
				break;
			case 7:
				range = target.getBeforeSleep().equals("0.0") ? "3.9~11.1" : target
						.getBeforeSleep();
				break;
			case 8:
				range = target.getRandom().equals("0.0") ? "3.9~11.1" : target
						.getRandom();
				break;

		}

		canvas.drawText(range, center, marginTop + outCircle - width / 18,
				paint);
		paint.setTextSize(width / 27);
		canvas.drawText("mmol/L", center, marginTop + outCircle, paint);
		paint.setColor(0XFFeb6f7d);
		paint.setTextSize(width / 15);
		canvas.drawText(value.setScale(1, BigDecimal.ROUND_HALF_EVEN)
						.doubleValue() + "", center,
				marginTop + outCircle + width / 12, paint);

		if (value.doubleValue() < targetValue.doubleValue()) {
			if (targetValue.doubleValue() - value.doubleValue() >= 1) {
				value = value.add(new BigDecimal(1));
			} else {
				value = value.add(new BigDecimal(0.1));
			}
			postInvalidateDelayed(25);
		}
		if (value.doubleValue() > targetValue.doubleValue()) {
			if (value.doubleValue() - targetValue.doubleValue() >= 1) {
				value = value.subtract(new BigDecimal(1));
			} else {
				value = value.subtract(new BigDecimal(0.1));
			}
			postInvalidateDelayed(25);
		}
	}

}