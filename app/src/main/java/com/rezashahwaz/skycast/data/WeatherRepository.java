// WeatherRepository.java
// Loads weather network-first with a SQLite cache fallback; runs off the UI thread
package com.rezashahwaz.skycast.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rezashahwaz.skycast.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class WeatherRepository {
   private final Context context;
   private final DatabaseHelper database;

   // constructor
   public WeatherRepository(Context context) {
      this.context = context.getApplicationContext();
      database = DatabaseHelper.getInstance(context);
   }

   // downloads a city's forecast and caches it; if the download fails,
   // returns the cached copy, or an error result if there is no cache
   public WeatherResult loadForecast(City city, String units) {
      try {
         String json = WeatherApiClient.get(WeatherApiClient.buildForecastUrl(
            city.getLatitude(), city.getLongitude(), units));
         Forecast forecast = ForecastParser.parseForecast(json, units);
         long now = System.currentTimeMillis();
         database.saveForecast(city.getId(), json, units, now);
         return new WeatherResult(city, forecast, json, false, now, 0);
      }
      catch (IOException | JSONException e) {
         return loadCachedForecast(city);
      }
   }

   // returns the cached forecast for a city, or an error result
   private WeatherResult loadCachedForecast(City city) {
      int error = isOnline() ? R.string.error_loading : R.string.error_offline;
      CachedForecast cached = database.getCachedForecast(city.getId());
      if (cached == null)
         return WeatherResult.error(city, error);

      try {
         Forecast forecast =
            ForecastParser.parseForecast(cached.json, cached.units);
         return new WeatherResult(city, forecast, cached.json, true,
            cached.fetchedAt, 0);
      }
      catch (JSONException e) {
         return WeatherResult.error(city, error);
      }
   }

   // searches cities by name; throws if the request fails
   public List<City> searchCities(String name)
      throws IOException, JSONException {
      return ForecastParser.parseCities(
         WeatherApiClient.get(WeatherApiClient.buildSearchUrl(name)));
   }

   // returns true if the device has a network connection
   public boolean isOnline() {
      ConnectivityManager manager = (ConnectivityManager)
         context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo info = manager.getActiveNetworkInfo();
      return info != null && info.isConnected();
   }

   public List<City> getFavorites() { return database.getFavorites(); }
   public void addFavorite(City city) { database.addFavorite(city); }
   public void removeFavorite(City city) {
      database.removeFavorite(city.getId());
   }
}
