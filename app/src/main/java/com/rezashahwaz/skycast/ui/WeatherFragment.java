// WeatherFragment.java
// Base class for the tab fragments: registers with MainActivity for updates
package com.rezashahwaz.skycast.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public abstract class WeatherFragment extends Fragment
   implements MainActivity.WeatherListener {

   // redraws the fragment from MainActivity's current state
   protected abstract void render();

   // starts listening for weather and favorites changes
   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      ((MainActivity) context).addWeatherListener(this);
   }

   // stops listening
   @Override
   public void onDetach() {
      ((MainActivity) getActivity()).removeWeatherListener(this);
      super.onDetach();
   }

   // draws the current state once the views exist
   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      render();
   }

   @Override
   public void onWeatherChanged() {
      if (getView() != null)
         render();
   }

   @Override
   public void onFavoritesChanged() {
      if (getView() != null)
         render();
   }

   // returns the hosting MainActivity
   protected MainActivity getMainActivity() {
      return (MainActivity) getActivity();
   }
}
