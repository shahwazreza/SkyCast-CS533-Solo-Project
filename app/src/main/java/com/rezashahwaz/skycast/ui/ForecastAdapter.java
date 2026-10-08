// ForecastAdapter.java
// RecyclerView adapter: a header with the two 7-day charts, then one row per day
package com.rezashahwaz.skycast.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.DailyForecast;
import com.rezashahwaz.skycast.data.Forecast;
import com.rezashahwaz.skycast.data.WeatherResult;
import com.rezashahwaz.skycast.util.Formatters;
import com.rezashahwaz.skycast.util.WeatherCodes;
import com.rezashahwaz.skycast.view.SimpleChartView;

import java.util.Arrays;
import java.util.List;

public class ForecastAdapter
   extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   private static final int TYPE_HEADER = 0;
   private static final int TYPE_DAY = 1;

   private WeatherResult result; // null when there's nothing to show

   // ViewHolder for the charts at the top of the list
   static class HeaderViewHolder extends RecyclerView.ViewHolder {
      final TextView offlineBanner;
      final SimpleChartView temperatureChart;
      final SimpleChartView rainChart;

      HeaderViewHolder(View itemView) {
         super(itemView);
         offlineBanner = (TextView) itemView.findViewById(R.id.offlineBanner);
         temperatureChart =
            (SimpleChartView) itemView.findViewById(R.id.temperatureChart);
         rainChart = (SimpleChartView) itemView.findViewById(R.id.rainChart);
      }
   }

   // ViewHolder for one day's row
   static class DayViewHolder extends RecyclerView.ViewHolder {
      final TextView dayTextView;
      final TextView iconTextView;
      final TextView descriptionTextView;
      final TextView highLowTextView;

      DayViewHolder(View itemView) {
         super(itemView);
         dayTextView = (TextView) itemView.findViewById(R.id.dayTextView);
         iconTextView = (TextView) itemView.findViewById(R.id.iconTextView);
         descriptionTextView =
            (TextView) itemView.findViewById(R.id.descriptionTextView);
         highLowTextView =
            (TextView) itemView.findViewById(R.id.highLowTextView);
      }
   }

   // replaces the forecast being shown
   public void setResult(WeatherResult result) {
      this.result = result;
      notifyDataSetChanged();
   }

   @Override
   public int getItemCount() {
      return (result == null) ? 0 : 1 + result.forecast.daily.size();
   }

   @Override
   public int getItemViewType(int position) {
      return (position == 0) ? TYPE_HEADER : TYPE_DAY;
   }

   // inflates the header or a day row
   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      if (viewType == TYPE_HEADER) {
         return new HeaderViewHolder(inflater.inflate(
            R.layout.forecast_header, parent, false));
      }
      return new DayViewHolder(
         inflater.inflate(R.layout.list_item_day, parent, false));
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      if (holder instanceof HeaderViewHolder)
         bindHeader((HeaderViewHolder) holder);
      else
         bindDay((DayViewHolder) holder,
            result.forecast.daily.get(position - 1), position == 1);
   }

   // draws the high/low line chart and the rain bar chart
   private void bindHeader(HeaderViewHolder holder) {
      Context context = holder.itemView.getContext();
      List<DailyForecast> days = result.forecast.daily;

      holder.offlineBanner.setVisibility(
         result.fromCache ? View.VISIBLE : View.GONE);
      holder.offlineBanner.setText(context.getString(R.string.offline_banner,
         Formatters.formatTimestamp(result.fetchedAt)));

      String[] labels = new String[days.size()];
      double[] highs = new double[days.size()];
      double[] lows = new double[days.size()];
      double[] rain = new double[days.size()];
      for (int i = 0; i < days.size(); ++i) {
         DailyForecast day = days.get(i);
         labels[i] = (i == 0) ? context.getString(R.string.today) :
            Formatters.formatDayShort(day.date);
         highs[i] = day.maxTemp;
         lows[i] = day.minTemp;
         rain[i] = day.rainChance;
      }

      String degree = context.getString(R.string.degree);
      holder.temperatureChart.setLineData(labels, Arrays.asList(
         new SimpleChartView.Series(context.getString(R.string.legend_high),
            highs, context.getColor(R.color.chartHigh)),
         new SimpleChartView.Series(context.getString(R.string.legend_low),
            lows, context.getColor(R.color.chartLow))), degree, true);

      holder.rainChart.setBarData(labels, new SimpleChartView.Series(
         context.getString(R.string.legend_rain), rain,
         context.getColor(R.color.chartRain)),
         context.getString(R.string.percent), 0, 100);
   }

   // fills in one day's row
   private void bindDay(DayViewHolder holder, DailyForecast day,
      boolean isToday) {
      Context context = holder.itemView.getContext();
      String description =
         context.getString(WeatherCodes.getDescription(day.weatherCode));

      holder.dayTextView.setText(isToday ?
         context.getString(R.string.today) :
         Formatters.formatDayLong(day.date));
      holder.iconTextView.setText(WeatherCodes.getIcon(day.weatherCode, true));
      holder.iconTextView.setContentDescription(description);
      holder.descriptionTextView.setText(description);
      holder.highLowTextView.setText(context.getString(R.string.high_low,
         Formatters.formatNumber(day.maxTemp),
         Formatters.formatNumber(day.minTemp)));
   }
}
