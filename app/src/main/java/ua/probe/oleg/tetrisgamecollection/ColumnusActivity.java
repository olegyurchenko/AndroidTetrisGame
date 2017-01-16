package ua.probe.oleg.tetrisgamecollection;

/**
 * Created by oleg on 13.01.17.
 */

public class ColumnusActivity extends TetrisBaseActivity
{
  @Override
  protected GameController onGameControllerCreate()
  {
    return new ColumnusGame.Controller();
  }
}
