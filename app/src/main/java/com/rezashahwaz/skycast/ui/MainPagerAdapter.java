// MainPagerAdapter.java
// FragmentPagerAdapter that supplies the Today, Forecast and Favorites tabs
package com.rezashahwaz.skycast.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rezashahwaz.skycast.R;

public class MainPagerAdapter extends FragmentPagerAdapter {
   private final Context context;

   // constructor
   public MainPagerAdapter(FragmentManager manager, Context context) {
      super(manager);
      this.context = context;
   }

   // creates the fragment for a tab
   @Override
   public Fragment getItem(int position) {
      switch (position) {
         case MainActivity.TAB_FORECAST:
            return new ForecastFragment();
         case MainActivity.TAB_FAVORITES:
            return new FavoritesFragment();
         default:
            return new TodayFragment();
      }
   }

   @Override
   public int getCount() {
      return 3;
   }

   // returns a tab's title
   @Override
   public CharSequence getPageTitle(int position) {
      switch (position) {
         case MainActivity.TAB_FORECAST:
            return context.getString(R.string.tab_forecast);
         case MainActivity.TAB_FAVORITES:
            return context.getString(R.string.tab_favorites);
         default:
            return context.getString(R.string.tab_today);
      }
   }
}
