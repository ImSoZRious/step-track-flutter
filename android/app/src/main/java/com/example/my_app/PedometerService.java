package com.example.my_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Binder;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import io.flutter.Log;

public class PedometerService extends Service implements SensorEventListener {
    int steps;

    SensorManager sensorManager;
    Sensor stepCounterSensor;
    Sensor stepDetectorSensor;

    private MainActivity mActivity = null;

    private final static String CHANNEL_NAME = "pedometer_service_channel";
    private final static String NOTIFICATION_TITLE = "Step Tracker";
    private final static int IMPORTANCE = NotificationManager.IMPORTANCE_MIN;
    private final static int NOTIFICATION_ID = 727;
    private final static int SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI;

    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        PedometerService getService() {
            return PedometerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "Created");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepCounterSensor, SENSOR_DELAY);
        sensorManager.registerListener(this, stepDetectorSensor, SENSOR_DELAY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service", "Started");

        Notification notification = getNotification();
        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service", String.format("aaa: %s", steps));
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("Service", "Destroyed!");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.i("Service", "onSensorChanged " + steps);
            steps = (int) event.values[0];
            Log.i("Service", "onSensorChanged " + steps);
    
            if (isBounded()) {
                mActivity.emitEvent(EventType.SensorEvent, steps);
            }
        }
    }

    public void setCallback(MainActivity activity) {
        mActivity = activity;
    }

    public void removeCallback() {
        mActivity = null;
    }

    public boolean isBounded() {
        return mActivity != null;
    }

    public int getSteps() {
        return steps;
    }

    // should not be called.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private Notification getNotification() {
        String channel = createChannel();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle(NOTIFICATION_TITLE);

        // Uncomment to remove visible notification.
        // mBuilder.setVisibility(NotificationCompat.VISIBILITY_SECRET);

        return mBuilder
                .setPriority(IMPORTANCE)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setNumber(0)
                .build();
    }

    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_NAME, NOTIFICATION_TITLE, IMPORTANCE);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            } else {
                stopSelf();
            }
            return CHANNEL_NAME;
        } else {
            return "";
        }
    }
}
