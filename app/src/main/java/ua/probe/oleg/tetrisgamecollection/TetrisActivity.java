package ua.probe.oleg.tetrisgamecollection;

import android.os.Bundle;


public class TetrisActivity extends TetrisBaseActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    sectionName = getString(R.string.tetris_section_name);
    super.onCreate(savedInstanceState);
  }
  @Override
  protected TetrisBase.Controller onGameControllerCreate()
  {
    return new TetrisGame.Controller(this, sectionName);
  }

  @Override
  protected Class<?> settinsActivityClass()
  {
    return TetrisSettingsActivity.class;
  }

}
