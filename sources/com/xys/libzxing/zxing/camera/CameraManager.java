package com.xys.libzxing.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import com.xys.libzxing.zxing.camera.open.OpenCameraInterface;
import java.io.IOException;

public class CameraManager {
    private static final String TAG = "CameraManager";
    private AutoFocusManager autoFocusManager;
    private Camera camera;
    private final CameraConfigurationManager configManager;
    private final Context context;
    private boolean initialized;
    private final PreviewCallback previewCallback;
    private boolean previewing;
    private int requestedCameraId = -1;

    public CameraManager(Context context2) {
        this.context = context2;
        this.configManager = new CameraConfigurationManager(context2);
        this.previewCallback = new PreviewCallback(this.configManager);
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0041 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x0070 */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0060  */
    public synchronized void openDriver(SurfaceHolder surfaceHolder) throws IOException {
        String str;
        Camera camera2 = this.camera;
        if (camera2 == null) {
            if (this.requestedCameraId >= 0) {
                camera2 = OpenCameraInterface.open(this.requestedCameraId);
            } else {
                camera2 = OpenCameraInterface.open();
            }
            if (camera2 != null) {
                this.camera = camera2;
            } else {
                throw new IOException();
            }
        }
        camera2.setPreviewDisplay(surfaceHolder);
        if (!this.initialized) {
            this.initialized = true;
            this.configManager.initFromCameraParameters(camera2);
        }
        Parameters parameters = camera2.getParameters();
        if (parameters == null) {
            str = null;
        } else {
            str = parameters.flatten();
        }
        this.configManager.setDesiredCameraParameters(camera2, false);
        Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Resetting to saved camera params: ");
        sb.append(str);
        Log.i(str2, sb.toString());
        if (str != null) {
            Parameters parameters2 = camera2.getParameters();
            parameters2.unflatten(str);
            camera2.setParameters(parameters2);
            this.configManager.setDesiredCameraParameters(camera2, true);
            Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
        }
        return;
    }

    public synchronized boolean isOpen() {
        return this.camera != null;
    }

    public synchronized void closeDriver() {
        if (this.camera != null) {
            this.camera.release();
            this.camera = null;
        }
    }

    public synchronized void startPreview() {
        Camera camera2 = this.camera;
        if (camera2 != null && !this.previewing) {
            camera2.startPreview();
            this.previewing = true;
            this.autoFocusManager = new AutoFocusManager(this.context, this.camera);
        }
    }

    public synchronized void stopPreview() {
        if (this.autoFocusManager != null) {
            this.autoFocusManager.stop();
            this.autoFocusManager = null;
        }
        if (this.camera != null && this.previewing) {
            this.camera.stopPreview();
            this.previewCallback.setHandler(null, 0);
            this.previewing = false;
        }
    }

    public synchronized void requestPreviewFrame(Handler handler, int i) {
        Camera camera2 = this.camera;
        if (camera2 != null && this.previewing) {
            this.previewCallback.setHandler(handler, i);
            camera2.setOneShotPreviewCallback(this.previewCallback);
        }
    }

    public synchronized void setManualCameraId(int i) {
        this.requestedCameraId = i;
    }

    public Point getCameraResolution() {
        return this.configManager.getCameraResolution();
    }

    public Size getPreviewSize() {
        if (this.camera != null) {
            return this.camera.getParameters().getPreviewSize();
        }
        return null;
    }
}
