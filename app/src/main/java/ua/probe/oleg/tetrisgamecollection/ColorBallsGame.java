package ua.probe.oleg.tetrisgamecollection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by oleg on 16.01.17.
 */

public class ColorBallsGame extends TetrisBase {

  /*-----------------------------------------------------------------------------------------------*/
  public static class Ball extends TetrisBase.Shape
  {
    Ball(int color)
    {
      super(color, Color.BLACK);
    }

    @Override
    void onDraw(Canvas canvas, int x, int y, int width, int height)
    {
      Paint p = new Paint();
      Rect rect = new Rect(x, y, x + width, y + height);


      // перенастраивам кисть на заливку
      p.setColor(fillColor);
      p.setStyle(Paint.Style.FILL);
      canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.height() / 2, Math.min(rect.width(), rect.height())/ 2, p);

      // перенастраивам кисть на контуры
      p.setColor(borderColor);
      p.setStyle(Paint.Style.STROKE);
      canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.height() / 2, Math.min(rect.width(), rect.height())/ 2, p);
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  public static class BallsFigure extends Figure
  {
    @Override
    public Figure rotate()
    {
      Figure f = new BallsFigure();
      for(int row = 0; row < rowCount; row ++)
      {
        int destRow = row + 1;
        if(destRow >= rowCount)
          destRow = 0;
        f.put(0, destRow, get(0, row));
      }

      return f;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  public static class BallsGlass extends TetrisBase.Glass
  {
    public BallsGlass(int colCount, int rowCount)
    {
      super(colCount, rowCount);
    }

    @Override
    boolean annigilation()
    {
      int modifications = 0;

      boolean modified;


      //do {
        modified = false;

        for (int row = 0; row < rowCount; row ++)
        {
          for (int column = 0; column < columnCount; column++)
          {
            Shape s = get(column, row);
            if(s == null)
              continue;
            // [x]
            // [ ]
            if (row < rowCount - 1 && get(column, row + 1) == null) {
              //Found pended block
              put(column, row + 1, s);
              put(column, row, null);
              modified = true;
              modifications ++;
              continue;
            }

            //    [X]
            // [ ][x]
            if (row < rowCount - 1
              && column > 0
              && get(column - 1, row + 1) == null) {
              //Found pended block
              put(column - 1, row + 1, s);
              put(column, row, null);
              modified = true;
              modifications ++;
              continue;
            }

            // [X]
            // [x][ ]
            if (row < rowCount - 1
              && column < columnCount - 1
              && get(column + 1, row + 1) == null) {
              //Found pended block
              put(column + 1, row + 1, s);
              put(column, row, null);
              modified = true;
              modifications ++;
              continue;
            }

          }

        }


      //} while (modified);

      ArrayList<Cell> list = new ArrayList<Cell>();

      //2) Find 3 or more colors and remove it
      for(int row = 0; row < rowCount; row ++) {
        for (int column = 0; column < columnCount; column++)
        {
          Shape s = get(column, row);
          if(s == null)
            continue;

          //[x][X][x]
          if(column > 0 && column < columnCount - 1)
          {
            Shape l = get(column - 1, row);
            Shape r = get(column + 1, row);
            if(l != null
              && r != null
              && l.color() == s.color()
              && r.color() == s.color())
            {
              list.add(new Cell(column, row));
              list.add(new Cell(column - 1, row));
              list.add(new Cell(column + 1, row));
            }

          }

          //[x]
          //[X]
          //[x]
          if(row > 0 && row < rowCount - 1)
          {
            Shape t = get(column, row - 1);
            Shape b = get(column, row + 1);
            if(t != null
              && b != null
              && t.color() == s.color()
              && b.color() == s.color())
            {
              list.add(new Cell(column, row));
              list.add(new Cell(column, row - 1));
              list.add(new Cell(column, row + 1));
            }

          }

          //[x]
          //   [X]
          //      [x]
          if(row > 0 && row < rowCount - 1 && column > 0 && column < columnCount - 1)
          {
            Shape t = get(column - 1, row - 1);
            Shape b = get(column + 1, row + 1);
            if(t != null
              && b != null
              && t.color() == s.color()
              && b.color() == s.color())
            {
              list.add(new Cell(column, row));
              list.add(new Cell(column - 1, row - 1));
              list.add(new Cell(column + 1, row + 1));
              continue;
            }
          }

          //      [x]
          //   [X]
          //[x]
          if(row > 0 && row < rowCount - 1 && column > 0 && column < columnCount - 1)
          {
            Shape t = get(column - 1, row + 1);
            Shape b = get(column + 1, row - 1);
            if(t != null
              && b != null
              && t.color() == s.color()
              && b.color() == s.color())
            {
              list.add(new Cell(column, row));
              list.add(new Cell(column - 1, row + 1));
              list.add(new Cell(column + 1, row - 1));
            }
          }

        }
      }

      for(Cell c:list)
      {
        put(c.column(), c.row(), null);
      }

      return modifications > 0 || !list.isEmpty();

    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  public static class Controller extends TetrisBase.Controller {

    @Override
    protected Glass onGlassCreate()
    {
      return new BallsGlass(8, 16);
    }

    @Override
    protected Figure onNewFigure() {
      Figure figure = new BallsFigure();
      figure.put(0, 0, new Ball(randomColor()));
      figure.put(0, 1, new Ball(randomColor()));
      figure.put(0, 2, new Ball(randomColor()));

      return figure;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
}
