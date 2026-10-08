// SearchActivity.java
// Searches cities by name and returns the chosen one to MainActivity
package com.rezashahwaz.skycast.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.City;
import com.rezashahwaz.skycast.data.WeatherRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
   // key for the chosen City in the result Intent
   public static final String EXTRA_CITY = "city";
   private static final String STATE_RESULTS = "results";

   private WeatherRepository repository;
   private EditText searchEditText;
   private ProgressBar progressBar;
   private TextView messageTextView;
   private CityAdapter adapter;
   private ArrayList<City> results = new ArrayList<>();
   private SearchTask searchTask;

   // sets up the search field, button and results list
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_search);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      repository = new WeatherRepository(this);

      searchEditText = (EditText) findViewById(R.id.searchEditText);
      progressBar = (ProgressBar) findViewById(R.id.progressBar);
      messageTextView = (TextView) findViewById(R.id.messageTextView);

      // search when the keyboard's Search key is pressed
      searchEditText.setOnEditorActionListener(
         new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
               KeyEvent event) {
               if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                  search();
                  return true;
               }
               return false;
            }
         }
      );

      Button searchButton = (Button) findViewById(R.id.searchButton);
      searchButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            search();
         }
      });

      RecyclerView recyclerView =
         (RecyclerView) findViewById(R.id.recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.addItemDecoration(new ItemDivider(this));
      adapter = new CityAdapter(new CityAdapter.CityClickListener() {
         // return the chosen city to MainActivity
         @Override
         public void onClick(City city) {
            Intent data = new Intent();
            data.putExtra(EXTRA_CITY, city);
            setResult(Activity.RESULT_OK, data);
            finish();
         }

         @Override
         public void onDeleteClick(City city) { } // no delete button here
      }, false);
      recyclerView.setAdapter(adapter);

      // restore results after rotation
      if (savedInstanceState != null) {
         @SuppressWarnings("unchecked")
         ArrayList<City> saved = (ArrayList<City>)
            savedInstanceState.getSerializable(STATE_RESULTS);
         if (saved != null) {
            results = saved;
            adapter.setCities(results);
         }
      }
   }

   // keeps the results across rotation
   @Override
   protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putSerializable(STATE_RESULTS, results);
   }

   // stops a running search
   @Override
   protected void onDestroy() {
      super.onDestroy();
      if (searchTask != null)
         searchTask.cancel(false);
   }

   // the Up button returns to MainActivity without choosing a city
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == android.R.id.home) {
         finish();
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

   // validates the query and starts a search
   private void search() {
      String query = searchEditText.getText().toString().trim();
      if (query.isEmpty()) {
         searchEditText.setError(getString(R.string.error_empty_search));
         return;
      }

      // hide the keyboard so the results are visible
      InputMethodManager imm = (InputMethodManager)
         getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

      if (searchTask != null)
         searchTask.cancel(false);
      progressBar.setVisibility(View.VISIBLE);
      messageTextView.setVisibility(View.GONE);
      searchTask = new SearchTask(query);
      searchTask.execute();
   }

   // calls the geocoding API off the UI thread; null result means failure
   private class SearchTask extends AsyncTask<Void, Void, List<City>> {
      private final String query;

      SearchTask(String query) {
         this.query = query;
      }

      @Override
      protected List<City> doInBackground(Void... params) {
         try {
            return repository.searchCities(query);
         }
         catch (Exception e) { // IOException or JSONException
            return null;
         }
      }

      // shows the results, "No cities found" or an error
      @Override
      protected void onPostExecute(List<City> cities) {
         progressBar.setVisibility(View.GONE);
         results = (cities != null) ? new ArrayList<>(cities) :
            new ArrayList<City>();
         adapter.setCities(results);

         int message = 0;
         if (cities == null)
            message = repository.isOnline() ?
               R.string.error_search : R.string.error_offline;
         else if (cities.isEmpty())
            message = R.string.no_cities_found;

         messageTextView.setVisibility(message != 0 ? View.VISIBLE : View.GONE);
         if (message != 0)
            messageTextView.setText(message);
      }
   }
}
