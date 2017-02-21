package ua.probe.oleg.tetrisgamecollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

/**
 * TetrisBase - base classes for game collections
 */

class TetrisBase {

  /*-----------------------------------------------------------------------------------------------*/
  static class Cell
  {
    private int col, row;
    Cell() {col = row = 0;}
    Cell(int c, int r) {col = c; row = r;}
    Cell(Cell other) { col = other.col; row = other.row;}

    int column() {return col;}
    int row() {return row;}
    void setColumn(int c) {col = c;}
    void setRow(int r) {row = r;}
    void save(DataOutputStream dos) throws IOException
    {
      dos.writeInt(col);
      dos.writeInt(row);
    }
    void load(DataInputStream dis)  throws IOException
    {
      col = dis.readInt();
      row = dis.readInt();
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  private static Paint borderPaint = new Paint();
  /*-----------------------------------------------------------------------------------------------*/
  private enum BevelCut {
    None,
    Lowered,
    Raised
  }
  /*-----------------------------------------------------------------------------------------------*/
  private static void drawShadow(Canvas canvas, Rect rect, int thick, BevelCut cut)
  {
    final int borderLightColor = Color.LTGRAY;
    final int borderShadowColor = Color.GRAY;

    Paint paint = borderPaint;
    paint.setStrokeWidth(thick);
    paint.setStyle(Paint.Style.STROKE);

    if(cut != BevelCut.None)
    {
      int color1 = borderLightColor;
      int color2 = borderShadowColor;

      if(cut == BevelCut.Raised)
      {
        color1 = borderShadowColor;
        color2 = borderLightColor;
      }

      //Top
      int x1 = rect.left;
      int x2 = rect.right - 2 * thick;
      int y1 = rect.top;
      int y2 = y1;

      paint.setColor(color1);
      canvas.drawLine(x1, y1, x2, y2, paint);
      y1 += thick; y2 += thick;

      paint.setColor(color2);
      canvas.drawLine(x1, y1, x2, y2, paint);

      //Left
      x1 = rect.left;
      x2 = x1;
      y1 = rect.top;
      y2 = rect.bottom - 2 * thick;

      paint.setColor(color1);
      canvas.drawLine(x1, y1, x2, y2, paint);
      x1 += thick; x2 += thick;
      y1 += thick;

      paint.setColor(color2);
      canvas.drawLine(x1, y1, x2, y2, paint);

      //bottom
      x1 = rect.left;
      x2 = rect.right - 2 * thick;
      y1 = rect.bottom - 2 * thick;
      y2 = y1;

      paint.setColor(color1);
      canvas.drawLine(x1, y1, x2, y2, paint);
      y1 += thick; y2 += thick;
      x1 += thick; x2 += thick;

      paint.setColor(color2);
      canvas.drawLine(x1, y1, x2, y2, paint);

      //Right
      x1 = rect.right - 2 * thick;
      x2 = x1;
      y1 = rect.top;
      y2 = rect.bottom - 2 * thick;

      paint.setColor(color1);
      canvas.drawLine(x1, y1, x2, y2, paint);
      x1 += thick; x2 += thick;
      y2 += thick;

      paint.setColor(color2);
      canvas.drawLine(x1, y1, x2, y2, paint);
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  private static void drawBorder(Canvas canvas, Rect rect, int width, BevelCut inner, BevelCut outer)
  {
    final int borderColor = Color.BLACK;

    if(inner != BevelCut.None && outer != BevelCut.None) {

      Paint paint = borderPaint;
      paint.setStrokeWidth(width);
      paint.setStyle(Paint.Style.STROKE);
      paint.setColor(borderColor);
      canvas.drawRect(rect, paint);
    }

    if(inner != BevelCut.None)
    {
      drawShadow(canvas,
        new Rect(rect.left, rect.top, rect.right - 2 * width, rect.bottom - 2 * width),
        width,
        inner);
    }
    if(outer != BevelCut.None)
    {
      drawShadow(canvas,
        new Rect(rect.left - 2 * width, rect.top - 2 * width,  rect.right, rect.bottom),
        width,
        outer);
    }

  }
  /*-----------------------------------------------------------------------------------------------*/
  private static Paint squarePaint = new Paint();
  private static Bitmap squareBitmap = null;

  static class Square
  {
    int fillColor, borderColor;
//    protected int width = 0, height = 0;
    Paint paint = squarePaint;
    Rect rect = new Rect();

    Square(int fColor, int bColor)
    {
      fillColor = fColor;
      borderColor = bColor;
    }

    Square(int fColor)
    {
      fillColor = fColor;
      borderColor = Color.BLACK;
    }

    Square(Square other)
    {
      fillColor = other.fillColor;
      borderColor = other.borderColor;
    }

    Square()
    {
      fillColor = Color.WHITE;
      borderColor = Color.BLACK;
    }

    public int color() {return fillColor;}

    void onDraw(Canvas canvas, int x, int y, int width, int height)
    {

      rect.set(x, y, x + width, y + height);

      // перенастраивам кисть на заливку
      paint.setColor(fillColor);
      paint.setStyle(Paint.Style.FILL);
      canvas.drawRect(rect, paint);

      /*
      if(squareBitmap != null) {
        canvas.drawBitmap(squareBitmap, null, rect, paint);
      }

      if(squareBitmap == null) {
        // перенастраивам кисть на контуры
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect, paint);
      }
      */
      drawShadow(canvas, rect, 1, BevelCut.Raised);
    }

    void save(DataOutputStream dos) throws IOException
    {
      dos.writeInt(fillColor);
    }
    void load(DataInputStream dis)  throws IOException
    {
      fillColor = dis.readInt();
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Figure implements Cloneable
  {
    SparseArray<Square> squareMap;
    int columnCount = 0, rowCount = 0;

    Figure()
    {
      squareMap = new SparseArray<>();
    }

    Figure(Figure other)
    {
      squareMap = other.squareMap.clone();
      columnCount = other.columnCount;
      rowCount = other.rowCount;
    }

    public Figure clone() throws CloneNotSupportedException
    {
      Figure f = (Figure) super.clone();
      f.squareMap = squareMap.clone();
      f.columnCount = columnCount;
      f.rowCount = rowCount;
      return f;
    }


    int index(int column, int row)
    {
      return column + 1000000 * row;
    }

    synchronized Square get(int column, int row)
    {
      return squareMap.get(index(column, row));
    }

    synchronized void put(int column, int row, Square s)
    {
      squareMap.put(index(column, row), s);
      if(column + 1 > columnCount)
        columnCount = column + 1;
      if(row + 1 > rowCount)
        rowCount = row + 1;
    }

    int getRowCount() {return rowCount;}
    int getColumnCount() {return columnCount;}

    public Figure rotateLeft()
    {
      //Pure abstract
      return new Figure(this);
    }

    public Figure rotateRight()
    {
      //Pure abstract
      return new Figure(this);
    }

    void onDraw(Canvas canvas, int left, int top, int shapeWidth, int shapeHeight)
    {

      for(int r = 0; r < rowCount; r++)
      {
        for(int c = 0; c < columnCount; c++)
        {
          Square s = get(c, r);
          if(s != null)
          {
            int x = left + c * shapeWidth;
            int y = top + r * shapeHeight;
            s.onDraw(canvas, x, y, shapeWidth, shapeHeight);
          }
        }
      }
    }

    Square onNewShape()
    {
      return new Square();
    }

    void save(DataOutputStream dos) throws IOException
    {
      dos.writeInt(columnCount);
      dos.writeInt(rowCount);

      int size = squareMap.size();
      dos.writeInt(size);
      for(int i = 0; i < size; i++)
      {
        int key = squareMap.keyAt(i);
        dos.writeInt(key);
        Square s = squareMap.valueAt(i);
        s.save(dos);

      }

    }
    void load(DataInputStream dis)  throws IOException
    {
      squareMap.clear();
      columnCount = dis.readInt();
      rowCount = dis.readInt();
      int size = dis.readInt();
      for(int i = 0; i < size; i++)
      {
        int key = dis.readInt();
        Square s = onNewShape();
        s.load(dis);
        squareMap.put(key, s);
      }
    }

  }

  /*-----------------------------------------------------------------------------------------------*/
  static class Statistics implements Cloneable
  {
    static final int
      TEXT_COL = 0,
      NUMBER_COL = 1,
      SCORE_ROW = 0,
      FIGURE_COUNT_ROW = 1,
      SQUARE_COUNT_ROW = 2;

    long score = 0;
    long figureCount = 0;
    long squareCount = 0;
    long workTime = 0; //ms

    public Statistics clone() throws CloneNotSupportedException
    {
      Statistics s = (Statistics) super.clone();
      s.score = score;
      s.figureCount = figureCount;
      s.squareCount = squareCount;
      s.workTime = workTime;
      return s;
    }

    void clear()
    {
      score = 0;
      figureCount = 0;
      squareCount = 0;
      workTime = 0;
    }

    void save(DataOutputStream dos) throws IOException
    {
      dos.writeLong(score);
      dos.writeLong(figureCount);
      dos.writeLong(squareCount);
      dos.writeLong(workTime);
    }

    void load(DataInputStream dis)  throws IOException
    {
      score = dis.readLong();
      figureCount = dis.readLong();
      squareCount = dis.readLong();
      workTime = dis.readLong();
    }

    String gameTimeText()
    {
      final long SEC_IN_MINUTE = 60;
      final long SEC_IN_HOUR = 3600;
      final long SEC_IN_DAY = 86400;
      long day, hour, min, sec;

      sec = workTime / 1000;

      day = sec / SEC_IN_DAY;
      sec %= SEC_IN_DAY;

      hour = sec / SEC_IN_HOUR;
      sec %= SEC_IN_HOUR;

      min = sec / SEC_IN_MINUTE;
      sec %= SEC_IN_MINUTE;


      return String.format(Locale.getDefault(), "%,d-%02d:%02d:%02d",
        day, hour, min, sec);
    }

    String getText(int col, int row, Context context)
    {
      String text = "";
      if(col == TEXT_COL) {
        switch(row) {
          case SCORE_ROW:
            text = context.getString(R.string.score);
            break;
          case FIGURE_COUNT_ROW:
            text = context.getString(R.string.number_of_figures);
            break;
          case SQUARE_COUNT_ROW:
            text = context.getString(R.string.number_of_squares);
            break;
        }
      }

      if(col == NUMBER_COL) {
        switch (row)
        {
          case SCORE_ROW:
            text = String.format(Locale.getDefault(), "%,d", score);
            break;
          case FIGURE_COUNT_ROW:
            text = String.format(Locale.getDefault(), "%,d", figureCount);
            break;
          case SQUARE_COUNT_ROW:
            text = String.format(Locale.getDefault(), "%,d", squareCount);
            break;
        }
      }
      return text;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  private static class GlassAction  implements Cloneable
  {
    int rotate = 0;
    int move = 0;
    int rate = 0;

    GlassAction() {
    rotate = 0;
    move = 0;
    rate = 0;
    }

    GlassAction(GlassAction other)
    {
      rotate = other.rotate;
      move = other.move;
      rate = other.rate;
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  //private static Bitmap glassBitmap = null;
  static class Glass implements Cloneable
  {
    int rowCount, columnCount;
    Rect rect;
    Figure activeFigure = null;
    Cell activeFigurePosition = new Cell();
    boolean modified = false;
    private Paint paint, guidePaint;
    private Path guidePath;
    private int bgColor = Color.WHITE;

    //private long score;
    private long scoreScale = 100;

    private boolean drawGuideLines = true;

    private SparseArray<Square> squareMap;
    private Statistics statistics;
    private int borderWidth = 1;

    Glass(int columns, int rows)
    {
      init(columns, rows);
    }

    void init(int columns, int rows)
    {
      columnCount = columns;
      rowCount = rows;
      squareMap = new SparseArray<>();
      paint = new Paint();

      guidePaint = new Paint();
      guidePaint.setColor(Color.BLACK);
      guidePaint.setStyle(Paint.Style.STROKE);
      guidePaint.setPathEffect(new DashPathEffect(new float[] {20 ,30}, 0));

      guidePath = new Path();

      statistics = onNewStatistics();
      bgColor = Color.WHITE;
    }

    Statistics onNewStatistics() {
      return new Statistics();
    }


    public Glass clone() throws CloneNotSupportedException
    {
      Glass g = (Glass) super.clone();
      g.init(columnCount, rowCount);
      g.squareMap = squareMap.clone();
      g.rect = new Rect(rect);
      if(activeFigure != null)
        g.activeFigure = activeFigure.clone();

      g.activeFigurePosition = new Cell(activeFigurePosition);
      g.scoreScale = scoreScale;
      g.drawGuideLines = drawGuideLines;
      g.statistics = statistics.clone();
      g.bgColor = bgColor;
      return g;
    }


    public boolean isModified() {return modified;}
    protected void setModified(boolean m) {modified = m;}

    Statistics getStatistics() {return statistics;}
    int getBgColor() {return bgColor;}
    void setBgColor(int c) {bgColor = c; setModified(true);}

    void setRect(Rect r)
    {
      rect = r;
    }

    Rect getRect()
    {
      return  rect;
    }

    long getScore() {return statistics.score;}
    void addRemovedShapes(int shapes) {
      statistics.score += shapes * scoreScale;
    }
    void setScoreScale(long s)
    {
      scoreScale = s;
    }

    int getRowCount() {return rowCount;}
    int getColumnCount() {return columnCount;}
    int getShapeWidth() { return rect.width() / columnCount;}
    int getShapeHeight() {return rect.height() / rowCount;}

    void onDraw(Canvas canvas)
    {
      Figure figure = this.activeFigure; //Thread safe

      // перенастраивам кисть на заливку
      paint.setColor(bgColor);
      paint.setStyle(Paint.Style.FILL);
      canvas.drawRect(rect, paint);

      drawBorder(canvas, rect, borderWidth,  BevelCut.Raised, BevelCut.Lowered);

      int shapeWidth = getShapeWidth();
      int shapeHeight = getShapeHeight();
      for(int r = 0; r < rowCount; r++)
      {
        for(int c = 0; c < columnCount; c++)
        {
          Square s = get(c, r);
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

      if(figure != null)
      {
        int x = rect.left + activeFigurePosition.column() * shapeWidth;
        int y = rect.top + activeFigurePosition.row() * shapeHeight;
        figure.onDraw(canvas, x, y, shapeWidth, shapeHeight);

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
          gy = y + figure.getRowCount() * shapeHeight;
          for (int i = figure.getRowCount() - 1; i >= 0; i--) {
            if (figure.get(0, i) != null)
              break;
            gy -= shapeHeight;
          }
          guidePath.reset();
          guidePath.moveTo(gx, gy + shapeHeight / 2);
          guidePath.lineTo(gx, rect.bottom);
          canvas.drawPath(guidePath, guidePaint);
          //canvas.drawLine(gx, gy, gx, rect.bottom, guidePaint);

          //Right
          gy = y + figure.getRowCount() * shapeHeight;
          for (int i = figure.getRowCount() - 1; i >= 0; i--) {
            if (figure.get(figure.getColumnCount() - 1, i) != null)
              break;
            gy -= shapeHeight;
          }

          gx += figure.getColumnCount() * shapeWidth;

          guidePath.reset();
          guidePath.moveTo(gx, gy + shapeHeight / 2);
          guidePath.lineTo(gx, rect.bottom);
          canvas.drawPath(guidePath, guidePaint);
          //canvas.drawLine(gx, gy, gx, rect.bottom, guidePaint);
        }
      }

      setModified(false);
    }

    private int index(int column, int row)
    {
      return column + 1000000 * row;
    }

    synchronized Square get(int column, int row)
    {
      if(column < 0
        || column >= columnCount
        || row < 0
        || row >= rowCount)
        return null;

      return squareMap.get(index(column, row));
    }

    synchronized void put(int column, int row, Square s)
    {
      if(column < 0
        || column >= columnCount
        || row < 0
        || row >= rowCount)
        return;

      if(s == null)
        squareMap.remove(index(column, row));
      else
         squareMap.put(index(column, row), s);
      setModified(true);
    }

    boolean validPosition(int column, int row, Figure f)
    {
      if(row + f.getRowCount() > rowCount || row < 0)
        return false;
      if(column + f.getColumnCount() > columnCount || column < 0)
        return false;

      int fRowCount = f.getRowCount();
      int fColumnCount = f.getColumnCount();
      for(int r = 0; r < fRowCount; r++)
      {
        for(int c = 0; c < fColumnCount; c++)
        {
          if(get(c + column, r + row) != null && f.get(c, r) != null)
            return false;
        }
      }
      return true;
    }

    synchronized boolean put(Figure f)
    {
      if(activeFigure != null)
        return false;
      Cell cell = new Cell();
      //Centered in top
      cell.setColumn( (columnCount - f.getColumnCount()) / 2 );
      if(!validPosition(cell.column(), cell.row(), f))
        return false;
      activeFigure = f;
      activeFigurePosition = cell;
      statistics.figureCount ++;
      setModified(true);
      return true;
    }

    Figure getActiveFigure() {return activeFigure;}


    boolean moveLeft()
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

    boolean moveRight()
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


    boolean rotateRight()
    {
      if(activeFigure == null)
        return false;
      Figure f = activeFigure.rotateRight();
      if(validPosition(activeFigurePosition.column(), activeFigurePosition.row(), f))
      {
        activeFigure = f;
        setModified(true);
        return true;
      }
      return false;
    }

    boolean rotateLeft()
    {
      if(activeFigure == null)
        return false;
      Figure f = activeFigure.rotateLeft();
      if(validPosition(activeFigurePosition.column(), activeFigurePosition.row(), f))
      {
        activeFigure = f;
        setModified(true);
        return true;
      }
      return false;
    }

    boolean rotate()
    {
      return rotateRight();
    }

    boolean moveDown()
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

    boolean moveBottom()
    {
      if(activeFigure == null)
        return false;

      boolean result = moveDown();
      while(result) {
        if(!moveDown())
          break;
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
          Square s = activeFigure.get(column, row);
          if(s != null)
          {
            put(activeFigurePosition.column() + column,
              activeFigurePosition.row() + row,
              s);
            statistics.squareCount++;
          }
        }
      }
      activeFigure = null;
    }

    boolean annigilation()
    {
      return false;
    }


    void onNewGame()
    {
      squareMap.clear();
      activeFigure = null;
      activeFigurePosition = new Cell();
      statistics.clear();
    }

    Square onNewShape()
    {
      return new Square();
    }

    void save(DataOutputStream dos) throws IOException
    {
      int size = squareMap.size();
      dos.writeInt(size);
      for(int i = 0; i < size; i++)
      {
        int key = squareMap.keyAt(i);
        dos.writeInt(key);
        Square s = squareMap.valueAt(i);
        s.save(dos);
      }
      statistics.save(dos);
    }

    void load(DataInputStream dis)  throws IOException
    {
      squareMap.clear();
      activeFigure = null;

      int size = dis.readInt();
      for(int i = 0; i < size; i++)
      {
        int key = dis.readInt();
        Square s = onNewShape();
        s.load(dis);
        squareMap.put(key, s);
      }
      statistics.load(dis);
    }

    /**
     * Calculate rating of glass content for brute variants for the best possible step
     * @return rateing of the glass content
     */
    int calcContentRating()
    {
      /* Pure virtual */
      return 0;
    }

    /**
     * Calculate best action for current state of glass
     * @return action or null
     */
    GlassAction calcBestWay()
    {
      if(activeFigure == null)
        return null;

      ArrayList<GlassAction> actionList = new ArrayList<>();
      for (int rotation = 0; rotation < 4; rotation++) {
        Glass g0;
        try {
          g0 = clone();
        } catch (CloneNotSupportedException e) {
          Log.e("Glass", e.getMessage());
          return null;
        }

        GlassAction action = new GlassAction();
        for (action.rotate = 0; action.rotate < rotation; action.rotate++) {
          if (!g0.rotate())
            break;
        }

        if (action.rotate < rotation)
          break;

        for(int col = 0; col < columnCount; col ++)
        {
            Glass g1;
            try {
              g1 = g0.clone();
            } catch (CloneNotSupportedException e) {
              Log.e("Glass", e.getMessage());
              return null;
            }

          action.move = 0;
          while(g1.activeFigurePosition.column() > col)
          {
            if(!g1.moveLeft())
              break;
            action.move -= 1;
          }
          while(g1.activeFigurePosition.column() < col)
          {
            if(!g1.moveRight())
              break;
            action.move += 1;
          }

          if(col != g1.activeFigurePosition.column())
            continue;

          g1.moveBottom();
          action.rate = g1.calcContentRating();
          actionList.add(new GlassAction(action));
        }
      }

      if(actionList.isEmpty())
        return null;


      String  txt = "";
      for(GlassAction action : actionList) {
        txt += String.format(Locale.getDefault(), "{move:%d, rotate:%d, rate:%d}",
          action.move,
          action.rotate,
          action.rate
          );
      }
      Log.d("CalcBestWay()", txt);

      //Find action with max rating
      int maxIdx = 0, maxRate = actionList.get(0).rate;
      int sz = actionList.size();
      for(int i = 1; i < sz; i++)
      {
        int rate = actionList.get(i).rate;
        if(rate > maxRate)
        {
          maxRate = rate;
          maxIdx = i;
        }
      }
      return actionList.get(maxIdx);
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Settings
  {
    static final int MinColumns = 8, MaxColumns = 16;
    static final int MinRows = 16, MaxRows = 32;

    int tickTime = 800;
    int complexRate = 0;
    int columnCount = 10, rowCount = 20;
    boolean showNextFigure = true;
    boolean showScore = true;
    boolean showGuideLines = true;
    boolean useAccelerometer = false;
    boolean useTouch = true;
    boolean useShake = false;
    long randomSeed;
    int glassColor = Color.argb(80, 102, 204, 255);
    int statusColor = Color.rgb(229, 234, 171);
    int statusTextColor = Color.BLACK;


    private void check()
    {
      if(tickTime <= 100)
        tickTime = 100;
      if(complexRate < 0)
        complexRate = 0;
      if(complexRate > 100)
        complexRate = 100;
      if(columnCount < MinColumns)
        columnCount = MinColumns;
      if(columnCount > MaxColumns)
        columnCount = MaxColumns;
      if(rowCount < MinRows)
        rowCount = MinRows;
      if(rowCount > MaxRows)
        rowCount = MaxRows;
    }

    void load(Context context, String sectionName)
    {
      SharedPreferences preferences = context.getSharedPreferences(sectionName, Context.MODE_PRIVATE);
      tickTime = preferences.getInt("tickTime", tickTime);
      complexRate = preferences.getInt("complexRate", complexRate);
      columnCount = preferences.getInt("columnCount", columnCount);
      rowCount = preferences.getInt("rowCount", rowCount);
      showNextFigure = preferences.getBoolean("showNextFigure", showNextFigure);
      showScore = preferences.getBoolean("showScore", showScore);
      showGuideLines = preferences.getBoolean("showGuideLines", showGuideLines);
      useAccelerometer = preferences.getBoolean("useAccelerometer", useAccelerometer);
      useTouch = preferences.getBoolean("useTouch", useTouch);
      useShake = preferences.getBoolean("useShake", useShake);
      randomSeed = preferences.getLong("randomSeed", 0);
      glassColor = preferences.getInt("glassColor", glassColor);

      statusColor = preferences.getInt("statusColor", statusColor);
      statusTextColor = preferences.getInt("statusTextColor", statusTextColor);
      check();
    }


    void save(Context context, String sectionName)
    {
      SharedPreferences preferences = context.getSharedPreferences(sectionName, Context.MODE_PRIVATE);
      SharedPreferences.Editor ed = preferences.edit();

      check();
      ed.putInt("tickTime", tickTime);
      ed.putInt("complexRate", complexRate);
      ed.putInt("columnCount", columnCount);
      ed.putInt("rowCount", rowCount);
      ed.putBoolean("showNextFigure", showNextFigure);
      ed.putBoolean("showScore", showScore);
      ed.putBoolean("showGuideLines", showGuideLines);
      ed.putBoolean("useAccelerometer", useAccelerometer);
      ed.putBoolean("useTouch", useTouch);
      ed.putBoolean("useShake", useShake);
      ed.putLong("randomSeed", randomSeed);
      ed.putInt("glassColor", glassColor);
      ed.putInt("statusColor", statusColor);
      ed.putInt("statusTextColor", statusTextColor);

      ed.apply();
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Controller
  {
    private boolean modified = false;
    Context context;
    String sectionName;
    Rect rect;
    Rect bounds;
    Paint paint;
    Glass glass;
    final int minInterval = 250, maxInterval = 2250;

    private int interval = 1000; //ms
    private long lastTime = 0;

    Settings settings;

    //protected int defaultColumnCount = 8, defaultRowCount = 16;
    //protected int complexRate = 50, tickTime = 50;
    enum State
    {
      PAUSED,
      WORKED,
      TRACKED,
      ROTATED,
      FINISHED
    }

    State state = State.PAUSED;

    //boolean showNextFigure = true;
    Figure nextFigure;
    //int nextFigureX, nextFigureY;

    Rect nextFigureRect = new Rect();
    Rect gameTimeRect = new Rect();
    Rect statisticRect = new Rect();
    Rect statusRect = new Rect();

    //boolean showScore = true;
    //int ratingX, ratingY;
    Accelerometer accelerometer;
    Bitmap leftArrowBitmap, rightArrowBitmap, touchBitmap, rotateBitmap, moveBitmap, screenRotationBitmap, robotBitmap;
    final double ROTATION_ANGLE = Math.PI / 6; //30 degree
    Random random;
    final int UNDO_SIZE = 32;
    Stack<byte[]> undo;
    final int TEXT_SIZE = 30;
    boolean demoMode = false;
    int backColor = Color.WHITE;
    /*============================================================*/
    Controller(Context c, String sectionName)
    {
      context = c;
      this.sectionName = sectionName;
      settings = new Settings();
      settings.load(context, sectionName);
      glass = onGlassCreate();
      paint = new Paint();
      bounds = new Rect();
      state = State.PAUSED;
      setup();
      leftArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_left);
      rightArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_right);
      touchBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_touch);
      rotateBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_rotate);
      moveBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_move);
      screenRotationBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_screen_rotation);
      robotBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_robot);

      squareBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.square);

      //glassBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.square);


      TypedValue a = new TypedValue();
      context.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
      if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
        // windowBackground is a glassColor
        backColor = a.data;
      }


      if(settings.useAccelerometer || settings.useShake)
        accelerometer = new Accelerometer(context);

      random = new Random();
      undo = new Stack<>();

      load();
    }
    /*============================================================*/
    void setup()
    {
      interval = settings.tickTime;
      glass.setBgColor(settings.glassColor);
      glass.setScoreScale(1); //TODK !
    }
    /*============================================================*/
    void onSettingsChanged()
    {
      settings.load(context, sectionName);
      glass = onGlassCreate();
      nextFigure = null;
      state = State.PAUSED;
      setup();
      geometryInit();
      setModified(true);
    }
    /*============================================================*/
    protected Glass onGlassCreate()
    {
      return new Glass(settings.columnCount, settings.rowCount);
    }
    /*============================================================*/
    protected Figure onNewFigure() {
      Figure figure = new Figure();
      figure.put(0, 0, new Square(Color.GREEN, Color.BLACK));
      figure.put(0, 1, new Square(Color.RED, Color.BLACK));
      figure.put(0, 2, new Square(Color.BLUE, Color.BLACK));

      return figure;
    }
    /*============================================================*/
    boolean isModified() {return modified;}
    /*============================================================*/
    void setModified(boolean m) {modified = m;}
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
      return colors[random.nextInt(colors.length)];
    }
    /*============================================================*/
    void setSize(int w, int h)
    {
      Log.d("setSize", "w=" + w + " h=" + h);
      rect = new Rect(0, 0, w, h);
      geometryInit();
    }
    /*============================================================*/
    private int nextFigureCellSize = 20;
    void geometryInit()
    {
      int x, y, w, h;
      int border = 20;
      int textHeight = 4 * TEXT_SIZE + TEXT_SIZE / 2;
      int textWidth = 10 * TEXT_SIZE;

      nextFigureRect.set(0, 0, 5 * nextFigureCellSize, 5 * nextFigureCellSize);
      if(rect.width() < rect.height()) //Verical
      {

        w = rect.width() - 2 * border;
        h = (w / glass.getColumnCount()) * glass.getRowCount();
        w = (h / glass.getRowCount()) * glass.getColumnCount();
        y = border + nextFigureRect.height();


        if(y + h + textHeight > rect.height())
        {
          h = rect.height() - y - textHeight;
          w = (h / glass.getRowCount()) * glass.getColumnCount();
          h = (w / glass.getColumnCount()) * glass.getRowCount();
        }

        x = (rect.width() - w) / 2;
        y = (rect.height() - h - nextFigureRect.height() - border - textHeight) / 2 + border + nextFigureRect.height();

        nextFigureRect.offsetTo(border, border);
        if(nextFigureRect.bottom > y - border)
          nextFigureRect.bottom = y;

        //nextFigureX = x ;
        //nextFigureY = border;
        gameTimeRect.set(nextFigureRect.right, nextFigureRect.top, rect.width() - border, nextFigureRect.bottom);
        statisticRect.set(border, y + h, rect.right - border, rect.bottom - border);
        //ratingX = x;
        //ratingY = y + h + TEXT_SIZE / 2;
      }
      else
      { //Horisontal
        x = border;
        h = rect.height() - 2 * border;
        w = (h / glass.getRowCount()) * glass.getColumnCount();
        h = (w / glass.getColumnCount()) * glass.getRowCount();

        if(x + w + textWidth > rect.width())
        {
          w = rect.width() - x - textWidth;
          h = (w / glass.getColumnCount()) * glass.getRowCount();
          w = (h / glass.getRowCount()) * glass.getColumnCount();
        }

        //x = (rect.width() - w) / 2;
        y = (rect.height() - h) / 2;

        nextFigureRect.offsetTo(x + w + border, border);
        gameTimeRect.set(nextFigureRect.right, nextFigureRect.top, rect.right - border, nextFigureRect.bottom);
        //nextFigureX = x + w + border;
        //nextFigureY = border;

        statisticRect.set(nextFigureRect.left, nextFigureRect.bottom, rect.right - border, nextFigureRect.bottom + textHeight);
        //ratingX = x + w + border + TEXT_SIZE / 2;
        //ratingY = border + glass.getShapeWidth() * 3;
      }

      glass.setRect(
        new Rect(x, y, x + w, y + h) );

    }
    /*============================================================*/
    private void drawNextFigure(Canvas canvas)
    {
      Figure figure = nextFigure;
      if(figure != null && settings.showNextFigure)
      {
        int x = nextFigureRect.left + (nextFigureRect.width() - figure.getColumnCount() * nextFigureCellSize) / 2;
        int y = nextFigureRect.top + (nextFigureRect.height() - figure.getRowCount() * nextFigureCellSize) / 2;
        figure.onDraw(canvas, x, y, nextFigureCellSize, nextFigureCellSize);
      }
      drawBorder(canvas, nextFigureRect, 1, BevelCut.Raised, BevelCut.Lowered);
    }
    /*============================================================*/
    private void drawStatusPanel(Canvas canvas, String text)
    {

      paint.setTypeface(Typeface.DEFAULT);// your preference here
      paint.setTextSize(TEXT_SIZE);// have this the same as your text size
      paint.getTextBounds(text, 0, text.length(), bounds);

      int x = rect.left + (rect.width() - bounds.width()) / 2;
      int y = rect.top + (rect.height() - bounds.height())/ 2;

      statusRect.set(x - TEXT_SIZE,
        y - 2 * TEXT_SIZE,
        x + bounds.width() + TEXT_SIZE,
        y + TEXT_SIZE);

      paint.setColor(settings.statusColor);
      paint.setAlpha(200);
      paint.setStyle(Paint.Style.FILL);
      canvas.drawRect(statusRect, paint);

      drawBorder(canvas, statusRect, 1, BevelCut.Raised, BevelCut.Lowered);

      paint.setColor(settings.statusTextColor);
      canvas.drawText(text, x, y, paint);
    }
    /*============================================================*/
    private void drawGameTime(Canvas canvas)
    {
      String text = glass.getStatistics().gameTimeText();

      paint.setTypeface(Typeface.DEFAULT);// your preference here
      paint.setTextSize(TEXT_SIZE);// have this the same as your text size
      paint.getTextBounds(text, 0, text.length(), bounds);

      int x = gameTimeRect.left + (gameTimeRect.width() - bounds.width()) / 2;
      int y = gameTimeRect.top + (gameTimeRect.height() - bounds.height()) / 2 + bounds.height();

      paint.setColor(Color.BLACK);
      canvas.drawText(text, x, y, paint);

      drawBorder(canvas, gameTimeRect, 1, BevelCut.Raised, BevelCut.Lowered);
    }
    /*============================================================*/
    private void drawStatistics(Canvas canvas)
    {
      long maxVal = 0;
      Statistics statistics = glass.getStatistics();

      //Find max value for call max string size
      if(maxVal < statistics.score)
        maxVal = statistics.score;
      if(maxVal < statistics.figureCount)
        maxVal = statistics.figureCount;
      if(maxVal < statistics.squareCount)
        maxVal = statistics.squareCount;

      String text = String.format(Locale.getDefault(), "%,d", maxVal);

      paint.setTypeface(Typeface.DEFAULT);// your preference here
      paint.setTextSize(TEXT_SIZE);// have this the same as your text size
      paint.getTextBounds(text, 0, text.length(), bounds);
      paint.setColor(Color.BLACK);

      int[] width = new int[] {
        statisticRect.width() - bounds.width() - 2 * TEXT_SIZE,
        bounds.width() + 2 * TEXT_SIZE
      };

      int height = statisticRect.height() / 3;

      if(height > TEXT_SIZE + TEXT_SIZE / 2)
        height = TEXT_SIZE + TEXT_SIZE / 2;

      if(height < bounds.height() + TEXT_SIZE / 2)
        height = bounds.height() + TEXT_SIZE / 2;


      for(int row = 0; row < 3; row ++)
      {
        text = statistics.getText(Statistics.TEXT_COL, row, context);

        paint.getTextBounds(text, 0, text.length(), bounds);


        int x = statisticRect.left + width[0] - bounds.width() - TEXT_SIZE;
        int y = statisticRect.top + height * row + (height - bounds.height()) / 2 + bounds.height();

        if(x < statisticRect.left)
          x = statisticRect.left + TEXT_SIZE;

        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(
          new Rect(statisticRect.left, statisticRect.top + height * row, statisticRect.left + width[0], statisticRect.top + height * (row + 1)),
          paint);

        paint.setColor(Color.BLACK);
        canvas.drawText(text, x, y, paint);


        text = statistics.getText(Statistics.NUMBER_COL, row, context);
        paint.getTextBounds(text, 0, text.length(), bounds);

        x = statisticRect.left + width[0] + TEXT_SIZE;

        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(
          new Rect(statisticRect.left + width[0], statisticRect.top + height * row, statisticRect.left + width[0] + width[1], statisticRect.top + height * (row + 1)),
          paint);

        paint.setColor(Color.BLACK);
        canvas.drawText(text, x, y, paint);

        drawBorder(canvas,
          new Rect(statisticRect.left, statisticRect.top + height * row, statisticRect.left + width[0], statisticRect.top + height * (row + 1)),
          1, BevelCut.Raised, BevelCut.Lowered);
        drawBorder(canvas,
          new Rect(statisticRect.left + width[0], statisticRect.top + height * row, statisticRect.left + width[0] + width[1], statisticRect.top + height * (row + 1)),
          1, BevelCut.Raised, BevelCut.Lowered);
      }

      //drawBorder(canvas, statisticRect, 1, BevelCut.Raised, BevelCut.Lowered);
    }
    /*============================================================*/
    void onDraw(Canvas canvas)
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

      //canvas.drawARGB(80, 102, 204, 255);
      canvas.drawColor(backColor);

      Rect glassRect = glass.getRect();
      glass.onDraw(canvas);

      drawNextFigure(canvas);
      drawGameTime(canvas);

      Bitmap leftBmp = null, rightBmp = null;

      if(demoMode) {
        leftBmp = robotBitmap;
        GlassAction action = demoAction;
        if (action != null) {
          if (action.rotate > 0)
            rightBmp = rotateBitmap;
          else
          if (action.move < 0)
            rightBmp = leftArrowBitmap;
          else
          if (action.move > 0)
            rightBmp = rightArrowBitmap;
        }
      }
      else {
        if (state == State.TRACKED) {
          leftBmp = touchBitmap;
          rightBmp = moveBitmap;
        } else if (state == State.ROTATED) {
          leftBmp = touchBitmap;
          rightBmp = rotateBitmap;
        } else if (settings.useAccelerometer && accelerometer != null) {
          Accelerometer.Orientation o = accelerometer.getActualDeviceOrientation();
          if (Math.abs(o.y) >= ROTATION_ANGLE) {
            leftBmp = screenRotationBitmap;

            if (o.y < 0) { //Left
              rightBmp = leftArrowBitmap;
            } else { //Right
              rightBmp = rightArrowBitmap;
            }
          }
        }
      }

      if(leftBmp != null) {
        canvas.drawBitmap(leftBmp,
          glassRect.left,
          glassRect.top,
          paint);
      }

      if(rightBmp != null) {
        canvas.drawBitmap(rightBmp,
          glassRect.right - rightBmp.getWidth(),
          glassRect.top,
          paint);
      }


      if(settings.showScore) {
        drawStatistics(canvas);
      }

      if(state == State.PAUSED || state == State.FINISHED)
      {
        String text;

        if(state == State.PAUSED)
          text = context.getString(R.string.paused);
        else
          text = context.getString(R.string.finished);

        drawStatusPanel(canvas, text);
      }

    }

    /*============================================================*/
    void onPause()
    {
      if(accelerometer != null)
        accelerometer.onPause();
      save();
      if(state != State.FINISHED) {
        state = State.PAUSED;
      }
    }
    /*============================================================*/
    void onResume()
    {
      if(accelerometer != null)
        accelerometer.onResume();

      if(state == State.PAUSED && demoMode)
        state = State.WORKED;
    }
    /*============================================================*/
    GlassAction demoAction = null;
    void onTimer()
    {
      int interval = demoMode ? 200 : this.interval;

      if(System.currentTimeMillis() - lastTime >= interval)
      {
        lastTime = System.currentTimeMillis();
        if(state == State.WORKED)
          glass.getStatistics().workTime += interval;
        nextInterval();
      }

      if(!demoMode
        && settings.useShake
        && accelerometer != null)
      {
        if(accelerometer.isModified())
          setModified(true);
        if(accelerometer.isShakeDetected())
        {
          if(state == State.WORKED)
          {
            rotate();
          }
          Log.d("ShakeDetected", String.format("%s%s%s",
            accelerometer.isShakeX() ? "X " : "",
            accelerometer.isShakeY() ? "Y " : "",
            accelerometer.isShakeZ() ? "Z " : "")
            );
          accelerometer.clearShakeDetected();
        }
      }
    }
    /*============================================================*/
    void nextInterval()
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
          mkUndoPoint();
          if (!glass.put(nextFigure)) {
            state = State.FINISHED;
            glass.setModified(true);
//          Log.d("Game", "Error add figure");
//            Toast.makeText(context, context.getString(R.string.finished), Toast.LENGTH_SHORT).show();
          } else {
//          Log.d("Game", "Add figure Ok");
            nextFigure = onNewFigure();
            if(demoMode)
            {
              demoAction = glass.calcBestWay();
              demoAction.rate = 1; //Use rate as counter
            }
          }
        }
      }
      else
      {
        if (demoMode) {
          if (demoAction != null) {
            if(demoAction.rate != 0) {
              demoAction.rate = 0;
            }
            else
            if (demoAction.rotate > 0) {
              while (demoAction.rotate > 0) {
                glass.rotate();
                demoAction.rotate--;
              }
            }
            else
            if (demoAction.move != 0) {

              while (demoAction.move > 0) {
                glass.moveRight();
                demoAction.move += -1;
              }

              while (demoAction.move < 0) {
                glass.moveLeft();
                demoAction.move += 1;
              }
            }
            else
            {
              glass.moveBottom();
              demoAction = null;
            }

            setModified(true);
          }
        }

        if(!demoMode && settings.useAccelerometer && accelerometer != null)
        {
          Accelerometer.Orientation o = accelerometer.getActualDeviceOrientation();
          if(Math.abs(o.y) >= ROTATION_ANGLE)
          {
            if(o.y < 0)
              glass.moveLeft();
            else
              glass.moveRight();
          }
        }

        glass.moveDown();
      }

      setModified(glass.isModified());
    }
    /*============================================================*/
    Rect activeFigureRect()
    {
      if(glass.activeFigure == null)
        return null;

      int x = glass.rect.left + glass.activeFigurePosition.column() * glass.getShapeWidth();
      int y = glass.rect.top + glass.activeFigurePosition.row() * glass.getShapeHeight();
      int w = 3 * glass.getShapeWidth();
      int h = 3 * glass.getShapeHeight();

      return new Rect(x, y, x+w, y + h);
    }
    /*============================================================*/
    int trackX, trackY;
    int trackX2, trackY2;

    void onTouchDown(int id, float fx, float fy)
    {
      if(settings.useTouch)
        demoMode = false;

      int x = Math.round(fx);
      int y = Math.round(fy);

      //Log.d("onTouchDown", String.format("id=%d x=%.0f y=%.0f", id, x, y));
      if( settings.useTouch
        && glass.activeFigure != null
        && id == 0
        && (state == State.PAUSED || state == State.WORKED) )
      { //First finger
        int sw = glass.getShapeWidth();
        int sh = glass.getShapeHeight();
        Rect r = activeFigureRect();

        r.left -= sw;
        r.right += sw;
        r.top -= sh;
        r.bottom += sh;
        //Log.d("onTouchDown", String.format("left=%d top=%d right=%d down=%d", r.left, r.top, r.right, r.bottom));
        if(r.contains(x, y))
        {
          state = State.TRACKED;
          trackX = x;
          trackY = y;
        }
        else
        {
          state = State.ROTATED;
          trackX2 = x;
          trackY2 = y;
        }
      }

      if( settings.useTouch
        && (state == State.TRACKED) )
      { //Second finger
        trackX2 = x;
        trackY2 = y;
        //Log.d("onTouchDown", String.format("2nd finger id=%d x=%.0f y=%.0f", id, x, y));
      }
    }
    /*============================================================*/
    void onTouchUp(int id, float x, float y)
    {
      if(state == State.TRACKED && id == 0)
      {
        state = State.WORKED;
      }

      if(state == State.ROTATED)
      {
        state = State.WORKED;
      }
    }
    /*============================================================*/
    void onTouchMove(int id, float fx, float fy)
    {
      //Log.d("onTouchMove", String.format("id=%d x=%.0f y=%.0f", id, x, y));
      int x = Math.round(fx);
      int y = Math.round(fy);
      if(state == State.TRACKED && id == 0)
      { //First finger
        int w = glass.getShapeWidth();
        int h = glass.getShapeHeight();
        if(Math.abs(x - trackX) >= w
          || Math.abs(y - trackY) >= h)
        {
          Cell c = new Cell(glass.activeFigurePosition);
          c.setColumn(c.column() + (x - trackX)/w);
          c.setRow(c.row() + (y - trackY)/h);

          //Log.d("onTouchMove", String.format("x=%.0f y=%.0f col=%d row=%d", x, y, c.column(), c.row()));
          //Log.d("onTouchMove", String.format("dc=%d dr=%d", ((int) x - trackX)/w, ((int) y - trackY)/h));

          if(glass.validPosition(c.column(), c.row(), glass.activeFigure))
          {
            trackX = x;
            trackY = y;
            glass.activeFigurePosition = c;
            glass.setModified(true);
            setModified(true);
          }
        }
      }

      if((state == State.TRACKED && id != 0)
        || state == State.ROTATED)
      { //Second finger
        double w = x - trackX2;
        double h = y - trackY2;
        //Log.d("onTouchMove", String.format("2nd finger id=%d x=%.0f y=%.0f", id, x, y));
        if(Math.sqrt(w * w + h * h) >= 3 * glass.getShapeWidth())
        {
          Rect r = activeFigureRect();
          int quadrant;
          //1 2 3
          //4[0]5
          //6 7 8
          if(trackY2 < r.top)
          {
            if(trackX2 < r.left)
              quadrant = 1;
            else
            if(trackX2 > r.right)
              quadrant = 3;
            else
              quadrant = 2;
          }
          else
          if(trackY2 > r.bottom)
          {
            if(trackX2 < r.left)
              quadrant = 6;
            else
            if(trackX2 > r.right)
              quadrant = 8;
            else
              quadrant = 7;
          }
          else
          {
            if(trackX2 < r.left)
              quadrant = 4;
            else
            if(trackX2 > r.right)
              quadrant = 5;
            else
              quadrant = 0;
          }

          boolean left = false;
          switch (quadrant)
          {
            case 1:
              if(w <= 0 && h >= 0)
                left = true;
              break;
            case 2:
              if(w < 0)
                left = true;
              break;
            case 3:
              if(w <= 0 && h <= 0)
                left = true;
              break;
            case 4:
              if(h > 0)
                left = true;
              break;
            case 5:
              if(h < 0)
                left = true;
              break;
            case 6:
              if(w >= 0 && h >= 0)
                left = true;
              break;
            case 7:
              if(w > 0)
                left = true;
              break;
            case 8:
              if(w >= 0 && h <= 0)
                left = true;
              break;
          }

          if(left)
            glass.rotateLeft();
          else
            glass.rotateRight();
          trackX2 = x;
          trackY2 = y;
        }
      }
    }
    /*============================================================*/
    void toglePause()
    {
      demoMode = false;
      switch(state)
      {
        case PAUSED:
          state = State.WORKED;
          break;
        case WORKED:
        case TRACKED:
        case ROTATED:
          state = State.PAUSED;
          Toast.makeText(context, context.getString(R.string.paused), Toast.LENGTH_SHORT).show();
          break;
        case FINISHED:
          break;
      }

    }
    /*============================================================*/
    void moveLeft()
    {
      demoMode = false;
      switch(state)
      {
        case PAUSED:
          state = State.WORKED;
          break;
        case WORKED:
          glass.moveLeft();
          setModified(glass.isModified());
          break;
        case TRACKED:
          break;
        case ROTATED:
          break;
        case FINISHED:
          break;
      }
    }
    /*============================================================*/
    void moveRight()
    {
      demoMode = false;
      switch(state)
      {
        case PAUSED:
          state = State.WORKED;
          break;
        case WORKED:
          glass.moveRight();
          setModified(glass.isModified());
          break;
        case TRACKED:
          break;
        case ROTATED:
          break;
        case FINISHED:
          break;
      }
    }
    /*============================================================*/
    void moveDown()
    {
      demoMode = false;
      switch(state)
      {
        case PAUSED:
          state = State.WORKED;
          break;
        case WORKED:
          glass.moveBottom();
          setModified(glass.isModified());
          break;
        case TRACKED:
          break;
        case ROTATED:
          break;
        case FINISHED:
          break;
      }
    }
    /*============================================================*/
    void rotate()
    {
      demoMode = false;
      switch(state)
      {
        case PAUSED:
          state = State.WORKED;
          break;
        case WORKED:
          glass.rotate();
          setModified(glass.isModified());
          break;
        case TRACKED:
          break;
        case FINISHED:
          break;
      }
    }
    /*============================================================*/
    void onNewGame()
    {
      mkUndoPoint();
      onSettingsChanged();
      state = State.PAUSED;
      demoMode = false;
      //glass.onNewGame();
      //nextFigure = null;
      //setModified(true);
    }
    /*============================================================*/
    void onNewGame(long randomSeed)
    {
      onNewGame();
      random.setSeed(randomSeed);
    }
    /*============================================================*/
    /**
     * Save controller state to file
     */
    void save()
    {
      String filename = String.format("%s.bin", sectionName);
      try {

        DataOutputStream dos = new DataOutputStream(
          context.openFileOutput(filename, Context.MODE_PRIVATE)
        );

        save(dos);

        //Save undo buffer
        int us = undo.size();
        dos.writeInt(us);
        for(int i = 0; i < us; i++) {
          byte[] b = undo.elementAt(i);
          dos.writeInt(b.length);
          dos.write(b);
        }


        dos.flush();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    /*============================================================*/
    /**
     * Load controller state from file
     */
    void load()
    {
      String filename = String.format("%s.bin", sectionName);
      try {

        DataInputStream dis = new DataInputStream(
          context.openFileInput(filename)
        );

        load(dis);

        //Load undo buffer
        int us = dis.readInt();
        for(int i = 0; i < us; i++) {
          int length = dis.readInt();
          byte [] b = new byte [length];
          if(dis.read(b) < 0) {
            throw new IOException("Error read undo");
          }
          undo.push(b);
        }
      }
      catch (IOException e)  {
        e.printStackTrace();
      }
    }
    /*============================================================*/
    private final int STAR = 0xdeadbeef;
    private final int STREAM_VERSION = 3;

    /**
     * Save controller state to stream
     * @param dos - stream to save
     * @throws IOException
     */
    void save(DataOutputStream dos) throws IOException
    {
      dos.writeInt(STAR);
      dos.writeInt(STREAM_VERSION);
      glass.save(dos);
      if(glass.activeFigure != null)
      {
        dos.writeByte(1);
        glass.activeFigure.save(dos);
        glass.activeFigurePosition.save(dos);
      }
      else
      {
        dos.writeByte(0);
      }

      if(nextFigure != null)
      {
        dos.writeByte(1);
        nextFigure.save(dos);
      }
      else
      {
        dos.writeByte(0);
      }
      //Save radnom generator state
      ObjectOutputStream oos = new ObjectOutputStream(dos);
      oos.writeObject(random);
    }
    /*============================================================*/

    /**
     * Load controller state from stream
     * @param dis - stream for loat
     * @throws IOException
     */
    void load(DataInputStream dis)  throws IOException
    {
      if(dis.readInt() != STAR)
      {
        Log.e("GameController.load()", "Invalid stream keyword");
        return;
      }
      if(dis.readInt() != STREAM_VERSION)
      {
        Log.e("GameController.load()", "Invalid stream version");
        return;
      }

      glass.load(dis);
      if(dis.readByte() != 0)
      {
        glass.activeFigure = onNewFigure();
        glass.activeFigure.load(dis);
        glass.activeFigurePosition.load(dis);
      }
      if(dis.readByte() != 0)
      {
        nextFigure = onNewFigure();
        nextFigure.load(dis);
      }

      //Restore radnom generator state
      ObjectInputStream ois = new ObjectInputStream(dis);
      try {
        random = (Random) ois.readObject();
      }
      catch(ClassNotFoundException e)  {
        Log.e("load()", e.getMessage());
      }
    }
    /*============================================================*/
    /**
     * Save current state to undo stack
     * */
    void mkUndoPoint() {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos;
      try {
        dos = new DataOutputStream(bos);
        save(dos);
        dos.flush();
        undo.push(bos.toByteArray());
      }

      catch (IOException e) {
        Log.e("mkUndoPoint()", e.getMessage());
      }


      while (undo.size() > UNDO_SIZE)
      {
        undo.remove(0);
      }
    }
    /*============================================================*/
    /**
     * Restore controller state from undo stack.
     */
    void onUndo() {
      if (!undo.isEmpty()) {
        ByteArrayInputStream bis = new ByteArrayInputStream(undo.pop());
        DataInputStream dis;
        try {
          dis = new DataInputStream(bis);
          load(dis);
        } catch (IOException e) {
          Log.e("mkUndoPoint()", e.getMessage());
        }

        state = State.PAUSED;
        setModified(true);
      }
    }
    /*============================================================*/
    void onRobotAction()
    {
      demoMode = false;
      if(state == State.PAUSED)
      {
        state = State.WORKED;
        return;
      }

      if(state != State.WORKED)
        return;

      GlassAction action = glass.calcBestWay();
      if(action != null)
      {
        while(action.rotate > 0) {
          glass.rotate();
          action.rotate--;
        }

        while ((action.move > 0))
        {
          glass.moveRight();
          action.move -= 1;
        }

        while ((action.move < 0))
        {
          glass.moveLeft();
          action.move += 1;
        }

        glass.moveBottom();
        setModified(glass.isModified());
      }
    }
    /*============================================================*/
    void onDemoMode()
    {
      if(demoMode) {
        if (state == State.WORKED)
          state = State.PAUSED;
        demoMode = false;
      }
      else {
        if (state == State.PAUSED)
          state = State.WORKED;
        demoMode = true;
        if(glass.activeFigure != null)
        {
          demoAction = glass.calcBestWay();
        }
      }
      setModified(true);
    }
  }
  /*-----------------------------------------------------------------------------------------------*/

}

