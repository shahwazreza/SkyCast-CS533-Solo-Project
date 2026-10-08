// Forecast.java
// Model for one forecast response: current conditions, hourly and daily data
package com.rezashahwaz.skycast.data;

import java.util.List;

public class Forecast {
   public final String units; // SettingsStore.UNITS_IMPERIAL or UNITS_METRIC

   // current conditions
   public final String currentTime; // yyyy-MM-ddTHH:mm, city time
   public final double temperature;
   public final double feelsLike;
   public final int humidity; // percent
   public final double windSpeed;
   public final int weatherCode;
   public final boolean isDay;

   // hourly data; the arrays are parallel
   public final String[] hourlyTimes;
   public final double[] hourlyTemps;

   public final List<DailyForecast> daily;

   // constructor
   public Forecast(String units, String currentTime, double temperature,
      double feelsLike, int humidity, double windSpeed, int weatherCode,
      boolean isDay, String[] hourlyTimes, double[] hourlyTemps,
      List<DailyForecast> daily) {
      this.units = units;
      this.currentTime = currentTime;
      this.temperature = temperature;
      this.feelsLike = feelsLike;
      this.humidity = humidity;
      this.windSpeed = windSpeed;
      this.weatherCode = weatherCode;
      this.isDay = isDay;
      this.hourlyTimes = hourlyTimes;
      this.hourlyTemps = hourlyTemps;
      this.daily = daily;
   }

   // returns the index of the hourly entry for the current hour, so the
   // 24-hour chart starts now instead of at midnight
   public int getCurrentHourIndex() {
      if (currentTime == null || currentTime.length() < 13)
         return 0;
      String hour = currentTime.substring(0, 13); // yyyy-MM-ddTHH

      for (int i = 0; i < hourlyTimes.length; ++i) {
         if (hourlyTimes[i].startsWith(hour))
            return i;
      }
      return 0;
   }
}
