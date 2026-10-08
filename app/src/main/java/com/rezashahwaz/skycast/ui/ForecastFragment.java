// ForecastFragment.java
// Shows the 7-day high/low and rain charts above a list of the 7 days
package com.rezashahwaz.skycast.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.WeatherResult;

public class ForecastFragment extends WeatherFragment {
   private RecyclerView recyclerView;
   private ForecastAdapter adapter;
   private ProgressBar progressBar;
   private TextView messageTextView;

   // inflates the layout and sets up the RecyclerView
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
      View view =
         inflater.inflate(R.layout.fragment_forecast, container, false);

      recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      recyclerView.addItemDecoration(new ItemDivider(getActivity()));
      adapter = new ForecastAdapter();
      recyclerView.setAdapter(adapter);

      progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
      messageTextView = (TextView) view.findViewById(R.id.messageTextView);
      return view;
   }

   // shows the forecast, a spinner while loading, or the error message
   @Override
   protected void render() {
      MainActivity activity = getMainActivity();
      WeatherResult result = activity.getResult();
      boolean hasForecast = result != null && result.forecast != null;
      boolean loading = activity.isLoading();

      adapter.setResult(hasForecast ? result : null);
      recyclerView.setVisibility(hasForecast ? View.VISIBLE : View.GONE);
      progressBar.setVisibility(
         loading && !hasForecast ? View.VISIBLE : View.GONE);

      boolean hasError = !loading && result != null && result.errorMessage != 0;
      messageTextView.setVisibility(hasError ? View.VISIBLE : View.GONE);
      if (hasError)
         messageTextView.setText(result.errorMessage);
   }
}
