// SampleJson.java
// Real Open-Meteo responses saved on Oct 7, 2026, used by the parser tests
package com.rezashahwaz.skycast.data;

class SampleJson {
   // New York forecast in Fahrenheit/mph (hourly trimmed to 30 hours)
   static final String FORECAST =
      "{\"latitude\":40.710335,\"longitude\":-73.99308,\"utc_offset_seconds\":-" +
      "14400,\"timezone\":\"America/New_York\",\"current\":{\"time\":\"2026-10-07T" +
      "16:45\",\"interval\":900,\"temperature_2m\":65.1,\"relative_humidity_2m\"" +
      ":51,\"apparent_temperature\":60.2,\"weather_code\":0,\"wind_speed_10m\":" +
      "9.6,\"is_day\":1},\"hourly\":{\"time\":[\"2026-10-07T00:00\",\"2026-10-07T0" +
      "1:00\",\"2026-10-07T02:00\",\"2026-10-07T03:00\",\"2026-10-07T04:00\",\"20" +
      "26-10-07T05:00\",\"2026-10-07T06:00\",\"2026-10-07T07:00\",\"2026-10-07T" +
      "08:00\",\"2026-10-07T09:00\",\"2026-10-07T10:00\",\"2026-10-07T11:00\",\"2" +
      "026-10-07T12:00\",\"2026-10-07T13:00\",\"2026-10-07T14:00\",\"2026-10-07" +
      "T15:00\",\"2026-10-07T16:00\",\"2026-10-07T17:00\",\"2026-10-07T18:00\",\"" +
      "2026-10-07T19:00\",\"2026-10-07T20:00\",\"2026-10-07T21:00\",\"2026-10-0" +
      "7T22:00\",\"2026-10-07T23:00\",\"2026-10-08T00:00\",\"2026-10-08T01:00\"," +
      "\"2026-10-08T02:00\",\"2026-10-08T03:00\",\"2026-10-08T04:00\",\"2026-10-" +
      "08T05:00\"],\"temperature_2m\":[47.6,47.2,47.9,47.0,46.4,46.3,45.2,44" +
      ".7,47.9,53.8,58.6,61.3,64.2,65.5,65.9,66.8,65.7,65.0,63.2,59.6,58." +
      "1,58.0,57.6,56.8,56.6,56.4,56.0,55.9,56.0,55.5],\"precipitation_pro" +
      "bability\":[0,null,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
      "0,0,0,1]},\"daily\":{\"time\":[\"2026-10-07\",\"2026-10-08\",\"2026-10-09\"," +
      "\"2026-10-10\",\"2026-10-11\",\"2026-10-12\",\"2026-10-13\"],\"weather_code" +
      "\":[0,2,3,3,81,51,51],\"temperature_2m_max\":[66.8,76.3,69.8,63.1,63." +
      "5,73.2,72.5],\"temperature_2m_min\":[44.7,55.5,48.3,55.4,60.3,60.5,5" +
      "8.1],\"precipitation_probability_max\":[0,2,0,0,69,70,22],\"sunrise\":" +
      "[\"2026-10-07T06:58\",\"2026-10-08T06:59\",\"2026-10-09T07:00\",\"2026-10" +
      "-10T07:01\",\"2026-10-11T07:02\",\"2026-10-12T07:03\",\"2026-10-13T07:05" +
      "\"],\"sunset\":[\"2026-10-07T18:28\",\"2026-10-08T18:26\",\"2026-10-09T18:" +
      "24\",\"2026-10-10T18:23\",\"2026-10-11T18:21\",\"2026-10-12T18:20\",\"2026" +
      "-10-13T18:18\"]}}";

   // geocoding search for "Paris" (count=2)
   static final String SEARCH_PARIS =
      "{\"results\":[{\"id\":2988507,\"name\":\"Paris\"," +
      "\"latitude\":48.85341,\"longitude\":2.3488," +
      "\"country\":\"France\",\"admin1\":\"\u00CEle-de-France Region\"}," +
      "{\"id\":4717560,\"name\":\"Paris\",\"latitude\":33.66094," +
      "\"longitude\":-95.55551,\"country\":\"United States\"," +
      "\"admin1\":\"Texas\"}],\"generationtime_ms\":0.79}";

   // geocoding search with no matches: the API omits "results"
   static final String SEARCH_EMPTY = "{\"generationtime_ms\":0.64}";
}
