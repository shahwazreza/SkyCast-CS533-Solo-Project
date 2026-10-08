// WeatherCodesTest.java
// JUnit tests for the WMO weather-code descriptions and icons
package com.rezashahwaz.skycast.util;

import com.rezashahwaz.skycast.R;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeatherCodesTest {
   @Test
   public void description_mapsKnownCodes() {
      assertEquals(R.string.wmo_clear, WeatherCodes.getDescription(0));
      assertEquals(R.string.wmo_partly_cloudy, WeatherCodes.getDescription(2));
      assertEquals(R.string.wmo_fog, WeatherCodes.getDescription(45));
      assertEquals(R.string.wmo_rain, WeatherCodes.getDescription(63));
      assertEquals(R.string.wmo_freezing_rain, WeatherCodes.getDescription(67));
      assertEquals(R.string.wmo_snow_heavy, WeatherCodes.getDescription(75));
      assertEquals(R.string.wmo_showers_violent,
         WeatherCodes.getDescription(82));
      assertEquals(R.string.wmo_thunderstorm, WeatherCodes.getDescription(95));
      assertEquals(R.string.wmo_thunderstorm_hail,
         WeatherCodes.getDescription(99));
   }

   @Test
   public void description_unknownCodes() {
      assertEquals(R.string.wmo_unknown, WeatherCodes.getDescription(4));
      assertEquals(R.string.wmo_unknown, WeatherCodes.getDescription(-1));
      assertEquals(R.string.wmo_unknown, WeatherCodes.getDescription(100));
   }

   @Test
   public void icon_clearSkyDependsOnDayOrNight() {
      assertEquals(WeatherCodes.SUN, WeatherCodes.getIcon(0, true));
      assertEquals(WeatherCodes.MOON, WeatherCodes.getIcon(0, false));
   }

   @Test
   public void icon_mapsGroups() {
      assertEquals(WeatherCodes.CLOUD, WeatherCodes.getIcon(3, true));
      assertEquals(WeatherCodes.FOG, WeatherCodes.getIcon(48, true));
      assertEquals(WeatherCodes.RAIN, WeatherCodes.getIcon(65, true));
      assertEquals(WeatherCodes.SNOW, WeatherCodes.getIcon(71, false));
      assertEquals(WeatherCodes.THUNDER, WeatherCodes.getIcon(96, true));
      assertEquals(WeatherCodes.THERMOMETER, WeatherCodes.getIcon(12, true));
   }

   @Test
   public void everyDocumentedCodeHasADescription() {
      int[] codes = {0, 1, 2, 3, 45, 48, 51, 53, 55, 56, 57, 61, 63, 65, 66,
         67, 71, 73, 75, 77, 80, 81, 82, 85, 86, 95, 96, 99};
      for (int code : codes)
         assertNotEquals("code " + code, R.string.wmo_unknown,
            WeatherCodes.getDescription(code));
   }
}
