package ua.probe.oleg.tetrisgamecollection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Color balls classes
 */

class ColorBallsGame extends TetrisBase {

  /*-----------------------------------------------------------------------------------------------*/
  private static Bitmap ballBitmap = null;

  private static class Ball extends Square
  {
    Ball(int color)
    {
      super(color, Color.BLACK);
    }
    Ball() {super();}
    @Override
    void onDraw(Canvas canvas, int x, int y, int width, int height)
    {
      rect.set(x, y, x + width, y + height);

      // перенастраивам кисть на заливку
      paint.setColor(fillColor);
      paint.setStyle(Paint.Style.FILL);
      canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.height() / 2, Math.min(rect.width(), rect.height())/ 2, paint);

      if(ballBitmap != null)  {
        canvas.drawBitmap(ballBitmap, null, rect, paint);
      }
      else {
        // перенастраивам кисть на контуры
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.height() / 2, Math.min(rect.width(), rect.height()) / 2, paint);
      }
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  private static class BallsFigure extends Figure
  {
    @Override
    public Figure rotateLeft()
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

    @Override
    public Figure rotateRight()
    {
      Figure f = new BallsFigure();
      for(int row = 0; row < rowCount; row ++)
      {
        int destRow = row - 1;
        if(destRow < 0)
          destRow = rowCount - 1;
        f.put(0, destRow, get(0, row));
      }

      return f;
    }

    Square onNewShape()
    {
      return new Ball();
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  private static class BallsGlass extends TetrisBase.Glass
  {
    BallsGlass(int colCount, int rowCount)
    {
      super(colCount, rowCount);
    }

    @Override
    boolean annigilation()
    {
      int modifications = 0;

      //boolean modified;


      //do {
        //modified = false;

        for (int row = 0; row < rowCount; row ++)
        {
          for (int column = 0; column < columnCount; column++)
          {
            Square s = get(column, row);
            if(s == null)
              continue;
            // [x]
            // [ ]
            if (row < rowCount - 1 && get(column, row + 1) == null) {
              //Found pended block
              put(column, row + 1, s);
              put(column, row, null);
              //modified = true;
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
              //modified = true;
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
              //modified = true;
              modifications ++;
            }

          }

        }


      //} while (modified);

      ArrayList<Cell> list = new ArrayList<>();

      //2) Find 3 or more colors and remove it
      for(int row = 0; row < rowCount; row ++) {
        for (int column = 0; column < columnCount; column++)
        {
          Square s = get(column, row);
          if(s == null)
            continue;

          //[x][X][x]
          if(column > 0 && column < columnCount - 1)
          {
            Square l = get(column - 1, row);
            Square r = get(column + 1, row);
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
            Square t = get(column, row - 1);
            Square b = get(column, row + 1);
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
            Square t = get(column - 1, row - 1);
            Square b = get(column + 1, row + 1);
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
            Square t = get(column - 1, row + 1);
            Square b = get(column + 1, row - 1);
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
        if(get(c.column(), c.row()) != null) {
          addRemovedShapes(1);
          put(c.column(), c.row(), null);
        }
      }

      return modifications > 0 || !list.isEmpty();

    }

    Square onNewShape()
    {
      return new Ball();
    }

    @Override
    int calcContentRating()
    {
      final int
        RATE_SCORE_WAIGHT = 20,
        RATE_EMPTY_ROW = 1,
        RATE_DOUBLED_SHAPE = 3,
        RATE_CLOSED_DOUBLED_SHAPE = -3;


      Glass g;
      try {
        g = clone();
      } catch (CloneNotSupportedException e) {
        Log.e("Glass", e.getMessage());
        return 0;
      }

      int rating = 0;
      long oldScore = g.getScore();
      g.setScoreScale(1);
      while (g.annigilation())
        rating ++;
      rating += (g.getScore() - oldScore) * RATE_SCORE_WAIGHT;

      //Calculate empty rows
      for(int row = 0; row < rowCount; row ++) {
        boolean empty = true;
        for (int column = 0; column < columnCount; column++) {
          if(null == g.get(column, row))
            continue;

          empty = false;
          break;
        }

        if(empty)
          rating += RATE_EMPTY_ROW;
      }

      //Find 2 colors sequences
      for (int column = 0; column < columnCount; column++) {
        for(int row = 0; row < rowCount; row ++) {

          if(null != g.get(column, row))
            break;

          //[x][x][X]
          if(column >= 2)
          {
            Square s1 = g.get(column - 1, row);
            Square s2 = g.get(column - 2, row);
            if(s1 != null && s2 != null && s1.color() == s2.color())
            {
              rating += RATE_DOUBLED_SHAPE;
            }
          }

          //[X][x][x]
          if(column < columnCount - 2)
          {
            Square s1 = g.get(column + 1, row);
            Square s2 = g.get(column + 2, row);
            if(s1 != null && s2 != null && s1.color() == s2.color())
            {
              rating += RATE_DOUBLED_SHAPE;
            }
          }

          //[x][x][y][X]
          if(column >= 3)
          {
            Square s1 = g.get(column - 1, row);
            Square s2 = g.get(column - 2, row);
            Square s3 = g.get(column - 3, row);
            if(s1 != null && s2 != null && s3 != null
              && s1.color() != s2.color()
              && s2.color() == s3.color())
            {
              rating += RATE_CLOSED_DOUBLED_SHAPE;
            }
          }

          //[X][y][x][x]
          if(column < columnCount - 3)
          {
            Square s1 = g.get(column + 1, row);
            Square s2 = g.get(column + 2, row);
            Square s3 = g.get(column + 3, row);
            if(s1 != null && s2 != null && s3 != null
              && s1.color() != s2.color()
              && s2.color() == s3.color())
            {
              rating += RATE_CLOSED_DOUBLED_SHAPE;
            }
          }

          //[X]
          //[x]
          //[x]
          if(row < rowCount - 2)
          {
            Square s1 = g.get(column, row + 1);
            Square s2 = g.get(column, row + 2);
            if(s1 != null && s2 != null && s1.color() == s2.color())
            {
              rating += RATE_DOUBLED_SHAPE;
            }
          }

          //[X]
          //[y]
          //[x]
          //[x]
          if(row < rowCount - 3)
          {
            Square s1 = g.get(column, row + 1);
            Square s2 = g.get(column, row + 2);
            Square s3 = g.get(column, row + 3);
            if(s1 != null && s2 != null && s3 != null
              && s1.color() != s2.color()
              && s2.color() == s3.color())
            {
              rating += RATE_CLOSED_DOUBLED_SHAPE;
            }
          }

          //[X]
          //   [x]
          //      [x]
          if(column < columnCount - 2 && row < rowCount - 2)
          {
            Square s1 = g.get(column + 1, row + 1);
            Square s2 = g.get(column + 2, row + 2);
            if(s1 != null && s2 != null && s1.color() == s2.color())
            {
              rating += RATE_DOUBLED_SHAPE;
            }
          }

          //[X]
          //   [y]
          //      [x]
          //         [x]
          if(column < columnCount - 3 && row < rowCount - 3)
          {
            Square s1 = g.get(column + 1, row + 1);
            Square s2 = g.get(column + 2, row + 2);
            Square s3 = g.get(column + 3, row + 3);
            if(s1 != null && s2 != null && s3 != null
              && s1.color() != s2.color()
              && s2.color() == s3.color())
            {
              rating += RATE_CLOSED_DOUBLED_SHAPE;
            }
          }

          //      [X]
          //   [x]
          //[x]
          if(column >= 2 && row < rowCount - 2)
          {
            Square s1 = g.get(column - 1, row + 1);
            Square s2 = g.get(column - 2, row + 2);
            if(s1 != null && s2 != null && s1.color() == s2.color())
            {
              rating += RATE_DOUBLED_SHAPE;
            }
          }

          //         [X]
          //      [y]
          //   [x]
          //[x]
          if(column >= 3 && row < rowCount - 3)
          {
            Square s1 = g.get(column - 1, row + 1);
            Square s2 = g.get(column - 2, row + 2);
            Square s3 = g.get(column - 3, row + 3);
            if(s1 != null && s2 != null && s3 != null
              && s1.color() != s2.color()
              && s2.color() == s3.color())
            {
              rating += RATE_CLOSED_DOUBLED_SHAPE;
            }
          }

        }
      }

      return rating;
    }

  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Controller extends TetrisBase.Controller {

    Controller(Context c, String sectionName)
    {
      super(c, sectionName);
      ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ball);
    }

    @Override
    protected Glass onGlassCreate()
    {
      return new BallsGlass(settings.columnCount, settings.rowCount);
    }

    @Override
    String[] ratingText()
    {
      String[] strings = super.ratingText();
      Statistics statistics = glass.getStatistics();


      if(strings.length > 3)
      {
        strings[3] = String.format(Locale.getDefault(), "%s: %,d",
          context.getString(R.string.number_of_balls),
          statistics.squareCount);
      }

      return strings;
    }

    @Override
    protected Figure onNewFigure() {
      Figure figure = new BallsFigure();
      figure.put(0, 0, new Ball(randomComplexColor()));
      figure.put(0, 1, new Ball(randomComplexColor()));
      figure.put(0, 2, new Ball(randomComplexColor()));

      return figure;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
}
