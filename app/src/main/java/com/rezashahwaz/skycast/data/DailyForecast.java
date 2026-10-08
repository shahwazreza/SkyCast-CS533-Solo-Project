// DailyForecast.java
// Model for one day of the 7-day forecast
package com.rezashahwaz.skycast.data;

public class DailyForecast {
   public final String date; // yyyy-MM-dd in the city's time zone
   public final int weatherCode; // WMO weather code
   public final double maxTemp;
   public final double minTemp;
   public final int rainChance; // 0-100 percent
   public final String sunrise; // yyyy-MM-ddTHH:mm, city time
   public final String sunset;

   // constructor
   public DailyForecast(String date, int weatherCode, double maxTemp,
      double minTemp, int rainChance, String sunrise, String sunset) {
      this.date = date;
      this.weatherCode = weatherCode;
      this.maxTemp = maxTemp;
      this.minTemp = minTemp;
      this.rainChance = rainChance;
      this.sunrise = sunrise;
      this.sunset = sunset;
   }
}
