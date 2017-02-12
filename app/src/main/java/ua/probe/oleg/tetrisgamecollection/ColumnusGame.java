package ua.probe.oleg.tetrisgamecollection;

import android.content.Context;
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
        RATE_SCORE_WAIT = 20,
        RATE_EMPTY_ROW = 10,
        RATE_DOUBLED_SHAPE = 5;

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
      rating += (g.getScore() - oldScore) * RATE_SCORE_WAIT;

      int notEmptyCount = 0;
      //Find 2 colors
      for(int row = 0; row < rowCount; row ++) {
        boolean empty = true;
        for (int column = 0; column < columnCount; column++)
        {
          Square s = g.get(column, row);
          if(s == null)
            continue;

          empty = false;
          //[x][X]
          if(column > 0 && column < columnCount - 1)
          {
            Square l = g.get(column - 1, row);
            if(l != null && l.color() == s.color())
            {
              rating += RATE_DOUBLED_SHAPE;// * (rowCount - c.row() + 1); //Better - higher
            }
          }

          //[x]
          //[X]
          if(row > 0 && row < rowCount - 1)
          {
            Square t = g.get(column, row - 1);
            if(t != null && t.color() == s.color() && notEmptyCount < 2)
            {
              rating += RATE_DOUBLED_SHAPE;
            }
          }

          //[x]
          //   [X]
          if(row > 0 && column > 0)
          {
            Square t = g.get(column - 1, row - 1);
            if(t != null && t.color() == s.color() && notEmptyCount < 2)
            {
              rating += RATE_DOUBLED_SHAPE;
            }
          }

          //      [x]
          //   [X]
          if(row > 0 && column < columnCount - 1)
          {
            Square t = g.get(column - 1, row + 1);
            if(t != null && t.color() == s.color()  && notEmptyCount < 2)
            {
              rating += RATE_DOUBLED_SHAPE * (rowCount - row + 1); //Better - higher
            }
          }

        }
        if(empty)
          rating += RATE_EMPTY_ROW;
        else
          notEmptyCount ++;
      }

      return rating;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Controller extends TetrisBase.Controller
  {

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
      figure.put(0, 0, new Square(randomComplexColor()));
      figure.put(0, 1, new Square(randomComplexColor()));
      figure.put(0, 2, new Square(randomComplexColor()));

      return figure;
    }
  }
}
