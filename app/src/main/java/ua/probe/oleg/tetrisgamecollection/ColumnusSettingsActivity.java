package ua.probe.oleg.tetrisgamecollection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ColumnusSettingsActivity extends SettingsActivity {

  int [] complexSeries = new int[] {
    2, 4, 8, 16, 32
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onSetup() {

    super.onSetup();


    ArrayList<String> strings;
    int selection = 0;

    strings = new ArrayList<>();

    if(settings.complexRate == 0)
      settings.complexRate = 8; //Columnus standard

    for (int i = 0; i < complexSeries.length; i ++)
    {
      int complex = complexSeries[i];
      strings.add(complex + " " + getString(R.string.of_colors));
      if(settings.complexRate == complex)
        selection = i;
    }

    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    complexSpinner.setAdapter(adapter);
    complexSpinner.setSelection(selection);
    complexSpinner.setPrompt(getString(R.string.complex));

  }

  @Override
  protected void onSave() {

    settings.complexRate = complexSeries[complexSpinner.getSelectedItemPosition()];

    super.onSave();
  }

}
