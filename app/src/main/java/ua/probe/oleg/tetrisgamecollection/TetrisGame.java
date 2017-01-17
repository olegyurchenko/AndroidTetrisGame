package ua.probe.oleg.tetrisgamecollection;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by oleg on 16.01.17.
 */

public class TetrisGame extends TetrisBase
{
  public static class TetrisFigure extends TetrisBase.Figure
  {

    public void strip()
    {
      for(int row = 0; row < rowCount; row ++)
      {
        boolean empty = true;
        for (int column = 0; column < columnCount; column ++)
        {
          Shape s = shapeMap.get(index(column, row));
          if(s != null)
          {
            empty = false;
            break;
          }
        }

        if(empty)
        {
          //Erase empty row
          Log.d("Game", "Erase empty row = " + row);
          for(int r = row + 1; r < rowCount; r ++) {
            for (int column = 0; column < columnCount; column++) {
              int idx = index(column, r);
              Shape s = shapeMap.get(idx);
              if (s != null)
              {
                shapeMap.remove(idx);
                shapeMap.put(index(column, r - 1), s);
              }
            }
          }
          row --;
          rowCount --;
        }
      }

      for (int column = 0; column < columnCount; column++)
      {
        boolean empty = true;
        for(int row = 0; row < rowCount; row ++)
        {
          Shape s = shapeMap.get(index(column, row));
          if(s != null)
          {
            empty = false;
            break;
          }
        }

        if(empty)
        {
          //Erase empty column
          Log.d("Game", "Erase empty column = " + column);
          for(int c = column + 1; c < columnCount; c ++) {
            for(int row = 0; row < rowCount; row ++)
            {
              int idx = index(c, row);
              Shape s = shapeMap.get(idx);
              if (s != null)
              {
                shapeMap.remove(idx);
                shapeMap.put(index(c - 1, row), s);
              }
            }
          }
          column --;
          columnCount --;
        }
      }
    }

    @Override
    public Figure rotate()
    {
      TetrisFigure f = new TetrisFigure();
      for(int row = 0; row < rowCount; row ++)
      {
        for (int column = 0; column < columnCount; column ++)
        {
          Shape s = get(column, row);
          if(s != null)
            f.put(rowCount - row, column, s);
        }
      }
      f.strip();
      return f;
    }
  }

  public static class Controller extends TetrisBase.Controller
  {
    @Override
    protected Figure onNewFigure() {
      Figure figure = new TetrisFigure();
      figure.put(0, 0, new Shape(Color.GREEN, Color.BLACK));
      figure.put(1, 0, new Shape(Color.GREEN, Color.BLACK));
      figure.put(1, 1, new Shape(Color.GREEN, Color.BLACK));
      figure.put(2, 0, new Shape(Color.GREEN, Color.BLACK));

      return figure;
    }
  }

}
