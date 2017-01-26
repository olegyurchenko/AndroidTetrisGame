package ua.probe.oleg.tetrisgamecollection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
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
  static class Shape
  {
    int fillColor, borderColor;
//    protected int width = 0, height = 0;
    protected Paint paint = new Paint();
    protected Rect rect = new Rect();

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
      rect.set(x, y, x + width, y + height);

      // перенастраивам кисть на заливку
      paint.setColor(fillColor);
      paint.setStyle(Paint.Style.FILL);
      canvas.drawRect(rect, paint);

      // перенастраивам кисть на контуры
      paint.setColor(borderColor);
      paint.setStyle(Paint.Style.STROKE);
      canvas.drawRect(rect, paint);
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
    private Paint paint, guidePaint;
    private long score;
    private long scoreScale = 100;
    private Path path;

    private boolean drawGuideLines = true;

    HashMap<Integer, Shape> shapeMap;

    Glass(int columns, int rows)
    {
      columnCount = columns;
      rowCount = rows;
      shapeMap = new HashMap<Integer, Shape>();
      paint = new Paint();

      guidePaint = new Paint();
      guidePaint.setColor(Color.BLACK);
      guidePaint.setStyle(Paint.Style.STROKE);
      guidePaint.setPathEffect(new DashPathEffect(new float[] {20 ,30}, 0));

      path = new Path();

      score = 0;

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

    public long getScore() {return score;}
    protected void addRemovedShapes(int shapes) {
      //TODO: waight koefficient
      score += shapes * 17 * scoreScale;
    }
    public void setScoreScale(long s)
    {
      scoreScale = s;
    }

    public int getRowCount() {return rowCount;}
    public int getColumnCount() {return columnCount;}
    public int getShapeWidth() { return rect.width() / columnCount;}
    public int getShapeHeight() {return rect.height() / rowCount;}

    public void onDraw(Canvas canvas)
    {
      // перенастраивам кисть на заливку
      paint.setColor(fillColor);
      paint.setStyle(Paint.Style.FILL);
      canvas.drawRect(rect, paint);

      // перенастраивам кисть на контуры
      paint.setColor(borderColor);
      paint.setStyle(Paint.Style.STROKE);
      canvas.drawRect(rect, paint);

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
        //Draw guide lines


        if(drawGuideLines) {
          int gx = x, gy;

          //Left
          gy = y + activeFigure.getRowCount() * shapeHeight;
          for (int i = activeFigure.getRowCount() - 1; i >= 0; i--) {
            if (activeFigure.get(0, i) != null)
              break;
            gy -= shapeHeight;
          }
          path.reset();
          path.moveTo(gx, gy + shapeHeight / 2);
          path.lineTo(gx, rect.bottom);
          canvas.drawPath(path, guidePaint);
          //canvas.drawLine(gx, gy, gx, rect.bottom, guidePaint);

          //Right
          gy = y + activeFigure.getRowCount() * shapeHeight;
          for (int i = activeFigure.getRowCount() - 1; i >= 0; i--) {
            if (activeFigure.get(activeFigure.getColumnCount() - 1, i) != null)
              break;
            gy -= shapeHeight;
          }

          gx += activeFigure.getColumnCount() * shapeWidth;

          path.reset();
          path.moveTo(gx, gy + shapeHeight / 2);
          path.lineTo(gx, rect.bottom);
          canvas.drawPath(path, guidePaint);
          //canvas.drawLine(gx, gy, gx, rect.bottom, guidePaint);
        }

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
      cell.setColumn(1 + (columnCount - f.getColumnCount()) / 2);
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
    Context context;
    Rect rect;
    Rect bounds;
    Paint paint;
    protected Glass glass;
    final int minInterval = 250, maxInterval = 2250;
    protected int interval = 1000; //ms
    private long lastTime = 0;
    protected int defaultColumnCount = 8, defaultRowCount = 16;
    protected int complexRate = 50, speedRate = 50;
    enum State
    {
      PAUSED,
      WORKED,
      FINISHED
    };

    protected State state = State.PAUSED;

    boolean showNextFigure = true;
    Figure nextFigure;
    int nextFigureX, nextFigureY;
    boolean showRating = true;
    int ratingX, ratingY;

    /*============================================================*/
    boolean isModified() {return modified;}
    /*============================================================*/
    void setModified(boolean m) {modified = m;}
    /*============================================================*/
    int getComplexRate() {return complexRate;}
    /*============================================================*/
    void setComplexRate(int r)
    {
      if(r > 0 && r <= 100)
      {
        complexRate = r;
        glass.setScoreScale(complexRate * speedRate);
        setModified(true);
      }
    }
    /*============================================================*/
    int getSpeedRate() {return speedRate;}
    /*============================================================*/
    void setSpeedRate(int r)
    {
      if(r > 0 && r <= 199)
      {
        speedRate = r;
        interval = minInterval + ((maxInterval - minInterval) * (100 - speedRate)) / 100;
        Log.d("GameController", "interval=" + interval);
        glass.setScoreScale(complexRate * speedRate);
        setModified(true);
      }
    }
    /*============================================================*/
    static int colors[] = {
      Color.rgb(0xff, 0, 0),
      Color.rgb(0, 0xff, 0),
      Color.rgb(0, 0, 0xff),
      Color.rgb(0xff, 0xff, 0),
      Color.rgb(0xff, 0, 0xff),
      Color.rgb(0, 0xff, 0xff),
      Color.rgb(0, 0, 0),
      Color.rgb(0xA0, 0xA0, 0xA0),
      Color.rgb(0xff, 0x80, 0),
      Color.rgb(0xff, 0, 0x80),
      Color.rgb(0x80, 0xff, 0),
      Color.rgb(0, 0xff, 0x80),
      Color.rgb(0x80, 0, 0xff),
      Color.rgb(0x0, 0x80, 0xff),
      Color.rgb(0xff, 0x80, 0x80),
      Color.rgb(0x80, 0xff, 0x80),
      Color.rgb(0x80, 0x80, 0xff),
    };
    /*============================================================*/
    int randomColor()
    {
      int n = (int)(Math.random() * 1000.0);
      return colors[n % colors.length];
    }
    /*============================================================*/
    int randomComplexColor()
    {
      int n = (int)(Math.random() * 1000.0);
      int d =  (colors.length * complexRate) / 100;
      n %= d < 1 ? 1 : d;
      return colors[n % colors.length];
    }
    /*============================================================*/
    Controller(Context c)
    {
      context = c;
      glass = onGlassCreate();
      paint = new Paint();
      bounds = new Rect();
      state = State.PAUSED;
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
      int border = 20;

      if(rect.width() < rect.height()) //Verical
      {
        x = border;
        w = rect.width() - 2 * border;
        h = (w / glass.getColumnCount()) * glass.getRowCount();
        y = border + 3 * w / glass.getColumnCount();
        glass.setRect(
          new Rect(x, y, x + w, y + h)
        );

        nextFigureX = x ;
        nextFigureY = border;

        ratingX = x;
        ratingY = y + h;
      }
      else
      { //Horisontal
        x = y = border;
        h = rect.height() - 2 * border;
        w = (h / glass.getRowCount()) * glass.getColumnCount();
        glass.setRect(
          new Rect(x, y, x + w, y + h)
        );
        nextFigureX = x + w + border;
        nextFigureY = border;

        ratingX = x + w + border;
        ratingY = border + glass.getShapeWidth() * 3;
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
        nextFigure.onDraw(canvas, nextFigureX, nextFigureY, 20, 20);
        //nextFigure.onDraw(canvas, nextFigureX, nextFigureY, glass.getShapeWidth(), glass.getShapeHeight());
      }

      if(state == State.PAUSED || state == State.FINISHED)
      {
        String text;

        if(state == State.PAUSED)
          text = context.getString(R.string.paused);
        else
          text = context.getString(R.string.finished);


        paint.setTypeface(Typeface.DEFAULT);// your preference here
        paint.setTextSize(40);// have this the same as your text size
        paint.getTextBounds(text, 0, text.length(), bounds);

        Rect glassRect = glass.getRect();
        int x = glassRect.left + (glassRect.width() - bounds.width()) / 2;
        int y = glassRect.top + (glassRect.height() - bounds.height())/ 2;


        bounds.set(glassRect.left, y - bounds.height() * 2, glassRect.right, y + bounds.height());

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(bounds, paint);

        paint.setColor(Color.BLACK);
        canvas.drawText(text, x, y, paint);
      }

      if(showRating)
      {
        paint.setColor(Color.BLUE);
        paint.setTextSize(30);

        int y = ratingY + 30;
        canvas.drawText(context.getString(R.string.speed) + ": " + speedRate + "%", ratingX, y, paint);
        y += 30;
        canvas.drawText(context.getString(R.string.complex) + ": " + complexRate + "%", ratingX, y, paint);
        y += 30;
        canvas.drawText(context.getString(R.string.score) + ": " + glass.getScore(), ratingX, y, paint);
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
    public void toglePause()
    {
      if(state == State.WORKED)
        state = State.PAUSED;
      else
      if(state == State.PAUSED)
        state = State.WORKED;

    }
    /*============================================================*/
    public void moveLeft()
    {
      if(state == State.PAUSED)
      {
        state = State.WORKED;
      }
      else
      if(state == State.WORKED) {
        glass.moveLeft();
        setModified(glass.isModified());
      }
    }
    /*============================================================*/
    public void moveRight()
    {
      if(state == State.PAUSED)
      {
        state = State.WORKED;
      }
      else
      if(state == State.WORKED) {
        glass.moveRight();
        setModified(glass.isModified());
      }
    }
    /*============================================================*/
    public void moveDown()
    {
      if(state == State.PAUSED)
      {
        state = State.WORKED;
      }
      else
      if(state == State.WORKED) {
        glass.moveBottom();
        setModified(glass.isModified());
      }
    }
    /*============================================================*/
    public void rotate()
    {
      if(state == State.PAUSED)
      {
        state = State.WORKED;
      }
      else
      if(state == State.WORKED) {
        glass.rotate();
        setModified(glass.isModified());
      }
    }
    /*============================================================*/
    protected void nextInterval()
    {
      if(nextFigure == null)
        nextFigure = onNewFigure();

      if(state != State.WORKED)
        return;

      if(glass.getActiveFigure() == null)
      {

        if(glass.annigilation()) {
          lastTime = System.currentTimeMillis() - interval;

        }
        else
        {

          if (!glass.put(nextFigure)) {
            state = State.FINISHED;
            glass.setModified(true);
//          Log.d("Game", "Error add figure");
          } else {
//          Log.d("Game", "Add figure Ok");
            nextFigure = onNewFigure();
          }
        }
      }
      else
      {
        glass.moveDown();
      }

      setModified(glass.isModified());
    }

  }
  /*-----------------------------------------------------------------------------------------------*/

}

