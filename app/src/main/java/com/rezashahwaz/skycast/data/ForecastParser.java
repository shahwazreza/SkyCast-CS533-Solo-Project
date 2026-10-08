// ForecastParser.java
// Converts Open-Meteo forecast and geocoding JSON into model objects
package com.rezashahwaz.skycast.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForecastParser {
   private ForecastParser() { } // static methods only

   // parses a /v1/forecast response downloaded in the given units
   public static Forecast parseForecast(String json, String units)
      throws JSONException {
      JSONObject root = new JSONObject(json);

      JSONObject current = root.getJSONObject("current");
      String currentTime = current.getString("time");
      double temperature = current.getDouble("temperature_2m");
      double feelsLike = current.getDouble("apparent_temperature");
      int humidity = current.getInt("relative_humidity_2m");
      double windSpeed = current.getDouble("wind_speed_10m");
      int weatherCode = current.getInt("weather_code");
      boolean isDay = current.optInt("is_day", 1) == 1;

      JSONObject hourly = root.getJSONObject("hourly");
      JSONArray hourlyTimeArray = hourly.getJSONArray("time");
      JSONArray hourlyTempArray = hourly.getJSONArray("temperature_2m");
      String[] hourlyTimes = new String[hourlyTimeArray.length()];
      double[] hourlyTemps = new double[hourlyTimeArray.length()];
      for (int i = 0; i < hourlyTimes.length; ++i) {
         hourlyTimes[i] = hourlyTimeArray.getString(i);
         hourlyTemps[i] = getDouble(hourlyTempArray, i);
      }

      JSONObject dailyJson = root.getJSONObject("daily");
      JSONArray dates = dailyJson.getJSONArray("time");
      JSONArray codes = dailyJson.getJSONArray("weather_code");
      JSONArray maxTemps = dailyJson.getJSONArray("temperature_2m_max");
      JSONArray minTemps = dailyJson.getJSONArray("temperature_2m_min");
      JSONArray rain =
         dailyJson.getJSONArray("precipitation_probability_max");
      JSONArray sunrises = dailyJson.getJSONArray("sunrise");
      JSONArray sunsets = dailyJson.getJSONArray("sunset");

      List<DailyForecast> daily = new ArrayList<>();
      for (int i = 0; i < dates.length(); ++i) {
         daily.add(new DailyForecast(dates.getString(i),
            codes.optInt(i, -1), getDouble(maxTemps, i),
            getDouble(minTemps, i), rain.optInt(i, 0),
            sunrises.optString(i, ""), sunsets.optString(i, "")));
      }

      return new Forecast(units, currentTime, temperature, feelsLike,
         humidity, windSpeed, weatherCode, isDay, hourlyTimes, hourlyTemps,
         daily);
   }

   // parses a geocoding /v1/search response; returns an empty list if
   // nothing matched (the API then omits the "results" array)
   public static List<City> parseCities(String json) throws JSONException {
      JSONObject root = new JSONObject(json);
      List<City> cities = new ArrayList<>();
      JSONArray results = root.optJSONArray("results");

      if (results != null) {
         for (int i = 0; i < results.length(); ++i) {
            JSONObject city = results.getJSONObject(i);
            cities.add(new City(city.getLong("id"),
               city.getString("name"), city.optString("admin1", ""),
               city.optString("country", ""),
               city.getDouble("latitude"), city.getDouble("longitude")));
         }
      }
      return cities;
   }

   // returns the value at index, or NaN if it's missing or null
   private static double getDouble(JSONArray array, int index) {
      return array.isNull(index) ? Double.NaN : array.optDouble(index);
   }
}
