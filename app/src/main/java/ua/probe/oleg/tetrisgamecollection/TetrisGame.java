package ua.probe.oleg.tetrisgamecollection;

import android.content.Context;

/**
 * Tetris game logic
 */

class TetrisGame extends TetrisBase
{
  /*-----------------------------------------------------------------------------------------------*/
  private static class TetrisFigure extends TetrisBase.Figure
  {

    TetrisFigure() {
      super();
    }

    TetrisFigure(byte data[], int color)
    {
      super();

      int row = 0;
      for(byte b : data)
      {
        for(int i = 0; i < 8; i++)
        {
          if((b & (1 << i)) != 0)
          {
            put(i, row, new Square(color));
          }
        }
        row ++;
      }
    }

    void strip()
    {
      for(int row = 0; row < rowCount; row ++)
      {
        boolean empty = true;
        for (int column = 0; column < columnCount; column ++)
        {
          Square s = squareMap.get(index(column, row));
          if(s != null)
          {
            empty = false;
            break;
          }
        }

        if(empty)
        {
          //Erase empty row
          //Log.d("Game", "Erase empty row = " + row);
          for(int r = row + 1; r < rowCount; r ++) {
            for (int column = 0; column < columnCount; column++) {
              int idx = index(column, r);
              Square s = squareMap.get(idx);
              if (s != null)
              {
                squareMap.remove(idx);
                squareMap.put(index(column, r - 1), s);
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
          Square s = squareMap.get(index(column, row));
          if(s != null)
          {
            empty = false;
            break;
          }
        }

        if(empty)
        {
          //Erase empty column
          //Log.d("Game", "Erase empty column = " + column);
          for(int c = column + 1; c < columnCount; c ++) {
            for(int row = 0; row < rowCount; row ++)
            {
              int idx = index(c, row);
              Square s = squareMap.get(idx);
              if (s != null)
              {
                squareMap.remove(idx);
                squareMap.put(index(c - 1, row), s);
              }
            }
          }
          column --;
          columnCount --;
        }
      }
    }

    @Override
    public Figure rotateLeft()
    {
      TetrisFigure f = new TetrisFigure();
      for(int row = 0; row < rowCount; row ++)
      {
        for (int column = 0; column < columnCount; column ++)
        {
          Square s = get(column, row);
          if(s != null) {
            f.put(row, columnCount - column, s);
          }
        }
      }
      f.strip();
      return f;
    }

    @Override
    public Figure rotateRight()
    {
      TetrisFigure f = new TetrisFigure();
      for(int row = 0; row < rowCount; row ++)
      {
        for (int column = 0; column < columnCount; column ++)
        {
          Square s = get(column, row);
          if(s != null)
            f.put(rowCount - row, column, s);
        }
      }
      f.strip();
      return f;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  private static class TetrisGlass extends TetrisBase.Glass
  {
    TetrisGlass(int colCount, int rowCount)
    {
      super(colCount, rowCount);
    }

    @Override
    boolean annigilation()
    {
      for(int row = 0; row < rowCount; row ++)
      {
        boolean full = true;
        for(int column = 0; column < columnCount; column ++)
        {
          if(get(column, row) == null)
          {
            full = false;
            break;
          }
        }

        if(full)
        {
          //remove row
          for(int r = row - 1; r >= 0; r --)
          {
            for(int column = 0; column < columnCount; column ++)
              put(column, r + 1, get(column, r));

          }
          addRemovedShapes(columnCount);
          return true;
        }
      }
      return false;
    }

    /**
     * Calculate rating of glass content for brute variants for the best possible step
     * @return rateing of the glass content
     */
    @Override
    int calcContentRating()
    {
      final int
        RATE_EMPTY_MIDDLE = -50,
        RATE_FULL_ROW = 100,
        RATE_EMPTY_ROW = 100,
        RATE_HOLE = -20;



      int rating = 0;

      for(int row = 0; row < rowCount; row++)
      {

        boolean empty = true, full = true;
        for(int col = 0; col < columnCount; col++)
        {
          if(get(col, row) != null)
          {
            empty = false;
          }
          else
          {
            full = false;
          }
        }

        if(full)
          rating += RATE_FULL_ROW;

        if(empty)
          rating += RATE_EMPTY_ROW;

      }

      for(int col = 0; col < columnCount; col++)
      {
        boolean blank = true;
        int holeHeight = 0, holeWidth = columnCount;
        for(int row = 0; row < rowCount; row++) {
          Square s = get(col, row);
          if(s != null)
          {
            if(blank) {
              for (int r = row + 1; r < rowCount; r++) {
                if (get(col, r) == null) {
                  rating += RATE_EMPTY_MIDDLE;
                }
              }
              blank = false;
            }
          }
          else
          {
            if(blank)
            {
              int wl = 0, wr = 0;
              for (int c = col + 1; c < columnCount; c++) {
                if (c != col && get(c, row) != null)
                {
                  wr = c - col;
                  break;
                }
              }
              for (int c = col - 1; c >= 0; c--) {
                if (c != col && get(c, row) != null)
                {
                  wl = col - c;
                  break;
                }
              }
              if(wl > 0 || wr > 0)
                holeHeight ++;
              if(holeWidth > wl + wr)
                holeWidth = wl + wr;
            }
          }
        }
        if(holeHeight > 1 && holeWidth < 2)
          rating += RATE_HOLE * (holeHeight - 1);
      }

      return rating;
    }

  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Controller extends TetrisBase.Controller
  {
    static byte figures[][] = {

      //1x1
      {0b1},
      //2x2
      {0b11},
      {0b11, 0b01},
      {0b11, 0b11},
      //3x2
      {0b111},
      {0b111, 0b100},
      {0b111, 0b010},
      {0b111, 0b001},
      {0b111, 0b101},
      {0b110, 0b011},
      {0b011, 0b110},

      {0b110, 0b011},
      {0b011, 0b110},
      {0b111, 0b111},
      //3x3
      {0b111, 0b100, 0b100},
      {0b111, 0b010, 0b010},
      {0b111, 0b001, 0b001},
      {0b111, 0b100, 0b100},
      {0b111, 0b101, 0b101},

      {0b111, 0b110, 0b110},
      {0b111, 0b011, 0b011},

      {0b111, 0b111, 0b111},

      {0b011, 0b111, 0b110},
      {0b110, 0b111, 0b011},

    };

    Controller(Context c, String sectionName)
    {
      super(c, sectionName);
    }

    byte[] randomFigure()
    {
      int n = random.nextInt(1000);
      n %= (figures.length * settings.complexRate) / 100;
      return figures[n % figures.length];
    }

    @Override
    protected Glass onGlassCreate()
    {
      return new TetrisGlass(settings.columnCount, settings.rowCount);
    }

    @Override
    protected Figure onNewFigure() {
      Figure figure = new TetrisFigure(randomFigure(), randomColor());

      int n = random.nextInt(1000) % 3;
      for(int i = 0; i < n; i++)
        figure = figure.rotateRight();

      return figure;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/

}
