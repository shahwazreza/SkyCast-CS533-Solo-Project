// WeatherResult.java
// Result of loading a city's weather: the forecast, or an error message
package com.rezashahwaz.skycast.data;

public class WeatherResult {
   public final City city;
   public final Forecast forecast; // null if loading failed
   public final String json; // raw JSON, kept so it survives rotation
   public final boolean fromCache; // true if the network call failed
   public final long fetchedAt; // when the data was downloaded
   public final int errorMessage; // string resource ID, 0 if no error

   // constructor
   public WeatherResult(City city, Forecast forecast, String json,
      boolean fromCache, long fetchedAt, int errorMessage) {
      this.city = city;
      this.forecast = forecast;
      this.json = json;
      this.fromCache = fromCache;
      this.fetchedAt = fetchedAt;
      this.errorMessage = errorMessage;
   }

   // creates a result for a failed load
   public static WeatherResult error(City city, int errorMessage) {
      return new WeatherResult(city, null, null, false, 0, errorMessage);
   }
}
