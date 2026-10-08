// CityAdapter.java
// RecyclerView adapter for city lists (favorites and search results)
package com.rezashahwaz.skycast.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.City;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
   // implemented by the screens to respond to taps on a city
   public interface CityClickListener {
      void onClick(City city);
      void onDeleteClick(City city); // only called if deleting is enabled
   }

   // ViewHolder for one city
   public class ViewHolder extends RecyclerView.ViewHolder {
      final TextView nameTextView;
      final TextView regionTextView;
      final ImageButton deleteButton;
      private City city;

      public ViewHolder(View itemView) {
         super(itemView);
         nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
         regionTextView = (TextView) itemView.findViewById(R.id.regionTextView);
         deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);

         itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clickListener.onClick(city);
            }
         });
         deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clickListener.onDeleteClick(city);
            }
         });
      }
   }

   private List<City> cities = new ArrayList<>();
   private final CityClickListener clickListener;
   private final boolean showDelete;

   // constructor
   public CityAdapter(CityClickListener clickListener, boolean showDelete) {
      this.clickListener = clickListener;
      this.showDelete = showDelete;
   }

   // replaces the list of cities
   public void setCities(List<City> cities) {
      this.cities = cities;
      notifyDataSetChanged();
   }

   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(
         R.layout.list_item_city, parent, false);
      return new ViewHolder(view);
   }

   // shows a city's name and "region, country"
   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      City city = cities.get(position);
      holder.city = city;
      holder.nameTextView.setText(city.getName());
      holder.regionTextView.setText(city.getRegionAndCountry());
      holder.deleteButton.setVisibility(showDelete ? View.VISIBLE : View.GONE);
      holder.deleteButton.setContentDescription(
         holder.itemView.getContext().getString(
            R.string.delete_city, city.getName()));
   }

   @Override
   public int getItemCount() {
      return cities.size();
   }
}
