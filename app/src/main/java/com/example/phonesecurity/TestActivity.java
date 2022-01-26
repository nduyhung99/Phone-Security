package com.example.phonesecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itsxtt.patternlock.PatternLockView;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    TextView aX1,aY1,aZ1,gX1,gY1,gZ1,pX1,pY1,pZ1,grX1,grY1,grZ1, txtCharge, txtFull;
    Accelerometer accelerometer;
    Gyroscope gyroscope;
    AudioManager audioManager;
    Proximity proximity;
    Gravity gravity;
    int once=0;
    PatternLockView patternLockView;
    RangeSeekBar rangeSeekBar;
    Button btnLol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        aX1 = findViewById(R.id.aX);
        aY1 = findViewById(R.id.aY);
        aZ1 = findViewById(R.id.aZ);
        gX1 = findViewById(R.id.gX);
        gY1 = findViewById(R.id.gY);
        gZ1 = findViewById(R.id.gZ);
        pX1 = findViewById(R.id.pX);
        pY1 = findViewById(R.id.pY);
        pZ1 = findViewById(R.id.pZ);
        grX1 = findViewById(R.id.grX);
        grY1 = findViewById(R.id.grY);
        grZ1 = findViewById(R.id.grZ);
        txtCharge = findViewById(R.id.txtCharge);
        txtFull = findViewById(R.id.txtFull);
        patternLockView = findViewById(R.id.patternLockView);
        rangeSeekBar = findViewById(R.id.rangeSeekBar);
        btnLol = findViewById(R.id.btnLol);


        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                btnLol.setText(String.valueOf(Math.round(leftValue)));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        rangeSeekBar.setIndicatorTextDecimalFormat("0");

        patternLockView.setOnPatternListener(new PatternLockView.OnPatternListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(@NonNull ArrayList<Integer> arrayList) {

            }

            @Override
            public boolean onComplete(@NonNull ArrayList<Integer> arrayList) {
                StringBuilder lol = new StringBuilder();
                for (int i = 0; i < arrayList.size(); i++) {
                    lol.append(arrayList.get(i));
                }
                Toast.makeText(TestActivity.this, lol.toString(), Toast.LENGTH_SHORT).show();
                patternLockView.setActivated(true);
                return true;
            }
        });

        Context context = getApplicationContext();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(broadcastReceiver1,intentFilter);
        context.registerReceiver(broadcastReceiver,intentFilter);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);
        proximity = new Proximity(this);
        gravity = new Gravity(this);


        accelerometer.setListener(new Accelerometer.listener() {
            @Override
            public void onTranslaltion(float aX, float aY, float az) {
                if (aX>1.0f || aX<-1.0f){
                    aX1.setText("ax: "+aX);
                    v.vibrate(400);
                }
                if (aY>1.0f || aY<-1.0f){
                    aY1.setText("aY: "+aY);
                    v.vibrate(400);
                }
                if (az>1.0f || az<-1.0f){
                    aZ1.setText("az: "+az);
                    v.vibrate(400);
                }
            }
        });

        gyroscope.setListener(new Gyroscope.listener() {
            @Override
            public void onRotation(float gX, float gY, float gz) {

                if (gX>1.0f || gX<-1.0f){
                    gX1.setText("gX: "+gX);
                    v.vibrate(400);
                }
                if (gY>1.0f || gY<-1.0f){
                    gY1.setText("gY: "+gY);
                    v.vibrate(400);
                }
                if (gz>1.0f || gz<-1.0f){
                    gZ1.setText("gz: "+gz);
                    v.vibrate(400);
                }
            }
        });

        proximity.setL(new Proximity.listener() {
            @Override
            public void onChange(float pX, float pY, float pZ) {
                if (once==0){
                    pX1.setText(String.valueOf(pX));
                    once=1;
                }else {
                    if (!pX1.getText().equals(String.valueOf(pX))){
                        v.vibrate(400);
                    }
                }
                pY1.setText("pY: "+pY);
                pZ1.setText("pZ: "+pZ);
            }
        });

        gravity.setListener(new Gravity.listener() {
            @Override
            public void onTranslaltion(float grX, float grY, float grz) {

                grX1.setText("gX: "+grX);
                grY1.setText("gY: "+grY);
                grZ1.setText("gz: "+grz);
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int charging = intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            if (charging==BatteryManager.BATTERY_STATUS_CHARGING){
                txtCharge.setText("Charging");
            }else if (charging==BatteryManager.BATTERY_STATUS_NOT_CHARGING){
                txtCharge.setText("Not Charging");
            }else if (charging==BatteryManager.BATTERY_STATUS_DISCHARGING){
                txtCharge.setText("Discharging");
            }
        }
    };

    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int charging = intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            if (charging==BatteryManager.BATTERY_STATUS_FULL){
                txtFull.setText("Battery Full");
            }
        }
    };

    public void close(View v){
        if (broadcastReceiver!=null){
            Context context = getApplicationContext();
            context.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        accelerometer.registerListener();
//        gyroscope.registerListener();
        proximity.registerListener();
//        gravity.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        accelerometer.unregisterListener();
//        gyroscope.unregisterListener();
        proximity.unregisterListener();
//        gravity.unregisterListener();
    }
}