// City.java
// Model for a city: geocoding ID, display names and coordinates
package com.rezashahwaz.skycast.data;

import java.io.Serializable;

public class City implements Serializable {
   // default city shown on first launch
   public static final City NEW_YORK = new City(5128581, "New York",
      "New York", "United States", 40.7143, -74.006);

   private final long id; // Open-Meteo geocoding ID, also the database key
   private final String name;
   private final String region; // state or province; may be empty
   private final String country;
   private final double latitude;
   private final double longitude;

   // constructor
   public City(long id, String name, String region, String country,
      double latitude, double longitude) {
      this.id = id;
      this.name = name;
      this.region = (region != null) ? region : "";
      this.country = (country != null) ? country : "";
      this.latitude = latitude;
      this.longitude = longitude;
   }

   public long getId() { return id; }
   public String getName() { return name; }
   public String getRegion() { return region; }
   public String getCountry() { return country; }
   public double getLatitude() { return latitude; }
   public double getLongitude() { return longitude; }

   // returns "Region, Country", or just the country if there's no region
   public String getRegionAndCountry() {
      if (region.isEmpty() || region.equals(name))
         return country;
      if (country.isEmpty())
         return region;
      return region + ", " + country;
   }
}
