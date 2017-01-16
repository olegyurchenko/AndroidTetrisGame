package ua.probe.oleg.tetrisgamecollection;

/**
 * Created by oleg on 13.01.17.
 */

public class ColorBallsActivity extends TetrisBaseActivity
{
  @Override
  protected GameController onGameControllerCreate()
  {
    return new ColorBallsGame.Controller();
  }
}
