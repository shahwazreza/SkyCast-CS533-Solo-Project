// Units.java
// Returns the temperature and wind-speed symbols for a unit setting
package com.rezashahwaz.skycast.util;

import android.content.Context;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.SettingsStore;

public class Units {
   private Units() { } // static methods only

   // "°F" or "°C"
   public static String temperature(Context context, String units) {
      return context.getString(SettingsStore.UNITS_METRIC.equals(units) ?
         R.string.unit_celsius : R.string.unit_fahrenheit);
   }

   // "mph" or "km/h"
   public static String windSpeed(Context context, String units) {
      return context.getString(SettingsStore.UNITS_METRIC.equals(units) ?
         R.string.unit_kmh : R.string.unit_mph);
   }
}
