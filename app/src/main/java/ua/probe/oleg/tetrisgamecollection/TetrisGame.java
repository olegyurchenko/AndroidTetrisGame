package ua.probe.oleg.tetrisgamecollection;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Tetris game logic
 */

class TetrisGame extends TetrisBase {
  /*-----------------------------------------------------------------------------------------------*/
  private static class TetrisFigure extends TetrisBase.Figure {

    TetrisFigure() {
      super();
    }

    TetrisFigure(byte data[], int color) {
      super();

      int row = 0;
      for (byte b : data) {
        for (int i = 0; i < 8; i++) {
          if ((b & (1 << i)) != 0) {
            put(i, row, new Square(color));
          }
        }
        row++;
      }
    }

    void strip() {
      for (int row = 0; row < rowCount; row++) {
        boolean empty = true;
        for (int column = 0; column < columnCount; column++) {
          Square s = squareMap.get(index(column, row));
          if (s != null) {
            empty = false;
            break;
          }
        }

        if (empty) {
          //Erase empty row
          //Log.d("Game", "Erase empty row = " + row);
          for (int r = row + 1; r < rowCount; r++) {
            for (int column = 0; column < columnCount; column++) {
              int idx = index(column, r);
              Square s = squareMap.get(idx);
              if (s != null) {
                squareMap.remove(idx);
                squareMap.put(index(column, r - 1), s);
              }
            }
          }
          row--;
          rowCount--;
        }
      }

      for (int column = 0; column < columnCount; column++) {
        boolean empty = true;
        for (int row = 0; row < rowCount; row++) {
          Square s = squareMap.get(index(column, row));
          if (s != null) {
            empty = false;
            break;
          }
        }

        if (empty) {
          //Erase empty column
          //Log.d("Game", "Erase empty column = " + column);
          for (int c = column + 1; c < columnCount; c++) {
            for (int row = 0; row < rowCount; row++) {
              int idx = index(c, row);
              Square s = squareMap.get(idx);
              if (s != null) {
                squareMap.remove(idx);
                squareMap.put(index(c - 1, row), s);
              }
            }
          }
          column--;
          columnCount--;
        }
      }
    }

    @Override
    public Figure rotateLeft() {
      TetrisFigure f = new TetrisFigure();
      for (int row = 0; row < rowCount; row++) {
        for (int column = 0; column < columnCount; column++) {
          Square s = get(column, row);
          if (s != null) {
            f.put(row, columnCount - column, s);
          }
        }
      }
      f.strip();
      return f;
    }

    @Override
    public Figure rotateRight() {
      TetrisFigure f = new TetrisFigure();
      for (int row = 0; row < rowCount; row++) {
        for (int column = 0; column < columnCount; column++) {
          Square s = get(column, row);
          if (s != null)
            f.put(rowCount - row, column, s);
        }
      }
      f.strip();
      return f;
    }
  }

  /*-----------------------------------------------------------------------------------------------*/
  private static class TetrisGlass extends TetrisBase.Glass {
    TetrisGlass(int colCount, int rowCount) {
      super(colCount, rowCount);
    }

    @Override
    boolean annigilation() {
      for (int row = 0; row < rowCount; row++) {
        boolean full = true;
        for (int column = 0; column < columnCount; column++) {
          if (get(column, row) == null) {
            full = false;
            break;
          }
        }

        if (full) {
          //remove row
          for (int r = row - 1; r >= 0; r--) {
            for (int column = 0; column < columnCount; column++) {
              //put(column, r + 1, get(column, r));
              moveSquere(column, r, column, r + 1);
            }
          }
          addRemovedShapes(columnCount);
          return true;
        }
      }
      return super.annigilation();
    }

    /**
     * Calculate rating of glass content for brute variants for the best possible step
     *
     * @return rating of the glass content
     */
    @Override
    int calcContentRating() {
      //Collated rates - be careful !!!, pure magic
      final int
        RATE_SINGE_CELL = 1,
        RATE_FULL_ROW = 500,
        RATE_EMPTY_ROW = 400,
        RATE_BUBBLE = -300,
        RATE_HOLE = -100;


      int rating = 0;

      int emptyRows = 0;
      int fillRows = 0;
      int fillCells = 0;
      int bubbles = 0;
      int holes = 0;

      //Calculate empty row and fill row
      for (int row = 0; row < rowCount; row++) {
        boolean empty = true, full = true;
        int fillCount = 0;
        for (int col = 0; col < columnCount; col++) {
          if (get(col, row) != null) {
            empty = false;
            fillCount++;
            fillCells++;
          } else {
            full = false;
          }
        }

        if (full) {
          fillRows++;
          rating += RATE_FULL_ROW;
        }

        if (empty) {
          emptyRows++;
          rating += RATE_EMPTY_ROW;
        }

        if (!full && !empty) {

          rating += RATE_SINGE_CELL * fillCount * row;
        }
      }

      //Find holes and bubbles
      for (int col = 0; col < columnCount; col++) {
        boolean blank = true;
        int holeHeight = 0, holeWidth = -1;
        int fillCount = 0;
        for (int row = 0; row < rowCount; row++) {
          Square s = get(col, row);
          if (s != null) {
            if (blank) {
              fillCount ++;
              for (int r = row + 1; r < rowCount; r++) {
                if (get(col, r) == null) {
                  bubbles++;
                  rating += RATE_BUBBLE + fillCount * RATE_BUBBLE;
                  fillCount = 0;
                  //break;
                }
              }
              blank = false;
            }
          }
          else
          {
            if (blank) {
              int w = 1;
              for (int c = col + 1; c < columnCount; c++) {
                if (get(c, row) != null)
                  break;
                w++;
              }

              for (int c = col - 1; c >= 0; c--) {
                if (get(c, row) != null)
                  break;
                w++;
              }

              if ( holeWidth == -1 && w < 2 )
                holeWidth = w;

              if( holeWidth != -1 )
                holeHeight ++;


              //Log.d("1", String.format("col:%d row:%d w:%d holeWidth:%d holeHeight:%d", col, row, w, holeWidth, holeHeight));
            }

          }

        } //col

        //Log.d("2", String.format("col:%d holeHeight:%d holeWidth:%d", col, holeHeight, holeWidth));
        if (holeHeight > 1 && holeWidth > 0 && holeWidth < 2) {
          holes += holeHeight;
          //Log.d("CalcRating:found hole", String.format("col:%d holeHeight:%d holeWidth:%d", col, holeHeight, holeWidth));

          holeHeight -= 1;
          rating += RATE_HOLE * holeHeight * holeHeight; //Square
        }
      } //row

      //Log.d("CalcRating", String.format("rating:%d", rating));
/*
        Log.d("CalcRating", String.format("{emptyRows:%d,fillRows:%d,fillCells:%d,bubbles:%d,holes:%d} rating:%d",
          emptyRows,
          fillRows,
          fillCells,
          bubbles,
          holes,
          rating));
*/

      return rating;
    }

  }

  /*-----------------------------------------------------------------------------------------------*/
  static class Controller extends TetrisBase.Controller {

    static final int DEFAULT_COMPLEX = 4;

    //3 - square figures
    static byte figures3[][] = {
      {0b1},
      {0b11},
      {0b11, 0b01},
      {0b111}
    };

    //4 - square figures
    static byte figures4[][] = {
      {0b11, 0b11},
      {0b111, 0b100},
      {0b111, 0b010},
      {0b111, 0b001},
      {0b110, 0b011},
      {0b011, 0b110},
      {0b1111}
    };

    //5 - square figures (pentamino)
    static byte figures5[][] = {
      {0b010, 0b111, 0b100}, //F
      {0b11111}, //I
      {0b1111, 0b0001}, //L
      {0b0011, 0b1110}, //N
      {0b111, 0b110}, //P
      {0b111, 0b010, 0b010}, //T
      {0b111, 0b101}, //U
      {0b111, 0b001, 0b001 }, //V
      {0b110, 0b011, 0b001 }, //W
      {0b010, 0b111, 0b010 }, //X
      {0b01, 0b01, 0b11, 0b01 }, //Y
      {0b011, 0b010, 0b110 }, //Z
    };

    //6 - square figures
    static byte figures6[][] = {
      {0b111, 0b111},
      {0b1110, 0b0111},
      {0b0111, 0b1110},
    };

    ArrayList<byte []> figures;


    Controller(Context c, String sectionName) {
      super(c, sectionName);
    }

    byte[] randomFigure()
    {
      if(figures == null)
        figures = new ArrayList<>();

      if(figures.isEmpty()) {

        if(settings.complexRate == 0)
          settings.complexRate = DEFAULT_COMPLEX;

        if(settings.complexRate >= 3 && settings.complexRate < 10)
        {
          Collections.addAll(figures, figures3);
        }
        if(settings.complexRate >= 4 && settings.complexRate < 10)
        {
          Collections.addAll(figures, figures4);
        }
        if(settings.complexRate >= 5 && settings.complexRate < 10)
        {
          Collections.addAll(figures, figures5);
        }
        if(settings.complexRate >= 6 && settings.complexRate < 10)
        {
          Collections.addAll(figures, figures6);
        }

        if(settings.complexRate == 14)
        {
          Collections.addAll(figures, figures4);
        }

        if(settings.complexRate == 15)
        {
          Collections.addAll(figures, figures5);
        }

        if(settings.complexRate == 16)
        {
          Collections.addAll(figures, figures6);
        }

      }


      int n = random.nextInt(0x7fffffff);
      return figures.get(n % figures.size());
    }

    @Override
    void onSettingsChanged() {
      super.onSettingsChanged();
      if(figures != null)
        figures.clear();
    }

    @Override
    protected Glass onGlassCreate() {
      return new TetrisGlass(settings.columnCount, settings.rowCount);
    }
    /*============================================================*/
    @Override
    void setup() {
      super.setup();

      if(settings.complexRate == 0)
        settings.complexRate = DEFAULT_COMPLEX;

      long scale = settings.complexRate * settings.complexRate * 10;
      if(settings.complexRate > 10)
        scale = (settings.complexRate - 9) * (settings.complexRate - 9) * 10;

      glass.setScoreScale(scale);
    }
    /*============================================================*/
    @Override
    protected Figure onNewFigure() {
      Figure figure = new TetrisFigure(randomFigure(), randomColor());

      int n = random.nextInt(1000) % 3;
      for (int i = 0; i < n; i++)
        figure = figure.rotateRight();

      return figure;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/

}
