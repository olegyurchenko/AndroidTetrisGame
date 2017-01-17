package ua.probe.oleg.tetrisgamecollection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

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
  static public class Shape
  {
    protected int fillColor, borderColor;
//    protected int width = 0, height = 0;

    Shape(int fColor, int bColor)
    {
      fillColor = fColor;
      borderColor = bColor;
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
  static public class Figure
  {
    protected HashMap<Integer, Shape> shapeMap;
    protected int columnCount = 0, rowCount = 0;

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
  static public class Glass
  {
    int rowCount, columnCount;
    protected int fillColor = Color.WHITE, borderColor = Color.BLUE;
    protected Rect rect;
    protected Figure activeFigure = null;
    protected Cell activeFigurePosition = new Cell();
    protected boolean modified = false;

    protected HashMap<Integer, Shape> shapeMap;

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

      int shapeWidth = rect.width() / columnCount;
      int shapeHeight = rect.height() / rowCount;
      for(int r = 0; r < rowCount; r++)
      {
        for(int c = 0; c < columnCount; c++)
        {
          Shape s = get(c, r);
          if(s != null)
          {
            int x = rect.left + c * shapeWidth;
            int y = rect.top + (rowCount - r) * shapeHeight;
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

        //Debug

        int textSize = 30;
        p.setTextSize(textSize);
        p.setColor(Color.BLUE);

        canvas.drawText("Figure: x = " + x + ", y = " + y, x, y + textSize, p);
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

      return shapeMap.get(index(column, rowCount - row));
    }

    public void put(int column, int row, Shape s)
    {
      if(column < 0
        || column >= columnCount
        || row < 0
        || row >= rowCount)
        return;

      if(s == null)
        shapeMap.remove(index(column, rowCount - row));
      else
         shapeMap.put(index(column, rowCount - row), s);
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

  }
  /*-----------------------------------------------------------------------------------------------*/
  static public class Controller extends GameController
  {
    protected Glass glass;
    protected int interval = 1000; //ms
    private long lastTime = 0;
    Figure nextFigure;

    Controller()
    {
      super();
      glass = onGlassCreate();
    }


    protected Glass onGlassCreate()
    {
      return new Glass(8, 16);
    }
    protected Figure onNewFigure() {
      Figure figure = new Figure();
      figure.put(0, 0, new Shape(Color.GREEN, Color.BLACK));
      figure.put(0, 1, new Shape(Color.RED, Color.BLACK));
      figure.put(0, 2, new Shape(Color.BLUE, Color.BLACK));

      return figure;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
      if(glass.getRect() == null)
      {
        if(clientRect == null)
        {
          clientRect = new Rect(100, 100, canvas.getWidth() * 2 / 3, canvas.getHeight() * 2  /  3);
        }

        glass.setRect(clientRect);
      }

      glass.onDraw(canvas);
    }


    @Override
    public void onQuant()
    {
      if(System.currentTimeMillis() - lastTime >= interval)
      {
        nextInterval();
        lastTime = System.currentTimeMillis();
      }
      //Log.d("TIME TEST", "Current sec = " + seconds);
    }

    @Override
    public void onTouchDown(float x, float y)
    {
    }

    @Override
    public void onTouchUp(float x, float y)
    {
    }

    @Override
    public void onTouchMove(float x, float y)
    {
    }

    @Override
    public void moveLeft()
    {
      glass.moveLeft();
      setModified(glass.isModified());
    }

    @Override
    public void moveRight()
    {
      glass.moveRight();
      setModified(glass.isModified());
    }

    @Override
    public void moveDown()
    {
      glass.moveBottom();
      setModified(glass.isModified());
    }

    @Override
    public void rotate()
    {
      glass.rotate();
      setModified(glass.isModified());
    }


    protected void nextInterval()
    {
      if(nextFigure == null)
        nextFigure = onNewFigure();
      if(glass.getActiveFigure() == null)
      {
        if(!glass.put(nextFigure))
        {
          Log.d("Game", "Error add figure");
        }
        else
        {
          Log.d("Game", "Add figure Ok");
          nextFigure = onNewFigure();
        }

      }
      setModified(true);
    }

  }
  /*-----------------------------------------------------------------------------------------------*/


}

