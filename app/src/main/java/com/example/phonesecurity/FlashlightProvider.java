package com.example.phonesecurity;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.widget.Toast;

public class FlashlightProvider {

    private Camera mCamera;
    private Camera.Parameters parameters;
    private CameraManager camManager;
    private final Context context;
    private final FlashChangeResult flashChangeResult;
    private boolean isOn;

    public FlashlightProvider(Context context, FlashChangeResult flashChangeResult) {
        this.context = context;
        this.flashChangeResult = flashChangeResult;
    }

    public void turnFlashlightOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                if (camManager != null) {
                    String cameraId = camManager.getCameraIdList()[0];
                    camManager.setTorchMode(cameraId, true);
                    isOn = true;
                } else {
                    isOn = false;
                }
            } catch (Exception e) {
                isOn = false;
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();

            }
        } else {
            try {
                mCamera = Camera.open();
                parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                isOn = true;
            } catch (Exception e) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        }
        flashChangeResult.onChangeFlash(isOn);
    }

    public void turnFlashlightOff() {
        isOn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId;
                camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                if (camManager != null) {
                    cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
                    camManager.setTorchMode(cameraId, false);
                }
            } catch (Exception e) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                mCamera = Camera.open();
                parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mCamera.stopPreview();
            } catch (Exception e) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        }
        flashChangeResult.onChangeFlash(isOn);
    }

    public boolean isOn() {
        return isOn;
    }

    public interface FlashChangeResult {
        void onChangeFlash(boolean isOn);
    }
}