package ua.probe.oleg.tetrisgamecollection;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Locale;

public class HelpAboutActivity extends Activity {

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help_about_layout);
    TextView aboutText = (TextView) findViewById(R.id.text_abouut);
    //int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    String versionText = String.format(Locale.getDefault(), getString(R.string.version_format),
      versionName,
      //versionCode,
      getAppTimeStamp());
    aboutText.setText(versionText);
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
