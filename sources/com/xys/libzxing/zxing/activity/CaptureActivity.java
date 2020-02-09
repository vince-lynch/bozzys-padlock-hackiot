package com.xys.libzxing.zxing.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.xys.libzxing.R;
import com.xys.libzxing.zxing.camera.CameraManager;
import com.xys.libzxing.zxing.decode.DecodeThread;
import com.xys.libzxing.zxing.utils.BeepManager;
import com.xys.libzxing.zxing.utils.CaptureActivityHandler;
import com.xys.libzxing.zxing.utils.InactivityTimer;
import java.io.IOException;
import java.util.Hashtable;

public final class CaptureActivity extends MyActivity implements Callback {
    private static final int CAMERA_JAVA_REQUEST_CODE = 1;
    private static final int REQUEST_CODE = 234;
    private static final String TAG = "CaptureActivity";
    private BeepManager beepManager;
    private Button btn_pic;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    public Uri imageCropedCacheUri;
    private ImageView img_p_back;
    private ImageView img_p_p;
    private InactivityTimer inactivityTimer;
    private boolean isHasSurface = false;
    /* access modifiers changed from: private */
    public Rect mCropRect = null;
    public String photo_path = "";
    private Bitmap scanBitmap;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;
    private SurfaceView scanPreview = null;

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    public Handler getHandler() {
        return this.handler;
    }

    public CameraManager getCameraManager() {
        return this.cameraManager;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_capture);
        setTintColor(-11560724);
        this.scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        this.scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        this.scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        this.scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        this.inactivityTimer = new InactivityTimer(this);
        this.beepManager = new BeepManager(this);
        TranslateAnimation translateAnimation = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 0.0f, 2, 0.9f);
        translateAnimation.setDuration(4500);
        translateAnimation.setRepeatCount(-1);
        translateAnimation.setRepeatMode(1);
        this.scanLine.startAnimation(translateAnimation);
        this.img_p_p = (ImageView) findViewById(R.id.img_p_p);
        this.img_p_p.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CaptureActivity.this.photo();
            }
        });
        this.btn_pic = (Button) findViewById(R.id.btn_pic);
        this.btn_pic.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CaptureActivity.this.photo();
            }
        });
        this.img_p_back = (ImageView) findViewById(R.id.img_p_back);
        this.img_p_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CaptureActivity.this.finish();
            }
        });
    }

    /* access modifiers changed from: private */
    public void photo() {
        String[] strArr = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        if (ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, strArr, 1);
        }
        startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), REQUEST_CODE);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.cameraManager = new CameraManager(getApplication());
        this.handler = null;
        if (this.isHasSurface) {
            initCamera(this.scanPreview.getHolder());
        } else {
            this.scanPreview.getHolder().addCallback(this);
        }
        this.inactivityTimer.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        if (this.handler != null) {
            this.handler.quitSynchronously();
            this.handler = null;
        }
        this.inactivityTimer.onPause();
        this.beepManager.close();
        this.cameraManager.closeDriver();
        if (!this.isHasSurface) {
            this.scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!this.isHasSurface) {
            this.isHasSurface = true;
            initCamera(surfaceHolder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.isHasSurface = false;
    }

    public void handleDecode(Result result, Bundle bundle) {
        this.inactivityTimer.onActivity();
        this.beepManager.playBeepSoundAndVibrate();
        Intent intent = new Intent();
        bundle.putInt("width", this.mCropRect.width());
        bundle.putInt("height", this.mCropRect.height());
        bundle.putString("result", result.getText());
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        } else if (this.cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
        } else {
            try {
                this.cameraManager.openDriver(surfaceHolder);
                if (this.handler == null) {
                    this.handler = new CaptureActivityHandler(this, this.cameraManager, DecodeThread.ALL_MODE);
                }
                initCrop();
            } catch (IOException e) {
                Log.w(TAG, e);
                displayFrameworkBugMessageAndExit();
            } catch (RuntimeException e2) {
                Log.w(TAG, "Unexpected error initializing camera", e2);
                displayFrameworkBugMessageAndExit();
            }
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        if (VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }
        Builder builder = new Builder(this);
        builder.setTitle("Camera");
        builder.setMessage("Camera error");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                CaptureActivity.this.finish();
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                CaptureActivity.this.finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long j) {
        if (this.handler != null) {
            this.handler.sendEmptyMessageDelayed(R.id.restart_preview, j);
        }
    }

    public Rect getCropRect() {
        return this.mCropRect;
    }

    private void initCrop() {
        int i = this.cameraManager.getCameraResolution().y;
        int i2 = this.cameraManager.getCameraResolution().x;
        int[] iArr = new int[2];
        this.scanCropView.getLocationInWindow(iArr);
        int i3 = iArr[0];
        int statusBarHeight = iArr[1] - getStatusBarHeight();
        int width = this.scanCropView.getWidth();
        int height = this.scanCropView.getHeight();
        int width2 = this.scanContainer.getWidth();
        int height2 = this.scanContainer.getHeight();
        int i4 = (i3 * i) / width2;
        int i5 = (statusBarHeight * i2) / height2;
        this.mCropRect = new Rect(i4, i5, ((width * i) / width2) + i4, ((height * i2) / height2) + i5);
    }

    private int getStatusBarHeight() {
        try {
            Class cls = Class.forName("com.android.internal.R$dimen");
            return getResources().getDimensionPixelSize(Integer.parseInt(cls.getField("status_bar_height").get(cls.newInstance()).toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == REQUEST_CODE) {
            this.imageCropedCacheUri = intent.getData();
            new Thread(new Runnable() {
                public void run() {
                    Result scanningImage = CaptureActivity.this.scanningImage(CaptureActivity.this.photo_path);
                    if (scanningImage == null) {
                        Log.e("123", "   -----------");
                        Looper.prepare();
                        Toast.makeText(CaptureActivity.this.getApplicationContext(), "图片无法解析,请选择APP生成的图片", 0).show();
                        Looper.loop();
                        return;
                    }
                    Log.e("123result", scanningImage.toString());
                    String result = scanningImage.toString();
                    Looper.prepare();
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
                    bundle.putInt("width", CaptureActivity.this.mCropRect.width());
                    bundle.putInt("height", CaptureActivity.this.mCropRect.height());
                    bundle.putString("result", result);
                    intent.putExtras(bundle);
                    CaptureActivity.this.setResult(-1, intent);
                    CaptureActivity.this.finish();
                    Looper.loop();
                }
            }).start();
        }
    }

    /* access modifiers changed from: protected */
    public Result scanningImage(String str) {
        new Hashtable().put(DecodeHintType.CHARACTER_SET, "utf-8");
        new Options().inJustDecodeBounds = true;
        try {
            this.scanBitmap = Media.getBitmap(getContentResolver(), this.imageCropedCacheUri);
            Log.e("Ex", "--exe--");
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(e.getMessage());
            Log.e("Ex", sb.toString());
            e.printStackTrace();
        }
        if (this.scanBitmap != null) {
            Log.e("b", "scanBitmap!=null");
            try {
                return new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(this.scanBitmap))));
            } catch (NotFoundException e2) {
                e2.printStackTrace();
                return null;
            } catch (ChecksumException e3) {
                e3.printStackTrace();
                return null;
            } catch (FormatException e4) {
                e4.printStackTrace();
                return null;
            }
        } else {
            Log.e("b", "scanBitmap==null");
            return null;
        }
    }
}
