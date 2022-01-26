package com.example.phonesecurity;

import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.hsalf.smileyrating.SmileyRating;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.ldt.springback.view.SpringBackLayout;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 100;
    private ImageView imgSettings, imgMenu, imgBack, imgStart, imgStart1;
    private DrawerLayout drawerLayout;
    private SpringBackLayout mainLayout, settingsLayout, layoutStart;
    private TextView txtTitle;
    private TextView txtTest, txtStart, txtCountDown;
    private RelativeLayout layoutDrawer;
    private TextView txtVersion;
    private LottieAnimationView lottieAnimationView;
    public static final String ID_DEV = "8333071100450515738";

    public static int typeListener = 0, headsetPlug=0, checkCharging =0,listenerStatus=0, once=0, typePassword;
    public static boolean stopRunning=false, isWarning=false;
    public static final String keyType = "KEY_TYPE_PASSWORD", keyPin = "KEY_PIN", keyDraw = "KEY_DRAW"
            , keyFlashlight="KEY_FLASHLIGHT", keyHideDraw = "KEY_HIDE_DRAW", keyVibrate = "KEY_VIBRATE"
            , keySound = "KEY_SOUND", keyTimeCountdown="KEY_TIME_COUNTDOWN", keyVolume="KEY_VOLUME",keyTakePhoto="KEY_TAKE_PHOTO";
    private static final int REQUEST_PERMISSION_CODE = 1, REQUEST_PERMISSION_CODE_1=2, REQUEST_PERMISSION_CODE_2=3;

    private RelativeLayout layoutBatteryFull, layoutHeadsetPlug, layoutCharge, layoutMove, layoutProximity;
    private IntentFilter intentFilter, intentFilter1;

    public static final int timeVibrate=600, timeNotify=10;
    ArrayList<String> lol = new ArrayList<>();
    private RelativeLayout layoutVolume, layoutSound, layoutVibrate, layoutFlash, layoutTimeCountDown,
            layoutChangePassword, layoutChangeTypePassword, layoutHideDraw, layoutTakePhoto, layoutToolbar;
    private SwitchButton switchVibrate, switchFlash, switchHideDraw, switchTakePhoto;
    private TextView currentTypePassword, txtChangeTypePassword;

    Context context;
    Vibrator v;

    private Proximity proximity;
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    SoundPool soundPool;
    MediaPlayer mediaPlayer, mediaPlayerSound;
    boolean flashState=false;
    private CountDownTimer countDownTimer;

    int countingsStars;
    String rateStatus = "RateStatus";

    ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onActivityResult(ActivityResult result) {
            if (Settings.canDrawOverlays(MainActivity.this)){
                Toast.makeText(MainActivity.this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.white), false, false, MainActivity.this);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        createFileDataApp();
        checkPin();
        addControls();

        layoutDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        layoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissionTakePhoto()){
                    return;
                }
                Intent intent = new Intent(MainActivity.this, BreakInAlertActivity.class);
                startActivity(intent);
            }
        });

        switchTakePhoto.setOnClickToggle(new SwitchButton.OnClickToggle() {
            @Override
            public void onclick() {
                if (!checkPermissionTakePhoto()){
                    switchTakePhoto.setEnabled(false);
                    showDialogPermission("take_photo");
                }
            }
        });




        switchTakePhoto.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (!checkPermissionTakePhoto()){
//                    switchTakePhoto.setChecked(false);
                    showDialogPermission("take_photo");
                }else {
                    SharedPreferences sharedPreferences = getSharedPreferences(keyTakePhoto,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (isChecked){
                        editor.putBoolean("takePhoto",true);
                        editor.apply();
                    }else {
                        editor.putBoolean("takePhoto",false);
                        editor.apply();
                    }
                }
            }
        });

        layoutVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogVolume();
            }
        });

        layoutSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSound();
            }
        });

        layoutTimeCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogTimeCountDown();
            }
        });

        switchVibrate.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(keyVibrate,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked){
                    editor.putBoolean("vibrate",true);
                    editor.apply();
                }else {
                    editor.putBoolean("vibrate",false);
                    editor.apply();
                }
            }
        });

        switchHideDraw.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(keyHideDraw,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked){
                    editor.putBoolean("hideDraw",true);
                    editor.apply();
                }else {
                    editor.putBoolean("hideDraw",false);
                    editor.apply();
                }
            }
        });

        switchFlash.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(keyFlashlight,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked){
                    editor.putBoolean("flashlight",true);
                    editor.apply();
                }else {
                    editor.putBoolean("flashlight",false);
                    editor.apply();
                }
            }
        });

        layoutChangeTypePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PatternActivity.class);
                intent.putExtra("TYPE_START","change type password");
                startActivity(intent);
            }
        });

        layoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PatternActivity.class);
                intent.putExtra("TYPE_START","change password");
                startActivity(intent);
            }
        });

        imgStart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeListener();
            }
        });
        
        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (typeListener){
                    case 1:
                        startProximity();
                        break;
                    case 2:
                        startMove();
                        break;
                    case 3:
                        startCharge();
                        break;
                    case 4:
                        startHeadsetPlug();
                        break;
                    case 5:
                        startBatteryFull();
                        break;
                    default:
                        break;
                }
            }
        });

        layoutBatteryFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLayoutStart(5);
                context.registerReceiver(broadcastReceiverChargingCheck,intentFilter);
            }
        });

        layoutHeadsetPlug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLayoutStart(4);
                context.registerReceiver(broadcastReceiverHeadsetPlugCheck,intentFilter1);
            }
        });

        layoutCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLayoutStart(3);
                context.registerReceiver(broadcastReceiverChargingCheck,intentFilter);
            }
        });

        layoutMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLayoutStart(2);
            }
        });

        layoutProximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLayoutStart(1);
            }
        });


        //TYPE_ACCELEROMETER
        //TYPE_GRAVITY

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSettingsLayout();
            }
        });

        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLayoutSettings();
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
    }

    private boolean checkPermissionTakePhoto() {
        if (SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        if (MainActivity.this.checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED &&
                MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && MainActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
//            String[] permissions= {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
//            requestPermissions(permissions,REQUEST_PERMISSION_CODE);
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                switchTakePhoto.setEnabled(true);
                if (!switchTakePhoto.isChecked()){
                    switchTakePhoto.setChecked(true);
                }else {
                    switchTakePhoto.setChecked(false);
                }
                Toast.makeText(MainActivity.this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            }else{
                switchTakePhoto.setEnabled(true);
                Toast.makeText(MainActivity.this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
//                checkRatio(permissions);
            }
        }

        if (requestCode == REQUEST_PERMISSION_CODE_1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                switchTakePhoto.setEnabled(true);
                if (!switchTakePhoto.isChecked()){
                    switchTakePhoto.setChecked(true);
                }else {
                    switchTakePhoto.setChecked(false);
                }
                Toast.makeText(MainActivity.this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            }else{
                switchTakePhoto.setEnabled(true);
                Toast.makeText(MainActivity.this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
//                checkRatio(permissions);
            }
        }

        if (requestCode == REQUEST_PERMISSION_CODE_2){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switchTakePhoto.setEnabled(true);
                if (!switchTakePhoto.isChecked()){
                    switchTakePhoto.setChecked(true);
                }else {
                    switchTakePhoto.setChecked(false);
                }
                Toast.makeText(MainActivity.this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            }else{
                switchTakePhoto.setEnabled(true);
                Toast.makeText(MainActivity.this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
//                checkRatio(permissions);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkRatio(String[] permissions) {
        boolean showRationale = shouldShowRequestPermissionRationale(String.valueOf(permissions[0]));
        if (!showRationale){
            goToSettingPermissions();
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogVolume() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_volume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        SeekBar seekBarVolume = dialog.findViewById(R.id.seekBarVolume);
        TextView txtApply = dialog.findViewById(R.id.txtApply);
        RangeSeekBar rangeSeekBar = dialog.findViewById(R.id.rangeSeekBar);

        rangeSeekBar.setIndicatorTextDecimalFormat("0");
        seekBarVolume.setMax(15);
        SharedPreferences sharedPreferences = getSharedPreferences(keyVolume,MODE_PRIVATE);
        int currentVolume = sharedPreferences.getInt("volume",68);
        final int[] volumeChange = {sharedPreferences.getInt("volume", 68)};
        seekBarVolume.setProgress(currentVolume);
        rangeSeekBar.setProgress(currentVolume);

        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                volumeChange[0] = Math.round(leftValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });

        txtApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int volume = seekBarVolume.getProgress();
//                if (volume!=currentVolume){
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putInt("volume",volume);
//                    editor.apply();
//                    Toast.makeText(MainActivity.this, getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
//                }
                if (volumeChange[0]!=currentVolume){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("volume",volumeChange[0]);
                    editor.apply();
                    Toast.makeText(MainActivity.this, getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDialogTimeCountDown() {
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_time_countdown);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        TextView txtApply = dialog.findViewById(R.id.txtApply);
        RadioGroup radioGroupTimeCountdown = dialog.findViewById(R.id.radioGroupTimeCountdown);
        SharedPreferences sharedPreferences = getSharedPreferences(keyTimeCountdown,MODE_PRIVATE);
        int idTimeCountdown = sharedPreferences.getInt("idTimeCountdown",R.id.radTime1);
        radioGroupTimeCountdown.check(idTimeCountdown);

        txtApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = radioGroupTimeCountdown.getCheckedRadioButtonId();
                if (id!=idTimeCountdown){
                    saveTimeCountdown(id);
                    Toast.makeText(MainActivity.this, getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void saveTimeCountdown(int id) {
        SharedPreferences sharedPreferences = getSharedPreferences(keyTimeCountdown,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idTimeCountdown",id);
        editor.apply();
    }

    private void showDialogSound() {
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_sound);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        TextView txtApply = dialog.findViewById(R.id.txtApply);
        RadioGroup radioGroupSound = dialog.findViewById(R.id.radioGroupSound);

        SharedPreferences sharedPreferences = getSharedPreferences(keySound,MODE_PRIVATE);
        int idSound = sharedPreferences.getInt("idSound",R.id.radSound1);
        radioGroupSound.check(idSound);

        radioGroupSound.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radSound1:
                        playTestSound("notification1.wav");
                        break;
                    case R.id.radSound2:
                        playTestSound("notification2.wav");
                        break;
                    case R.id.radSound3:
                        playTestSound("notification3.wav");
                        break;
                    case R.id.radSound4:
                        playTestSound("notification4.wav");
                        break;
                    case R.id.radSound5:
                        playTestSound("notification5.wav");
                        break;
                    case R.id.radSound6:
                        playTestSound("notification6.wav");
                        break;
                    default:
                        break;
                }
            }
        });

        txtApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = radioGroupSound.getCheckedRadioButtonId();
                if (id!=idSound){
                    saveSoundNotification(id);
                    Toast.makeText(MainActivity.this, getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void saveSoundNotification(int id) {
        SharedPreferences sharedPreferences = getSharedPreferences(keySound,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idSound",id);
        editor.apply();
    }

    private void createFileDataApp() {
        String path = getStore(this);
        File file = new File(path);
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }

    private void checkPin() {
        SharedPreferences preferences = getSharedPreferences(keyDraw, MODE_PRIVATE);
        String pattern = preferences.getString("pattern", "");

        SharedPreferences preferences1 = getSharedPreferences(keyPin, MODE_PRIVATE);
        String pin = preferences1.getString("pin", "");
        if (pin.equals("")){
            if (!pattern.equals("")){
                return;
            }else {
                Intent intent = new Intent(MainActivity.this,PatternActivity.class);
                intent.putExtra("TYPE_START","start new");
                startActivity(intent);
            }
        }else {
            return;
        }
    }

    public void clickStopService(View v){
        stopListenerService();
    }

    private void startNotifyMove() {
        startNotify();
        listenerStatus=0;
        accelerometer.unregisterListener();
        gyroscope.unregisterListener();
    }

    private void closeListener() {
        if (typeListener==4){
            context.registerReceiver(broadcastReceiverHeadsetPlugCheck,intentFilter1);
        }else if(typeListener==3||typeListener==5){
            context.registerReceiver(broadcastReceiverChargingCheck,intentFilter);
        }
        stopListenerService();
        listenerStatus=0;
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
        restart();
    }

    private void startBatteryFull() {
        if (checkTypeListener(typeListener)){
            return;
        }

        if (!checkSAWPermission()){
            return;
        }

        if (checkCharging==0){
            Toast.makeText(MainActivity.this, getString(R.string.plug_in_the_charge), Toast.LENGTH_SHORT).show();
        }else if (checkCharging==2){
            Toast.makeText(MainActivity.this, getString(R.string.battery_full), Toast.LENGTH_SHORT).show();
        }else {
            start();
            countDownTimer = new CountDownTimer(getTimeCountdown(), 1000) {
                public void onTick(long millisUntilFinished) {
                    txtCountDown.setText(String.valueOf((millisUntilFinished / 1000)+1));
                }
                public void onFinish() {
                    if (checkCharging==0){
                        restart();
                        Toast.makeText(MainActivity.this, getString(R.string.plug_in_the_charge), Toast.LENGTH_SHORT).show();
                    }else {
                        txtCountDown.setText("");
                        if (broadcastReceiverChargingCheck.isOrderedBroadcast()){
                            context.unregisterReceiver(broadcastReceiverChargingCheck);
                        }
                        listenerStatus=5;
//                        context.registerReceiver(broadcastReceiverBatteryFull,intentFilter);
                        startListenerService(5);
                        start2();
                    }
                }
            }.start();
        }
    }

    private void startHeadsetPlug() {
        if (checkTypeListener(typeListener)){
            return;
        }

        if (!checkSAWPermission()){
            return;
        }

        if (headsetPlug==0){
            Toast.makeText(MainActivity.this, getString(R.string.plug_in_headphones), Toast.LENGTH_SHORT).show();
        }else {
            start();
            countDownTimer = new CountDownTimer(getTimeCountdown(), 1000) {
                public void onTick(long millisUntilFinished) {
                    txtCountDown.setText(String.valueOf((millisUntilFinished / 1000)+1));
                }
                public void onFinish() {

                    if (headsetPlug==0){
                        restart();
                        Toast.makeText(MainActivity.this, getString(R.string.plug_in_headphones), Toast.LENGTH_SHORT).show();
                    }else {
                        txtCountDown.setText("");
                        if (broadcastReceiverHeadsetPlugCheck.isOrderedBroadcast()){
                            context.unregisterReceiver(broadcastReceiverHeadsetPlugCheck);
                        }
                        listenerStatus=4;
//                        context.registerReceiver(broadcastReceiverHeadsetPlug,intentFilter1);
                        startListenerService(4);
                        start2();
                    }
                }
            }.start();
        }
    }

    private void startCharge() {
        if (checkTypeListener(typeListener)){
            return;
        }

        if (!checkSAWPermission()){
            return;
        }

        if (checkCharging==0){
            Toast.makeText(MainActivity.this, getString(R.string.plug_in_the_charge), Toast.LENGTH_SHORT).show();
        }else {
            start();
            countDownTimer = new CountDownTimer(getTimeCountdown(), 1000) {
                public void onTick(long millisUntilFinished) {
                    txtCountDown.setText(String.valueOf((millisUntilFinished / 1000)+1));
                }
                public void onFinish() {
                    if (checkCharging==0){
                        restart();
                        Toast.makeText(MainActivity.this, getString(R.string.plug_in_the_charge), Toast.LENGTH_SHORT).show();
                    }else {
                        txtCountDown.setText("");
                        context.unregisterReceiver(broadcastReceiverChargingCheck);
                        listenerStatus=3;
//                        context.registerReceiver(broadcastReceiverCharging,intentFilter);
                        startListenerService(3);
                        start2();
                    }
                }
            }.start();
        }
    }

    private void startMove() {
        if (checkTypeListener(typeListener)){
            return;
        }

        if (!checkSAWPermission()){
            return;
        }

        start();
        countDownTimer = new CountDownTimer(getTimeCountdown(), 1000) {
            public void onTick(long millisUntilFinished) {
                txtCountDown.setText(String.valueOf((millisUntilFinished / 1000)+1));
            }
            public void onFinish() {
                txtCountDown.setText("");
                listenerStatus=2;
                startListenerService(2);
                start2();
            }
        }.start();
    }

    private void startProximity() {
        if (checkTypeListener(typeListener)){
            return;
        }

        if (!checkSAWPermission()){
            return;
        }
        start();
        countDownTimer = new CountDownTimer(getTimeCountdown(), 1000) {
            public void onTick(long millisUntilFinished) {
                txtCountDown.setText(String.valueOf((millisUntilFinished / 1000)+1));
            }
            public void onFinish() {
                    txtCountDown.setText("");
                    listenerStatus=1;
                    startListenerService(1);
                    start2();
            }
        }.start();
    }

    private boolean checkSAWPermission() {
        if (SDK_INT>=Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(MainActivity.this)){
                showDialogPermission("SAW");
                return false;
            }
        }
        return true;
    }

    private void showDialogPermission(String text) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_permission);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtPermission = dialog.findViewById(R.id.txtPermission);
        TextView txtGrantPermission = dialog.findViewById(R.id.txtGrantPermission);

        if (text.equals("SAW")){
            txtPermission.setText(R.string.SAW_permission);
        }else if (text.equals("take_photo")){
            txtPermission.setText(R.string.take_photo_permission);
        }

        txtGrantPermission.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (text.equals("SAW")){
                    checkPermission();
                }else if (text.equals("take_photo")){
//                    checkPermissionTakePhoto();
                    switchTakePhoto.setChecked(false);
                    String[] permissions;
                    if (MainActivity.this.checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                        permissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions,REQUEST_PERMISSION_CODE_1);
                    }else if (MainActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
                            MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        permissions= new String[]{Manifest.permission.CAMERA};
                        requestPermissions(permissions,REQUEST_PERMISSION_CODE_2);
                    }else {
                        permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
                        requestPermissions(permissions,REQUEST_PERMISSION_CODE);
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                switchTakePhoto.setEnabled(true);
                if (text.equals("take_photo")){
                    if (!checkPermissionTakePhoto()){
                        switchTakePhoto.setChecked(false);
                    }
                }
            }
        });

        dialog.show();
    }

    private long getTimeCountdown() {
        SharedPreferences sharedPreferences = getSharedPreferences(keyTimeCountdown,MODE_PRIVATE);
        int id = sharedPreferences.getInt("idTimeCountdown",R.id.radTime1);
        switch (id){
            case R.id.radTime1:
                return 3000;
            case R.id.radTime2:
                return 5000;
            case R.id.radTime3:
                return 10000;
            case R.id.radTime4:
                return 15000;
            case R.id.radTime5:
                return 20000;
            case R.id.radTime6:
                return 25000;
            case R.id.radTime7:
                return 30000;
        }
        return 3000;
    }

    private void startListenerService(int i) {
        Intent intent = new Intent(MainActivity.this,MyService.class);
        intent.putExtra("key data intent", getString(R.string.activated));
        intent.putExtra("type listener",i);
        startService(intent);
    }

    private void stopListenerService(){
        Intent intent = new Intent(MainActivity.this,MyService.class);
        stopService(intent);
    }

    private boolean checkTypeListener(int typeListener) {
        if (listenerStatus!=0){
            if (typeListener!=listenerStatus){
                String toast = "";
                switch (listenerStatus){
                    case 1:
                        toast=getString(R.string.activated_proximity);
                        break;
                    case 2:
                        toast=getString(R.string.activated_motion);
                        break;
                    case 3:
                        toast=getString(R.string.activated_charge);
                        break;
                    case 4:
                        toast=getString(R.string.activated_headphone);
                        break;
                    case 5:
                        toast=getString(R.string.activated_battery_full);
                        break;
                    default:
                        break;
                }
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private void restart() {
        imgStart.setVisibility(View.VISIBLE);
        txtStart.setVisibility(View.VISIBLE);
        imgStart1.setVisibility(View.GONE);
        txtCountDown.setVisibility(View.GONE);
        txtStart.setText(getString(R.string.active));
    }

    private void start() {
        imgStart.setVisibility(View.GONE);
        txtStart.setVisibility(View.GONE);
        imgStart1.setVisibility(View.VISIBLE);
        txtCountDown.setVisibility(View.VISIBLE);
    }

    private void start2(){
        imgStart.setVisibility(View.GONE);
        txtStart.setVisibility(View.VISIBLE);
        imgStart1.setVisibility(View.VISIBLE);
        txtCountDown.setVisibility(View.GONE);
        txtStart.setText(getString(R.string.activated));
    }

    private BroadcastReceiver broadcastReceiverHeadsetPlugCheck = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state;
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)){
                state = intent.getIntExtra("state",-1);
                if(Integer.valueOf(state)==0){
                    headsetPlug=0;
                }if(Integer.valueOf(state)==1){
                    headsetPlug=1;
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiverChargingCheck = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int charging = intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            if (charging==BatteryManager.BATTERY_STATUS_CHARGING){
                checkCharging=1;
            }else if (charging==BatteryManager.BATTERY_STATUS_NOT_CHARGING){
                checkCharging=0;
            }else if (charging==BatteryManager.BATTERY_STATUS_DISCHARGING){
                checkCharging=0;
            }else if (charging==BatteryManager.BATTERY_STATUS_FULL){
                checkCharging=2;
            }
        }
    };

    private void addControls() {
        imgSettings = findViewById(R.id.imgSettings);
        imgMenu = findViewById(R.id.imgMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        mainLayout = findViewById(R.id.mainLayout);
        settingsLayout = findViewById(R.id.settingsLayout);
        txtTitle = findViewById(R.id.txtTitle);
        imgBack = findViewById(R.id.imgBack);
        layoutStart = findViewById(R.id.layoutStart);
        imgStart = findViewById(R.id.imgStart);
        imgStart1 = findViewById(R.id.imgStart1);
        txtStart = findViewById(R.id.txtStart);
        txtCountDown = findViewById(R.id.txtCountDown);

        layoutBatteryFull = findViewById(R.id.layoutBatteryFull);
        layoutHeadsetPlug = findViewById(R.id.layoutHeadsetPlug);
        layoutCharge = findViewById(R.id.layoutCharge);
        layoutMove = findViewById(R.id.layoutMove);
        layoutProximity = findViewById(R.id.layoutProximity);
        txtTest = findViewById(R.id.txtTest);

        layoutVolume = findViewById(R.id.layoutVolume);
        layoutSound = findViewById(R.id.layoutSound);
        layoutVibrate = findViewById(R.id.layoutVibrate);
        layoutFlash = findViewById(R.id.layoutFlash);
        layoutTimeCountDown = findViewById(R.id.layoutTimeCountDown);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);
        layoutChangeTypePassword = findViewById(R.id.layoutChangeTypePassword);
        layoutHideDraw = findViewById(R.id.layoutHideDraw);
        layoutTakePhoto = findViewById(R.id.layoutTakePhoto);
        switchVibrate = findViewById(R.id.switchVibrate);
        switchFlash = findViewById(R.id.switchFlash);
        switchHideDraw = findViewById(R.id.switchHideDraw);
        switchTakePhoto = findViewById(R.id.switchTakePhoto);
        currentTypePassword = findViewById(R.id.currentTypePassword);
        txtChangeTypePassword = findViewById(R.id.txtChangeTypePassword);

        layoutDrawer = findViewById(R.id.layoutDrawer);
        txtVersion = findViewById(R.id.txtVersion);
//        txtVersion.setText(getString(R.string.version)+" 1.0.0");
        layoutToolbar = findViewById(R.id.layoutToolbar);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        int lol = getIntent().getIntExtra("listening",0);
        if (lol==1){
            openLayoutStart(typeListener);
        }

        SensorManager sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> list = new ArrayList<>();
        for (Sensor s : sensors){
            list.add(s.getName()+"\n");
        }
//        txtTest.setText(list.toString());

        context = getApplicationContext();
        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        intentFilter1 = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = new MediaPlayer();

        proximity = new Proximity(context);
        accelerometer = new Accelerometer(context);
        gyroscope = new Gyroscope(context);
        WifiLevelReceiver receiver = new WifiLevelReceiver();
        registerReceiver(receiver, new IntentFilter("GET_SIGNAL_STRENGTH"));

//        boolean flash = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//        Toast.makeText(MainActivity.this, String.valueOf(flash), Toast.LENGTH_SHORT).show();

        createSoundPool();
        checkSettings();
    }


    private void startNotify() {
//        long[] pattern = {0, timeVibrate, 1000};
//        v.vibrate(pattern,2);
        playAssetSound(context,"notification1.wav",mediaPlayer);
        restart();
        txtCountDown.setText("");
    }

    private void openLayoutStart(int i) {
        txtTitle.setText(getStringTitle(i));
        imgSettings.setVisibility(View.GONE);
        imgMenu.setVisibility(View.GONE);
        imgBack.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(drawerLayout.getWidth(), 0, 0, 0);
        animate.setDuration(400);
        animate.setFillAfter(true);
        typeListener = i;
        layoutStart.startAnimation(animate);
        layoutStart.setVisibility(View.VISIBLE);
        checkListener(i);
    }

    private String getStringTitle(int i) {
        switch (i){
            case 1:
                return getString(R.string.neighborhood);
            case 2:
                return getString(R.string.motion);
            case 3:
                return getString(R.string.charger);
            case 4:
                return getString(R.string.headphone);
            case 5:
                return getString(R.string.battery);
        }
        return "";
    }

    private void checkListener(int i) {
        checkListenerStatus(i,listenerStatus);
    }

    private void checkListenerStatus(int typeListener, int listenerStatus) {
        if (typeListener==listenerStatus){
            txtCountDown.setText("");
            start2();
        }else {
            restart();
        }
    }

    private boolean closeSettingsLayout() {
        imgSettings.setVisibility(View.VISIBLE);
        imgMenu.setVisibility(View.VISIBLE);
        imgBack.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(0, drawerLayout.getWidth(), 0, 0);
        animate.setDuration(400);
        animate.setFillAfter(false);
        if (settingsLayout.getVisibility()==View.VISIBLE){
            settingsLayout.startAnimation(animate);
            settingsLayout.setVisibility(View.GONE);
            txtTitle.setText(getString(R.string.app_name));
            return true;
        }else if (layoutStart.getVisibility()==View.VISIBLE){
            layoutStart.startAnimation(animate);
            layoutStart.setVisibility(View.GONE);
            txtTitle.setText(getString(R.string.app_name));
            if (countDownTimer!=null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        countDownTimer.cancel();
                        restart();
                    }
                },300);
            }
            return true;
        }
        return false;
    }

    private void openLayoutSettings() {
        txtTitle.setText(getString(R.string.settings));
        imgSettings.setVisibility(View.GONE);
        imgMenu.setVisibility(View.GONE);
        imgBack.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(drawerLayout.getWidth(), 0, 0, 0);
        animate.setDuration(400);
        animate.setFillAfter(true);
        settingsLayout.startAnimation(animate);
        settingsLayout.setVisibility(View.VISIBLE);
    }

    @SuppressLint("WrongConstant")
    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(Gravity.START);
    }

    public void clickLogo(View view){
        closeDrawer(drawerLayout);
    }

    private void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void checkPermissionApp(){
        if (SDK_INT<Build.VERSION_CODES.M){
            return;
        }else {
            if (Settings.canDrawOverlays(this)){
                return;
            }else {
                checkPermission();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE){
            if (Settings.canDrawOverlays(MainActivity.this)){
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + MainActivity.this.getPackageName()));
                activityResultLauncher.launch(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

        if (isWarning){
            Intent intent1 = new Intent(MainActivity.this,PatternActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("TYPE_START","warning");
            startActivity(intent1);
        }

        if (!broadcastReceiverChargingCheck.isOrderedBroadcast()){
            registerReceiver(broadcastReceiverChargingCheck,intentFilter);
        }
        checkServiceRunning();
        checkSettings();
    }

    private void checkSettings() {
        SharedPreferences preferences = getSharedPreferences(keyType, MODE_PRIVATE);
        typePassword = preferences.getInt("TYPE", 0);
        if (typePassword==0){
            currentTypePassword.setText(getString(R.string.pin_lock));
            txtChangeTypePassword.setText(getString(R.string.switch_to_pattern));
        }else {
            currentTypePassword.setText(getString(R.string.pattern_lock));
            txtChangeTypePassword.setText(getString(R.string.switch_to_pin));
        }

        SharedPreferences sharedPreferences = getSharedPreferences(keyFlashlight,MODE_PRIVATE);
        boolean turnOnFlashlight = sharedPreferences.getBoolean("flashlight",false);
        if (turnOnFlashlight){
            switchFlash.setChecked(true);
        }else {
            switchFlash.setChecked(false);
        }

        SharedPreferences sharedPreferences1 = getSharedPreferences(keyHideDraw,MODE_PRIVATE);
        boolean hideDraw = sharedPreferences1.getBoolean("hideDraw",false);
        if (hideDraw){
            switchHideDraw.setChecked(true);
        }else {
            switchHideDraw.setChecked(false);
        }

        SharedPreferences sharedPreferences2 = getSharedPreferences(keyVibrate,MODE_PRIVATE);
        boolean vibrate = sharedPreferences2.getBoolean("vibrate",true);
        if (vibrate){
            switchVibrate.setChecked(true);
        }else {
            switchVibrate.setChecked(false);
        }

        SharedPreferences sharedPreferences3 = getSharedPreferences(keyTakePhoto,MODE_PRIVATE);
        boolean takePhoto = sharedPreferences3.getBoolean("takePhoto",false);
        if (takePhoto){
            switchTakePhoto.setChecked(true);
        }else {
            switchTakePhoto.setChecked(false);
        }
    }

    private void checkServiceRunning() {
        if (listenerStatus==0){
            restart();
        }
    }

    public void clickFeedback(View v){
        feedback(this);
    }

    public void clickShare(View v){
        shareApp(this);
    }

    public void clickRate(View v){
        showRatingDialog();
    }

    public void clickMoreApps(View v){
        moreApps(this);
    }

    public void clickPrivacyPolicy(View v){
        policy(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (closeSettingsLayout()){
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /////// CREATE SOUNDPOOL

    protected void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(timeNotify)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        soundPool = new SoundPool(timeNotify, AudioManager.STREAM_MUSIC,0);
    }

    public static void playAssetSound(Context context, String soundFileName, MediaPlayer mediaPlayer) {
        try {

            AssetFileDescriptor descriptor = context.getAssets().openFd(soundFileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                }
            });
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(timeVibrate);
            mediaPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class WifiLevelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("GET_SIGNAL_STRENGTH"))
            {
                stopRunning = intent.getBooleanExtra("STOP_RUNNING",false);
                if (stopRunning){
                    listenerStatus=0;
                }
                isWarning = intent.getBooleanExtra("WARNING",false);
            }
        }

    }

    public static String getStore(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File f = c.getExternalFilesDir(null);
            if (f != null)
                return f.getAbsolutePath();
            else
                return "/storage/emulated/0/Android/data/" + c.getPackageName();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Android/data/" + c.getPackageName();
        }
    }

    public void setIsWarning(){
        isWarning=false;
    }

    public void shareApp(Context context) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "Install now";
            shareMessage = "https://play.google.com/store/apps/details?id=com.example.phonesecurity";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }

    }

    public void moreApps(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri.Builder uriBuilder = Uri.parse("https://play.google.com/store/apps/developer?id=REMI+Studio")
                .buildUpon()
                .appendQueryParameter("id", ID_DEV);
        intent.setData(uriBuilder.build());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void moreApps1(Context context) {
//        final String devName = Constant.DEV_NAME;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + ID_DEV)));
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:" + ID_DEV)));
        }
    }

    public void policy(Context context) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://antitheftalarmforandroid.blogspot.com/"));
            context.startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application can handle this request."
                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public static void feedback(Context c) {
        String[] email = {"tatcachilathuthach92@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Anti-Theft Alarm Feedback");
        try {
            c.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(c, "no email", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setLayout(getWindowWidth() - 50, LinearLayout.LayoutParams.WRAP_CONTENT);

        SmileyRating rating = dialog.findViewById(R.id.smileyRating);
        rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                switch (type) {
                    case TERRIBLE:
                        countingsStars = 1;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                    case BAD:
                        countingsStars = 2;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                    case OKAY:
                        countingsStars = 3;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                    case GOOD:
                        countingsStars = 4;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                    case GREAT:
                        countingsStars = 5;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                }
                SharedPreferences preferences = MainActivity.this.getSharedPreferences(rateStatus, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("STAR", countingsStars);
                editor.commit();
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finishAffinity();
                ratePkg(MainActivity.this, MainActivity.this.getPackageName());
            }
        });
        dialog.show();
    }

    public void ratePkg(Context context, String pkg) {
        if (pkg == null)
            return;
        Uri uri = Uri.parse("market://details?id=" + pkg);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int getWindowWidth() {
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public void clickTest(View v){
        Intent intent = new Intent(this,TestActivity.class);
        startActivity(intent);
    }

    public void clickSoundPool(View view){
//        int id = 0;
//        try {
//            id = soundPool.load(this.getAssets().openFd("notification2.wav"), 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int finalId = id;
//        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
//                soundPool.play(finalId, 1.0f, 1.0f, 0, 0, 1.0f);
//            }
//        });
        playTestSound("notification.wav");
    }

    private void playTestSound(String s) {
        mediaPlayerSound = new MediaPlayer();
        mediaPlayerSound.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AssetFileDescriptor descriptor = null;
        try {
            descriptor = context.getAssets().openFd(s);
            mediaPlayerSound.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mediaPlayerSound.setLooping(false);
            mediaPlayerSound.prepare();
            mediaPlayerSound.setVolume(1.0f, 1.0f);
            mediaPlayerSound.start();
            mediaPlayerSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (mediaPlayerSound!=null){
                        mediaPlayerSound.reset();
                        mediaPlayerSound.release();
                        mediaPlayerSound = null;
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToSettingPermissions(){
        Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri=Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }

}