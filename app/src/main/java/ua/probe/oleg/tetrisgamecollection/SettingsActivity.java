package ua.probe.oleg.tetrisgamecollection;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;

import ua.probe.oleg.tetrisgamecollection.colorpicker.LineColorPicker;
import ua.probe.oleg.tetrisgamecollection.colorpicker.OnColorChangedListener;

public class SettingsActivity extends Activity
  implements View.OnClickListener
{
  String sectionName;
  //EditText speedEdit, complexEdit;
  Spinner columnsSpinner, rowsSpinner, speedSpinner, complexSpinner;
  TetrisBase.Settings settings;
  Switch showNextFigureSwitch, showScoreSwitch, showGudeLinesSwitch,
    useAccelerometerSwitch, useTouchSwitch, useShakeSwitch;

  LineColorPicker glassColorPicker;
  EditText glassColorEdit;
  View glassColorView;


  protected final int[] speedSeries = new int[]{
    100, 200, 300, 400, 500, 800,
    1000, 2000, 3000, 4000, 5000, 8000,
    100000000
  };

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    Bundle b = getIntent().getExtras();
    sectionName = b.getString("sectionName");

    settings = new TetrisBase.Settings();
    settings.load(this, sectionName);

    columnsSpinner = (Spinner) findViewById(R.id.spinner_columns);
    rowsSpinner = (Spinner) findViewById(R.id.spinner_rows);
    speedSpinner = (Spinner) findViewById(R.id.spinner_speed);
    complexSpinner = (Spinner) findViewById(R.id.spinner_complex);


    showNextFigureSwitch = (Switch) findViewById(R.id.switch_next_figure);
    showScoreSwitch = (Switch) findViewById(R.id.switch_show_score);
    showGudeLinesSwitch = (Switch) findViewById(R.id.switch_show_guide_line);
    useAccelerometerSwitch = (Switch) findViewById(R.id.switch_use_accelerometer);
    useTouchSwitch = (Switch) findViewById(R.id.switch_use_touch);
    useShakeSwitch = (Switch) findViewById(R.id.switch_use_shake);

    glassColorPicker = (LineColorPicker) findViewById(R.id.picker_glass_color);
    glassColorEdit = (EditText) findViewById(R.id.edit_glass_color);
    glassColorView = findViewById(R.id.view_glass_color);

    glassColorEdit.addTextChangedListener(new TextWatcher() {

      public void afterTextChanged(Editable s) {}

      public void beforeTextChanged(CharSequence s, int start,
                                    int count, int after) {
      }

      public void onTextChanged(CharSequence s, int start,
                                int before, int count) {
        onGlassColorEditChanged();}
    });

    glassColorPicker.setOnColorChangedListener(new OnColorChangedListener() {
      @Override
      public void onColorChanged(int c) {
        onGlassColorChanged(c);
      }
    });


    onSetup();
  }

  /**
   * Generate palette of count colors
   */
  private int[] generateColors(int from, int to, int count) {
    int[] palette = new int[count];

    float[] hsvFrom = new float[3];
    float[] hsvTo = new float[3];

    Color.colorToHSV(from, hsvFrom);
    Color.colorToHSV(to, hsvTo);

    float step = (hsvTo[0] - hsvFrom[0]) / count;

    for (int i = 0; i < count; i++) {
      palette[i] = Color.HSVToColor(hsvFrom);

      hsvFrom[0] += step;
    }

    return palette;
  }

  private void onGlassColorChanged(int c) {
    glassColorEdit.setText(String.format("#%06X", (0xFFFFFF & c)));
    glassColorView.setBackgroundColor(c);

    settings.glassColor = c;
  }


  private void onGlassColorEditChanged() {
    int color;
    String text = glassColorEdit.getText().toString();
    if(text.isEmpty())
      return;

    //Log.d("SettingsActivity", String.format("Text:'%s'", text));
    try {
      color = Color.parseColor(text);
    }
    catch(IllegalArgumentException e)
    {
      //Log.d("SettingsActivity", e.getMessage());
      return;
    }

    //Log.d("SettingsActivity", String.format("Text:'%s' color:%d", text, color));
    if(color == settings.glassColor)
      return;

    glassColorPicker.setSelectedColor(color);
    glassColorView.setBackgroundColor(color);

    settings.glassColor = color;

  }

  protected void onSetup()
  {
    //Log.d("onSetup", "settings.columnCount=" + settings.columnCount);
    //Log.d("onSetup", "settings.rowCount=" + settings.rowCount);

    ArrayList<String> strings;
    int selection = 0;

    strings = new ArrayList<>();
    for (int i = 0; i <= TetrisBase.Settings.MaxColumns - TetrisBase.Settings.MinColumns; i ++)
    {
      int column = i + TetrisBase.Settings.MinColumns;
      strings.add("" + column);
      if(settings.columnCount == column)
        selection = i;
    }

    ArrayAdapter<String> adapter;

    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    columnsSpinner.setAdapter(adapter);
    columnsSpinner.setSelection(selection);
    columnsSpinner.setPrompt(getString(R.string.column_count));

    strings = new ArrayList<>();
    for (int i = 0; i <= TetrisBase.Settings.MaxRows - TetrisBase.Settings.MinRows; i ++)
    {
      int row = i + TetrisBase.Settings.MinRows;
      strings.add("" + row);
      if(settings.rowCount == row)
        selection = i;
    }

    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    rowsSpinner.setAdapter(adapter);
    rowsSpinner.setSelection(selection);
    rowsSpinner.setPrompt(getString(R.string.row_count));

    strings = new ArrayList<>();
    for (int i = 0; i < speedSeries.length; i ++)
    {
      int speed = speedSeries[i];
      if(speed < 30000)
        strings.add("1 / " + speed + "" + getString(R.string.ms));
      else
        strings.add("0");
      if(settings.tickTime == speed)
        selection = i;
    }

    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    speedSpinner.setAdapter(adapter);
    speedSpinner.setSelection(selection);
    speedSpinner.setPrompt(getString(R.string.speed));


//    columnsBar.setProgress(settings.columnCount - settings.MinColumns);
//    rowsBar.setProgress(settings.rowCount - settings.MinRows);
//    speedBar.setProgress(settings.tickTime);
//    complexBar.setProgress(settings.complexRate);

    showNextFigureSwitch.setChecked(settings.showNextFigure);
    showScoreSwitch.setChecked(settings.showScore);
    showGudeLinesSwitch.setChecked(settings.showGuideLines);
    useAccelerometerSwitch.setChecked(settings.useAccelerometer);
    useTouchSwitch.setChecked(settings.useTouch);
    useShakeSwitch.setChecked(settings.useShake);

    //speedEdit.setText("" + settings.tickTime);
    //complexEdit.setText("" + settings.complexRate);

    // set color palette
    glassColorPicker.setColors(generateColors(Color.BLUE, Color.RED, 16));

    // set selected color [optional]
    glassColorPicker.setSelectedColor(settings.glassColor);

    onGlassColorChanged(settings.glassColor);


  }

  protected void onSave() {

    //settings.tickTime = Integer.parseInt(speedEdit.getText().toString());
    //settings.complexRate = Integer.parseInt(complexEdit.getText().toString());
    //    settings.columnCount = columnsBar.getProgress() + settings.MinColumns;
    //    settings.rowCount = rowsBar.getProgress() + settings.MinRows;
    //    settings.tickTime = speedBar.getProgress();
    //    settings.complexRate = complexBar.getProgress();

    settings.columnCount = columnsSpinner.getSelectedItemPosition() + TetrisBase.Settings.MinColumns;
    settings.rowCount = rowsSpinner.getSelectedItemPosition() + TetrisBase.Settings.MinRows;
    settings.tickTime = speedSeries[speedSpinner.getSelectedItemPosition()];

    settings.showNextFigure = showNextFigureSwitch.isChecked();
    settings.showScore = showScoreSwitch.isChecked();
    settings.showGuideLines = showGudeLinesSwitch.isChecked();
    settings.useAccelerometer = useAccelerometerSwitch.isChecked();
    settings.useTouch = useTouchSwitch.isChecked();
    settings.useShake = useShakeSwitch.isChecked();

    //Log.d("onClick", "settings.columnCount=" + settings.columnCount);
    //Log.d("onClick", "settings.rowCount=" + settings.rowCount);
    settings.save(this, sectionName);

  }

  @Override
  public void onClick(View v) {
    int id = v.getId();

    if(id == R.id.btn_ok)
    {
      onSave();
    }

    switch(id)
    {
      case R.id.btn_ok:
      case R.id.btn_cancel:
        Intent intent = new Intent();
        //intent.putExtra("name", etName.getText().toString());
        setResult(id == R.id.btn_ok ? RESULT_OK : RESULT_CANCELED, intent);
        finish();
        break;

      case R.id.btn_default:
        settings = new TetrisBase.Settings();
        onSetup();
        break;
    }
  }



}
