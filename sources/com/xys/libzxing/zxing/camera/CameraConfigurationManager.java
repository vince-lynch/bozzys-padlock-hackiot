package com.xys.libzxing.zxing.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class CameraConfigurationManager {
    private static final double MAX_ASPECT_DISTORTION = 0.15d;
    private static final int MIN_PREVIEW_PIXELS = 153600;
    private static final String TAG = "CameraConfiguration";
    private Point cameraResolution;
    private final Context context;
    private Point screenResolution;

    public CameraConfigurationManager(Context context2) {
        this.context = context2;
    }

    public void initFromCameraParameters(Camera camera) {
        Parameters parameters = camera.getParameters();
        Display defaultDisplay = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay();
        new Point();
        this.screenResolution = getDisplaySize(defaultDisplay);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Screen resolution: ");
        sb.append(this.screenResolution);
        Log.i(str, sb.toString());
        Point point = new Point();
        point.x = this.screenResolution.x;
        point.y = this.screenResolution.y;
        if (this.screenResolution.x < this.screenResolution.y) {
            point.x = this.screenResolution.y;
            point.y = this.screenResolution.x;
        }
        this.cameraResolution = findBestPreviewSizeValue(parameters, point);
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Camera resolution x: ");
        sb2.append(this.cameraResolution.x);
        Log.i(str2, sb2.toString());
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Camera resolution y: ");
        sb3.append(this.cameraResolution.y);
        Log.i(str3, sb3.toString());
    }

    @SuppressLint({"NewApi"})
    private Point getDisplaySize(Display display) {
        Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError unused) {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        return point;
    }

    public void setDesiredCameraParameters(Camera camera, boolean z) {
        Parameters parameters = camera.getParameters();
        if (parameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Initial camera parameters: ");
        sb.append(parameters.flatten());
        Log.i(str, sb.toString());
        if (z) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }
        parameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
        camera.setParameters(parameters);
        Size previewSize = camera.getParameters().getPreviewSize();
        if (!(previewSize == null || (this.cameraResolution.x == previewSize.width && this.cameraResolution.y == previewSize.height))) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Camera said it supported preview size ");
            sb2.append(this.cameraResolution.x);
            sb2.append('x');
            sb2.append(this.cameraResolution.y);
            sb2.append(", but after setting it, preview size is ");
            sb2.append(previewSize.width);
            sb2.append('x');
            sb2.append(previewSize.height);
            Log.w(str2, sb2.toString());
            this.cameraResolution.x = previewSize.width;
            this.cameraResolution.y = previewSize.height;
        }
        camera.setDisplayOrientation(90);
    }

    public Point getCameraResolution() {
        return this.cameraResolution;
    }

    public Point getScreenResolution() {
        return this.screenResolution;
    }

    private Point findBestPreviewSizeValue(Parameters parameters, Point point) {
        List supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if (supportedPreviewSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Size previewSize = parameters.getPreviewSize();
            return new Point(previewSize.width, previewSize.height);
        }
        ArrayList<Size> arrayList = new ArrayList<>(supportedPreviewSizes);
        Collections.sort(arrayList, new Comparator<Size>() {
            public int compare(Size size, Size size2) {
                int i = size.height * size.width;
                int i2 = size2.height * size2.width;
                if (i2 < i) {
                    return -1;
                }
                return i2 > i ? 1 : 0;
            }
        });
        if (Log.isLoggable(TAG, 4)) {
            StringBuilder sb = new StringBuilder();
            for (Size size : arrayList) {
                sb.append(size.width);
                sb.append('x');
                sb.append(size.height);
                sb.append(' ');
            }
            String str = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Supported preview sizes: ");
            sb2.append(sb);
            Log.i(str, sb2.toString());
        }
        double d = (double) point.x;
        double d2 = (double) point.y;
        Double.isNaN(d);
        Double.isNaN(d2);
        double d3 = d / d2;
        Iterator it = arrayList.iterator();
        while (true) {
            boolean z = false;
            if (it.hasNext()) {
                Size size2 = (Size) it.next();
                int i = size2.width;
                int i2 = size2.height;
                if (i * i2 < MIN_PREVIEW_PIXELS) {
                    it.remove();
                } else {
                    if (i < i2) {
                        z = true;
                    }
                    int i3 = z ? i2 : i;
                    int i4 = z ? i : i2;
                    double d4 = (double) i3;
                    double d5 = (double) i4;
                    Double.isNaN(d4);
                    Double.isNaN(d5);
                    if (Math.abs((d4 / d5) - d3) > MAX_ASPECT_DISTORTION) {
                        it.remove();
                    } else if (i3 == point.x && i4 == point.y) {
                        Point point2 = new Point(i, i2);
                        String str2 = TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Found preview size exactly matching screen size: ");
                        sb3.append(point2);
                        Log.i(str2, sb3.toString());
                        return point2;
                    }
                }
            } else if (!arrayList.isEmpty()) {
                Size size3 = (Size) arrayList.get(0);
                Point point3 = new Point(size3.width, size3.height);
                String str3 = TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Using largest suitable preview size: ");
                sb4.append(point3);
                Log.i(str3, sb4.toString());
                return point3;
            } else {
                Size previewSize2 = parameters.getPreviewSize();
                Point point4 = new Point(previewSize2.width, previewSize2.height);
                String str4 = TAG;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("No suitable preview sizes, using default: ");
                sb5.append(point4);
                Log.i(str4, sb5.toString());
                return point4;
            }
        }
    }
}
