package ua.probe.oleg.tetrisgamecollection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity
  implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
  String sectionName;
  //EditText speedEdit, complexEdit;
  SeekBar speedBar, complexBar;
  TetrisBase.Settings settings;
  TextView speedLabel, complexLabel;
  Switch showNextFigureSwitch;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    Bundle b = getIntent().getExtras();
    sectionName = b.getString("sectionName");

    //Get controls
    //speedEdit = (EditText) findViewById(R.id.editSpeed);
    //complexEdit = (EditText) findViewById(R.id.editComplex);
    speedBar = (SeekBar) findViewById(R.id.seekSpeed);
    complexBar = (SeekBar) findViewById(R.id.seekComplex);

    speedLabel = (TextView) findViewById(R.id.speel_label);
    complexLabel = (TextView) findViewById(R.id.complex_label);

    showNextFigureSwitch = (Switch) findViewById(R.id.switch_next_figure);


    speedBar.setOnSeekBarChangeListener(this);
    complexBar.setOnSeekBarChangeListener(this);

    settings = new TetrisBase.Settings();
    settings.load(this, sectionName);

    speedBar.setProgress(settings.speedRate);
    complexBar.setProgress(settings.complexRate);
    showNextFigureSwitch.setChecked(settings.showNextFigure);

    //speedEdit.setText("" + settings.speedRate);
    //complexEdit.setText("" + settings.complexRate);

  }

  @Override
  public void onClick(View v) {
    int id = v.getId();

    if(id == R.id.btnOk)
    {
      //settings.speedRate = Integer.parseInt(speedEdit.getText().toString());
      //settings.complexRate = Integer.parseInt(complexEdit.getText().toString());
      settings.speedRate = speedBar.getProgress();
      settings.complexRate = complexBar.getProgress();
      settings.showNextFigure = showNextFigureSwitch.isChecked();

      settings.save(this, sectionName);
    }

    Intent intent = new Intent();
    //intent.putExtra("name", etName.getText().toString());
    setResult(id == R.id.btnOk ? RESULT_OK : RESULT_CANCELED, intent);
    finish();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
  {

    int id = seekBar.getId();
    switch (id) {
      case R.id.seekSpeed:
        speedLabel.setText(progress + "%");
        break;
      case R.id.seekComplex:
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
