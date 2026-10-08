// DatabaseHelper.java
// SQLiteOpenHelper for the favorites and cached_forecasts tables
package com.rezashahwaz.skycast.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
   private static final String DATABASE_NAME = "SkyCast.db";
   private static final int DATABASE_VERSION = 1;

   // favorites table
   private static final String TABLE_FAVORITES = "favorites";
   private static final String COLUMN_ID = "id";
   private static final String COLUMN_NAME = "name";
   private static final String COLUMN_REGION = "region";
   private static final String COLUMN_COUNTRY = "country";
   private static final String COLUMN_LAT = "lat";
   private static final String COLUMN_LON = "lon";

   // cached_forecasts table: one row per city (NFR-09)
   private static final String TABLE_CACHE = "cached_forecasts";
   private static final String COLUMN_CITY_ID = "city_id";
   private static final String COLUMN_JSON = "json";
   private static final String COLUMN_UNITS = "units";
   private static final String COLUMN_FETCHED_AT = "fetched_at";

   private static DatabaseHelper instance;

   // returns the app-wide helper, so all screens share one connection
   public static synchronized DatabaseHelper getInstance(Context context) {
      if (instance == null)
         instance = new DatabaseHelper(context.getApplicationContext());
      return instance;
   }

   // constructor
   private DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   // creates both tables when the database is created
   @Override
   public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + TABLE_FAVORITES + "(" +
         COLUMN_ID + " INTEGER PRIMARY KEY, " +
         COLUMN_NAME + " TEXT NOT NULL, " +
         COLUMN_REGION + " TEXT, " +
         COLUMN_COUNTRY + " TEXT, " +
         COLUMN_LAT + " REAL NOT NULL, " +
         COLUMN_LON + " REAL NOT NULL);");
      db.execSQL("CREATE TABLE " + TABLE_CACHE + "(" +
         COLUMN_CITY_ID + " INTEGER PRIMARY KEY, " +
         COLUMN_JSON + " TEXT NOT NULL, " +
         COLUMN_UNITS + " TEXT NOT NULL, " +
         COLUMN_FETCHED_AT + " INTEGER NOT NULL);");
   }

   // normally defines how to upgrade the database when the schema changes
   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion,
      int newVersion) { }

   // saves a city as a favorite, replacing any existing row for it
   public void addFavorite(City city) {
      ContentValues values = new ContentValues();
      values.put(COLUMN_ID, city.getId());
      values.put(COLUMN_NAME, city.getName());
      values.put(COLUMN_REGION, city.getRegion());
      values.put(COLUMN_COUNTRY, city.getCountry());
      values.put(COLUMN_LAT, city.getLatitude());
      values.put(COLUMN_LON, city.getLongitude());
      getWritableDatabase().insertWithOnConflict(TABLE_FAVORITES, null,
         values, SQLiteDatabase.CONFLICT_REPLACE);
   }

   // removes a city from the favorites
   public void removeFavorite(long cityId) {
      getWritableDatabase().delete(TABLE_FAVORITES, COLUMN_ID + "=?",
         new String[]{String.valueOf(cityId)});
   }

   // returns all favorites sorted by name
   public List<City> getFavorites() {
      List<City> cities = new ArrayList<>();
      Cursor cursor = getReadableDatabase().query(TABLE_FAVORITES, null,
         null, null, null, null, COLUMN_NAME + " COLLATE NOCASE");

      try {
         while (cursor.moveToNext()) {
            cities.add(new City(
               cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
               cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
               cursor.getString(cursor.getColumnIndex(COLUMN_REGION)),
               cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY)),
               cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT)),
               cursor.getDouble(cursor.getColumnIndex(COLUMN_LON))));
         }
      }
      finally {
         cursor.close();
      }
      return cities;
   }

   // saves the latest forecast JSON for a city, replacing the old one
   public void saveForecast(long cityId, String json, String units,
      long fetchedAt) {
      ContentValues values = new ContentValues();
      values.put(COLUMN_CITY_ID, cityId);
      values.put(COLUMN_JSON, json);
      values.put(COLUMN_UNITS, units);
      values.put(COLUMN_FETCHED_AT, fetchedAt);
      getWritableDatabase().insertWithOnConflict(TABLE_CACHE, null, values,
         SQLiteDatabase.CONFLICT_REPLACE);
   }

   // returns the cached forecast for a city, or null if there is none
   public CachedForecast getCachedForecast(long cityId) {
      Cursor cursor = getReadableDatabase().query(TABLE_CACHE, null,
         COLUMN_CITY_ID + "=?", new String[]{String.valueOf(cityId)},
         null, null, null);

      try {
         if (!cursor.moveToFirst())
            return null;
         return new CachedForecast(
            cursor.getString(cursor.getColumnIndex(COLUMN_JSON)),
            cursor.getString(cursor.getColumnIndex(COLUMN_UNITS)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_FETCHED_AT)));
      }
      finally {
         cursor.close();
      }
   }
}
