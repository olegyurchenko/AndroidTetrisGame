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



class Accelerometer {
  private Context context;
  private SensorManager sensorManager;
  private Sensor sensorAccel;
  private Sensor sensorMagnet;
  private float[] valuesAccel = new float[3];
  private float[] valuesMagnet = new float[3];
  private int rotation;
  private boolean modified, shakeDetected, shakeX, shakeY, shakeZ;
  private float currentAccel = SensorManager.GRAVITY_EARTH;
  private long shakeTime = 0;
  private final long SHAKE_TIMEOUT = 500;
  private final float DHAKE_THRESHOLD = 4.0f;
  /*============================================================*/
  class Orientation
  {
    float x = 0;
    float y = 0;
    float z = 0;
    Orientation(float data[])
    {
      z = data[0];
      x = data[1];
      y = data[2];
    }
  }
  /*============================================================*/
  Accelerometer(Context c)
  {
    context = c;
    sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    modified = false;
    shakeDetected = false;
    shakeX = shakeY = shakeZ = false;
  }
  /*============================================================*/
  void onPause()
  {
    sensorManager.unregisterListener(listener);
  }
  /*============================================================*/
  void onResume()
  {
    sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
    sensorManager.registerListener(listener, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);

    WindowManager windowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    Display display = windowManager.getDefaultDisplay();
    rotation = display.getRotation();
    shakeTime = System.currentTimeMillis();
  }
  /*============================================================*/
  public synchronized void setModified(boolean m)
  {
    modified = m;
  }
  /*============================================================*/
  public synchronized boolean isModified()
  {
    return modified;
  }
  /*============================================================*/
  private synchronized void setShakeDetected(boolean s, boolean x, boolean y, boolean z)
  {
    shakeDetected = s;
    shakeX = x;
    shakeY = y;
    shakeZ = z;
  }
  /*============================================================*/
  void clearShakeDetected()
  {
    setShakeDetected(false, false, false, false);
  }
  /*============================================================*/
  synchronized boolean isShakeDetected()
  {
    return shakeDetected;
  }
  /*============================================================*/
  synchronized boolean isShakeX()
  {
    return shakeX;
  }
  /*============================================================*/
  synchronized boolean isShakeY()
  {
    return shakeY;
  }
  /*============================================================*/
  synchronized boolean isShakeZ()
  {
    return shakeZ;
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

    setModified(false);
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
    //valuesResult2[0] = (float) Math.toDegrees(valuesResult2[0]);
    //valuesResult2[1] = (float) Math.toDegrees(valuesResult2[1]);
    //valuesResult2[2] = (float) Math.toDegrees(valuesResult2[2]);

    setModified(false);
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
          setModified(true);

          double accel = Math.sqrt((double) (event.values[0] * event.values[0]
            + event.values[1] * event.values[1]
            + event.values[2] * event.values[2]));

          double currentAccel = Math.sqrt((double) (valuesAccel[0] * valuesAccel[0]
            + valuesAccel[1] * valuesAccel[1]
            + valuesAccel[2] * valuesAccel[2]));

          if (accel - currentAccel > DHAKE_THRESHOLD)
          {
            // SHAKE EVENT
            long t = System.currentTimeMillis();
            if(t - shakeTime >= SHAKE_TIMEOUT)
            {
              double threshold = DHAKE_THRESHOLD;
              boolean sx = Math.abs(valuesAccel[1] - event.values[1]) >= threshold;
              boolean sy = Math.abs(valuesAccel[2] - event.values[2]) >= threshold;
              boolean sz = Math.abs(valuesAccel[0] - event.values[0]) >= threshold;

              setShakeDetected(true, sx, sy, sz);
            }

            shakeTime = t;
          }
          valuesAccel[0] = event.values[0];
          valuesAccel[1] = event.values[1];
          valuesAccel[2] = event.values[2];
          break;
        case Sensor.TYPE_MAGNETIC_FIELD:
          for (int i=0; i < 3; i++){
            valuesMagnet[i] = event.values[i];
          }
          setModified(true);
          break;
      }
    }
  };
}
