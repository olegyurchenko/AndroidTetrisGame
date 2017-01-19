package ua.probe.oleg.tetrisgamecollection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class TetrisBaseActivity extends Activity
  implements View.OnTouchListener, View.OnClickListener {
  protected Button btn;
  protected TetrisBase.Controller gameController;
  protected View drawView = null;

  Timer myTimer = new Timer(); // Создаем таймер
  Handler uiHandler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tetris_base);

    //Buttons
    btn = (Button) findViewById(R.id.btnLeft1);
    btn.setOnClickListener(this);
    btn = (Button) findViewById(R.id.btnLeft2);
    btn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btnRight1);
    btn.setOnClickListener(this);
    btn = (Button) findViewById(R.id.btnRight2);
    btn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btnDown1);
    btn.setOnClickListener(this);
    btn = (Button) findViewById(R.id.btnDown2);
    btn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btnRotate1);
    btn.setOnClickListener(this);
    btn = (Button) findViewById(R.id.btnRotate2);
    btn.setOnClickListener(this);


    drawView = new DrawView(this);
    gameController = onGameControllerCreate();


    //gameController.setRect(new Rect(100, 100, drawView.getWidth(), drawView.getHeight()));

    LinearLayout layout = (LinearLayout)findViewById(R.id.draw_layout);
    layout.addView(drawView);

    drawView.setOnTouchListener(this);


    myTimer.schedule(new TimerTask() { // Определяем задачу
      @Override
      public void run()
      {
        gameController.onQuant();
        uiHandler.post(new Runnable() {
          @Override
          public void run()
          {
            if(gameController.isModified())
              drawView.invalidate();
          }
        });
      }
    }, 100, 10); // интервал - 100 миллисекунд, 0 миллисекунд до первого запуска.

  }


  protected TetrisBase.Controller onGameControllerCreate()
  {
    return new TetrisBase.Controller();
  }



  @Override
  public boolean onTouch(View v, MotionEvent event)
  {
    float x = event.getX();
    float y = event.getY();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: // нажатие
        gameController.onTouchDown(x, y);
        break;
      case MotionEvent.ACTION_MOVE: // движение
        gameController.onTouchMove(x, y);
        break;
      case MotionEvent.ACTION_UP: // отпускание
      case MotionEvent.ACTION_CANCEL:
        gameController.onTouchUp(x, y);
        break;
    }
    return true;
  }

  @Override
  public void onClick(View v)
  {
    switch(v.getId())
    {
      case R.id.btnLeft1:
      case R.id.btnLeft2:
        gameController.moveLeft();
        break;
      case R.id.btnRight1:
      case R.id.btnRight2:
        gameController.moveRight();
        break;
      case R.id.btnDown1:
      case R.id.btnDown2:
        gameController.moveDown();
        break;
      case R.id.btnRotate1:
      case R.id.btnRotate2:
        gameController.rotate();
        break;
    }
  }

  class DrawView extends View {

    public DrawView(Context context) {
      super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      super.onSizeChanged(w, h, oldw, oldh);
      gameController.setSize(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
      gameController.onDraw(canvas);
    }

  }
}
