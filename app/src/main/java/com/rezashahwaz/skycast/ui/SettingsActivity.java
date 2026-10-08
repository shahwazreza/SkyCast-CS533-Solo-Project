// SettingsActivity.java
// Lets the user choose °F + mph or °C + km/h; saved in SharedPreferences
package com.rezashahwaz.skycast.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.rezashahwaz.skycast.R;
import com.rezashahwaz.skycast.data.SettingsStore;

public class SettingsActivity extends AppCompatActivity {
   // checks the saved unit option and saves any change right away
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      final SettingsStore settings = new SettingsStore(this);
      RadioGroup unitsRadioGroup =
         (RadioGroup) findViewById(R.id.unitsRadioGroup);
      unitsRadioGroup.check(
         SettingsStore.UNITS_METRIC.equals(settings.getUnits()) ?
            R.id.metricRadioButton : R.id.imperialRadioButton);

      unitsRadioGroup.setOnCheckedChangeListener(
         new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               settings.setUnits(checkedId == R.id.metricRadioButton ?
                  SettingsStore.UNITS_METRIC : SettingsStore.UNITS_IMPERIAL);
            }
         }
      );
   }

   // the Up button returns to MainActivity, which reloads if units changed
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == android.R.id.home) {
         finish();
         return true;
      }
      return super.onOptionsItemSelected(item);
   }
}
