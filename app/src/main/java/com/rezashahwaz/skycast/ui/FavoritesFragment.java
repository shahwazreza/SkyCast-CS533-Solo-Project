// FavoritesFragment.java
// Lists saved cities; tap one to view it, or delete it with Undo
package com.rezashahwaz.skycast.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.City;

import java.util.List;

public class FavoritesFragment extends WeatherFragment {
   private CityAdapter adapter;
   private View emptyTextView;

   // inflates the layout and sets up the RecyclerView
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
      View view =
         inflater.inflate(R.layout.fragment_favorites, container, false);

      RecyclerView recyclerView =
         (RecyclerView) view.findViewById(R.id.recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      recyclerView.addItemDecoration(new ItemDivider(getActivity()));

      adapter = new CityAdapter(new CityAdapter.CityClickListener() {
         // load the city and switch to the Today tab
         @Override
         public void onClick(City city) {
            getMainActivity().selectCity(city);
         }

         // remove the city; MainActivity offers Undo
         @Override
         public void onDeleteClick(City city) {
            getMainActivity().deleteFavorite(city);
         }
      }, true);
      recyclerView.setAdapter(adapter);

      emptyTextView = view.findViewById(R.id.emptyTextView);
      return view;
   }

   // shows the favorites, or the empty-state message
   @Override
   protected void render() {
      List<City> favorites = getMainActivity().getFavorites();
      adapter.setCities(favorites);
      emptyTextView.setVisibility(
         favorites.isEmpty() ? View.VISIBLE : View.GONE);
   }
}
