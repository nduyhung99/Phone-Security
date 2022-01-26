package com.example.phonesecurity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Gravity {
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    private listener l;

    public void setListener(listener listener){
        l = listener;
    }

    Gravity(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (l != null){
                    l.onTranslaltion(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    public void registerListener(){
        sensorManager.registerListener(sensorEventListener,sensor,500);
    }

    public void unregisterListener(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    public interface listener{
        void onTranslaltion(float grX, float grY, float grz);
    }
}
