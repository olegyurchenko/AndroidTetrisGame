package ua.probe.oleg.tetrisgamecollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
  Button btnTetris, btnColumnus, btnColorBall;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    btnTetris = (Button) findViewById(R.id.btnTetris);
    btnTetris.setOnClickListener(this);

    btnColumnus = (Button) findViewById(R.id.btnColumnus);
    btnColumnus.setOnClickListener(this);

    btnColorBall = (Button) findViewById(R.id.btnColorBall);
    btnColorBall.setOnClickListener(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId())
    {
      case R.id.action_tetris:
        startTetris();
        return true;
      case R.id.action_columnus:
        startColumnus();
        return true;
      case R.id.action_color_balls:
        startColorBalls();
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
  public void onClick(View v)
  {
    switch(v.getId())
    {
      case R.id.btnTetris:
        startTetris();
        break;
      case R.id.btnColumnus:
        startColumnus();
        break;
      case R.id.btnColorBall:
        startColorBalls();
        break;
    }
  }


  protected void startTetris()
  {
    Toast.makeText(getApplicationContext(), "You selected " + getString(R.string.action_tetris), Toast.LENGTH_SHORT).show();
    // Создаем объект Intent для вызова новой Activity
    Intent intent = new Intent(this, TetrisActivity.class);
    // запуск activity
    startActivity(intent);
  }

  protected void startColumnus()
  {
    Toast.makeText(getApplicationContext(), "You selected " + getString(R.string.action_columnus), Toast.LENGTH_SHORT).show();
    // Создаем объект Intent для вызова новой Activity
    Intent intent = new Intent(this, ColumnusActivity.class);
    // запуск activity
    startActivity(intent);
  }

  protected void startColorBalls()
  {
    Toast.makeText(getApplicationContext(), "You selected " + getString(R.string.action_color_balls), Toast.LENGTH_SHORT).show();
    // Создаем объект Intent для вызова новой Activity
    Intent intent = new Intent(this, ColorBallsActivity.class);
    // запуск activity
    startActivity(intent);

  }
}
