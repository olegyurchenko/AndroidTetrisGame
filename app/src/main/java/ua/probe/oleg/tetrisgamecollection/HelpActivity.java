package ua.probe.oleg.tetrisgamecollection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TabHost;

public class HelpActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
    // инициализация
    tabHost.setup();

    TabHost.TabSpec tabSpec;

    tabSpec = tabHost.newTabSpec("tag1");
    tabSpec.setIndicator(getString(R.string.about));
    tabSpec.setContent(R.id.text_abouut);
    tabHost.addTab(tabSpec);

    tabSpec = tabHost.newTabSpec("tag2");
    tabSpec.setIndicator(getString(R.string.action_tetris));
    tabSpec.setContent(R.id.text_tetris);
    tabHost.addTab(tabSpec);

    tabSpec = tabHost.newTabSpec("tag3");
    tabSpec.setIndicator(getString(R.string.action_columnus));
    tabSpec.setContent(R.id.text_columnus);
    tabHost.addTab(tabSpec);

    tabSpec = tabHost.newTabSpec("tag4");
    tabSpec.setIndicator(getString(R.string.action_color_balls));
    tabSpec.setContent(R.id.text_color_balls);
    tabHost.addTab(tabSpec);

    // вторая вкладка будет выбрана по умолчанию
    tabHost.setCurrentTabByTag("tag1");
  }
}
