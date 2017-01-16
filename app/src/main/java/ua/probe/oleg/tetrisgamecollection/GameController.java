package ua.probe.oleg.tetrisgamecollection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by oleg on 13.01.17.
 */

public class GameController
{
  private int unique;
  private boolean modified = false;
  Rect clientRect;

  public boolean isModified()
  {
    return modified;
  }

  protected void setModified(boolean m)
  {
    modified = m;
  }

  public void onDraw(Canvas canvas) {
    setModified(false);
  }

  protected void onUpdate()
  {
    setModified(true);
  }

  public void onQuant()
  {
    //onUpdate();
  }

  public Rect getRect()
  {
    return clientRect;
  }

  public void setRect(Rect r)
  {
    clientRect = r;
  }

  public void onTouchDown(float x, float y)
  {
    onUpdate();
  }

  public void onTouchUp(float x, float y)
  {
    onUpdate();
  }

  public void onTouchMove(float x, float y)
  {
    onUpdate();
  }

  public void moveLeft()
  {
    onUpdate();
  }

  public void moveRight()
  {
    onUpdate();
  }

  public void moveDown()
  {
    onUpdate();
  }

  public void rotate()
  {
    onUpdate();
  }
}