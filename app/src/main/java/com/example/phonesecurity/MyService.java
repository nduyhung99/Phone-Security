package com.example.phonesecurity;

import static com.example.phonesecurity.MyApplication.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MyService extends Service {
    public static final String keyFlashlight = "KEY_FLASHLIGHT", keyVibrate = "KEY_VIBRATE",keySound = "KEY_SOUND",keyVolume="KEY_VOLUME";
    public int type, destroyActivity, pauseActivity;
    public static float valueProximi;
    public static int once = 0;
    public MediaPlayer mediaPlayer;
    public static float levelMove = 1.0f;
    public static final int timeVibrate = 600;
    Vibrator v;
    boolean endVibration = true;
    public static int volume = 10;
    AudioManager audioManager;

    private Proximity proximity;
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;
    private IntentFilter intentFilter, intentFilter1;
    boolean endHandler=true;
    boolean endFlashlight=true;
    FlashlightProvider flashlightProvider;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private CameraManager cameraManager;
    boolean flashState=false;
    @RequiresApi(api = Build.VERSION_CODES.M)
    CameraManager.TorchCallback torchCallback = new CameraManager.TorchCallback() {
        @Override
        public void onTorchModeUnavailable(String cameraId) {
            super.onTorchModeUnavailable(cameraId);
        }

        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            super.onTorchModeChanged(cameraId, enabled);
            flashState = enabled;
        }
    };

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();
        proximity = new Proximity(this);
        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = new MediaPlayer();
        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        intentFilter1 = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION,15,AudioManager.FLAG_PLAY_SOUND);

        flashlightProvider = new FlashlightProvider(this, new FlashlightProvider.FlashChangeResult() {
            @Override
            public void onChangeFlash(boolean isOn) {

            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.registerTorchCallback(torchCallback,null);
        }

        String dataIntent = intent.getStringExtra("key data intent");
        type = intent.getIntExtra("type listener", 0);
        destroyActivity = intent.getIntExtra("destroy activity", 0);
        pauseActivity = intent.getIntExtra("pause activity", 0);
        sendNotification(dataIntent);
        if (type == 1) {
            startProximityListener();
        } else if (type == 2) {
            startMoveListener();
        } else if (type == 3) {
            startChargeListener();
        } else if (type == 4) {
            startHeadsetPlugListener();
        } else if (type == 5) {
            startBatteryFullListener();
        }
        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_SIGNAL_STRENGTH");
        sendLevel.putExtra("LEVEL_DATA", true);
        sendBroadcast(sendLevel);
        return START_NOT_STICKY;
    }

    private void startBatteryFullListener() {
        this.registerReceiver(broadcastReceiverBatteryFull, intentFilter);
    }

    private void startHeadsetPlugListener() {
        this.registerReceiver(broadcastReceiverHeadsetPlug, intentFilter1);
    }

    private void startChargeListener() {
        this.registerReceiver(broadcastReceiverCharging, intentFilter);
    }

    private void startMoveListener() {
        accelerometer.registerListener();
        gyroscope.registerListener();
        gyroscope.setListener(new Gyroscope.listener() {
            @Override
            public void onRotation(float gX, float gY, float gz) {
                if (gX > levelMove || gX < -levelMove) {

                    startNotify();
                    endVibration = false;
                }
                if (gY > levelMove || gY < -levelMove) {
                    startNotify();
                    endVibration = false;
                }
                if (gz > levelMove || gz < -levelMove) {
                    startNotify();
                    endVibration = false;
                }
            }
        });

        accelerometer.setListener(new Accelerometer.listener() {
            @Override
            public void onTranslaltion(float aX, float aY, float az) {
                if (aX > levelMove || aX < -levelMove) {
                    startNotify();
                    endVibration = false;
                }
                if (aY > levelMove || aY < -levelMove) {
                    startNotify();
                    endVibration = false;
                }
                if (az > levelMove || az < -levelMove) {
                    startNotify();
                    endVibration = false;
                }
            }
        });
    }

    private void startProximityListener() {
        proximity.registerListener();
        proximity.setL(new Proximity.listener() {
            @Override
            public void onChange(float pX, float pY, float pZ) {
                if (once == 0) {
                    valueProximi = pX;
                    once = 1;
                } else {
                    if (valueProximi != pX) {
                        startNotify();
                        endVibration = false;
                        once = 0;
                    }
                }
            }
        });
    }

    private void openApp() {
        if (destroyActivity == 1 || pauseActivity == 1) {
            PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(getPackageName());
            launchIntent.putExtra("listening", 1);
            startActivity(launchIntent);
        }
    }

    private BroadcastReceiver broadcastReceiverHeadsetPlug = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state;
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                state = intent.getIntExtra("state", -1);
                if (Integer.valueOf(state) == 0) {
                    startNotify();
                    context.unregisterReceiver(broadcastReceiverHeadsetPlug);
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiverBatteryFull = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int charging = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            if (charging == BatteryManager.BATTERY_STATUS_FULL) {
                startNotify();
//                context.unregisterReceiver(broadcastReceiverBatteryFull);
            }
        }
    };

    private BroadcastReceiver broadcastReceiverCharging = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int charging = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            if (charging == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                startNotify();
                context.unregisterReceiver(broadcastReceiverCharging);
            }
        }
    };

    private void startNotify() {
        unregisterListener();
        playAssetSound(this, getFileNotification());
        Intent intent = new Intent(this,PatternActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("TYPE_START","warning");
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_MUTABLE);
        }
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        final boolean[] isLoop = {true};

        if (turnOnFlashlight()){
            endFlashlight=false;
            flashlightProvider.turnFlashlightOn();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!endFlashlight){
                        if (isLoop[0] ==true){
                            flashlightProvider.turnFlashlightOff();
                            isLoop[0] =false;
                            handler.postDelayed(this,500);
                        }else {
                            flashlightProvider.turnFlashlightOn();
                            isLoop[0] =true;
                            handler.postDelayed(this,1000);
                        }

                    }else {
                        flashlightProvider.turnFlashlightOff();
                    }
                }
            },1000);
        }

//        if (turnOnFlashlight()){
//            endFlashlight=false;
//            try {
//                String[] cameras = cameraManager.getCameraIdList();
//                for (int i = 0; i < cameras.length; i++) {
//                    int count=i;
//                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameras[i]);
//                    boolean flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
//                    if (flashAvailable) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            cameraManager.setTorchMode(cameras[count], !flashState);
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        cameraManager.setTorchMode(cameras[count], !flashState);
//                                        if (!endFlashlight){
//                                            handler.postDelayed(this,1000);
//                                        }else {
//                                            cameraManager.setTorchMode(cameras[count], false);
//                                        }
//                                    } catch (CameraAccessException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            },500);
//                        }
//                    }
//                }
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private String getFileNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences(keySound,MODE_PRIVATE);
        int id = sharedPreferences.getInt("idSound",R.id.radSound1);;
        switch (id){
            case R.id.radSound1:
                return "notification1.wav";
            case R.id.radSound2:
                return "notification2.wav";
            case R.id.radSound3:
                return "notification3.wav";
            case R.id.radSound4:
                return "notification4.wav";
            case R.id.radSound5:
                return "notification5.wav";
            case R.id.radSound6:
                return "notification6.wav";
        }
        return "notification1.wav";
    }

    private boolean turnOnFlashlight() {
        SharedPreferences sharedPreferences = getSharedPreferences(keyFlashlight,MODE_PRIVATE);
        boolean turnOnFlashlight = sharedPreferences.getBoolean("flashlight",false);
        return turnOnFlashlight;
    }

    private void sendNotification(String dataIntent) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("listening", 1);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(dataIntent)
                .setSmallIcon(R.drawable.ic_notification_warning)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterListener();
        endVibration = true;
//        if (volume < 6) {
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
//        }
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume,0);
        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_SIGNAL_STRENGTH");
        sendLevel.putExtra("STOP_RUNNING", true);
        sendLevel.putExtra("WARNING", false);
        sendBroadcast(sendLevel);
        endHandler=true;
        volume = 10;
        audioManager.setSpeakerphoneOn(false);
//        audioManager.setMode(AudioManager.MODE_NORMAL);
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        endFlashlight = true;
    }

    private void unregisterListener(){
        if (type == 1) {
            proximity.unregisterListener();
        } else if (type == 2) {
            accelerometer.unregisterListener();
            gyroscope.unregisterListener();
        } else if (type == 3) {
//            if (broadcastReceiverCharging.isOrderedBroadcast()){
//                this.unregisterReceiver(broadcastReceiverCharging);
//            }
        } else if (type == 4) {
//            if (broadcastReceiverHeadsetPlug.isOrderedBroadcast()){
//                this.unregisterReceiver(broadcastReceiverHeadsetPlug);
//            }
        } else if (type == 5) {
            this.unregisterReceiver(broadcastReceiverBatteryFull);
        }
    }

    @SuppressLint("WrongConstant")
    public void playAssetSound(Context context, String soundFileName) {
        try {
            endHandler=false;
            volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setRingerMode(AudioManager.STREAM_NOTIFICATION);
            audioManager.setSpeakerphoneOn(true);
            audioManager.requestAudioFocus(audioFocusChangeListener,AudioManager.STREAM_NOTIFICATION,AudioManager.AUDIOFOCUS_GAIN);
            SharedPreferences sharedPreferences = getSharedPreferences(keyVolume,MODE_PRIVATE);
            int volumeSetting = sharedPreferences.getInt("volume",68);
            int volume1 = (int) Math.round(0.15*volumeSetting);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume1,0);

            Intent sendLevel = new Intent();
            sendLevel.setAction("GET_SIGNAL_STRENGTH");
            sendLevel.putExtra("WARNING", true);
            sendBroadcast(sendLevel);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            AssetFileDescriptor descriptor = context.getAssets().openFd(soundFileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.start();
            int timeVibration;
            if (vibrate()){
                endVibration = false;
                timeVibration=timeVibrate;
            }else {
                timeVibration = 0;
            }
            v.vibrate(timeVibration);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (!endVibration) {
                        mediaPlayer.start();
                        v.vibrate(timeVibration);
                    }
                }
            });

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int a = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                    if (endHandler==false){
                        if (a < volume1) {
                            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume1,0);
                        }
                        handler.postDelayed(this,500);
                    }
                }
            },500);
        } catch (IOException ioException) {
            ioException.printStackTrace();

//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    v.vibrate(timeVibrate);
//                    if (!endVibration){
//                        handler.postDelayed(this,1000);
//                    }
//                }
//            },1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean vibrate() {
        SharedPreferences sharedPreferences = getSharedPreferences(keyVibrate,MODE_PRIVATE);
        boolean vibrate = sharedPreferences.getBoolean("vibrate",true);
        return vibrate;
    }

    class LooperThread extends Thread {
        public Handler mHandler;

        @Override
        public void run() {
            Looper.prepare();

            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.e("Lol1", "handleMessage: " );
                }
            };

            Looper.loop();
        }
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // another app took the stream temporarily

                mediaPlayer.setVolume(0.2f,0.2f);
            } else if (i == AudioManager.AUDIOFOCUS_LOSS) {
                // another app took full control of the stream
                mediaPlayer.setVolume(0.4f,0.4f);
            }else if(i==AudioManager.AUDIOFOCUS_GAIN){
                mediaPlayer.setVolume(1.0f,1.0f);
            }
            Log.e("tag", String.valueOf(i) );
        }
    };
}
