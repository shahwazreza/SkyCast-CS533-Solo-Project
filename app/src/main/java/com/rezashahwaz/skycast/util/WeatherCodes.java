// WeatherCodes.java
// Maps WMO weather codes to a description string resource and an emoji icon
package com.rezashahwaz.skycast.util;

import com.rezashahwaz.skycast.R;

public class WeatherCodes {
   // emoji icons, written as escapes so the source stays ASCII
   static final String SUN = "☀️";
   static final String MOON = "🌙";
   static final String SUN_SMALL_CLOUD = "🌤️";
   static final String SUN_CLOUD = "⛅";
   static final String CLOUD = "☁️";
   static final String FOG = "🌫️";
   static final String SUN_RAIN = "🌦️";
   static final String RAIN = "🌧️";
   static final String SNOW = "🌨️";
   static final String SNOWFLAKE = "❄️";
   static final String THUNDER = "⛈️";
   static final String THERMOMETER = "🌡️";

   private WeatherCodes() { } // static methods only

   // returns the string resource ID describing a WMO code
   public static int getDescription(int code) {
      switch (code) {
         case 0: return R.string.wmo_clear;
         case 1: return R.string.wmo_mainly_clear;
         case 2: return R.string.wmo_partly_cloudy;
         case 3: return R.string.wmo_overcast;
         case 45: return R.string.wmo_fog;
         case 48: return R.string.wmo_rime_fog;
         case 51: return R.string.wmo_drizzle_light;
         case 53: return R.string.wmo_drizzle;
         case 55: return R.string.wmo_drizzle_dense;
         case 56: case 57: return R.string.wmo_freezing_drizzle;
         case 61: return R.string.wmo_rain_light;
         case 63: return R.string.wmo_rain;
         case 65: return R.string.wmo_rain_heavy;
         case 66: case 67: return R.string.wmo_freezing_rain;
         case 71: return R.string.wmo_snow_light;
         case 73: return R.string.wmo_snow;
         case 75: return R.string.wmo_snow_heavy;
         case 77: return R.string.wmo_snow_grains;
         case 80: return R.string.wmo_showers_light;
         case 81: return R.string.wmo_showers;
         case 82: return R.string.wmo_showers_violent;
         case 85: case 86: return R.string.wmo_snow_showers;
         case 95: return R.string.wmo_thunderstorm;
         case 96: case 99: return R.string.wmo_thunderstorm_hail;
         default: return R.string.wmo_unknown;
      }
   }

   // returns an emoji icon for a WMO code; clear skies at night get a moon
   public static String getIcon(int code, boolean isDay) {
      switch (code) {
         case 0: return isDay ? SUN : MOON;
         case 1: return isDay ? SUN_SMALL_CLOUD : MOON;
         case 2: return isDay ? SUN_CLOUD : CLOUD;
         case 3: return CLOUD;
         case 45: case 48: return FOG;
         case 51: case 53: case 55: case 56: case 57:
         case 80: case 81: case 82:
            return isDay ? SUN_RAIN : RAIN;
         case 61: case 63: case 65: case 66: case 67: return RAIN;
         case 71: case 73: case 75: case 85: case 86: return SNOW;
         case 77: return SNOWFLAKE;
         case 95: case 96: case 99: return THUNDER;
         default: return THERMOMETER;
      }
   }
}
