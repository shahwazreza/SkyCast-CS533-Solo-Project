// WeatherApiClient.java
// Builds Open-Meteo URLs and downloads responses with HttpURLConnection
package com.rezashahwaz.skycast.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class WeatherApiClient {
   private static final String FORECAST_URL =
      "https://api.open-meteo.com/v1/forecast";
   private static final String SEARCH_URL =
      "https://geocoding-api.open-meteo.com/v1/search";
   private static final int TIMEOUT_MILLIS = 10000;

   private WeatherApiClient() { } // static methods only

   // returns the forecast URL for a location in the given units
   public static String buildForecastUrl(double latitude, double longitude,
      String units) {
      boolean metric = SettingsStore.UNITS_METRIC.equals(units);
      return String.format(Locale.US, "%s?latitude=%.4f&longitude=%.4f" +
         "&current=temperature_2m,relative_humidity_2m," +
         "apparent_temperature,weather_code,wind_speed_10m,is_day" +
         "&hourly=temperature_2m,precipitation_probability" +
         "&daily=weather_code,temperature_2m_max,temperature_2m_min," +
         "precipitation_probability_max,sunrise,sunset" +
         "&timezone=auto&forecast_days=7" +
         "&temperature_unit=%s&wind_speed_unit=%s",
         FORECAST_URL, latitude, longitude,
         metric ? "celsius" : "fahrenheit", metric ? "kmh" : "mph");
   }

   // returns the geocoding URL for a city name
   public static String buildSearchUrl(String name) {
      try {
         return SEARCH_URL + "?name=" + URLEncoder.encode(name, "UTF-8") +
            "&count=10&language=en&format=json";
      }
      catch (java.io.UnsupportedEncodingException e) {
         throw new IllegalStateException(e); // UTF-8 is always supported
      }
   }

   // downloads the response body of an HTTP GET; call off the UI thread
   public static String get(String url) throws IOException {
      HttpURLConnection connection =
         (HttpURLConnection) new URL(url).openConnection();
      connection.setConnectTimeout(TIMEOUT_MILLIS);
      connection.setReadTimeout(TIMEOUT_MILLIS);

      try {
         int responseCode = connection.getResponseCode();
         if (responseCode != HttpURLConnection.HTTP_OK)
            throw new IOException("HTTP " + responseCode);

         StringBuilder builder = new StringBuilder();
         try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null)
               builder.append(line);
         }
         return builder.toString();
      }
      finally {
         connection.disconnect();
      }
   }
}
