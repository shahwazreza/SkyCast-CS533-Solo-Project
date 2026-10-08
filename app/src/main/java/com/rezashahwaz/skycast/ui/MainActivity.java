// MainActivity.java
// Hosts the toolbar and tabs, loads the weather and favorites for the fragments
package com.rezashahwaz.skycast.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.City;
import com.rezashahwaz.skycast.data.Forecast;
import com.rezashahwaz.skycast.data.ForecastParser;
import com.rezashahwaz.skycast.data.SettingsStore;
import com.rezashahwaz.skycast.data.WeatherRepository;
import com.rezashahwaz.skycast.data.WeatherResult;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
   // implemented by the fragments to redraw when data changes
   public interface WeatherListener {
      void onWeatherChanged(); // weather loaded, failed or started loading
      void onFavoritesChanged(); // favorites list was reloaded
   }

   private static final int REQUEST_SEARCH = 1; // startActivityForResult code

   // tab positions
   public static final int TAB_TODAY = 0;
   public static final int TAB_FORECAST = 1;
   public static final int TAB_FAVORITES = 2;

   // keys for keeping the loaded forecast across rotation
   private static final String STATE_JSON = "json";
   private static final String STATE_UNITS = "units";
   private static final String STATE_FROM_CACHE = "from_cache";
   private static final String STATE_FETCHED_AT = "fetched_at";
   private static final String STATE_REQUESTED_UNITS = "requested_units";

   private SettingsStore settings;
   private WeatherRepository repository;
   private ViewPager viewPager;

   private City city; // city being viewed
   private WeatherResult result; // null while the first load runs
   private boolean loading;
   private String requestedUnits; // units of the last weather request
   private List<City> favorites = new ArrayList<>();
   private final List<WeatherListener> listeners = new ArrayList<>();

   private LoadWeatherTask loadWeatherTask;
   private final List<AsyncTask<?, ?, ?>> favoriteTasks = new ArrayList<>();

   // sets up the toolbar and tabs, then loads the last viewed city
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      viewPager = (ViewPager) findViewById(R.id.viewPager);
      viewPager.setAdapter(
         new MainPagerAdapter(getSupportFragmentManager(), this));
      viewPager.setOffscreenPageLimit(2); // keep all three tabs alive
      TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
      tabLayout.setupWithViewPager(viewPager);

      settings = new SettingsStore(this);
      repository = new WeatherRepository(this);
      city = settings.getLastCity();

      if (!restoreWeather(savedInstanceState))
         loadWeather();
      loadFavorites();
   }

   // reloads the weather if the units changed in SettingsActivity
   @Override
   protected void onResume() {
      super.onResume();
      if (requestedUnits != null &&
         !requestedUnits.equals(settings.getUnits()))
         loadWeather();
   }

   // saves the loaded forecast so rotation doesn't download it again
   @Override
   protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      if (!loading && result != null && result.forecast != null) {
         outState.putString(STATE_JSON, result.json);
         outState.putString(STATE_UNITS, result.forecast.units);
         outState.putBoolean(STATE_FROM_CACHE, result.fromCache);
         outState.putLong(STATE_FETCHED_AT, result.fetchedAt);
         outState.putString(STATE_REQUESTED_UNITS, requestedUnits);
      }
   }

   // stops background work so it doesn't update a destroyed activity
   @Override
   protected void onDestroy() {
      super.onDestroy();
      if (loadWeatherTask != null)
         loadWeatherTask.cancel(false);
      for (AsyncTask<?, ?, ?> task : favoriteTasks)
         task.cancel(false);
   }

   // restores a forecast saved by onSaveInstanceState; false if none
   private boolean restoreWeather(Bundle state) {
      if (state == null || !state.containsKey(STATE_JSON))
         return false;
      try {
         Forecast forecast = ForecastParser.parseForecast(
            state.getString(STATE_JSON), state.getString(STATE_UNITS));
         result = new WeatherResult(city, forecast,
            state.getString(STATE_JSON), state.getBoolean(STATE_FROM_CACHE),
            state.getLong(STATE_FETCHED_AT), 0);
         requestedUnits = state.getString(STATE_REQUESTED_UNITS);
         return true;
      }
      catch (JSONException e) {
         return false;
      }
   }

   // displays the menu
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   // handles the toolbar's menu items
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_search:
            startActivityForResult(new Intent(this, SearchActivity.class),
               REQUEST_SEARCH);
            return true;
         case R.id.action_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
         case R.id.action_refresh:
            loadWeather();
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   // shows the city chosen in SearchActivity
   @Override
   protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == REQUEST_SEARCH && resultCode == RESULT_OK &&
         data != null) {
         City chosen = (City) data.getSerializableExtra(SearchActivity.EXTRA_CITY);
         if (chosen != null)
            selectCity(chosen);
      }
   }

   // shows a city's weather on the Today tab and remembers it
   public void selectCity(City newCity) {
      city = newCity;
      settings.setLastCity(newCity);
      result = null; // don't show the previous city's weather
      loadWeather();
      viewPager.setCurrentItem(TAB_TODAY);
   }

   // downloads the current city's weather in the background
   public void loadWeather() {
      if (loadWeatherTask != null)
         loadWeatherTask.cancel(false);
      loading = true;
      requestedUnits = settings.getUnits();
      notifyWeatherChanged();
      loadWeatherTask = new LoadWeatherTask(city, requestedUnits);
      loadWeatherTask.execute();
   }

   // methods the fragments use to read the current state
   public City getCity() { return city; }
   public WeatherResult getResult() { return result; }
   public boolean isLoading() { return loading; }
   public List<City> getFavorites() { return favorites; }

   // returns true if the city being viewed is a favorite
   public boolean isCurrentCityFavorite() {
      for (City favorite : favorites) {
         if (favorite.getId() == city.getId())
            return true;
      }
      return false;
   }

   // saves or unsaves the city being viewed
   public void toggleCurrentCityFavorite() {
      new FavoriteTask(city, !isCurrentCityFavorite()).execute();
   }

   // removes a city from the favorites and offers to undo it
   public void deleteFavorite(final City favorite) {
      new FavoriteTask(favorite, false).execute();
      Snackbar.make(findViewById(R.id.coordinatorLayout),
         getString(R.string.favorite_removed, favorite.getName()),
         Snackbar.LENGTH_LONG)
         .setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new FavoriteTask(favorite, true).execute();
            }
         }).show();
   }

   // reloads the favorites list in the background
   private void loadFavorites() {
      new FavoriteTask(null, false).execute();
   }

   // fragments register here in onAttach and unregister in onDetach
   public void addWeatherListener(WeatherListener listener) {
      listeners.add(listener);
   }

   public void removeWeatherListener(WeatherListener listener) {
      listeners.remove(listener);
   }

   private void notifyWeatherChanged() {
      for (WeatherListener listener : new ArrayList<>(listeners))
         listener.onWeatherChanged();
   }

   private void notifyFavoritesChanged() {
      for (WeatherListener listener : new ArrayList<>(listeners))
         listener.onFavoritesChanged();
   }

   // loads a forecast (network first, then cache) off the UI thread
   private class LoadWeatherTask extends AsyncTask<Void, Void, WeatherResult> {
      private final City taskCity;
      private final String units;

      LoadWeatherTask(City taskCity, String units) {
         this.taskCity = taskCity;
         this.units = units;
      }

      @Override
      protected WeatherResult doInBackground(Void... params) {
         return repository.loadForecast(taskCity, units);
      }

      // not called if the task was cancelled
      @Override
      protected void onPostExecute(WeatherResult weatherResult) {
         result = weatherResult;
         loading = false;
         notifyWeatherChanged();
      }
   }

   // adds or removes a favorite (if city isn't null), then reloads the list
   private class FavoriteTask extends AsyncTask<Void, Void, List<City>> {
      private final City taskCity;
      private final boolean add;

      FavoriteTask(City taskCity, boolean add) {
         this.taskCity = taskCity;
         this.add = add;
         favoriteTasks.add(this);
      }

      @Override
      protected List<City> doInBackground(Void... params) {
         if (taskCity != null) {
            if (add)
               repository.addFavorite(taskCity);
            else
               repository.removeFavorite(taskCity);
         }
         return repository.getFavorites();
      }

      @Override
      protected void onPostExecute(List<City> cities) {
         favoriteTasks.remove(this);
         favorites = cities;
         notifyFavoritesChanged();
      }
   }
}
