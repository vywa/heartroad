package com.hykj.manager;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.GraphicalView.DescriptionChangeListener;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.support.SupportColorLevel;
import org.achartengine.renderer.support.SupportSeriesRender;
import org.achartengine.renderer.support.SupportTargetLineStyle;
import org.achartengine.renderer.support.SupportXAlign;
import org.achartengine.renderer.support.SupportYAlign;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.hykj.App;
import com.hykj.entity.BloodSugarInfo;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.TimeUtil;

public class BloodSugarLineUtils {

	ArrayList<BloodSugarInfo> sugarInfos = new ArrayList<BloodSugarInfo>();

	private double minValue;
	private double maxBeforeMealValue;
	private double maxAfterMealValue;

	public BloodSugarLineUtils(Context context, ArrayList<BloodSugarInfo> sugarInfos, double minValue, double maxBeforeMealValue, double maxAfterMealValue) {
		mContext = context;
		mXYRenderer = new XYMultipleSeriesRenderer();
		this.minValue = minValue;
		this.maxAfterMealValue = maxAfterMealValue;
		this.maxBeforeMealValue = maxBeforeMealValue;
		initRender();
		this.sugarInfos.addAll(sugarInfos);
	}

	protected XYSeriesRenderer getSimpleSeriesRender(int color) {
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(color);
		renderer.setFillPoints(true); // 是否是实心的点
		renderer.setDisplayChartValues(true); // 设置是否在点上显示数据
		renderer.setLineWidth(DensityUtils.dp2px(App.getContext(), 1.5f)); // 设置曲线的宽度
		renderer.setPointStyle(PointStyle.CIRCLE);
        
		renderer.setChartValuesTextSize(DensityUtils.dp2px(App.getContext(), 12f));
		renderer.setChartValuesSpacing(DensityUtils.dp2px(App.getContext(), 10f));

		// FillOutsideLine fill = new
		// FillOutsideLine(FillOutsideLine.Type.BELOW);
		// renderer.addFillOutsideLine(fill);

		// FillOutsideLine fill = new
		// FillOutsideLine(FillOutsideLine.Type.ABOVE);
		// renderer.addFillOutsideLine(fill);

		// FillOutsideLine fill = new
		// FillOutsideLine(FillOutsideLine.Type.BOUNDS_ABOVE);
		// fill.setColor(Color.argb(255, 0, 200, 100));
		// fill.setFillRange(new int[] {10, 19});
		// renderer.addFillOutsideLine(fill);

		return renderer;
	}

	public View initCubicLineGraphView() {
		mXYMultipleSeriesDataSet = new XYMultipleSeriesDataset();

		// 扩展的属性
		final SupportSeriesRender supportSeriesRender = new SupportSeriesRender();
		// 设置曲线的颜色
		supportSeriesRender.setLineColor(Color.parseColor("#F8DE7D"));

		supportSeriesRender.setColorLevelValid(true);
		ArrayList<SupportColorLevel> list = new ArrayList<SupportColorLevel>();

		SupportColorLevel supportColorLevel_a = new SupportColorLevel(0, minValue, Color.parseColor("#E04459"));
		SupportColorLevel supportColorLevel_b = new SupportColorLevel(minValue, maxAfterMealValue, Color.parseColor("#6EA526"));
		SupportColorLevel supportColorLevel_c = new SupportColorLevel(maxAfterMealValue, 36, Color.parseColor("#E04459"));

		list.add(supportColorLevel_a);
		list.add(supportColorLevel_b);
		list.add(supportColorLevel_c);
		
		supportSeriesRender.setColorLevelList(list);

		// 设置曲线颜色为渐变色，默认的渐变色从上到下，3色渐变
		// supportSeriesRender.setShapeLineColor(new int[] { COLOR_HIGH,
		// COLOR_HIGH, COLOR_LOW, COLOR_LOW });

		TimeSeries series = new TimeSeries("血糖正常");
		
		for (BloodSugarInfo info : sugarInfos) {
			series.add(new Date(info.getMeasureTime()), info.getBsValue());
		}

		mXYRenderer.addSupportRenderer(supportSeriesRender);
		
		SupportSeriesRender supportSeriesRender1 = new SupportSeriesRender();
		supportSeriesRender1.setLineColor(Color.parseColor("#F8DE7D"));
		mXYRenderer.addSupportRenderer(supportSeriesRender1);

		mXYRenderer.setXAxisMin(getMinTime(sugarInfos));
		mXYRenderer.setXAxisMax(getMaxTime(sugarInfos));

		mXYMultipleSeriesDataSet.addSeries(series);
		
		TimeSeries series1 = new TimeSeries("血糖异常");
		mXYMultipleSeriesDataSet.addSeries(series1);
		
		GraphicalView view = ChartFactory.getSupportCubeLineChartView(mContext, mXYMultipleSeriesDataSet, mXYRenderer, 0, "yy年MM月dd日 HH:mm");

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GraphicalView graphicalView = (GraphicalView) v;
				graphicalView.handPointClickEvent(supportSeriesRender, "nei rong");
				graphicalView.setOnDescriptionChangeListener(new DescriptionChangeListener() {
					@Override
					public String getChangedDescription(int index) {

						BloodSugarInfo info = sugarInfos.get(index);

						StringBuilder builder = new StringBuilder();

						builder.append("测量时间:");

						builder.append(TimeUtil.getStringTime(info.getMeasureTime()) + "\n");

						switch (info.getMeasureType()) {
						case 0:
							builder.append("凌晨测量");
							break;
						case 1:
							builder.append("早餐前测量");
							break;
						case 2:
							builder.append("早餐后测量");
							break;
						case 3:
							builder.append("午餐前测量");
							break;
						case 4:
							builder.append("午餐后测量");
							break;
						case 5:
							builder.append("晚餐前测量");
							break;
						case 6:
							builder.append("晚餐后测量");
							break;
						case 7:
							builder.append("睡前测量");
							break;
						case 8:
							builder.append("随机测量");
							break;
						}

						builder.append("\t测量值:" + info.getBsValue());
						return builder.toString();
					}
				});
			}
		});
		return view;
	}

	protected Context mContext;
	protected XYMultipleSeriesRenderer mXYRenderer;
	protected XYMultipleSeriesDataset mXYMultipleSeriesDataSet;

	private void initRender() {
		// LogUtils.LOGE(TAG,"initRender");
		// 1, 构造显示用渲染图
		float textSize = DensityUtils.dp2px(App.getContext(), 10f);

		// mXYRenderer.setChartTitle("血糖测量记录");
//		mXYRenderer.setXTitle("测量时间");
		mXYRenderer.setYTitle("mmol/L");

		// 设置字体的大小
		mXYRenderer.setAxisTitleTextSize(textSize);
		mXYRenderer.setChartTitleTextSize(textSize);
		mXYRenderer.setLabelsTextSize(textSize);
		mXYRenderer.setLegendTextSize(DensityUtils.dp2px(App.getContext(), 7f));

		// 设置背景颜色
		// mXYRenderer.setBackgroundColor(Color.parseColor("#4f525d"));
		mXYRenderer.setBackgroundColor(Color.parseColor("#FFFFFF"));
		// 设置页边空白的颜色
		// mXYRenderer.setMarginsColor(Color.parseColor("#4f525d"));
		mXYRenderer.setMarginsColor(Color.parseColor("#FFFFFF"));

		// 设置背景颜色生效
		mXYRenderer.setApplyBackgroundColor(true);

		// 设置坐标轴,轴的颜色
		// mXYRenderer.setXLabelsColor(Color.parseColor("#797C7E"));
		// mXYRenderer.setYLabelsColor(0, Color.parseColor("#797C7E"));
		mXYRenderer.setXLabelsColor(Color.parseColor("#6E5F60"));
		mXYRenderer.setYLabelsColor(0, Color.parseColor("#6E5F60"));
		// 设置网格的颜色
		mXYRenderer.setShowGrid(false);
		mXYRenderer.addSeriesRenderer(getSimpleSeriesRender(Color.parseColor("#6EA526")));
		mXYRenderer.addSeriesRenderer(getSimpleSeriesRender(Color.parseColor("#E04459")));

		// 设置合适的刻度,在轴上显示的数量是 MAX / labels
		mXYRenderer.setYLabels(6);
		mXYRenderer.setXLabels(5); // 设置 X 轴不显示数字（改用我们手动添加的文字标签）

		mXYRenderer.setMargins(new int[] { 30, 50, 30, 0 });// 设置图表的外边框(上/左/下/右)

		mXYRenderer.setYAxisMin(0);
		mXYRenderer.setYAxisMax(36);

		// 设置x,y轴显示的排列,默认是 Align.CENTER
		mXYRenderer.setXLabelsAlign(Paint.Align.CENTER);
		mXYRenderer.setYLabelsAlign(Paint.Align.RIGHT);

		mXYRenderer.setAntialiasing(true);
		mXYRenderer.setFitLegend(true);
		mXYRenderer.setShowAxes(true); // 设置坐标轴是否可见
		mXYRenderer.setShowLegend(true);

		// 设置x,y轴上的刻度的颜色
		// 设置是否显示,坐标轴的轴,默认为 true
		// 是否支持图表移动
		mXYRenderer.setPanEnabled(true, false);// 表盘移动
		mXYRenderer.setZoomEnabled(true, false);
		// TODO
		mXYRenderer.setClickEnabled(true);// 是否可点击
		mXYRenderer.setSelectableBuffer(50);// 点击区域大小

		// 设置目标线
		mXYRenderer.setTargetLineVisible(true);
		mXYRenderer.setTargetLineStyle(SupportTargetLineStyle.Line_Dotted);

		mXYRenderer.addTargetValue((float) maxAfterMealValue);
		// mXYRenderer.addTargetLineColor(Color.parseColor("#f58494"));
		mXYRenderer.addTargetLineColor(Color.parseColor("#30d85c"));
		mXYRenderer.addTargetDescription("餐后血糖最高值:" + maxAfterMealValue + "mmol/L");

		// 设置第二个
//		mXYRenderer.addTargetValue((float) maxBeforeMealValue);
		// mXYRenderer.addTargetLineColor(Color.parseColor("#0bb5c3"));
//		mXYRenderer.addTargetLineColor(Color.parseColor("#6EA526"));
//		mXYRenderer.addTargetDescription("空腹血糖最高值:" + maxBeforeMealValue + "mmol/L");

		// 顺序很重要！！！！！！！！！！！！！！！
		// 设置第三个
		mXYRenderer.addTargetValue((float) minValue);
		// mXYRenderer.addTargetLineColor(Color.parseColor("#f58494"));
		mXYRenderer.addTargetLineColor(Color.parseColor("#30d85c"));
		mXYRenderer.addTargetDescription("血糖最低值:" + minValue + "mmol/L");

		mXYRenderer.setFitLegend(true);

		mXYRenderer.setPointSize(DensityUtils.dp2px(App.getContext(), 5f));

		mXYRenderer.setSupportXAlign(SupportXAlign.RIGHT);
		mXYRenderer.setSupportYAlign(SupportYAlign.CENTER);
	}

	private double getMaxTime(ArrayList<BloodSugarInfo> sugarInfos) {
		if (sugarInfos == null || sugarInfos.size() == 0) {
			return 0;
		}
		if (sugarInfos.size() == 1) {
			return sugarInfos.get(0).getMeasureTime() + 2 * 3600 * 1000;
		}
		if (sugarInfos.size() < 15) {
			return sugarInfos.get(sugarInfos.size() - 1).getMeasureTime();
		}
		return sugarInfos.get(14).getMeasureTime();
	}

	private double getMinTime(ArrayList<BloodSugarInfo> sugarInfos) {
		if (sugarInfos == null || sugarInfos.size() == 0) {
			return 0;
		}
		if (sugarInfos.size() == 1) {
			return sugarInfos.get(0).getMeasureTime() - 2 * 3600 * 1000;
		}
		return sugarInfos.get(0).getMeasureTime();
	}

}
