package ua.probe.oleg.tetrisgamecollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    Button btn;
    ImageButton iBtn;

    btn = (Button) findViewById(R.id.btn_tetris);
    btn.setOnClickListener(this);

    iBtn = (ImageButton) findViewById(R.id.btn_tetris_settings);
    iBtn.setOnClickListener(this);

    iBtn = (ImageButton) findViewById(R.id.btn_tetris_help);
    iBtn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btn_columnus);
    btn.setOnClickListener(this);

    iBtn = (ImageButton) findViewById(R.id.btn_columnus_settings);
    iBtn.setOnClickListener(this);

    iBtn = (ImageButton) findViewById(R.id.btn_columnus_help);
    iBtn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btn_color_balls);
    btn.setOnClickListener(this);

    iBtn = (ImageButton) findViewById(R.id.btn_color_balls_settings);
    iBtn.setOnClickListener(this);

    iBtn = (ImageButton) findViewById(R.id.btn_color_balls_help);
    iBtn.setOnClickListener(this);

    btn = (Button) findViewById(R.id.btn_help);
    btn.setOnClickListener(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_tetris:
        startTetris();
        return true;
      case R.id.action_columnus:
        startColumnus();
        return true;
      case R.id.action_color_balls:
        startColorBalls();
        return true;
      case R.id.action_help:
        help("");
        return true;

    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_tetris:
        startTetris();
        break;
      case R.id.btn_columnus:
        startColumnus();
        break;
      case R.id.btn_color_balls:
        startColorBalls();
        break;

      case R.id.btn_tetris_settings:
        tetrisSettings();
        break;
      case R.id.btn_columnus_settings:
        columnusSettings();
        break;
      case R.id.btn_color_balls_settings:
        colorBallsSettings();
        break;

      case R.id.btn_tetris_help:
        help(getString(R.string.tetris_help_file));
        break;
      case R.id.btn_columnus_help:
        help(getString(R.string.columnus_help_file));
        break;
      case R.id.btn_color_balls_help:
        help(getString(R.string.color_balls_help_file));
        break;

      case R.id.btn_help:
        help("");
        break;
    }
  }


  protected void startTetris() {
    Toast.makeText(getApplicationContext(), "You selected " + getString(R.string.action_tetris), Toast.LENGTH_SHORT).show();
    // Создаем объект Intent для вызова новой Activity
    Intent intent = new Intent(this, TetrisActivity.class);
    // запуск activity
    startActivity(intent);
  }

  protected void startColumnus() {
    Toast.makeText(getApplicationContext(), "You selected " + getString(R.string.action_columnus), Toast.LENGTH_SHORT).show();
    // Создаем объект Intent для вызова новой Activity
    Intent intent = new Intent(this, ColumnusActivity.class);
    // запуск activity
    startActivity(intent);
  }

  protected void startColorBalls() {
    Toast.makeText(getApplicationContext(), "You selected " + getString(R.string.action_color_balls), Toast.LENGTH_SHORT).show();
    // Создаем объект Intent для вызова новой Activity
    Intent intent = new Intent(this, ColorBallsActivity.class);
    // запуск activity
    startActivity(intent);

  }

  private void statrSettings(String sectionName, Class<?> cls)
  {
    Intent intent = new Intent(this, cls);
    Bundle b = new Bundle();
    b.putString("sectionName", sectionName);
    intent.putExtras(b);
    startActivity(intent);
  }

  protected void tetrisSettings()
  {
    statrSettings(getString(R.string.tetris_section_name), TetrisSettingsActivity.class);
  }

  protected void columnusSettings()
  {

    statrSettings(getString(R.string.columnus_section_name), ColumnusSettingsActivity.class);
  }

  protected void colorBallsSettings()
  {
    statrSettings(getString(R.string.color_balls_section_name), ColorBallsSettingsActivity.class);
  }

  protected void help(String content)
  {
    Intent intent = new Intent(this, HelpActivity.class);
    Bundle b = new Bundle();
    b.putString("content", content);
    intent.putExtras(b);
    startActivity(intent);
  }
}
