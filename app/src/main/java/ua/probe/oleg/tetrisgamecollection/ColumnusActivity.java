package ua.probe.oleg.tetrisgamecollection;

import android.os.Bundle;

/**
 * Created by oleg on 13.01.17.
 */

public class ColumnusActivity extends TetrisBaseActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    sectionName = getString(R.string.columnus_section_name);
    super.onCreate(savedInstanceState);
  }

  @Override
  protected TetrisBase.Controller onGameControllerCreate()
  {
    return new ColumnusGame.Controller(this, sectionName);
  }
}
