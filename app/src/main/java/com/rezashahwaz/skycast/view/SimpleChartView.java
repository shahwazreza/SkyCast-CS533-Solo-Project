// SimpleChartView.java
// Custom View that draws line and bar charts with Canvas, scaled to its size
package com.rezashahwaz.skycast.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SimpleChartView extends View {
   private static final int TYPE_LINE = 0;
   private static final int TYPE_BAR = 1;
   private static final int TARGET_GRIDLINES = 4;

   // one line (or the bars) in a chart
   public static class Series {
      final String name; // shown in the legend
      final double[] values; // NaN values are skipped
      final int color;

      // constructor
      public Series(String name, double[] values, int color) {
         this.name = name;
         this.values = values;
         this.color = color;
      }
   }

   // chart data
   private int type = TYPE_LINE;
   private String[] labels = new String[0]; // x-axis labels
   private final List<Series> seriesList = new ArrayList<>();
   private String valueSuffix = ""; // appended to y labels, e.g. "°"
   private boolean showValues; // draw each value next to its point/bar
   private boolean fixedRange; // use rangeMin/rangeMax instead of the data
   private double rangeMin;
   private double rangeMax;
   private String emptyText = "";

   // drawing tools, created once and reused in onDraw
   private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
   private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
   private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
   private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
   private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
   private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
   private final Path path = new Path();
   private final RectF rect = new RectF();

   // constructors used by code and by XML layouts
   public SimpleChartView(Context context) {
      this(context, null);
   }

   public SimpleChartView(Context context, AttributeSet attrs) {
      super(context, attrs);

      gridPaint.setColor(Color.rgb(224, 224, 224));
      gridPaint.setStrokeWidth(dp(1));
      axisPaint.setColor(Color.rgb(117, 117, 117));
      axisPaint.setStrokeWidth(dp(1.5f));
      labelPaint.setColor(Color.rgb(97, 97, 97));
      labelPaint.setTextSize(sp(12));
      valuePaint.setColor(Color.rgb(33, 33, 33));
      valuePaint.setTextSize(sp(11));
      valuePaint.setTextAlign(Paint.Align.CENTER);
      linePaint.setStyle(Paint.Style.STROKE);
      linePaint.setStrokeWidth(dp(2.5f));
      linePaint.setStrokeJoin(Paint.Join.ROUND);
      linePaint.setStrokeCap(Paint.Cap.ROUND);
      fillPaint.setStyle(Paint.Style.FILL);
   }

   // shows one or more lines; y range is computed from the data
   public void setLineData(String[] labels, List<Series> series,
      String valueSuffix, boolean showValues) {
      this.type = TYPE_LINE;
      this.labels = labels;
      this.seriesList.clear();
      this.seriesList.addAll(series);
      this.valueSuffix = valueSuffix;
      this.showValues = showValues;
      this.fixedRange = false;
      invalidate();
   }

   // shows a bar chart with a fixed y range, e.g. 0-100 for percentages
   public void setBarData(String[] labels, Series series, String valueSuffix,
      double min, double max) {
      this.type = TYPE_BAR;
      this.labels = labels;
      this.seriesList.clear();
      this.seriesList.add(series);
      this.valueSuffix = valueSuffix;
      this.showValues = true;
      this.fixedRange = true;
      this.rangeMin = min;
      this.rangeMax = max;
      invalidate();
   }

   // removes the data and shows a message instead
   public void clear(String emptyText) {
      this.labels = new String[0];
      this.seriesList.clear();
      this.emptyText = emptyText;
      invalidate();
   }

   // draws the whole chart, scaled to the view's current size
   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);

      int count = labels.length;
      double[] range = computeRange();
      if (count == 0 || seriesList.isEmpty() || range == null) {
         labelPaint.setTextAlign(Paint.Align.CENTER);
         canvas.drawText(emptyText, getWidth() / 2f, getHeight() / 2f,
            labelPaint);
         return;
      }
      double min = range[0];
      double max = range[1];
      double step = range[2];

      float textHeight = labelPaint.getTextSize();
      float gap = dp(6);

      // space for the legend (2+ series) and labels outside the plot area
      float legendHeight = (seriesList.size() > 1) ? textHeight + gap * 2 : 0;
      float valueRoom = showValues ? valuePaint.getTextSize() + gap : gap;
      float belowRoom = (showValues && seriesList.size() > 1) ?
         valuePaint.getTextSize() + gap : 0;

      float maxYLabelWidth = 0;
      for (double v = min; v <= max + step / 2; v += step) {
         maxYLabelWidth = Math.max(maxYLabelWidth,
            labelPaint.measureText(formatValue(v)));
      }

      float left = getPaddingLeft() + maxYLabelWidth + gap;
      float right = getWidth() - getPaddingRight() - gap;
      float top = getPaddingTop() + legendHeight + valueRoom;
      float bottom = getHeight() - getPaddingBottom() - textHeight - gap * 2 -
         belowRoom;
      if (right <= left || bottom <= top)
         return; // view too small to draw anything useful

      float plotHeight = bottom - top;
      float slotWidth = (right - left) / count; // one slot per x label

      // horizontal gridlines and y-axis labels
      labelPaint.setTextAlign(Paint.Align.RIGHT);
      for (double v = min; v <= max + step / 2; v += step) {
         float y = toY(v, min, max, top, plotHeight);
         canvas.drawLine(left, y, right, y, gridPaint);
         canvas.drawText(formatValue(v), left - gap, y + textHeight / 3,
            labelPaint);
      }

      // axes
      canvas.drawLine(left, top, left, bottom, axisPaint);
      canvas.drawLine(left, bottom, right, bottom, axisPaint);

      // x-axis labels, skipping some if they would overlap
      float maxXLabelWidth = 0;
      for (String label : labels)
         maxXLabelWidth = Math.max(maxXLabelWidth, labelPaint.measureText(label));
      int labelEvery = Math.max(1,
         (int) Math.ceil((maxXLabelWidth + gap) / slotWidth));
      labelPaint.setTextAlign(Paint.Align.CENTER);
      for (int i = 0; i < count; i += labelEvery) {
         canvas.drawText(labels[i], left + slotWidth * (i + 0.5f),
            bottom + gap + textHeight + belowRoom, labelPaint);
      }

      // data
      if (type == TYPE_BAR)
         drawBars(canvas, min, max, left, top, bottom, plotHeight, slotWidth);
      else
         drawLines(canvas, min, max, left, top, plotHeight, slotWidth);

      if (legendHeight > 0)
         drawLegend(canvas, left, getPaddingTop() + gap + textHeight);
   }

   // draws one bar per value with its value above it
   private void drawBars(Canvas canvas, double min, double max, float left,
      float top, float bottom, float plotHeight, float slotWidth) {
      Series series = seriesList.get(0);
      fillPaint.setColor(series.color);
      float barWidth = slotWidth * 0.6f;

      for (int i = 0; i < series.values.length && i < labels.length; ++i) {
         double value = series.values[i];
         if (Double.isNaN(value))
            continue;
         float centerX = left + slotWidth * (i + 0.5f);
         float y = toY(value, min, max, top, plotHeight);
         rect.set(centerX - barWidth / 2, y, centerX + barWidth / 2, bottom);
         canvas.drawRect(rect, fillPaint);
         canvas.drawText(formatValue(value), centerX, y - dp(4), valuePaint);
      }
   }

   // draws each series as a line with a dot at every data point
   private void drawLines(Canvas canvas, double min, double max, float left,
      float top, float plotHeight, float slotWidth) {
      float radius = (labels.length > 12) ? dp(2) : dp(3.5f);

      for (int s = 0; s < seriesList.size(); ++s) {
         Series series = seriesList.get(s);
         linePaint.setColor(series.color);
         fillPaint.setColor(series.color);
         path.reset();
         boolean penDown = false;

         for (int i = 0; i < series.values.length && i < labels.length; ++i) {
            double value = series.values[i];
            if (Double.isNaN(value)) {
               penDown = false; // leave a gap for missing data
               continue;
            }
            float x = left + slotWidth * (i + 0.5f);
            float y = toY(value, min, max, top, plotHeight);
            if (penDown)
               path.lineTo(x, y);
            else
               path.moveTo(x, y);
            penDown = true;
         }
         canvas.drawPath(path, linePaint);

         // dots and values; the first series' values go above its points,
         // the others' below, so two lines' labels don't collide
         for (int i = 0; i < series.values.length && i < labels.length; ++i) {
            double value = series.values[i];
            if (Double.isNaN(value))
               continue;
            float x = left + slotWidth * (i + 0.5f);
            float y = toY(value, min, max, top, plotHeight);
            canvas.drawCircle(x, y, radius, fillPaint);
            if (showValues) {
               float textY = (s == 0) ? y - dp(8) :
                  y + dp(8) + valuePaint.getTextSize();
               canvas.drawText(formatValue(value), x, textY, valuePaint);
            }
         }
      }
   }

   // draws a color swatch and name for each series, left to right
   private void drawLegend(Canvas canvas, float x, float baseline) {
      float swatch = labelPaint.getTextSize() * 0.8f;
      labelPaint.setTextAlign(Paint.Align.LEFT);

      for (Series series : seriesList) {
         fillPaint.setColor(series.color);
         rect.set(x, baseline - swatch, x + swatch, baseline);
         canvas.drawRect(rect, fillPaint);
         x += swatch + dp(6);
         canvas.drawText(series.name, x, baseline, labelPaint);
         x += labelPaint.measureText(series.name) + dp(16);
      }
   }

   // returns {min, max, step} for the y axis, or null if there's no data;
   // min and max are rounded out to "nice" gridline values
   private double[] computeRange() {
      if (fixedRange) {
         return new double[]{rangeMin, rangeMax,
            (rangeMax - rangeMin) / TARGET_GRIDLINES}; // 0, 25, 50...
      }

      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for (Series series : seriesList) {
         for (double value : series.values) {
            if (!Double.isNaN(value)) {
               min = Math.min(min, value);
               max = Math.max(max, value);
            }
         }
      }
      if (min > max)
         return null; // every value is missing

      double step = niceStep((max - min) / TARGET_GRIDLINES);
      double niceMin = Math.floor(min / step) * step;
      double niceMax = Math.ceil(max / step) * step;
      if (niceMax - niceMin < step) // flat line: give it some room
         niceMax = niceMin + step;
      return new double[]{niceMin, niceMax, step};
   }

   // rounds a raw gridline step to 1, 2, 5 or 10 times a power of ten
   static double niceStep(double rawStep) {
      if (rawStep <= 0 || Double.isNaN(rawStep))
         return 1;
      double magnitude = Math.pow(10, Math.floor(Math.log10(rawStep)));
      double fraction = rawStep / magnitude;
      double nice = (fraction <= 1) ? 1 : (fraction <= 2) ? 2 :
         (fraction <= 5) ? 5 : 10;
      return Math.max(1, nice * magnitude); // whole degrees/percent only
   }

   // converts a data value to a y coordinate inside the plot area
   private static float toY(double value, double min, double max, float top,
      float plotHeight) {
      return (float) (top + plotHeight * (1 - (value - min) / (max - min)));
   }

   // formats a value as a whole number plus the suffix, e.g. "72°"
   private String formatValue(double value) {
      long rounded = Math.round(value);
      return (rounded == 0 ? 0 : rounded) + valueSuffix;
   }

   // converts dp to pixels
   private float dp(float value) {
      return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
         getResources().getDisplayMetrics());
   }

   // converts sp to pixels, so labels follow the user's font size
   private float sp(float value) {
      return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,
         getResources().getDisplayMetrics());
   }
}
