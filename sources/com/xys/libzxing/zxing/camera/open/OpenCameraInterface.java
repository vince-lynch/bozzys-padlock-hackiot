package com.xys.libzxing.zxing.camera.open;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

public class OpenCameraInterface {
    private static final String TAG = "com.xys.libzxing.zxing.camera.open.OpenCameraInterface";

    public static Camera open(int i) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera camera = null;
        if (numberOfCameras == 0) {
            Log.w(TAG, "No cameras!");
            return null;
        }
        boolean z = i >= 0;
        if (!z) {
            i = 0;
            while (i < numberOfCameras) {
                CameraInfo cameraInfo = new CameraInfo();
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == 0) {
                    break;
                }
                i++;
            }
        }
        if (i < numberOfCameras) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Opening camera #");
            sb.append(i);
            Log.i(str, sb.toString());
            camera = Camera.open(i);
        } else if (z) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Requested camera does not exist: ");
            sb2.append(i);
            Log.w(str2, sb2.toString());
        } else {
            Log.i(TAG, "No camera facing back; returning camera #0");
            camera = Camera.open(0);
        }
        return camera;
    }

    public static Camera open() {
        return open(-1);
    }
}
