// ForecastParserTest.java
// Instrumented tests for ForecastParser using saved Open-Meteo responses; they
// run on a device because org.json is only a stub in plain JVM unit tests
package com.rezashahwaz.skycast.data;

import android.test.AndroidTestCase;

import org.json.JSONException;

import java.util.List;

public class ForecastParserTest extends AndroidTestCase {
   private static final double DELTA = 0.001;

   public void testParseForecastReadsCurrentConditions() throws Exception {
      Forecast forecast = ForecastParser.parseForecast(SampleJson.FORECAST,
         SettingsStore.UNITS_IMPERIAL);
      assertEquals(SettingsStore.UNITS_IMPERIAL, forecast.units);
      assertEquals("2026-10-07T16:45", forecast.currentTime);
      assertEquals(65.1, forecast.temperature, DELTA);
      assertEquals(60.2, forecast.feelsLike, DELTA);
      assertEquals(51, forecast.humidity);
      assertEquals(9.6, forecast.windSpeed, DELTA);
      assertEquals(0, forecast.weatherCode);
      assertTrue(forecast.isDay);
   }

   public void testParseForecastReadsHourlyAndDaily() throws Exception {
      Forecast forecast = ForecastParser.parseForecast(SampleJson.FORECAST,
         SettingsStore.UNITS_IMPERIAL);
      assertEquals(30, forecast.hourlyTimes.length);
      assertEquals(30, forecast.hourlyTemps.length);
      assertEquals("2026-10-07T00:00", forecast.hourlyTimes[0]);
      assertEquals(47.6, forecast.hourlyTemps[0], DELTA);

      assertEquals(7, forecast.daily.size());
      DailyForecast tomorrow = forecast.daily.get(1);
      assertEquals("2026-10-08", tomorrow.date);
      assertEquals(2, tomorrow.weatherCode);
      assertEquals(76.3, tomorrow.maxTemp, DELTA);
      assertEquals(55.5, tomorrow.minTemp, DELTA);
      assertEquals(2, tomorrow.rainChance);
      assertEquals("2026-10-07T18:28", forecast.daily.get(0).sunset);
   }

   public void testCurrentHourIndexPointsAtCurrentHour() throws Exception {
      Forecast forecast = ForecastParser.parseForecast(SampleJson.FORECAST,
         SettingsStore.UNITS_IMPERIAL);
      // current time is 16:45, so the chart starts at the 16:00 entry
      assertEquals(16, forecast.getCurrentHourIndex());
   }

   public void testParseForecastRejectsInvalidJson() {
      try {
         ForecastParser.parseForecast("{\"error\":true}",
            SettingsStore.UNITS_IMPERIAL);
         fail("Expected JSONException");
      }
      catch (JSONException expected) { }
   }

   public void testParseCitiesReadsResults() throws Exception {
      List<City> cities = ForecastParser.parseCities(SampleJson.SEARCH_PARIS);
      assertEquals(2, cities.size());
      City paris = cities.get(0);
      assertEquals(2988507, paris.getId());
      assertEquals("Paris", paris.getName());
      assertEquals("Île-de-France Region, France",
         paris.getRegionAndCountry());
      assertEquals(48.85341, paris.getLatitude(), DELTA);
      assertEquals("Texas, United States",
         cities.get(1).getRegionAndCountry());
   }

   public void testParseCitiesEmptyResultsGiveEmptyList() throws Exception {
      assertTrue(ForecastParser.parseCities(SampleJson.SEARCH_EMPTY).isEmpty());
   }
}
