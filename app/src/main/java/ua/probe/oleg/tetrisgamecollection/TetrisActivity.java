package ua.probe.oleg.tetrisgamecollection;

/**
 * Created by oleg on 13.01.17.
 */

public class TetrisActivity extends TetrisBaseActivity
{
  @Override
  protected TetrisBase.Controller onGameControllerCreate()
  {
    return new TetrisGame.Controller(this);
  }

}
