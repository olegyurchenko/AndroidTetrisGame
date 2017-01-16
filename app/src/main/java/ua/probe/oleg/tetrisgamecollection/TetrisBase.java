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

  static public class Shape
  {
    protected Glass parent;
    protected int fillColor, borderColor;
//    protected int width = 0, height = 0;

    Shape(Glass p, int fColor, int bColor)
    {
      parent = p;
      fillColor = fColor;
      borderColor = bColor;
    }

    Shape(Glass p)
    {
      parent = p;
      fillColor = Color.WHITE;
      borderColor = Color.BLACK;
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

  static public class Figure
  {

    public void onDraw(Canvas canvas)
    {
    }
  }

  static public class Glass
  {
    int rowCount, columnCount;
    Rect rect;

    protected HashMap<Integer, Shape> shapeMap;

    Glass(int columns, int rows)
    {
      columnCount = columns;
      rowCount = rows;
      shapeMap = new HashMap<Integer, Shape>();
      rect = new Rect();

      //Test shapes
      for(int i = 0; i < 16; i++)
      {
        int c = i % columnCount;
        int r = i / rowCount;
        Shape s = new Shape(this);
        put(c, r, s);
      }
    }

    public void setRect(Rect r)
    {
      rect = r;
    }

    public Rect getRect()
    {
      return  rect;
    }

    public void onDraw(Canvas canvas)
    {
      Paint p = new Paint();

      // перенастраивам кисть на контуры
      p.setColor(Color.BLACK);
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
            int y = rect.top + r * shapeHeight;
            s.onDraw(canvas, x, y, shapeWidth, shapeHeight);
          }
        }
      }
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
      if(s == null)
        shapeMap.remove(index(column, row));
      else
        shapeMap.put(index(column, row), s);
    }
  }

  static public class Controller extends GameController
  {
    Glass glass;
    Controller()
    {
      super();
      glass = onGlassCreate();
    }

    protected Glass onGlassCreate()
    {
      return new Glass(8, 16);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
      if(glass.getRect().width() == 0)
      {
        Rect r = new Rect();
        r.left = 100;
        r.right = canvas.getWidth() - 100;
        r.top = 100;
        r.bottom = canvas.getHeight() - 100;
        glass.setRect(r);
      }

      glass.onDraw(canvas);
    }


    @Override
    public void onQuant()
    {
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
    }

    @Override
    public void moveRight()
    {
    }

    @Override
    public void moveDown()
    {
    }

    @Override
    public void rotate()
    {
    }
  }


}

