package ua.probe.oleg.tetrisgamecollection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.*;

/**
 * Created by oleg on 13.01.17.
 */

public class TetrisBase {

  /*-----------------------------------------------------------------------------------------------*/
  static public class Cell
  {
    protected int col, row;
    public Cell() {col = row = 0;}
    public Cell(int c, int r) {col = c; row = r;}
    public Cell(Cell other) { col = other.col; row = other.row;}

    public int column() {return col;}
    public int row() {return row;}
    public void setColumn(int c) {col = c;}
    public void setRow(int r) {row = r;}
  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Shape
  {
    int fillColor, borderColor;
//    protected int width = 0, height = 0;

    Shape(int fColor, int bColor)
    {
      fillColor = fColor;
      borderColor = bColor;
    }

    Shape(int fColor)
    {
      fillColor = fColor;
      borderColor = Color.BLACK;
    }

    Shape()
    {
      fillColor = Color.WHITE;
      borderColor = Color.BLACK;
    }

    Shape(Shape other)
    {
      fillColor = other.fillColor;
      borderColor = other.borderColor;
    }

    public int color() {return fillColor;}

    void onDraw(Canvas canvas, int x, int y, int width, int height)
    {
      Paint p = new Paint();
      Rect rect = new Rect(x, y, x + width, y + height);


      // перенастраивам кисть на заливку
      p.setColor(fillColor);
      p.setStyle(Paint.Style.FILL);
      canvas.drawRect(rect, p);

      // перенастраивам кисть на контуры
      p.setColor(borderColor);
      p.setStyle(Paint.Style.STROKE);
      canvas.drawRect(rect, p);
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Figure
  {
    HashMap<Integer, Shape> shapeMap;
    int columnCount = 0, rowCount = 0;

    Figure()
    {
      shapeMap = new HashMap<Integer, Shape>();
    }

    Figure(Figure other)
    {
      shapeMap = new HashMap<Integer, Shape>(other.shapeMap);
      columnCount = other.columnCount;
      rowCount = other.rowCount;
    }

    protected int index(int column, int row)
    {
      return column + 1000000 * row;
    }

    public Shape get(int column, int row)
    {
      return shapeMap.get(index(column, row));
    }

    public void put(int column, int row, Shape s)
    {
      shapeMap.put(index(column, row), s);
      if(column + 1 > columnCount)
        columnCount = column + 1;
      if(row + 1 > rowCount)
        rowCount = row + 1;
    }

    public int getRowCount() {return rowCount;}
    public int getColumnCount() {return columnCount;}

    public Figure rotate()
    {
      //Pure abstract
      return new Figure(this);
    }

    public void onDraw(Canvas canvas, int left, int top, int shapeWidth, int shapeHeight)
    {
      Paint p = new Paint();

      for(int r = 0; r < rowCount; r++)
      {
        for(int c = 0; c < columnCount; c++)
        {
          Shape s = get(c, r);
          if(s != null)
          {
            int x = left + c * shapeWidth;
            int y = top + r * shapeHeight;
            s.onDraw(canvas, x, y, shapeWidth, shapeHeight);
          }
        }
      }
    }
  }

  /*-----------------------------------------------------------------------------------------------*/
  static class Glass
  {
    int rowCount, columnCount;
    int fillColor = Color.WHITE, borderColor = Color.BLUE;
    Rect rect;
    Figure activeFigure = null;
    Cell activeFigurePosition = new Cell();
    boolean modified = false;

    HashMap<Integer, Shape> shapeMap;

    Glass(int columns, int rows)
    {
      columnCount = columns;
      rowCount = rows;
      shapeMap = new HashMap<Integer, Shape>();
      //rect = new Rect();

      //Test shapes
      for(int i = 0; i < 0; i++)
      {
        int c = i % columnCount;
        int r = i / columnCount;
        Shape s = new Shape();
        put(c, r, s);
      }
    }

    public boolean isModified() {return modified;}
    protected void setModified(boolean m) {modified = m;}

    public void setRect(Rect r)
    {
      rect = r;
    }

    public Rect getRect()
    {
      return  rect;
    }

    public int getRowCount() {return rowCount;}
    public int getColumnCount() {return columnCount;}
    public int getShapeWidth() { return rect.width() / columnCount;}
    public int getShapeHeight() {return rect.height() / rowCount;}

    public void onDraw(Canvas canvas)
    {
      Paint p = new Paint();

      // перенастраивам кисть на заливку
      p.setColor(fillColor);
      p.setStyle(Paint.Style.FILL);
      canvas.drawRect(rect, p);

      // перенастраивам кисть на контуры
      p.setColor(borderColor);
      p.setStyle(Paint.Style.STROKE);
      canvas.drawRect(rect, p);

      int shapeWidth = getShapeWidth();
      int shapeHeight = getShapeHeight();
      for(int r = 0; r < rowCount; r++)
      {
        for(int c = 0; c < columnCount; c++)
        {
          Shape s = get(c, r);
          if(s != null)
          {
            int x = rect.left + c * shapeWidth;
            int y = rect.top + r * shapeHeight;
            s.onDraw(canvas, x, y, shapeWidth, shapeHeight);

            /*
            //Debug
            int textSize = 30;
            p.setTextSize(textSize);
            p.setColor(Color.BLUE);

            canvas.drawText("#" + c + "." + r, x, y + textSize, p);
            */
          }
        }
      }

      if(activeFigure != null)
      {
        int x = rect.left + activeFigurePosition.column() * shapeWidth;
        int y = rect.top + activeFigurePosition.row() * shapeHeight;
        activeFigure.onDraw(canvas, x, y, shapeWidth, shapeHeight);

        /*
        //Debug
        int textSize = 30;
        p.setTextSize(textSize);
        p.setColor(Color.BLUE);

        canvas.drawText("Figure: x = " + x + ", y = " + y, x, y + textSize, p);
        */
      }

      setModified(false);
    }

    protected int index(int column, int row)
    {
      return column + 1000000 * row;
    }

    public Shape get(int column, int row)
    {
      if(column < 0
        || column >= columnCount
        || row < 0
        || row >= rowCount)
        return null;

      return shapeMap.get(index(column, row));
    }

    public void put(int column, int row, Shape s)
    {
      if(column < 0
        || column >= columnCount
        || row < 0
        || row >= rowCount)
        return;

      if(s == null)
        shapeMap.remove(index(column, row));
      else
         shapeMap.put(index(column, row), s);
      setModified(true);
    }

    boolean validPosition(int column, int row, Figure f)
    {
      if(row + f.getRowCount() > rowCount || row < 0)
        return false;
      if(column + f.getColumnCount() > columnCount || column < 0)
        return false;

      for(int r = 0; r < f.getRowCount(); r++)
      {
        for(int c = 0; c < f.getColumnCount(); c++)
        {
          if(get(c + column, r + row) != null && f.get(c, r) != null)
            return false;
        }
      }
      return true;
    }

    boolean put(Figure f)
    {
      if(activeFigure != null)
        return false;
      Cell cell = new Cell();
      //Centered in top
      cell.setColumn((columnCount - f.getColumnCount()) / 2);
      if(!validPosition(cell.column(), cell.row(), f))
        return false;
      activeFigure = f;
      activeFigurePosition = cell;
      setModified(true);
      return true;
    }

    public Figure getActiveFigure() {return activeFigure;}


    public boolean moveLeft()
    {
      if(activeFigure == null)
        return false;

      Cell c = new Cell(activeFigurePosition.column() - 1, activeFigurePosition.row());
      if(validPosition(c.column(), c.row(), activeFigure))
      {
        activeFigurePosition = c;
        setModified(true);
        return true;
      }
      return false;
    }

    public boolean moveRight()
    {
      if(activeFigure == null)
        return false;

      Cell c = new Cell(activeFigurePosition.column() + 1, activeFigurePosition.row());
      if(validPosition(c.column(), c.row(), activeFigure))
      {
        activeFigurePosition = c;
        setModified(true);
        return true;
      }
      return false;
    }


    public boolean rotate()
    {
      if(activeFigure == null)
        return false;
      Figure f = activeFigure.rotate();
      if(validPosition(activeFigurePosition.column(), activeFigurePosition.row(), f))
      {
        activeFigure = f;
        setModified(true);
        return true;
      }
      return false;
    }

    public boolean moveDown()
    {
      if(activeFigure == null)
        return false;

      Cell c = new Cell(activeFigurePosition.column(), activeFigurePosition.row() + 1);
      if(validPosition(c.column(), c.row(), activeFigure))
      {
        activeFigurePosition = c;
        setModified(true);
        return true;
      }
      else
      {
        onFigureLended();
      }
      return false;
    }

    public boolean moveBottom()
    {
      if(activeFigure == null)
        return false;

      boolean result = moveDown();
      while (moveDown())
      {
      }
      return result;
    }


    void onFigureLended()
    {
      //Convert figure to shapes
      int rowCount = activeFigure.getRowCount();
      int columnCount = activeFigure.getColumnCount();
      for(int row = 0; row < rowCount; row ++)
      {
        for(int column = 0; column < columnCount; column ++)
        {
          Shape s = activeFigure.get(column, row);
          if(s != null)
          {
            put(activeFigurePosition.column() + column,
              activeFigurePosition.row() + row,
              s);
          }
        }
      }
      activeFigure = null;
    }

    boolean annigilation()
    {
      return false;
    }

  }
  /*-----------------------------------------------------------------------------------------------*/
  static public class Controller
  {
    private boolean modified = false;
    Rect rect;
    protected Glass glass;
    protected int interval = 1000; //ms
    private long lastTime = 0;
    protected int defaultColumnCount = 6, defaultRowCount = 12;

    boolean showNextFigure = true;
    Figure nextFigure;
    int nextFigureX, nextFigureY;

    /*============================================================*/
    boolean isModified() {return modified;}
    /*============================================================*/
    void setModified(boolean m) {modified = m;}
    /*============================================================*/
    static int colors[] = {
      Color.rgb(2, 53, 58), //Color.BLACK,
      Color.rgb(215, 235, 237),
      Color.CYAN,
      Color.MAGENTA,
      Color.BLUE,
      Color.GRAY,
      Color.GREEN,
      Color.RED,
      Color.YELLOW
    };
    /*============================================================*/
    int randomColor()
    {
      int n = (int)(Math.random() * 1000.0);
      return colors[n % colors.length];
    }
    /*============================================================*/
    Controller()
    {
      glass = onGlassCreate();
    }
    /*============================================================*/
    protected Glass onGlassCreate()
    {
      return new Glass(defaultColumnCount, defaultRowCount);
    }
    /*============================================================*/
    protected Figure onNewFigure() {
      Figure figure = new Figure();
      figure.put(0, 0, new Shape(Color.GREEN, Color.BLACK));
      figure.put(0, 1, new Shape(Color.RED, Color.BLACK));
      figure.put(0, 2, new Shape(Color.BLUE, Color.BLACK));

      return figure;
    }
    /*============================================================*/
    public void setSize(int w, int h)
    {
      rect = new Rect(0, 0, w, h);
      geometryInit();
    }
    /*============================================================*/
    protected void geometryInit()
    {
      int x, y, w, h;

      //if(rect.width() < rect.height()) //Verical
      if(false)
      {
        x = 100;
        w = rect.width() - 200;
        h = (w / glass.getColumnCount()) * glass.getRowCount();
        y = 200 + 3 * w / glass.getColumnCount();
        glass.setRect(
          new Rect(x, y, x + w, y + h)
        );

        nextFigureX = x + (w - glass.getShapeWidth() * 3) / 2;
        nextFigureY = 100;

      }
      else
      { //Horisontal
        x = y = 100;
        h = rect.height() - 200;
        w = (h / glass.getRowCount()) * glass.getColumnCount();
        glass.setRect(
          new Rect(x, y, x + w, y + h)
        );
        nextFigureX = y + w + 100;
        nextFigureY = 100;
      }
    }
    /*============================================================*/
    public void onDraw(Canvas canvas)
    {
      if(glass.getRect() == null)
      {
        if(rect == null)
        {
          rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
          geometryInit();
        }

        glass.setRect(rect);
      }

      glass.onDraw(canvas);
      if(nextFigure != null && showNextFigure)
      {
        nextFigure.onDraw(canvas, nextFigureX, nextFigureY, glass.getShapeWidth(), glass.getShapeHeight());
      }
    }

    /*============================================================*/
    public void onQuant()
    {
      if(System.currentTimeMillis() - lastTime >= interval)
      {
        lastTime = System.currentTimeMillis();
        nextInterval();
      }
      //Log.d("TIME TEST", "Current sec = " + seconds);
    }
    /*============================================================*/
    public void onTouchDown(float x, float y)
    {
    }
    /*============================================================*/
    public void onTouchUp(float x, float y)
    {
    }
    /*============================================================*/
    public void onTouchMove(float x, float y)
    {
    }
    /*============================================================*/
    public void moveLeft()
    {
      glass.moveLeft();
      setModified(glass.isModified());
    }
    /*============================================================*/
    public void moveRight()
    {
      glass.moveRight();
      setModified(glass.isModified());
    }
    /*============================================================*/
    public void moveDown()
    {
      glass.moveBottom();
      setModified(glass.isModified());
    }
    /*============================================================*/
    public void rotate()
    {
      glass.rotate();
      setModified(glass.isModified());
    }
    /*============================================================*/
    protected void nextInterval()
    {
      if(nextFigure == null)
        nextFigure = onNewFigure();

      if(glass.getActiveFigure() == null)
      {

        if(glass.annigilation()) {
          lastTime = System.currentTimeMillis() - interval;

        }
        else
        {

          if (!glass.put(nextFigure)) {
//          Log.d("Game", "Error add figure");
          } else {
//          Log.d("Game", "Add figure Ok");
            nextFigure = onNewFigure();
          }
        }
      }
      else
        glass.moveDown();

      setModified(glass.isModified());
    }

  }
  /*-----------------------------------------------------------------------------------------------*/


}

