package com.xys.libzxing.zxing.decode;

import android.os.Handler;
import android.os.Looper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class DecodeThread extends Thread {
    public static final int ALL_MODE = 768;
    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final int BARCODE_MODE = 256;
    public static final int QRCODE_MODE = 512;
    private final CaptureActivity activity;
    private Handler handler;
    private final CountDownLatch handlerInitLatch = new CountDownLatch(1);
    private final Map<DecodeHintType, Object> hints = new EnumMap(DecodeHintType.class);

    public DecodeThread(CaptureActivity captureActivity, int i) {
        this.activity = captureActivity;
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(EnumSet.of(BarcodeFormat.AZTEC));
        arrayList.addAll(EnumSet.of(BarcodeFormat.PDF_417));
        if (i == 256) {
            arrayList.addAll(DecodeFormatManager.getBarCodeFormats());
        } else if (i == 512) {
            arrayList.addAll(DecodeFormatManager.getQrCodeFormats());
        } else if (i == 768) {
            arrayList.addAll(DecodeFormatManager.getBarCodeFormats());
            arrayList.addAll(DecodeFormatManager.getQrCodeFormats());
        }
        this.hints.put(DecodeHintType.POSSIBLE_FORMATS, arrayList);
    }

    public Handler getHandler() {
        try {
            this.handlerInitLatch.await();
        } catch (InterruptedException unused) {
        }
        return this.handler;
    }

    public void run() {
        Looper.prepare();
        this.handler = new DecodeHandler(this.activity, this.hints);
        this.handlerInitLatch.countDown();
        Looper.loop();
    }
}
