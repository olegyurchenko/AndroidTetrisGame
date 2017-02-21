package ua.probe.oleg.tetrisgamecollection;

import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TetrisBaseActivity extends AppCompatActivity
  implements View.OnTouchListener, View.OnClickListener, View.OnLayoutChangeListener {
  protected TetrisBase.Controller gameController;
  protected View drawView = null;
  protected String sectionName = "TetrisBase";
  private EditText seedEdit;

  Timer myTimer;
  Handler uiHandler = new Handler();
  ImageButton leftBtn1, leftBtn2;
  ImageButton rightBtn1, rightBtn2;
  ImageButton rotateBtn1, rotateBtn2;
  ImageButton downBtn1, downBtn2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //Disable SLEEP !!!!!
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.activity_tetris_base);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ImageButton btn;

    //Buttons
    leftBtn1 = btn = (ImageButton) findViewById(R.id.btnLeft1);
    if(btn != null)
      btn.setOnClickListener(this);
    leftBtn2 = btn = (ImageButton) findViewById(R.id.btnLeft2);
    if(btn != null)
      btn.setOnClickListener(this);

    rightBtn1 = btn = (ImageButton) findViewById(R.id.btnRight1);
    if(btn != null)
      btn.setOnClickListener(this);
    rightBtn2 = btn = (ImageButton) findViewById(R.id.btnRight2);
    if(btn != null)
      btn.setOnClickListener(this);

    downBtn1 = btn = (ImageButton) findViewById(R.id.btnDown1);
    if(btn != null)
      btn.setOnClickListener(this);
    downBtn2 = btn = (ImageButton) findViewById(R.id.btnDown2);
    if(btn != null)
      btn.setOnClickListener(this);

    rotateBtn1 = btn = (ImageButton) findViewById(R.id.btnRotate1);
    if(btn != null)
      btn.setOnClickListener(this);
    rotateBtn2 = btn = (ImageButton) findViewById(R.id.btnRotate2);
    if(btn != null)
      btn.setOnClickListener(this);

    btn = (ImageButton) findViewById(R.id.btnRobot);
    if(btn != null)
      btn.setOnClickListener(this);

    btn = (ImageButton) findViewById(R.id.btnPause);
    if(btn != null)
      btn.setOnClickListener(this);

//    btn = (Button) findViewById(R.id.btnSettings);
//    btn.setOnClickListener(this);

    btn = (ImageButton) findViewById(R.id.btnCreate);
    if(btn != null)
      btn.setOnClickListener(this);

    btn = (ImageButton) findViewById(R.id.btnMagic);
    if(btn != null)
      btn.setOnClickListener(this);

    btn = (ImageButton) findViewById(R.id.btnUndo);
    if(btn != null)
      btn.setOnClickListener(this);

    //gameController = (TetrisBase.Controller) getLastNonConfigurationInstance();
    gameController = (TetrisBase.Controller) getLastCustomNonConfigurationInstance();

    if(gameController == null) {

      gameController = onGameControllerCreate();
    }

    drawView = new DrawView(this);

    LinearLayout layout = (LinearLayout) findViewById(R.id.layout_main);
    if (layout != null) {
      layout.addOnLayoutChangeListener(this);
    }

    /* Insert main view*/
    layout = (LinearLayout) findViewById(R.id.draw_layout);
    if (layout != null) {
      layout.addView(drawView);
    }

    drawView.setOnTouchListener(this);
  }

  @Override
  public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                      int oldBottom) {
    // its possible that the layout is not complete in which case
    // we will get all zero values for the positions, so ignore the event
    if (left == 0 && top == 0 && right == 0 && bottom == 0) {
      return;
    }



    //Log.d("TetrisBaseActivity", String.format("onLayoutChange(%d,%d,%d,%d)", left, top, right, bottom));
    int height = bottom - top;
    int width = right - left;

    int oldHeight = oldBottom - oldTop;
    int oldWidth = oldRight - oldLeft;

    if(oldHeight == height && oldWidth == width)
      return;


    int btnHeight = height / 6;

    if(width > height) //Landscape
      btnHeight = height / 5;


    ImageButton[] buttons = new ImageButton[] {
      leftBtn1, leftBtn2,
      rightBtn1, rightBtn2,
      rotateBtn1, rotateBtn2,
      downBtn1, downBtn2
    };

    for(ImageButton btn:buttons) {
      if(btn != null)
        btn.setMinimumHeight(btnHeight);
    }

  }

  @Override
  //public Object onRetainNonConfigurationInstance() {
  public Object onRetainCustomNonConfigurationInstance() {
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
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.item_pause:
        gameController.toglePause();
        return true;
      case R.id.item_undo:
        gameController.onUndo();
        return true;
      case R.id.item_new_game:
        gameController.onNewGame();
        return true;
      case R.id.item_magic:
        if(gameController.state != TetrisBase.Controller.State.PAUSED)
          gameController.toglePause();
        showMagicDialog();
        return true;
      case R.id.item_settings:
        onSettings();
        return true;
      case R.id.item_demo_mode:
        gameController.onDemoMode();
        return true;
      case R.id.item_help:
        help();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.tetris_base, menu);
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
//      case R.id.btnSettings:
//        onSettings();
//        break;
      case R.id.btnCreate:
        gameController.onNewGame();
        break;
      case R.id.btnMagic:
        //gameController.onNewGame(0L);
        if(gameController.state != TetrisBase.Controller.State.PAUSED)
          gameController.toglePause();
        showMagicDialog();
        break;
      case R.id.btnUndo:
        gameController.onUndo();
        break;
      case R.id.btnRobot:
        gameController.onRobotAction();
        break;
    }
  }

  protected Class<?> settinsActivityClass()
  {
    return SettingsActivity.class;
  }

  public void onSettings() {
    Intent intent = new Intent(this, settinsActivityClass());
    Bundle b = new Bundle();
    b.putString("sectionName", sectionName);
    intent.putExtras(b);
    startActivityForResult(intent, 1);
  }

  void showMagicDialog()
  {
    Dialog dialog = createMagicDialog();

    Window window = dialog.getWindow();

    if(window != null)
      window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    dialog.show();
    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    //imm.showSoftInput(seedEdit, InputMethodManager.SHOW_IMPLICIT);
  }

  protected void help() {
    help(getString(R.string.html_help_file));
  }

  protected void help(String content)
  {
    Intent intent = new Intent(this, HelpActivity.class);
    Bundle b = new Bundle();
    b.putString("content", content);
    intent.putExtras(b);
    startActivity(intent);
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

  @SuppressLint("InflateParams")
  public Dialog createMagicDialog()
  {
    AlertDialog.Builder adb = new AlertDialog.Builder(this);
    adb.setTitle(R.string.new_game);
    // создаем magicLayout из dialog.xml
    LinearLayout magicLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.new_game_dialog, null);
    // устанавливаем ее, как содержимое тела диалога
    adb.setView(magicLayout);
    // находим seedEdit
    seedEdit = (EditText) magicLayout.findViewById(R.id.edit_seed);
    seedEdit.setText(String.format(Locale.getDefault(), "%d", gameController.settings.randomSeed));

    adb.setPositiveButton(R.string.ok, magicDlgListener);
    adb.setNegativeButton(R.string.cancel, magicDlgListener);

    return adb.create();
  }

  DialogInterface.OnClickListener magicDlgListener = new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
      switch (which) {
        // положительная кнопка
        case Dialog.BUTTON_POSITIVE:
          long randomSeed;
          try {
            randomSeed = Long.parseLong(seedEdit.getText().toString(), 10);
          }
          catch(NumberFormatException e) {
            dialog.cancel();
            break;
          }
          gameController.settings.randomSeed = randomSeed;
          gameController.settings.save(getBaseContext(), sectionName);
          gameController.onNewGame(randomSeed);
          break;
        // негативная кнопка
        case Dialog.BUTTON_NEGATIVE:
        case Dialog.BUTTON_NEUTRAL:
          break;
      }
    }
  };
}
