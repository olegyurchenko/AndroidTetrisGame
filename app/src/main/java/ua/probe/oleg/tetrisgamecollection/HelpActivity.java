package ua.probe.oleg.tetrisgamecollection;

import android.content.pm.ApplicationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Locale;

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
    // создаем View из layout-файла
    tabSpec.setContent(R.id.text_abouut);
    tabHost.addTab(tabSpec);

    tabSpec = tabHost.newTabSpec("tag2");
    tabSpec.setIndicator(getString(R.string.action_tetris));
    tabSpec.setContent(R.id.view_tetris);
    tabHost.addTab(tabSpec);

    tabSpec = tabHost.newTabSpec("tag3");
    tabSpec.setIndicator(getString(R.string.action_columnus));
    tabSpec.setContent(R.id.view_columnus);
    tabHost.addTab(tabSpec);

    tabSpec = tabHost.newTabSpec("tag4");
    tabSpec.setIndicator(getString(R.string.action_color_balls));
    tabSpec.setContent(R.id.view_color_balls);
    tabHost.addTab(tabSpec);

    // вторая вкладка будет выбрана по умолчанию
    tabHost.setCurrentTabByTag("tag1");



    TextView aboutText = (TextView) findViewById(R.id.text_abouut);
    //int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    String versionText = String.format(Locale.getDefault(), getString(R.string.version_format),
      versionName,
      //versionCode,
      getAppTimeStamp());
    aboutText.setText(versionText);


    WebView wv = (WebView)findViewById(R.id.view_tetris);
    wv.loadUrl(getString(R.string.tetris_html_help_file));

    wv = (WebView)findViewById(R.id.view_columnus);
    wv.loadUrl(getString(R.string.columnus_html_help_file));

    wv = (WebView)findViewById(R.id.view_color_balls);
    wv.loadUrl(getString(R.string.color_balls_html_help_file));
  }

  /*============================================================*/
  public String getAppTimeStamp()
  {
    String timeStamp = "";

    try {
      ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
      String appFile = appInfo.sourceDir;
      long time = new File(appFile).lastModified();

      DateFormat formatter = DateFormat.getDateInstance();
      timeStamp = formatter.format(time);

    }
    catch (Exception e) {
      //Ignore
    }

    return timeStamp;

  }
    /*============================================================*/
}
