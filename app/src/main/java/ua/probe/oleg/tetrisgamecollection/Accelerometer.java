package ua.probe.oleg.tetrisgamecollection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Class to measure device orientation
 */

public class Accelerometer {
  private Context context;
  private SensorManager sensorManager;
  private Sensor sensorAccel;
  private Sensor sensorMagnet;
  private float[] valuesAccel = new float[3];
  private float[] valuesMagnet = new float[3];
  private int rotation;
  /*============================================================*/
  public class Orientation
  {
    public float x = 0;
    public float y = 0;
    public float z = 0;
    Orientation(float data[])
    {
      z = data[0];
      x = data[1];
      y = data[2];
    }
  }
  /*============================================================*/
  public Accelerometer(Context c)
  {
    context = c;
    sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
  }
  /*============================================================*/
  public void onPause()
  {
    sensorManager.unregisterListener(listener);
  }
  /*============================================================*/
  public void onResume()
  {
    sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
    sensorManager.registerListener(listener, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);

    WindowManager windowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    Display display = windowManager.getDefaultDisplay();
    rotation = display.getRotation();
  }
  /*============================================================*/
  float[] r = new float[9];

  /**
   * Определяет текущую ориентацию девайса в пространстве без учета поворота экрана
   */
  Orientation getDeviceOrientation()
  {
    float[] valuesResult = new float[3];

    SensorManager.getRotationMatrix(r, null, valuesAccel, valuesMagnet);
    SensorManager.getOrientation(r, valuesResult);

    valuesResult[0] = (float) Math.toDegrees(valuesResult[0]);
    valuesResult[1] = (float) Math.toDegrees(valuesResult[1]);
    valuesResult[2] = (float) Math.toDegrees(valuesResult[2]);

    return new Orientation(valuesResult);
  }
  /*============================================================*/
  //float[] inR = new float[9];
  //float[] outR = new float[9];

  /**
   * Определяет текущую ориентацию девайса в пространстве с учетом поворота экрана
   */
  Orientation getActualDeviceOrientation()
  {
    float[] valuesResult2 = new float[3];
    float[] inR = new float[9];
    float[] outR = new float[9];
    SensorManager.getRotationMatrix(inR, null, valuesAccel, valuesMagnet);
    int x_axis = SensorManager.AXIS_X;
    int y_axis = SensorManager.AXIS_Y;
    switch (rotation) {
      case (Surface.ROTATION_0): break;
      case (Surface.ROTATION_90):
        x_axis = SensorManager.AXIS_Y;
        y_axis = SensorManager.AXIS_MINUS_X;
        break;
      case (Surface.ROTATION_180):
        y_axis = SensorManager.AXIS_MINUS_Y;
        break;
      case (Surface.ROTATION_270):
        x_axis = SensorManager.AXIS_MINUS_Y;
        y_axis = SensorManager.AXIS_X;
        break;
      default:
        break;
    }
    SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
    SensorManager.getOrientation(outR, valuesResult2);
    valuesResult2[0] = (float) Math.toDegrees(valuesResult2[0]);
    valuesResult2[1] = (float) Math.toDegrees(valuesResult2[1]);
    valuesResult2[2] = (float) Math.toDegrees(valuesResult2[2]);

    return new Orientation(valuesResult2);
  }
  /*============================================================*/
  private SensorEventListener listener = new SensorEventListener() {

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
      switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
          for (int i=0; i < 3; i++){
            valuesAccel[i] = event.values[i];
          }
          break;
        case Sensor.TYPE_MAGNETIC_FIELD:
          for (int i=0; i < 3; i++){
            valuesMagnet[i] = event.values[i];
          }
          break;
      }
    }
  };
}
