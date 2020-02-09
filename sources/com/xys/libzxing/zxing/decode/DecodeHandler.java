package com.xys.libzxing.zxing.decode;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.xys.libzxing.R;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class DecodeHandler extends Handler {
    private final CaptureActivity activity;
    private final MultiFormatReader multiFormatReader = new MultiFormatReader();
    private boolean running = true;

    public DecodeHandler(CaptureActivity captureActivity, Map<DecodeHintType, Object> map) {
        this.multiFormatReader.setHints(map);
        this.activity = captureActivity;
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource planarYUVLuminanceSource, Bundle bundle) {
        int[] renderThumbnail = planarYUVLuminanceSource.renderThumbnail();
        int thumbnailWidth = planarYUVLuminanceSource.getThumbnailWidth();
        Bitmap createBitmap = Bitmap.createBitmap(renderThumbnail, 0, thumbnailWidth, thumbnailWidth, planarYUVLuminanceSource.getThumbnailHeight(), Config.ARGB_8888);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        createBitmap.compress(CompressFormat.JPEG, 50, byteArrayOutputStream);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, byteArrayOutputStream.toByteArray());
    }

    public void handleMessage(Message message) {
        if (this.running) {
            if (message.what == R.id.decode) {
                decode((byte[]) message.obj, message.arg1, message.arg2);
            } else if (message.what == R.id.quit) {
                this.running = false;
                Looper.myLooper().quit();
            }
        }
    }

    private void decode(byte[] bArr, int i, int i2) {
        Size previewSize = this.activity.getCameraManager().getPreviewSize();
        byte[] bArr2 = new byte[bArr.length];
        for (int i3 = 0; i3 < previewSize.height; i3++) {
            for (int i4 = 0; i4 < previewSize.width; i4++) {
                bArr2[(((previewSize.height * i4) + previewSize.height) - i3) - 1] = bArr[(previewSize.width * i3) + i4];
            }
        }
        int i5 = previewSize.width;
        previewSize.width = previewSize.height;
        previewSize.height = i5;
        Object obj = null;
        PlanarYUVLuminanceSource buildLuminanceSource = buildLuminanceSource(bArr2, previewSize.width, previewSize.height);
        if (buildLuminanceSource != null) {
            try {
                Result decodeWithState = this.multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(buildLuminanceSource)));
                this.multiFormatReader.reset();
                obj = decodeWithState;
            } catch (ReaderException unused) {
                this.multiFormatReader.reset();
            } catch (Throwable th) {
                this.multiFormatReader.reset();
                throw th;
            }
        }
        Handler handler = this.activity.getHandler();
        if (obj != null) {
            if (handler != null) {
                Message obtain = Message.obtain(handler, R.id.decode_succeeded, obj);
                Bundle bundle = new Bundle();
                bundleThumbnail(buildLuminanceSource, bundle);
                obtain.setData(bundle);
                obtain.sendToTarget();
            }
        } else if (handler != null) {
            Message.obtain(handler, R.id.decode_failed).sendToTarget();
        }
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] bArr, int i, int i2) {
        Rect cropRect = this.activity.getCropRect();
        if (cropRect == null) {
            return null;
        }
        PlanarYUVLuminanceSource planarYUVLuminanceSource = new PlanarYUVLuminanceSource(bArr, i, i2, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), false);
        return planarYUVLuminanceSource;
    }
}
