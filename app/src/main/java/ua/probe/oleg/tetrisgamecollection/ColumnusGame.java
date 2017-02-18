package ua.probe.oleg.tetrisgamecollection;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

/**
 * Columnus game logic.
 */

class ColumnusGame extends TetrisBase {

  /*-----------------------------------------------------------------------------------------------*/
  private static class ColumnusFigure extends Figure
  {
    @Override
    public Figure rotateRight()
    {
      Figure f = new ColumnusFigure();
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
    public Figure rotateLeft()
    {
      Figure f = new ColumnusFigure();
      for(int row = 0; row < rowCount; row ++)
      {
        int destRow = row - 1;
        if(destRow < 0)
          destRow = rowCount - 1;
        f.put(0, destRow, get(0, row));
      }

      return f;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  private static class ColumnusGlass extends TetrisBase.Glass
  {
    ColumnusGlass(int colCount, int rowCount)
    {
      super(colCount, rowCount);
    }

    @Override
    boolean annigilation()
    {
      //1) Find and drop pended block
      //    [x]    ->
      // [x]   [x]    [x][x][x]
      int modifications = 0;

      boolean modified;


      do {
        modified = false;

        for (int row = rowCount - 2; row >= 0; row--) {
          for (int column = 0; column < columnCount; column++) {
            Square s = get(column, row);
            if (s != null && get(column, row + 1) == null) {
              //Found pended block
              put(column, row + 1, s);
              put(column, row, null);
              modified = true;
              modifications ++;
            }
          }
        }
      } while (modified);

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
        if(get(c.column(), c.row()) != null)
        {
          addRemovedShapes(1);
          put(c.column(), c.row(), null);
        }
      }

      return modifications > 0 || !list.isEmpty();

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
  static class Controller extends TetrisBase.Controller
  {
    ArrayList<Integer> colors;


    /*===========================================================*/
    @Override
    int randomColor()
    {
      if(colors == null)
        colors = new ArrayList<>();
      if(colors.isEmpty())
      {

        if(settings.complexRate == 0)
          settings.complexRate = 8;

        colors.add(Color.rgb(255, 0, 0));
        colors.add(Color.rgb(0, 255, 0));


        if(settings.complexRate >= 4)
        {
          colors.add(Color.rgb(200, 200, 200));
          colors.add(Color.BLACK);
          colors.add(Color.rgb(0, 0, 255));
        }

        if(settings.complexRate >= 8)
        {
          colors.add(Color.GRAY);

          for(int r = 0; r < 256; r += 255) {
            for (int g = 0; g < 256; g += 255) {
              for (int b = 0; b < 256; b += 255) {
                Log.d("Columnus", String.format("rgb(%d,%d,%d)", r, g, b));
                colors.add(Color.rgb(r, g, b));
              }
            }
          }
        }

        if(settings.complexRate >= 16)
        {
          for(int r = 0; r < 256; r += 128) {
            for (int g = 0; g < 256; g += 128) {
              colors.add(Color.rgb(r, g, 255));
            }
          }

          for(int g = 0; g < 256; g += 128) {
            for (int b = 0; b < 256; b += 128) {
              colors.add(Color.rgb(255, g, b));
            }
          }

          for(int r = 0; r < 256; r += 128) {
            for (int b = 0; b < 256; b += 127) {
              colors.add(Color.rgb(r, 255, b));
            }
          }
        }

        if(settings.complexRate >= 32)
        {
          for(int r = 0; r < 256; r += 64) {
            for (int g = 0; g < 256; g += 64) {
              for (int b = 0; b < 256; b += 64) {
                colors.add(Color.rgb(r, g, b));
              }
            }
          }
        }
      }

      int n = random.nextInt(0x7fffffff);
      return colors.get(n % colors.size());
    }
    /*============================================================*/

    Controller(Context c, String sectionName)
    {
      super(c, sectionName);
    }

    @Override
    protected Glass onGlassCreate()
    {
      return new ColumnusGlass(settings.columnCount, settings.rowCount);
    }

    @Override
    protected Figure onNewFigure() {
      Figure figure = new ColumnusFigure();
      figure.put(0, 0, new Square(randomColor()));
      figure.put(0, 1, new Square(randomColor()));
      figure.put(0, 2, new Square(randomColor()));

      return figure;
    }

    @Override
    void onSettingsChanged() {
      super.onSettingsChanged();
      if(colors != null)
        colors.clear();
    }

  }
}
