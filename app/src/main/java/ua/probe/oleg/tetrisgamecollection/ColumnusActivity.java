package ua.probe.oleg.tetrisgamecollection;

import android.os.Bundle;

/**
 * Created by oleg on 13.01.17.
 */

public class ColumnusActivity extends TetrisBaseActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    sectionName = "Columnus";
    super.onCreate(savedInstanceState);
  }

  @Override
  protected TetrisBase.Controller onGameControllerCreate()
  {
    return new ColumnusGame.Controller(this);
  }
}
