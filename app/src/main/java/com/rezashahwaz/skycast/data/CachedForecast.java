// CachedForecast.java
// Model for a forecast JSON string saved in the offline cache
package com.rezashahwaz.skycast.data;

public class CachedForecast {
   public final String json;
   public final String units; // units the JSON was downloaded in
   public final long fetchedAt; // System.currentTimeMillis() when saved

   // constructor
   public CachedForecast(String json, String units, long fetchedAt) {
      this.json = json;
      this.units = units;
      this.fetchedAt = fetchedAt;
   }
}
