/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.chart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.model.Point;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.hykj.App;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.View;

/**
 * The interpolated (cubic) line chart rendering class.
 * 会在原有的CubicLine的基础上进行功能的扩展
 */
public class SupportCubicLineChart extends LineChart {
    /**
     * The chart type.
     */
    public static final String TYPE = "Cubic";

    private float firstMultiplier;

    private float secondMultiplier;

    private Point p1 = new Point();

    private Point p2 = new Point();

    private Point p3 = new Point();
    
    private String mFormat;


    public SupportCubicLineChart() {

        // default is to have first control point at about 33% of the distance,
        firstMultiplier = 0.33f;
        // and the next at 66% of the distance.
        secondMultiplier = 1 - firstMultiplier;
    }

    /**
     * Builds a cubic line chart.
     *
     * @param dataset    the dataset
     * @param renderer   the renderer
     * @param smoothness smoothness determines how smooth the curve should be,
     *                   range [0->0.5] super smooth, 0.5, means that it might not get
     *                   close to control points if you have random data // less smooth,
     *                   (close to 0) means that it will most likely touch all control //
     *                   points
     */
    public SupportCubicLineChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer,
                                 float smoothness,String format) {
        super(dataset, renderer);
        firstMultiplier = smoothness;
        secondMultiplier = 1 - firstMultiplier;
        this.mFormat = format;
    }

    @Override
    protected void drawPath(Canvas canvas, List<Float> points, Paint paint, boolean circular) {
        Path path = new Path();
        Path oPath = new Path();
        
        float x = points.get(0);
        float y = points.get(1);
        path.moveTo(x, y);
        
        float oX = points.get(0);
        float oy = points.get(1)+10;
        oPath.moveTo(oX, oy);

        int length = points.size();
        if (circular) {
            length -= 4;
        }

        for (int i = 0; i < length; i += 2) {
            int nextIndex = i + 2 < length ? i + 2 : i;
            int nextNextIndex = i + 4 < length ? i + 4 : nextIndex;
            calc(points, p1, i, nextIndex, secondMultiplier);
            p2.setX(points.get(nextIndex));
            p2.setY(points.get(nextIndex + 1));
            calc(points, p3, nextIndex, nextNextIndex, firstMultiplier);
            // From last point, approaching x1/y1 and x2/y2 and ends up at x3/y3
            path.cubicTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
            oPath.cubicTo(p1.getX(), p1.getY()+10, p2.getX(), p2.getY()+10, p3.getX(), p3.getY()+10);
        }
        if (circular) {
            for (int i = length; i < length + 4; i += 2) {
                path.lineTo(points.get(i), points.get(i + 1));
                oPath.lineTo(points.get(i), points.get(i + 1)+10);
            }
            path.lineTo(points.get(0), points.get(1));
            oPath.lineTo(points.get(0), points.get(1)+10);
        }

        paint.setAntiAlias(true);
       
        // 开始为曲线设置颜色，渐变等属性  cvte.wangjia
        //TODO
        if (supportSeriesRender.isSupportShapeLineColor()) {
            paint.setShader(null);
            Shader mShader = null;
            if (supportSeriesRender.getCount() != 0) { //相当于一个标记 判断是血压 还是 血糖
                if (supportSeriesRender.getCount() == 1) {
                	mShader = new LinearGradient(0, 0, 0, canvas.getHeight(),supportSeriesRender.getShapeLineColor(),new float[]{0f,0.45f,0.8f,1f}, Shader.TileMode.CLAMP);
				}
			}else{
				mShader = new LinearGradient(0, 0, 0, canvas.getHeight(),supportSeriesRender.getShapeLineColor(),new float[]{0f,0.45f,0.8f,1f}, Shader.TileMode.CLAMP);
			}
            
            paint.setShader(mShader);

        } else {
            paint.setColor(supportSeriesRender.getLineColor());
        }
        
        //画阴影
        paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
       // paint.setShader(new LinearGradient(0, 0, 0, canvas.getHeight(),new int[]{0XFF000000,0XFFAAAAAA},new float[]{0f,1f}, Shader.TileMode.CLAMP));
        canvas.drawPath(oPath, paint);
        paint.setMaskFilter(null);
//        paint.setShader(null);
        canvas.drawPath(path, paint);
        paint.setShader(null);
        
    }

    private void calc(List<Float> points, Point result, int index1, int index2, final float multiplier) {
        float p1x = points.get(index1);
        float p1y = points.get(index1 + 1);
        float p2x = points.get(index2);
        float p2y = points.get(index2 + 1);

        float diffX = p2x - p1x; // p2.x - p1.x;
        float diffY = p2y - p1y; // p2.y - p1.y;
        result.setX(p1x + (diffX * multiplier));
        result.setY(p1y + (diffY * multiplier));
    }

    /**
     * Returns the chart type identifier.
     *
     * @return the chart type
     */
    public String getChartType() {
        return TYPE;
    }
    
    @Override
    protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas,
        Paint paint, int left, int top, int bottom, double xPixelsPerUnit, double minX, double maxX) {
      int length = xLabels.size();
      if (length > 0) {
        boolean showLabels = mRenderer.isShowLabels();
        boolean showGridY = mRenderer.isShowGridY();
        DateFormat format =  new SimpleDateFormat(mFormat);
        for (int i = 0; i < length; i++) {
          long label = Math.round(xLabels.get(i));
          float xLabel = (float) (left + xPixelsPerUnit * (label - minX));
          if (showLabels) {
            paint.setColor(mRenderer.getXLabelsColor());
            canvas
                .drawLine(xLabel, bottom, xLabel, bottom + mRenderer.getLabelsTextSize() / 3, paint);
            drawText(canvas, format.format(new Date(label)), xLabel,
                bottom + mRenderer.getLabelsTextSize() * 4 / 3 + mRenderer.getXLabelsPadding(), paint, mRenderer.getXLabelsAngle());
          }
          if (showGridY) {
            paint.setColor(mRenderer.getGridColor(0));
            canvas.drawLine(xLabel, bottom, xLabel, top, paint);
          }
        }
      }
      drawXTextLabels(xTextLabelLocations, canvas, paint, true, left, top, bottom, xPixelsPerUnit,minX, maxX);
    }
    
    @Override
    protected List<Double> getXLabels(double min, double max, int count) {
      final List<Double> result = new ArrayList<Double>();
      if (!mRenderer.isXRoundedLabels()) {
        if (mDataset.getSeriesCount() > 0) {
          XYSeries series = mDataset.getSeriesAt(0);
          int length = series.getItemCount();
          int intervalLength = 0;
          int startIndex = -1;
          for (int i = 0; i < length; i++) {
            double value = series.getX(i);
            if (min <= value && value <= max) {
              intervalLength++;
              if (startIndex < 0) {
                startIndex = i;
              }
            }
          }
          if (intervalLength < count) {
            for (int i = startIndex; i < startIndex + intervalLength; i++) {
              result.add(series.getX(i));
            }
          } else {
            float step = (float) intervalLength / count;
            int intervalCount = 0;
            for (int i = 0; i < length && intervalCount < count; i++) {
              double value = series.getX(Math.round(i * step));
              if (min <= value && value <= max) {
                result.add(value);
                intervalCount++;
              }
            }
          }
          return result;
        } else {
          return super.getXLabels(min, max, count);
        }
      }
      if (mStartPoint == null) {
        mStartPoint = min - (min % DAY) + DAY + new Date(Math.round(min)).getTimezoneOffset() * 60
            * 1000;
      }
      if (count > 25) {
        count = 25;
      }

      
      final double cycleMath = (max - min) / count;
      if (cycleMath <= 0) {
        return result;
      }
      double cycle = DAY;

      if (cycleMath <= DAY) {
        while (cycleMath < cycle / 2) {
          cycle = cycle / 2;
        }
      } else {
        while (cycleMath > cycle) {
          cycle = cycle * 2;
        }
      }

      double val = mStartPoint - Math.floor((mStartPoint - min) / cycle) * cycle;
      int i = 0;
      while (val < max && i++ <= count) {
        result.add(val);
        val += cycle;
      }

      return result;
    }
    
    /** The number of milliseconds in a day. */
    public static final long DAY = 24 * 60 * 60 * 1000;
    /** The date format pattern to be used in formatting the X axis labels. */
    private String mDateFormat;
    /** The starting point for labels. */
    private Double mStartPoint;

}
