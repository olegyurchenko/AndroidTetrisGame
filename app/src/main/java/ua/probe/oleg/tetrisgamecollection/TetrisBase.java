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
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

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
  static class Shape
  {
    int fillColor, borderColor;
//    protected int width = 0, height = 0;
    Paint paint = new Paint();
    Rect rect = new Rect();

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

    Shape(Shape other)
    {
      fillColor = other.fillColor;
      borderColor = other.borderColor;
    }

    Shape()
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

      // перенастраивам кисть на контуры
      paint.setColor(borderColor);
      paint.setStyle(Paint.Style.STROKE);
      canvas.drawRect(rect, paint);
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
  static class Figure
  {
    SparseArray<Shape> shapeMap;
    int columnCount = 0, rowCount = 0;

    Figure()
    {
      shapeMap = new SparseArray<>();
    }

    Figure(Figure other)
    {
      shapeMap = other.shapeMap.clone();
      columnCount = other.columnCount;
      rowCount = other.rowCount;
    }

    int index(int column, int row)
    {
      return column + 1000000 * row;
    }

    Shape get(int column, int row)
    {
      return shapeMap.get(index(column, row));
    }

    void put(int column, int row, Shape s)
    {
      shapeMap.put(index(column, row), s);
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

    Shape onNewShape()
    {
      return new Shape();
    }

    void save(DataOutputStream dos) throws IOException
    {
      dos.writeInt(columnCount);
      dos.writeInt(rowCount);

      int size = shapeMap.size();
      dos.writeInt(size);
      for(int i = 0; i < size; i++)
      {
        int key = shapeMap.keyAt(i);
        dos.writeInt(key);
        Shape s = shapeMap.valueAt(i);
        s.save(dos);

      }

    }
    void load(DataInputStream dis)  throws IOException
    {
      shapeMap.clear();
      columnCount = dis.readInt();
      rowCount = dis.readInt();
      int size = dis.readInt();
      for(int i = 0; i < size; i++)
      {
        int key = dis.readInt();
        Shape s = onNewShape();
        s.load(dis);
        shapeMap.put(key, s);
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
    private Path guidePath;

    private long score;
    private long scoreScale = 100;

    private boolean drawGuideLines = true;

    private SparseArray<Shape> shapeMap;

    Glass(int columns, int rows)
    {
      columnCount = columns;
      rowCount = rows;
      shapeMap = new SparseArray<>();
      paint = new Paint();

      guidePaint = new Paint();
      guidePaint.setColor(Color.BLACK);
      guidePaint.setStyle(Paint.Style.STROKE);
      guidePaint.setPathEffect(new DashPathEffect(new float[] {20 ,30}, 0));

      guidePath = new Path();

      score = 0;
    }

    public boolean isModified() {return modified;}
    protected void setModified(boolean m) {modified = m;}

    void setRect(Rect r)
    {
      rect = r;
    }

    Rect getRect()
    {
      return  rect;
    }

    long getScore() {return score;}
    void addRemovedShapes(int shapes) {
      score += shapes * 17 * scoreScale;
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

    Shape get(int column, int row)
    {
      if(column < 0
        || column >= columnCount
        || row < 0
        || row >= rowCount)
        return null;

      return shapeMap.get(index(column, row));
    }

    void put(int column, int row, Shape s)
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
    void onNewGame()
    {
      shapeMap.clear();
      activeFigure = null;
      activeFigurePosition = new Cell();
    }

    Shape onNewShape()
    {
      return new Shape();
    }

    void save(DataOutputStream dos) throws IOException
    {
      dos.writeInt(columnCount);
      dos.writeInt(rowCount);

      int size = shapeMap.size();
      dos.writeInt(size);
      for(int i = 0; i < size; i++)
      {
        int key = shapeMap.keyAt(i);
        dos.writeInt(key);
        Shape s = shapeMap.valueAt(i);
        s.save(dos);
      }
      dos.writeLong(score);
    }

    void load(DataInputStream dis)  throws IOException
    {
      shapeMap.clear();
      activeFigure = null;

      columnCount = dis.readInt();
      rowCount = dis.readInt();
      int size = dis.readInt();
      for(int i = 0; i < size; i++)
      {
        int key = dis.readInt();
        Shape s = onNewShape();
        s.load(dis);
        shapeMap.put(key, s);
      }
      score = dis.readLong();
    }
  }
  /*-----------------------------------------------------------------------------------------------*/
  static class Settings
  {
    final int MinColumns = 3, MaxColumns = 20;
    final int MinRows = 5, MaxRows = 30;

    int speedRate = 50;
    int complexRate = 50;
    int columnCount = 8, rowCount = 16;
    boolean showNextFigure = true;
    boolean showScore = true;
    boolean showGuideLines = true;
    boolean useAccelerometer = true;
    boolean useTouch = true;
    boolean useShake = true;

    private void check()
    {
      if(speedRate <= 0)
        speedRate = 1;
      if(speedRate > 100)
        speedRate = 100;
      if(complexRate <= 0)
        complexRate = 1;
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
      speedRate = preferences.getInt("speedRate", 50);
      complexRate = preferences.getInt("complexRate", 50);
      columnCount = preferences.getInt("columnCount", 8);
      rowCount = preferences.getInt("rowCount", 16);
      showNextFigure = preferences.getBoolean("showNextFigure", true);
      showScore = preferences.getBoolean("showScore", true);
      showGuideLines = preferences.getBoolean("showGuideLines", true);
      useAccelerometer = preferences.getBoolean("useAccelerometer", true);
      useTouch = preferences.getBoolean("useTouch", true);
      useShake = preferences.getBoolean("useShake", true);
      check();
    }


    void save(Context context, String sectionName)
    {
      SharedPreferences preferences = context.getSharedPreferences(sectionName, Context.MODE_PRIVATE);
      SharedPreferences.Editor ed = preferences.edit();

      check();
      ed.putInt("speedRate", speedRate);
      ed.putInt("complexRate", complexRate);
      ed.putInt("columnCount", columnCount);
      ed.putInt("rowCount", rowCount);
      ed.putBoolean("showNextFigure", showNextFigure);
      ed.putBoolean("showScore", showScore);
      ed.putBoolean("showGuideLines", showGuideLines);
      ed.putBoolean("useAccelerometer", useAccelerometer);
      ed.putBoolean("useTouch", useTouch);
      ed.putBoolean("useShake", useShake);

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
    //protected int complexRate = 50, speedRate = 50;
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
    int nextFigureX, nextFigureY;
    //boolean showScore = true;
    int ratingX, ratingY;
    Accelerometer accelerometer;
    Bitmap leftArrowBitmap, rightArrowBitmap, touchBitmap, rotateBitmap, moveBitmap, screenRotationBitmap;
    final double ROTATION_ANGLE = Math.PI / 6; //30 degree
    Random random;
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

      if(settings.useAccelerometer || settings.useShake)
        accelerometer = new Accelerometer(context);

      random = new Random();

      load();
    }
    /*============================================================*/
    private void setup()
    {
      interval = minInterval + ((maxInterval - minInterval) * (100 - settings.speedRate)) / 100;
      //Log.d("GameController", "interval=" + interval);
      glass.setScoreScale(settings.complexRate * settings.speedRate);
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
      figure.put(0, 0, new Shape(Color.GREEN, Color.BLACK));
      figure.put(0, 1, new Shape(Color.RED, Color.BLACK));
      figure.put(0, 2, new Shape(Color.BLUE, Color.BLACK));

      return figure;
    }
    /*============================================================*/
    boolean isModified() {return modified;}
    /*============================================================*/
    void setModified(boolean m) {modified = m;}
    /*============================================================*/
    //int getComplexRate() {return complexRate;}
    /*============================================================*/
    /*
    void setComplexRate(int r)     {
      if(r > 0 && r <= 100)
      {
        complexRate = r;
        glass.setScoreScale(complexRate * speedRate);
        setModified(true);
      }
    }
    */
    /*============================================================*/
    //int getSpeedRate() {return speedRate;}
    /*============================================================*/
    /*
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
    */
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
    int randomComplexColor()
    {
      int d =  (colors.length * settings.complexRate) / 100;
      int n = random.nextInt(d < 1 ? 1 : d);
      return colors[n];
    }
    /*============================================================*/
    void setSize(int w, int h)
    {
      Log.d("setSize", "w=" + w + " h=" + h);
      rect = new Rect(0, 0, w, h);
      geometryInit();
    }
    /*============================================================*/
    void geometryInit()
    {
      int x, y, w, h;
      int border = 20;

      if(rect.width() < rect.height()) //Verical
      {
        x = border;
        w = rect.width() - 2 * border;
        h = (w / glass.getColumnCount()) * glass.getRowCount();
        w = (h / glass.getRowCount()) * glass.getColumnCount();
        y = border + 3 * 20;

        nextFigureX = x ;
        nextFigureY = border;

        ratingX = x;
        ratingY = y + h;

        glass.setRect(
          new Rect(x, y, x + w, y + h) );

        if(y + h > rect.height())
        {
          h = rect.height() - y;
          w = (h / glass.getRowCount()) * glass.getColumnCount();
          h = (w / glass.getColumnCount()) * glass.getRowCount();

          glass.setRect(
            new Rect(x, y, x + w, y + h) );

          nextFigureX = x + w + border;
          nextFigureY = border;

          ratingX = x + w + border;
          ratingY = border + glass.getShapeWidth() * 3;
        }
      }
      else
      { //Horisontal
        x = y = border;
        h = rect.height() - 2 * border;
        w = (h / glass.getRowCount()) * glass.getColumnCount();
        h = (w / glass.getColumnCount()) * glass.getRowCount();
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
    void onDraw(Canvas canvas)
    {
      Figure figure = this.nextFigure; //For thread safe

      if(glass.getRect() == null)
      {
        if(rect == null)
        {
          rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
          geometryInit();
        }

        glass.setRect(rect);
      }

      Rect glassRect = glass.getRect();
      glass.onDraw(canvas);
      if(figure != null && settings.showNextFigure)
      {
        figure.onDraw(canvas, nextFigureX, nextFigureY, 20, 20);
        //nextFigure.onDraw(canvas, nextFigureX, nextFigureY, glass.getShapeWidth(), glass.getShapeHeight());
      }

      Bitmap leftBmp = null, rightBmp = null;
      if(state == State.TRACKED)
      {
        leftBmp = touchBitmap;
        rightBmp = moveBitmap;
      }
      else
      if(state == State.ROTATED)
      {
        leftBmp = touchBitmap;
        rightBmp = rotateBitmap;
      }
      else
      if(settings.useAccelerometer && accelerometer != null)
      {
        Accelerometer.Orientation o = accelerometer.getActualDeviceOrientation();
        if(Math.abs(o.y) >= ROTATION_ANGLE)
        {
          leftBmp = screenRotationBitmap;

          if (o.y < 0)
          { //Left
            rightBmp = leftArrowBitmap;
          } else
          { //Right
            rightBmp = rightArrowBitmap;
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

        int x = glassRect.left + (glassRect.width() - bounds.width()) / 2;
        int y = glassRect.top + (glassRect.height() - bounds.height())/ 2;


        bounds.set(glassRect.left, y - bounds.height() * 2, glassRect.right, y + bounds.height());

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(bounds, paint);

        paint.setColor(Color.BLACK);
        canvas.drawText(text, x, y, paint);
      }

      if(settings.showScore)
      {
        paint.setColor(Color.BLUE);
        paint.setTextSize(30);

        int y = ratingY + 30;
        canvas.drawText(context.getString(R.string.speed) + ": " + settings.speedRate + "%", ratingX, y, paint);
        y += 30;
        canvas.drawText(context.getString(R.string.complex) + ": " + settings.complexRate + "%", ratingX, y, paint);
        y += 30;
        canvas.drawText(context.getString(R.string.score) + ": " + glass.getScore(), ratingX, y, paint);
/*
        if(accelerometer != null)
        {
          y += 30;
          Accelerometer.Orientation o = accelerometer.getActualDeviceOrientation();
          canvas.drawText(String.format("x=%1$.01f y=%2$.01f z=%3$.01f", o.x, o.y, o.z), ratingX, y, paint);
        }
*/
      }
    }

    /*============================================================*/
    void onPause()
    {
      if(accelerometer != null)
        accelerometer.onPause();
      save();
      if(state != State.FINISHED)
        state = State.PAUSED;
    }
    /*============================================================*/
    void onResume()
    {
      if(accelerometer != null)
        accelerometer.onResume();
    }
    /*============================================================*/
    void onTimer()
    {
      if(System.currentTimeMillis() - lastTime >= interval)
      {
        lastTime = System.currentTimeMillis();
        nextInterval();
      }


      if(settings.useShake
        && accelerometer != null)
      {
        if(accelerometer.isModified())
          setModified(true);
        if(accelerometer.isShakeDetected())
        {
          if(state == State.PAUSED)
          {
            state = State.WORKED;
          }
          else
          if(state == State.WORKED)
          {
            //if(accelerometer.isShakeY())
              rotate();
            //if(accelerometer.isShakeX())
            //  moveDown();
          }
          Log.d("ShakeDetected", String.format("%s%s%s",
            accelerometer.isShakeX() ? "X " : "",
            accelerometer.isShakeY() ? "Y " : "",
            accelerometer.isShakeZ() ? "Z " : "")
            );
          accelerometer.clearShakeDetected();
        }
      }
      //Log.d("TIME TEST", "Current sec = " + seconds);
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
      int x = Math.round(fx);
      int y = Math.round(fy);

      //Log.d("onTouchDown", String.format("id=%d x=%.0f y=%.0f", id, x, y));
      if( settings.useTouch
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

          if (!glass.put(nextFigure)) {
            state = State.FINISHED;
            glass.setModified(true);
//          Log.d("Game", "Error add figure");
//            Toast.makeText(context, context.getString(R.string.finished), Toast.LENGTH_SHORT).show();
          } else {
//          Log.d("Game", "Add figure Ok");
            nextFigure = onNewFigure();
          }
        }
      }
      else
      {
        if(settings.useAccelerometer && accelerometer != null)
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
    void onNewGame()
    {
      state = State.PAUSED;
      glass.onNewGame();
      nextFigure = null;
      setModified(true);
    }
    /*============================================================*/
    void onNewGame(long randomSeed)
    {
      onNewGame();
      random.setSeed(randomSeed);
    }
    /*============================================================*/
    void save()
    {
      String filename = String.format("%s.bin", sectionName);
      try {

        DataOutputStream dos = new DataOutputStream(
          context.openFileOutput(filename, Context.MODE_PRIVATE)
        );

        save(dos);
      } catch (IOException e)
        {
          e.printStackTrace();
        }
    }

    void load()
    {
      String filename = String.format("%s.bin", sectionName);
      try {

        DataInputStream dis = new DataInputStream(
          context.openFileInput(filename)
        );

        load(dis);
      } catch (IOException e)

      {
        e.printStackTrace();
      }
    }

    void save(DataOutputStream dos) throws IOException
    {
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

    }

    void load(DataInputStream dis)  throws IOException
    {
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
    }
  }
  /*-----------------------------------------------------------------------------------------------*/

}

