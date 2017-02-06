package ua.probe.oleg.tetrisgamecollection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;



import java.util.Timer;
import java.util.TimerTask;

public class TetrisBaseActivity extends Activity
  implements View.OnTouchListener, View.OnClickListener/*, SeekBar.OnSeekBarChangeListener*/ {
  protected TetrisBase.Controller gameController;
  protected View drawView = null;
  protected String sectionName = "TetrisBase";

  Timer myTimer;
  Handler uiHandler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tetris_base);

    Button btn;

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

    btn = (Button) findViewById(R.id.btnPause);
    btn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btnSettings);
    btn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btnCreate);
    btn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btnMagic);
    btn.setOnClickListener(this);

    gameController = (TetrisBase.Controller) getLastNonConfigurationInstance();

    if(gameController == null) {

      gameController = onGameControllerCreate();
    }
/*
    SeekBar seekBar;

    seekBar = (SeekBar)findViewById(R.id.sppedSeekbar);
    seekBar.setProgress(gameController.getSpeedRate());
    seekBar.setOnSeekBarChangeListener(this);

    seekBar = (SeekBar)findViewById(R.id.complexSeekbar);
    seekBar.setProgress(gameController.getComplexRate());
    seekBar.setOnSeekBarChangeListener(this);
*/
    drawView = new DrawView(this);


    //gameController.setRect(new Rect(100, 100, drawView.getWidth(), drawView.getHeight()));

    LinearLayout layout = (LinearLayout) findViewById(R.id.draw_layout);
    layout.addView(drawView);

    drawView.setOnTouchListener(this);
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    return gameController;
  }

  protected TetrisBase.Controller onGameControllerCreate()
  {
    return new TetrisBase.Controller(this, sectionName);
  }
  @Override
  protected void onResume()
  {
    Log.d("TetrisBaseActivity", "onResume()");

    super.onResume();
    gameController.onResume();

    myTimer = new Timer(); // Создаем таймер
    myTimer.schedule(new TimerTask() { // Определяем задачу
      @Override
      public void run() {
        gameController.onTimer();
        uiHandler.post(new Runnable() {
          @Override
          public void run() {
            if (gameController.isModified())
              drawView.invalidate();
          }
        });
      }
    }, 100, 10); // интервал - 100 миллисекунд, 0 миллисекунд до первого запуска.

    Log.d("TetrisBaseActivity", "onResume() end");
  }
  @Override
  protected void onPause()
  {
    Log.d("TetrisBaseActivity", "onPause()");
    super.onPause();
    gameController.onPause();
    myTimer.cancel();
    Log.d("TetrisBaseActivity", "onPause() end");
  }
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    int actionMask = event.getActionMasked();
    int pointerIndex = event.getActionIndex();
    int pointerId = event.getPointerId(pointerIndex);
    int pointerCount = event.getPointerCount();
    float x = event.getX(pointerIndex);
    float y = event.getY(pointerIndex);

    switch (actionMask) {
      case MotionEvent.ACTION_DOWN: // нажатие
      case MotionEvent.ACTION_POINTER_DOWN:
        gameController.onTouchDown(pointerId, x, y);
        break;
      case MotionEvent.ACTION_MOVE: // движение
        for(int i = 0; i < pointerCount; i++)
        {
          x = event.getX(i);
          y = event.getY(i);
          pointerId = event.getPointerId(i);
          gameController.onTouchMove(pointerId, x, y);
        }
        break;
      case MotionEvent.ACTION_UP: // отпускание
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_POINTER_UP:
        gameController.onTouchUp(pointerId, x, y);
        break;
    }
    return true;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
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
      case R.id.btnPause:
        gameController.toglePause();
        break;
      case R.id.btnSettings:
        onSettings();
        break;
      case R.id.btnCreate:
        gameController.onNewGame();
        break;
      case R.id.btnMagic:
        //TODO:
        break;
    }
  }

  public void onSettings() {
    Intent intent = new Intent(this, SettingsActivity.class);
    Bundle b = new Bundle();
    b.putString("sectionName", sectionName);
    intent.putExtras(b);
    startActivityForResult(intent, 1);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch(requestCode) {
      case 1:
        if(Activity.RESULT_OK == resultCode)
        {
          gameController.onSettingsChanged();
        }
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
