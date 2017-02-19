package ua.probe.oleg.tetrisgamecollection;

import android.os.Bundle;


public class ColorBallsActivity extends TetrisBaseActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    sectionName = getString(R.string.color_balls_section_name);
    super.onCreate(savedInstanceState);

  }

  @Override
  protected TetrisBase.Controller onGameControllerCreate()
  {
    return new ColorBallsGame.Controller(this, sectionName);
  }

  @Override
  protected Class<?> settinsActivityClass()
  {
    return ColorBallsSettingsActivity.class;
  }

}
