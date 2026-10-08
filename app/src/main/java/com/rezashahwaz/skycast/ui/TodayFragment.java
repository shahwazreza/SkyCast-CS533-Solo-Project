// TodayFragment.java
// Shows current conditions, the favorite toggle and a 24-hour temperature chart
package com.rezashahwaz.skycast.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.City;
import com.rezashahwaz.skycast.data.DailyForecast;
import com.rezashahwaz.skycast.data.Forecast;
import com.rezashahwaz.skycast.data.WeatherResult;
import com.rezashahwaz.skycast.util.Formatters;
import com.rezashahwaz.skycast.util.Units;
import com.rezashahwaz.skycast.util.WeatherCodes;
import com.rezashahwaz.skycast.view.SimpleChartView;

import java.util.Collections;

public class TodayFragment extends WeatherFragment {
   private static final int CHART_HOURS = 24;

   private SwipeRefreshLayout swipeRefreshLayout;
   private TextView offlineBanner;
   private TextView errorTextView;
   private TextView cityTextView;
   private TextView regionTextView;
   private View weatherContent; // everything shown only when data loaded
   private TextView updatedTextView;
   private TextView iconTextView;
   private TextView temperatureTextView;
   private TextView descriptionTextView;
   private TextView feelsLikeTextView;
   private TextView humidityTextView;
   private TextView windTextView;
   private TextView sunriseTextView;
   private TextView sunsetTextView;
   private Button favoriteButton;
   private SimpleChartView hourlyChart;

   // inflates the layout and wires up refresh and the favorite button
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_today, container, false);

      swipeRefreshLayout =
         (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
      swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
         R.color.colorAccent);
      swipeRefreshLayout.setOnRefreshListener(
         new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               getMainActivity().loadWeather();
            }
         }
      );

      offlineBanner = (TextView) view.findViewById(R.id.offlineBanner);
      errorTextView = (TextView) view.findViewById(R.id.errorTextView);
      cityTextView = (TextView) view.findViewById(R.id.cityTextView);
      regionTextView = (TextView) view.findViewById(R.id.regionTextView);
      weatherContent = view.findViewById(R.id.weatherContent);
      updatedTextView = (TextView) view.findViewById(R.id.updatedTextView);
      iconTextView = (TextView) view.findViewById(R.id.iconTextView);
      temperatureTextView =
         (TextView) view.findViewById(R.id.temperatureTextView);
      descriptionTextView =
         (TextView) view.findViewById(R.id.descriptionTextView);
      feelsLikeTextView = (TextView) view.findViewById(R.id.feelsLikeTextView);
      humidityTextView = (TextView) view.findViewById(R.id.humidityTextView);
      windTextView = (TextView) view.findViewById(R.id.windTextView);
      sunriseTextView = (TextView) view.findViewById(R.id.sunriseTextView);
      sunsetTextView = (TextView) view.findViewById(R.id.sunsetTextView);
      hourlyChart = (SimpleChartView) view.findViewById(R.id.hourlyChart);

      favoriteButton = (Button) view.findViewById(R.id.favoriteButton);
      favoriteButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            getMainActivity().toggleCurrentCityFavorite();
         }
      });
      return view;
   }

   // shows MainActivity's current city, weather and loading state
   @Override
   protected void render() {
      MainActivity activity = getMainActivity();
      City city = activity.getCity();
      WeatherResult result = activity.getResult();
      final boolean loading = activity.isLoading();

      // post, because setRefreshing before the first layout is ignored
      swipeRefreshLayout.post(new Runnable() {
         @Override
         public void run() {
            swipeRefreshLayout.setRefreshing(loading);
         }
      });

      cityTextView.setText(city.getName());
      regionTextView.setText(city.getRegionAndCountry());
      favoriteButton.setText(activity.isCurrentCityFavorite() ?
         R.string.button_saved : R.string.button_save);

      boolean hasError = !loading && result != null && result.errorMessage != 0;
      errorTextView.setVisibility(hasError ? View.VISIBLE : View.GONE);
      if (hasError)
         errorTextView.setText(result.errorMessage);

      if (result == null || result.forecast == null) {
         weatherContent.setVisibility(View.GONE);
         offlineBanner.setVisibility(View.GONE);
         return;
      }
      weatherContent.setVisibility(View.VISIBLE);
      showForecast(result);
   }

   // fills in the current conditions and the hourly chart
   private void showForecast(WeatherResult result) {
      Forecast forecast = result.forecast;
      String time = Formatters.formatTimestamp(result.fetchedAt);
      String tempUnit = Units.temperature(getActivity(), forecast.units);
      String description = getString(
         WeatherCodes.getDescription(forecast.weatherCode));

      offlineBanner.setVisibility(result.fromCache ? View.VISIBLE : View.GONE);
      offlineBanner.setText(getString(R.string.offline_banner, time));
      updatedTextView.setText(getString(R.string.last_updated, time));

      iconTextView.setText(
         WeatherCodes.getIcon(forecast.weatherCode, forecast.isDay));
      iconTextView.setContentDescription(description);
      descriptionTextView.setText(description);
      temperatureTextView.setText(getString(R.string.temperature_format,
         Formatters.formatNumber(forecast.temperature), tempUnit));
      feelsLikeTextView.setText(getString(R.string.feels_like,
         Formatters.formatNumber(forecast.feelsLike), tempUnit));
      humidityTextView.setText(
         getString(R.string.percent_format, forecast.humidity));
      windTextView.setText(getString(R.string.wind_format,
         Formatters.formatNumber(forecast.windSpeed),
         Units.windSpeed(getActivity(), forecast.units)));

      if (!forecast.daily.isEmpty()) {
         DailyForecast today = forecast.daily.get(0);
         sunriseTextView.setText(Formatters.formatTime(today.sunrise));
         sunsetTextView.setText(Formatters.formatTime(today.sunset));
      }

      showHourlyChart(forecast);
   }

   // draws the next 24 hours of temperature, starting at the current hour
   private void showHourlyChart(Forecast forecast) {
      int start = forecast.getCurrentHourIndex();
      int count = Math.min(CHART_HOURS, forecast.hourlyTimes.length - start);
      if (count <= 0) {
         hourlyChart.clear(getString(R.string.chart_no_data));
         return;
      }

      String[] labels = new String[count];
      double[] temps = new double[count];
      for (int i = 0; i < count; ++i) {
         labels[i] = Formatters.formatHour(forecast.hourlyTimes[start + i]);
         temps[i] = forecast.hourlyTemps[start + i];
      }

      hourlyChart.setLineData(labels, Collections.singletonList(
         new SimpleChartView.Series(getString(R.string.legend_temperature),
            temps, getResources().getColor(R.color.chartTemperature, null))),
         getString(R.string.degree), false);
      hourlyChart.setContentDescription(getString(
         R.string.hourly_chart_description, labels[0], labels[count - 1]));
   }
}
