package com.example.phonesecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.bumptech.glide.load.engine.Resource;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.List;

public class PatternActivity extends AppCompatActivity {
    private String key="";
    private EditText edtPin1,edtPin2,edtPin3,edtPin4;
    int currentEditText=0;
    private RelativeLayout layoutPin;
    private LinearLayout layoutImagePin;
    private String type="", pin="", pattern="";
    public static int typePassword;
    private TextView txtStatus;
    private int stepPin=0, stepPattern=0, offCapture, timeWrong;
    private boolean takePhoto;
    int countWrongPin = 0;
    StringBuilder stringBuilderPin = new StringBuilder();
    Handler handler;
    public static final String keyType = "KEY_TYPE_PASSWORD", keyPin = "KEY_PIN", keyDraw = "KEY_DRAW", keyHideDraw = "KEY_HIDE_DRAW"
            ,keyTakePhoto="KEY_TAKE_PHOTO",keyTimeWrong="KEY_TIME_WRONG";
    public static boolean isWarning=false;

    AutoFitTextureView autoFitTextureView;
    CameraControllerV2WithPreview cameraControllerV2WithPreview;
    com.itsxtt.patternlock.PatternLockView patternLockView1,patternLockViewHideLine;
    private ImageView imgPin1,imgPin2,imgPin3,imgPin4,imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.white), false, false, PatternActivity.this);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_pattern);
        edtPin1 = findViewById(R.id.edtPin1);
        edtPin2 = findViewById(R.id.edtPin2);
        edtPin3 = findViewById(R.id.edtPin3);
        edtPin4 = findViewById(R.id.edtPin4);
        layoutPin = findViewById(R.id.layoutPin);
        txtStatus = findViewById(R.id.txtStatus);
        patternLockView1 = findViewById(R.id.patternLockView);
        patternLockViewHideLine = findViewById(R.id.patternLockViewHideLine);

        layoutImagePin = findViewById(R.id.layoutImagePin);
        imgPin1 = findViewById(R.id.imgPin1);
        imgPin2 = findViewById(R.id.imgPin2);
        imgPin3 = findViewById(R.id.imgPin3);
        imgPin4 = findViewById(R.id.imgPin4);
        imgLogo = findViewById(R.id.imgLogo);

        autoFitTextureView = findViewById(R.id.textureView);
        cameraControllerV2WithPreview = new CameraControllerV2WithPreview(PatternActivity.this,autoFitTextureView);
        offCapture=0;

        handler = new Handler();

        type = getIntent().getStringExtra("TYPE_START");
        switch (type){
            case "warning":
                txtStatus.setText(getString(R.string.warning));
                imgLogo.setVisibility(View.VISIBLE);
                break;
            case "change type password":
                txtStatus.setText(getString(R.string.enter_password));
                imgLogo.setVisibility(View.GONE);
                break;
            case "change password":
                txtStatus.setText(getString(R.string.enter_old_password));
                imgLogo.setVisibility(View.GONE);
                break;
            case "start new":
                txtStatus.setText(getString(R.string.enter_new_password));
                imgLogo.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        SharedPreferences preferences = getSharedPreferences(keyType, MODE_PRIVATE);
        typePassword = preferences.getInt("TYPE", 0);

        SharedPreferences preferences1 = getSharedPreferences(keyPin, MODE_PRIVATE);
        pin = preferences1.getString("pin", "");

        SharedPreferences preferences2 = getSharedPreferences(keyDraw, MODE_PRIVATE);
        pattern = preferences2.getString("pattern", "");

        SharedPreferences preferences4 = getSharedPreferences(keyTimeWrong,MODE_PRIVATE);
        timeWrong = preferences4.getInt("timeWrong",1);

        SharedPreferences preferences3 = getSharedPreferences(keyTakePhoto,MODE_PRIVATE);
        takePhoto = preferences3.getBoolean("takePhoto",false);
        if (takePhoto && type.equals("warning")){
            autoFitTextureView.setVisibility(View.VISIBLE);
        }else {
            autoFitTextureView.setVisibility(View.GONE);
        }

        if (typePassword==0){
            layoutPin.setVisibility(View.VISIBLE);
            layoutImagePin.setVisibility(View.VISIBLE);
            hidePattern();
        }else {
            layoutPin.setVisibility(View.GONE);
            layoutImagePin.setVisibility(View.GONE);
            checkHideDraw();
        }

        edtPin1.requestFocus(); edtPin1.setCursorVisible(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            edtPin1.setShowSoftInputOnFocus(false);
            edtPin2.setShowSoftInputOnFocus(false);
            edtPin3.setShowSoftInputOnFocus(false);
            edtPin4.setShowSoftInputOnFocus(false);
        }

        patternLockView1.setOnPatternListener(new com.itsxtt.patternlock.PatternLockView.OnPatternListener() {
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
                if (checkPassDraw(lol.toString())){
                    return true;
                }else{
                    return false;
                }
            }
        });

        patternLockViewHideLine.setOnPatternListener(new com.itsxtt.patternlock.PatternLockView.OnPatternListener() {
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
                if (checkPassDraw(lol.toString())){
                    return true;
                }else{
                    return false;
                }
            }
        });
    }

    private void hidePattern() {
        patternLockView1.setVisibility(View.GONE);
        patternLockViewHideLine.setVisibility(View.GONE);
    }

    private void checkHideDraw() {
        if (hideDraw()){
            patternLockViewHideLine.setVisibility(View.VISIBLE);
            patternLockView1.setVisibility(View.GONE);
        }else {
            patternLockViewHideLine.setVisibility(View.GONE);
            patternLockView1.setVisibility(View.VISIBLE);
        }
    }

    private boolean hideDraw() {
        SharedPreferences sharedPreferences = getSharedPreferences(keyHideDraw,MODE_PRIVATE);
        boolean hideDraw = sharedPreferences.getBoolean("hideDraw",false);
        return hideDraw;
    }

    private boolean checkPassDraw(String passDraw) {
        switch (type){
            case "warning":
                return closeServicePattern(passDraw);
            case "change type password":
                if (typePassword==1){
                    return checkPass(passDraw);
                }else {
                    return setNewPattern(passDraw);
                }
            case "change password":
                return changePattern(passDraw);
            case "start new":
                return setNewPattern(passDraw);
            default:
                return false;
        }
    }

    private boolean closeServicePattern(String passDraw) {
        if (passDraw.equals(pattern)){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopListenerService();
                    onBackPressed();
                    closeCamera();
                }
            },300);
            return true;
        }else {
            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
            txtStatus.setText(getString(R.string.wrong_password));
            capturePhoto(passDraw);
            return false;
        }
    }

    private void capturePhoto(String passWrong) {
        if (countWrongPin==timeWrong-1 && takePhoto){
            cameraControllerV2WithPreview.takePicture(passWrong);
            offCapture=1;
            countWrongPin++;
        }else {
            countWrongPin++;
        }
    }

    private boolean changePattern(String passDraw) {
        switch (stepPattern){
            case 0:
                if (passDraw.equals(pattern)){
                    txtStatus.setText(getString(R.string.enter_new_password));
                    stepPattern=1;
                    return true;
                }else {
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
                    txtStatus.setText(getString(R.string.wrong_password));
                    return false;
                }
            case 1:
                if (passDraw.length()<4){
                    Toast.makeText(PatternActivity.this, getString(R.string.too_short), Toast.LENGTH_SHORT).show();
                    return true;
                }else {
                    pattern=passDraw;
                    txtStatus.setText(getString(R.string.confirm_password));
                    stepPattern=2;
                    return true;
                }
            case 2:
                if (!passDraw.equals(pattern)){
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
                    txtStatus.setText(getString(R.string.wrong_password));
                    return false;
                }else {
                    saveNewPass("",passDraw,1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PatternActivity.this, getString(R.string.change_password_successfully), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    },500);
                    return true;
                }
            default:
                return false;
        }
    }

    private boolean setNewPattern(String passDraw) {
        if (pattern.equals("")){
            pattern = passDraw;
            txtStatus.setText(getString(R.string.redraw_password));
            return true;
        }else {
            if (passDraw.equals(pattern)){
                saveNewPass("",passDraw,1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PatternActivity.this, getString(R.string.create_password_successfully), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                },500);
                return true;
            }else {
                YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
                txtStatus.setText(getString(R.string.wrong_password));
                return false;
            }
        }
    }

    private boolean checkPass(String passDraw) {
        if (!passDraw.equals(pattern)){
            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
            txtStatus.setText(getString(R.string.wrong_password));
            return false;
        }else {
            layoutPin.setVisibility(View.VISIBLE);
            layoutImagePin.setVisibility(View.VISIBLE);
            hidePattern();
            txtStatus.setText(getString(R.string.enter_new_password));
            return true;
        }
    }

    public void clickCheck(View v){
//        String string = edtPin1.getText().toString()+edtPin2.getText().toString()+edtPin3.getText().toString()+edtPin4.getText().toString();
//        if (string.length()!=4){
//            Toast.makeText(PatternActivity.this, getString(R.string.missing_input), Toast.LENGTH_SHORT).show();
//            return;
//        }
        String stringLol = String.valueOf(stringBuilderPin);

        switch (type){
            case "warning":
                stopServicePin(stringLol);
                /*
                *
                *
                *
                *
                *
                * */
                break;
            case "change type password":
                if (typePassword==0){
                    checkPin(stringLol);
                }else {
                    setNewPassword(stringLol);
                }
                break;
            case "change password":
                changePin(stringLol);
                break;
            case "start new":
                setNewPassword(stringLol);
                break;
            default:
                break;
        }
        closeCameraButton();
    }

    private void stopServicePin(String string) {
        if (!string.equals(pin)){
            clearEdt();
            clearImagePin();
            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
            txtStatus.setText(getString(R.string.wrong_password));
            capturePhoto(string);
        }else {
            stopListenerService();
            onBackPressed();
            closeCamera();
        }
    }

    private void checkPin(String string) {
        if (!string.equals(pin)){
            clearEdt();
            clearImagePin();
            YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
            txtStatus.setText(getString(R.string.wrong_password));
        }else {
            layoutPin.setVisibility(View.GONE);
            layoutImagePin.setVisibility(View.GONE);
            checkHideDraw();
            txtStatus.setText(getString(R.string.draw_new_password));
        }
    }

    private void changePin(String string) {
        switch (stepPin){
            case 0:
                if (string.equals(pin)){
                    clearEdt();
                    clearImagePin();
                    txtStatus.setText(getString(R.string.enter_new_password));
                    stepPin=1;
                }else {
                    clearEdt();
                    clearImagePin();
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
                    txtStatus.setText(getString(R.string.wrong_password));
                }
                break;
            case 1:
                pin=string;
                txtStatus.setText(getString(R.string.confirm_password));
                clearEdt();
                clearImagePin();
                stepPin=2;
                break;
            case 2:
                if (!string.equals(pin)){
                    clearEdt();
                    clearImagePin();
                    YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
                    txtStatus.setText(getString(R.string.wrong_password));
                }else {
                    saveNewPass(string,"",0);
                    Toast.makeText(PatternActivity.this, getString(R.string.change_password_successfully), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
            default:
                break;
        }
    }

    private void setNewPassword(String string) {
        if (pin.equals("")){
            pin = string;
            clearEdt();
            clearImagePin();
            txtStatus.setText(getString(R.string.confirm_password));
        }else {
            if (string.equals(pin)){
                saveNewPass(string,"",0);
                Toast.makeText(PatternActivity.this, getString(R.string.create_password_successfully), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(PatternActivity.this,MainActivity.class);
//                startActivity(intent);
                onBackPressed();
            }else {
                clearEdt();
                clearImagePin();
                YoYo.with(Techniques.Bounce).duration(200).repeat(1).playOn(txtStatus);
                txtStatus.setText(getString(R.string.wrong_password));
            }
        }
    }

    public void closeCamera(){
        if (takePhoto && offCapture==0){
            cameraControllerV2WithPreview.closeCamera();
        }
    }

    public void closeCameraButton(){
        if (offCapture==1 && takePhoto){
            cameraControllerV2WithPreview.closeCamera();
            offCapture=0;
//            offCaptureIntent=1;
        }
    }

    private void saveNewPass(String pin,String pattern,int type) {
        SharedPreferences preferences = getSharedPreferences(keyPin, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pin", pin);
        editor.apply();

        SharedPreferences preferences1 = getSharedPreferences(keyDraw, MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.putString("pattern", pattern);
        editor1.apply();

        SharedPreferences preferences2 = getSharedPreferences(keyType, MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        editor2.putInt("TYPE",type);
        editor2.apply();
    }

    private void clearImagePin(){
        stringBuilderPin.setLength(0);
        checkLengthPin(stringBuilderPin.length());
    }

    private void clearEdt() {
//        edtPin1.setText("");
//        edtPin2.setText("");
//        edtPin3.setText("");
//        edtPin4.setText("");
//        edtPin1.requestFocus(); edtPin1.setCursorVisible(true);
//        currentEditText=0;
    }

    public void clickOne(View v){
        inputToEditText(1);
        inputToImage(1);
    }

    public void clickTwo(View v){
        inputToEditText(2);
        inputToImage(2);
    }

    public void clickThree(View v){
        inputToEditText(3);
        inputToImage(3);
    }

    public void clickFour(View v){
        inputToEditText(4);
        inputToImage(4);
    }

    public void clickFive(View v){
        inputToEditText(5);
        inputToImage(5);
    }

    public void clickSix(View v){
        inputToEditText(6);
        inputToImage(6);
    }

    public void clickSeven(View v){
        inputToEditText(7);
        inputToImage(7);
    }

    public void clickEight(View v){
        inputToEditText(8);
        inputToImage(8);
    }

    public void clickNine(View v){
        inputToEditText(9);
        inputToImage(9);
    }

    public void clickZero(View v){
        inputToEditText(0);
        inputToImage(0);
    }

    public void clickBackspace(View v){
        deleteTextInEditText();
        deleteStringBuilderPin();
    }

    private void deleteStringBuilderPin() {
        if (stringBuilderPin.length()>0){
            stringBuilderPin.deleteCharAt(stringBuilderPin.length()-1);
            checkLengthPin(stringBuilderPin.length());
        }
    }

    private void inputToImage(int i) {
        if (stringBuilderPin.length()<4){
            stringBuilderPin.append(i);
            checkLengthPin(stringBuilderPin.length());
        }
        closeCameraButton();
    }

    private void checkLengthPin(int length) {
        switch (length){
            case 0:
                imgPin1.setVisibility(View.GONE);
                imgPin2.setVisibility(View.GONE);
                imgPin3.setVisibility(View.GONE);
                imgPin4.setVisibility(View.GONE);
                break;
            case 1:
                imgPin1.setVisibility(View.VISIBLE);
                imgPin2.setVisibility(View.GONE);
                imgPin3.setVisibility(View.GONE);
                imgPin4.setVisibility(View.GONE);
                break;
            case 2:
                imgPin1.setVisibility(View.VISIBLE);
                imgPin2.setVisibility(View.VISIBLE);
                imgPin3.setVisibility(View.GONE);
                imgPin4.setVisibility(View.GONE);
                break;
            case 3:
                imgPin1.setVisibility(View.VISIBLE);
                imgPin2.setVisibility(View.VISIBLE);
                imgPin3.setVisibility(View.VISIBLE);
                imgPin4.setVisibility(View.GONE);
                break;
            case 4:
                imgPin1.setVisibility(View.VISIBLE);
                imgPin2.setVisibility(View.VISIBLE);
                imgPin3.setVisibility(View.VISIBLE);
                imgPin4.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void inputToEditText(int number) {
//        String text = String.valueOf(number);
//        switch (currentEditText){
//            case 0:
//                edtPin1.setText(text);
//                edtPin1.setCursorVisible(false);
//                edtPin2.requestFocus(); edtPin2.setCursorVisible(true);
//                currentEditText=1;
//                break;
//            case 1:
//                edtPin2.setText(text);
//                edtPin2.setCursorVisible(false);
//                edtPin3.requestFocus(); edtPin3.setCursorVisible(true);
//                currentEditText=2;
//                break;
//            case 2:
//                edtPin3.setText(text);
//                edtPin3.setCursorVisible(false);
//                edtPin4.requestFocus(); edtPin4.setCursorVisible(true);
//                currentEditText=3;
//                break;
//            case 3:
//                edtPin4.setText(text);
//                edtPin4.setCursorVisible(false);
//                currentEditText=4;
//                break;
//            default:
//                break;
//        }
//        closeCameraButton();
    }

    private void deleteTextInEditText() {
//        switch (currentEditText){
//            case 0:
//                break;
//            case 1:
//                edtPin1.setText("");
//                edtPin2.setCursorVisible(false);
//                edtPin1.requestFocus(); edtPin1.setCursorVisible(true);
//                currentEditText=0;
//                break;
//            case 2:
//                edtPin2.setText("");
//                edtPin3.setCursorVisible(false);
//                edtPin2.requestFocus(); edtPin2.setCursorVisible(true);
//                currentEditText=1;
//                break;
//            case 3:
//                edtPin3.setText("");
//                edtPin4.setCursorVisible(false);
//                edtPin3.requestFocus(); edtPin3.setCursorVisible(true);
//                currentEditText=2;
//                break;
//            case 4:
//                edtPin4.setText("");
//                edtPin4.requestFocus(); edtPin4.setCursorVisible(true);
//                currentEditText=3;
//                break;
//            default:
//                break;
//        }
//        closeCameraButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isWarning = MainActivity.isWarning;
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        super.onBackPressed();
    }

    private void stopListenerService(){
        closeCamera();
        Intent intent = new Intent(PatternActivity.this,MyService.class);
        stopService(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (isWarning)
            {
                moveTaskToBack(true);
                return true; // return
            }else {
                onBackPressed();
            }
        }
        return false;
    }
}