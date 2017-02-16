package ua.probe.oleg.tetrisgamecollection;

import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    tabSpec.setContent(R.id.view_about);
    tabHost.addTab(tabSpec);

    tabSpec = tabHost.newTabSpec("tag2");
    tabSpec.setIndicator(getString(R.string.content));
    tabSpec.setContent(R.id.view_content);
    tabHost.addTab(tabSpec);


    // вторая вкладка будет выбрана по умолчанию
    tabHost.setCurrentTabByTag("tag1");


    WebView wv;
    wv = (WebView)findViewById(R.id.view_about);

    String versionName = BuildConfig.VERSION_NAME;

    String versionText = String.format(Locale.getDefault(), getString(R.string.version_format),
      versionName,
      //versionCode,
      getAppTimeStamp());

    String aboutText = String.format(Locale.getDefault(),"<p>%s</p><p>%s</p>",
      readAboutText(),
      versionText);

    wv.loadDataWithBaseURL("file:///android_asset/", aboutText, "text/html; charset=utf-8", "utf-8", null);

    wv = (WebView)findViewById(R.id.view_content);
    wv.loadUrl(getString(R.string.html_help_file));
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
  public String readAboutText() {
    StringBuilder result = new StringBuilder();
    try {
      AssetManager am = getAssets();
      InputStream is = am.open(getString(R.string.about_file));
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));

      String line;
      boolean flag = false;
      while ((line = reader.readLine()) != null) {
        result.append(flag ? "\n" : "").append(line);
        flag = true;
      }
    }

    catch(IOException e) {
      Log.e("HelpAbout", e.getLocalizedMessage());
    }

    return result.toString();
  }
}
