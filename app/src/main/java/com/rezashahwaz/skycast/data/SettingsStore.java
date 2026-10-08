// SettingsStore.java
// Reads and writes the unit setting and last viewed city in SharedPreferences
package com.rezashahwaz.skycast.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsStore {
   public static final String UNITS_IMPERIAL = "imperial"; // °F + mph
   public static final String UNITS_METRIC = "metric"; // °C + km/h

   private static final String PREFS_NAME = "skycast_settings";
   private static final String KEY_UNITS = "units";
   private static final String KEY_CITY_ID = "city_id";
   private static final String KEY_CITY_NAME = "city_name";
   private static final String KEY_CITY_REGION = "city_region";
   private static final String KEY_CITY_COUNTRY = "city_country";
   private static final String KEY_CITY_LAT = "city_lat";
   private static final String KEY_CITY_LON = "city_lon";

   private final SharedPreferences preferences;

   // constructor
   public SettingsStore(Context context) {
      preferences = context.getApplicationContext().getSharedPreferences(
         PREFS_NAME, Context.MODE_PRIVATE);
   }

   // returns UNITS_IMPERIAL (the default) or UNITS_METRIC
   public String getUnits() {
      return preferences.getString(KEY_UNITS, UNITS_IMPERIAL);
   }

   // saves the unit setting
   public void setUnits(String units) {
      preferences.edit().putString(KEY_UNITS, units).apply();
   }

   // returns the last viewed city, or New York on first launch
   public City getLastCity() {
      if (!preferences.contains(KEY_CITY_ID))
         return City.NEW_YORK;

      return new City(preferences.getLong(KEY_CITY_ID, 0),
         preferences.getString(KEY_CITY_NAME, ""),
         preferences.getString(KEY_CITY_REGION, ""),
         preferences.getString(KEY_CITY_COUNTRY, ""),
         Double.longBitsToDouble(preferences.getLong(KEY_CITY_LAT, 0)),
         Double.longBitsToDouble(preferences.getLong(KEY_CITY_LON, 0)));
   }

   // remembers the city being viewed
   public void setLastCity(City city) {
      // doubles are stored as raw bits; SharedPreferences has no putDouble
      preferences.edit()
         .putLong(KEY_CITY_ID, city.getId())
         .putString(KEY_CITY_NAME, city.getName())
         .putString(KEY_CITY_REGION, city.getRegion())
         .putString(KEY_CITY_COUNTRY, city.getCountry())
         .putLong(KEY_CITY_LAT, Double.doubleToLongBits(city.getLatitude()))
         .putLong(KEY_CITY_LON, Double.doubleToLongBits(city.getLongitude()))
         .apply();
   }
}
