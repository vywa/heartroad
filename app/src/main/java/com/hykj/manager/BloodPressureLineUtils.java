package com.hykj.manager;

import java.text.SimpleDateFormat;
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
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
import org.achartengine.renderer.support.SupportColorLevel;
import org.achartengine.renderer.support.SupportSeriesRender;
import org.achartengine.renderer.support.SupportTargetLineStyle;
import org.achartengine.renderer.support.SupportXAlign;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import com.hykj.App;
import com.hykj.entity.BloodPressureInfo;
import com.hykj.utils.DensityUtils;

public class BloodPressureLineUtils {

	/*public static final int COLOR_HIGH = Color.parseColor("#FFFFFF");
	public static final int COLOR_NORMAL = Color.parseColor("#33CC33");
	public static final int COLOR_LOW = Color.parseColor("#FF2A00");*/
	ArrayList<BloodPressureInfo> pressureInfos = new ArrayList<BloodPressureInfo>();
	private int lowBPValue;
	private int highBPValue;

	public BloodPressureLineUtils(Context context, ArrayList<BloodPressureInfo> pressureInfos,int lowBPValue,int highBPValue) {
		mContext = context;
		mXYRenderer = new XYMultipleSeriesRenderer();
		this.lowBPValue = lowBPValue;
		this.highBPValue = highBPValue;
		initRender();
		this.pressureInfos.addAll(pressureInfos);
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
		
		return renderer;
	}

	public View initCubicLineGraphView() {
		mXYMultipleSeriesDataSet = new XYMultipleSeriesDataset();

		final SupportSeriesRender supportSeriesRender = new SupportSeriesRender();
		supportSeriesRender.setLineColor(Color.parseColor("#F8DE7D"));
		supportSeriesRender.setColorLevelValid(true);
		ArrayList<SupportColorLevel> list = new ArrayList<SupportColorLevel>();
		SupportColorLevel supportColorLevel_a = new SupportColorLevel(0, highBPValue, Color.parseColor("#6EA526"));
		SupportColorLevel supportColorLevel_b = new SupportColorLevel(highBPValue, 320, Color.parseColor("#E04459"));
		list.add(supportColorLevel_a);
		list.add(supportColorLevel_b);
		supportSeriesRender.setColorLevelList(list);
		
		final SupportSeriesRender supportSeriesRender1 = new SupportSeriesRender();
		supportSeriesRender1.setLineColor(Color.parseColor("#F8DE7D"));
		supportSeriesRender1.setColorLevelValid(true);
		ArrayList<SupportColorLevel> list1 = new ArrayList<SupportColorLevel>();
		SupportColorLevel supportColorLevel_c = new SupportColorLevel(0, lowBPValue, Color.parseColor("#6EA526"));
		SupportColorLevel supportColorLevel_d = new SupportColorLevel(lowBPValue, 320, Color.parseColor("#E04459"));
		list1.add(supportColorLevel_c);
		list1.add(supportColorLevel_d);
		supportSeriesRender1.setColorLevelList(list1);
		
		TimeSeries highSeries = new TimeSeries("血压正常");
		for (BloodPressureInfo info : pressureInfos) {
			highSeries.add(new Date(info.getMeasureTime()), info.getHighBP());
		}
		
		TimeSeries lowSeries = new TimeSeries("血压偏高");
		for (BloodPressureInfo info : pressureInfos) {
			lowSeries.add(new Date(info.getMeasureTime()), info.getLowBP());
		}

		mXYRenderer.addSupportRenderer(supportSeriesRender);
		mXYRenderer.addSupportRenderer(supportSeriesRender1);
		
		mXYRenderer.setXAxisMin(getMinTime(pressureInfos));
		mXYRenderer.setXAxisMax(getMaxTime(pressureInfos));
		
		mXYMultipleSeriesDataSet.addSeries(highSeries);
		mXYMultipleSeriesDataSet.addSeries(lowSeries);
		
		GraphicalView view = ChartFactory.getSupportCubeLineChartView(mContext, mXYMultipleSeriesDataSet, mXYRenderer, 0, "yy年MM月dd日 HH:mm");

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GraphicalView graphicalView = (GraphicalView) v;
				graphicalView.handPointClickEvent(supportSeriesRender, "nei rong");
				graphicalView.setOnDescriptionChangeListener(new DescriptionChangeListener() {
					@Override
					public String getChangedDescription(int index) {

						BloodPressureInfo info = pressureInfos.get(index);

						StringBuilder builder = new StringBuilder();

						builder.append("测量时间:");

						SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

						builder.append(format.format(new Date(info.getMeasureTime())) + "\n");

						builder.append("高压"+ info.getHighBP()+"mmHg\t");
						builder.append("低压"+ info.getLowBP()+"mmHg\t");
						builder.append("心率"+ info.getHeartRate()+"次/min");
						
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

		/*mXYRenderer.setChartTitle("血压测量记录");
		mXYRenderer.setXTitle("测量时间");*/
		mXYRenderer.setYTitle("mmHg");

		// 设置字体的大小
		mXYRenderer.setAxisTitleTextSize(textSize);
		mXYRenderer.setChartTitleTextSize(textSize);
		mXYRenderer.setLabelsTextSize(textSize);
		mXYRenderer.setLegendTextSize(DensityUtils.dp2px(App.getContext(), 7f));

		// 设置背景颜色
		mXYRenderer.setBackgroundColor(Color.parseColor("#FFFFFF"));
		// 设置页边空白的颜色
		mXYRenderer.setMarginsColor(Color.parseColor("#FFFFFF"));
		// 设置背景颜色生效
		mXYRenderer.setApplyBackgroundColor(true);

		// 设置坐标轴,轴的颜色
		mXYRenderer.setXLabelsColor(Color.parseColor("#6E5F60"));
		mXYRenderer.setYLabelsColor(0, Color.parseColor("#6E5F60"));
		// 设置网格的颜色
		mXYRenderer.setShowGrid(false);
		mXYRenderer.addSeriesRenderer(getSimpleSeriesRender(Color.parseColor("#E04459")));
		mXYRenderer.addSeriesRenderer(getSimpleSeriesRender(Color.parseColor("#6EA526")));

		// 设置合适的刻度,在轴上显示的数量是 MAX / labels
		mXYRenderer.setYLabels(6);
		mXYRenderer.setXLabels(5); // 设置 X 轴不显示数字（改用我们手动添加的文字标签）

		mXYRenderer.setMargins(new int[] { 30, 50, 30, 0 });//设置图表的外边框(上/左/下/右)

		mXYRenderer.setYAxisMin(0);
		mXYRenderer.setYAxisMax(320);

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

		mXYRenderer.addTargetValue(highBPValue);
		mXYRenderer.addTargetLineColor(Color.parseColor("#30d85c"));
		mXYRenderer.addTargetDescription("正常高压最大值"+highBPValue);

		// 设置第二个
		mXYRenderer.addTargetValue(lowBPValue);
		mXYRenderer.addTargetLineColor(Color.parseColor("#30d85c"));
		mXYRenderer.addTargetDescription("正常低压最大值"+lowBPValue);

		mXYRenderer.setFitLegend(true);
		mXYRenderer.setPointSize(DensityUtils.dp2px(App.getContext(), 5f));

		mXYRenderer.setSupportXAlign(SupportXAlign.RIGHT);
	}

	private long getMaxTime(ArrayList<BloodPressureInfo> pressureInfos) {
		if (pressureInfos == null || pressureInfos.size() == 0) {
			return 0;
		}
		if (pressureInfos.size() == 1) {
			return pressureInfos.get(0).getMeasureTime() +2*3600*1000;
		}
		if (pressureInfos.size() < 15) {
			return pressureInfos.get(pressureInfos.size()-1).getMeasureTime();
		}
		return pressureInfos.get(14).getMeasureTime();
	}

	private long getMinTime(ArrayList<BloodPressureInfo> pressureInfos) {
		if (pressureInfos == null || pressureInfos.size() == 0) {
			return 0;
		}
		if (pressureInfos.size() == 1) {
			return pressureInfos.get(0).getMeasureTime() -2*3600*1000;
		}
		return pressureInfos.get(0).getMeasureTime();
	}

}
