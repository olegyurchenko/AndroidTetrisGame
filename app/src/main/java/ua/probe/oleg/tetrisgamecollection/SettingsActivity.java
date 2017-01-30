package ua.probe.oleg.tetrisgamecollection;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends Activity
  implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
  String sectionName;
  //EditText speedEdit, complexEdit;
  SeekBar columnsBar, rowsBar, speedBar, complexBar;
  TetrisBase.Settings settings;
  TextView columnsLabel, rowsLabel, speedLabel, complexLabel;
  Switch showNextFigureSwitch, showScoreSwitch, showGudeLinesSwitch, useAccelerometerSwitch, useTouchSwitch;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    Bundle b = getIntent().getExtras();
    sectionName = b.getString("sectionName");

    settings = new TetrisBase.Settings();
    settings.load(this, sectionName);

    //Get controls
    //speedEdit = (EditText) findViewById(R.id.editSpeed);
    //complexEdit = (EditText) findViewById(R.id.editComplex);
    columnsBar = (SeekBar) findViewById(R.id.seek_columns);
    rowsBar = (SeekBar) findViewById(R.id.seek_rows);
    speedBar = (SeekBar) findViewById(R.id.seek_speed);
    complexBar = (SeekBar) findViewById(R.id.seek_complex);

    columnsLabel = (TextView) findViewById(R.id.text_columns);
    rowsLabel = (TextView) findViewById(R.id.text_rows);
    speedLabel = (TextView) findViewById(R.id.text_speed);
    complexLabel = (TextView) findViewById(R.id.text_complex);

    showNextFigureSwitch = (Switch) findViewById(R.id.switch_next_figure);
    showScoreSwitch = (Switch) findViewById(R.id.switch_show_score);
    showGudeLinesSwitch = (Switch) findViewById(R.id.switch_show_guide_line);
    useAccelerometerSwitch = (Switch) findViewById(R.id.switch_use_accelerometer);
    useTouchSwitch = (Switch) findViewById(R.id.switch_use_touch);

    columnsBar.setMax(settings.MaxColumns - settings.MinColumns);
    rowsBar.setMax(settings.MaxRows - settings.MinRows);

    columnsBar.setOnSeekBarChangeListener(this);
    rowsBar.setOnSeekBarChangeListener(this);
    speedBar.setOnSeekBarChangeListener(this);
    complexBar.setOnSeekBarChangeListener(this);

    onSetup();
  }

  protected void onSetup()
  {
    Log.d("onSetup", "settings.columnCount=" + settings.columnCount);
    Log.d("onSetup", "settings.rowCount=" + settings.rowCount);

    columnsBar.setProgress(settings.columnCount - settings.MinColumns);
    rowsBar.setProgress(settings.rowCount - settings.MinRows);
    speedBar.setProgress(settings.speedRate);
    complexBar.setProgress(settings.complexRate);

    showNextFigureSwitch.setChecked(settings.showNextFigure);
    showScoreSwitch.setChecked(settings.showScore);
    showGudeLinesSwitch.setChecked(settings.showGuideLines);
    useAccelerometerSwitch.setChecked(settings.useAccelerometer);
    useTouchSwitch.setChecked(settings.useTouch);

    //speedEdit.setText("" + settings.speedRate);
    //complexEdit.setText("" + settings.complexRate);

    onProgressChanged(columnsBar, columnsBar.getProgress(), false);
    onProgressChanged(rowsBar, rowsBar.getProgress(), false);
    onProgressChanged(speedBar, speedBar.getProgress(), false);
    onProgressChanged(complexBar, complexBar.getProgress(), false);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();

    if(id == R.id.btn_ok)
    {
      //settings.speedRate = Integer.parseInt(speedEdit.getText().toString());
      //settings.complexRate = Integer.parseInt(complexEdit.getText().toString());
      settings.columnCount = columnsBar.getProgress() + settings.MinColumns;
      settings.rowCount = rowsBar.getProgress() + settings.MinRows;
      settings.speedRate = speedBar.getProgress();
      settings.complexRate = complexBar.getProgress();

      settings.showNextFigure = showNextFigureSwitch.isChecked();
      settings.showScore = showScoreSwitch.isChecked();
      settings.showGuideLines = showGudeLinesSwitch.isChecked();
      settings.useAccelerometer = useAccelerometerSwitch.isChecked();
      settings.useTouch = useTouchSwitch.isChecked();

      //Log.d("onClick", "settings.columnCount=" + settings.columnCount);
      //Log.d("onClick", "settings.rowCount=" + settings.rowCount);
      settings.save(this, sectionName);
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

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
  {

    int id = seekBar.getId();
    switch (id) {
      case R.id.seek_columns:
        columnsLabel.setText(progress + settings.MinColumns + "");
        break;
      case R.id.seek_rows:
        rowsLabel.setText(progress + settings.MinRows + "");
        break;
      case R.id.seek_speed:
        speedLabel.setText(progress + "%");
        break;
      case R.id.seek_complex:
        complexLabel.setText(progress + "%");
        break;
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {

  }


}
