package bsdq.bsdq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.DeviceTable;
import bsdq.bsdq.db.MyDevice;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class QRCodeActivity extends Activity {
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public final int MAX_PACKAGE_NUM = 3000;
    private final String TAG_REQUEST = "MY_TAG";
    public String addr = "";
    private Button btn_create;
    private ImageView img_QRCode;
    public Context mContext;
    public DBAdapter mDBAdapter;
    private Resources mResources;
    public MyDevice myDevice = new MyDevice();
    JSONObject objJson = new JSONObject();
    public ArrayList<String> packages = new ArrayList<>();
    public Bitmap qrbmp;
    public SharedPreferences setting;
    public TextView tv_save;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_qrcode);
        this.mResources = getResources();
        this.mContext = this;
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        boolean z = false;
        this.setting = getSharedPreferences("setting", 0);
        Bundle bundleExtra = getIntent().getBundleExtra("data");
        if (bundleExtra != null) {
            this.addr = bundleExtra.getString("addr");
        }
        Cursor queryDevice = this.mDBAdapter.queryDevice(this.addr);
        if (queryDevice.moveToNext()) {
            String string = queryDevice.getString(queryDevice.getColumnIndex("name"));
            String string2 = queryDevice.getString(queryDevice.getColumnIndex("addr"));
            String string3 = queryDevice.getString(queryDevice.getColumnIndex(DeviceTable.MECHANICAL_CODE));
            String string4 = queryDevice.getString(queryDevice.getColumnIndex(DeviceTable.PWD));
            String string5 = queryDevice.getString(queryDevice.getColumnIndex(DeviceTable.DEVICE_NAME));
            if (queryDevice.getInt(queryDevice.getColumnIndex(DeviceTable.AUTO)) != 0) {
                z = true;
            }
            this.myDevice.addr = string2;
            this.myDevice.name = string;
            this.myDevice.mechanical_code = string3;
            this.myDevice.pwd = string4;
            this.myDevice.auto = z;
            this.myDevice.devicename = string5;
            this.objJson = new JSONObject();
            try {
                this.objJson.put("T", this.myDevice.addr);
                this.objJson.put("K", this.myDevice.name);
                this.objJson.put("P", this.myDevice.pwd);
                this.objJson.put("N", this.myDevice.devicename);
                this.objJson.put("M", this.myDevice.mechanical_code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }
        this.img_QRCode = (ImageView) findViewById(R.id.img_QRCode);
        this.qrbmp = addLogo(generateBitmap(this.objJson.toString(), 400, 400), BitmapFactory.decodeResource(this.mResources, R.mipmap.ic_launcher));
        this.img_QRCode.setImageBitmap(this.qrbmp);
        this.tv_save = (TextView) findViewById(R.id.tv_save);
        this.tv_save.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                QRCodeActivity qRCodeActivity = QRCodeActivity.this;
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(QRCodeActivity.this.myDevice.name);
                sb.append(".png");
                qRCodeActivity.saveBitmap(sb.toString(), QRCodeActivity.this.qrbmp);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Cursor queryDevice = this.mDBAdapter.queryDevice(this.addr);
        if (queryDevice.moveToNext()) {
            String string = queryDevice.getString(queryDevice.getColumnIndex("name"));
            String string2 = queryDevice.getString(queryDevice.getColumnIndex("addr"));
            String string3 = queryDevice.getString(queryDevice.getColumnIndex(DeviceTable.MECHANICAL_CODE));
            String string4 = queryDevice.getString(queryDevice.getColumnIndex(DeviceTable.PWD));
            String string5 = queryDevice.getString(queryDevice.getColumnIndex(DeviceTable.DEVICE_NAME));
            boolean z = queryDevice.getInt(queryDevice.getColumnIndex(DeviceTable.AUTO)) != 0;
            this.myDevice.addr = string2;
            this.myDevice.name = string;
            this.myDevice.mechanical_code = string3;
            this.myDevice.pwd = string4;
            this.myDevice.auto = z;
            this.myDevice.devicename = string5;
            this.objJson = new JSONObject();
            try {
                this.objJson.put("T", this.myDevice.addr);
                this.objJson.put("K", this.myDevice.name);
                this.objJson.put("P", this.myDevice.pwd);
                this.objJson.put("N", this.myDevice.devicename);
                this.objJson.put("M", this.myDevice.mechanical_code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    private Bitmap generateBitmap(String str, int i, int i2) {
        QRCodeWriter qRCodeWriter = new QRCodeWriter();
        HashMap hashMap = new HashMap();
        hashMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qRCodeWriter.encode(str, BarcodeFormat.QR_CODE, i, i2, hashMap);
            int[] iArr = new int[(i * i2)];
            for (int i3 = 0; i3 < i2; i3++) {
                for (int i4 = 0; i4 < i; i4++) {
                    if (encode.get(i4, i3)) {
                        iArr[(i3 * i) + i4] = 0;
                    } else {
                        iArr[(i3 * i) + i4] = -1;
                    }
                }
            }
            return Bitmap.createBitmap(iArr, 0, i, i, i2, Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap addLogo(Bitmap bitmap, Bitmap bitmap2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int width2 = bitmap2.getWidth();
        int height2 = bitmap2.getHeight();
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        canvas.save();
        float f = 1.0f;
        while (true) {
            if (((float) width2) / f > ((float) (width / 5)) || ((float) height2) / f > ((float) (height / 5))) {
                f *= 2.0f;
            } else {
                float f2 = 1.0f / f;
                canvas.scale(f2, f2, (float) (width / 2), (float) (height / 2));
                canvas.drawBitmap(bitmap2, (float) ((width - width2) / 2), (float) ((height - height2) / 2), null);
                canvas.restore();
                return createBitmap;
            }
        }
    }

    private void showToast(String str) {
        Toast.makeText(this, str, 1).show();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00de, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00df, code lost:
        r6.printStackTrace();
        show(r5.mResources.getString(bsdq.bsdq.R.string.save_error));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00ec, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00ed, code lost:
        r6.printStackTrace();
        show(r5.mResources.getString(bsdq.bsdq.R.string.save_error));
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00de A[ExcHandler: IOException (r6v7 'e' java.io.IOException A[CUSTOM_DECLARE]), Splitter:B:16:0x0070] */
    public void saveBitmap(String str, Bitmap bitmap) {
        if (Environment.getExternalStorageState().equals("mounted")) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            StringBuilder sb = new StringBuilder();
            sb.append(externalStorageDirectory);
            sb.append("/Mylock/");
            File file = new File(sb.toString(), str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(externalStorageDirectory);
            sb2.append("/Mylock/");
            File file2 = new File(sb2.toString());
            if (!file2.exists()) {
                file2.mkdirs();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException unused) {
                    show(this.mResources.getString(R.string.save_error));
                }
            } else {
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    show(this.mResources.getString(R.string.save_error));
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(CompressFormat.PNG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Media.insertImage(this.mContext.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
            }
            Context context = this.mContext;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("file://");
            sb3.append(file.getPath());
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(sb3.toString())));
            StringBuilder sb4 = new StringBuilder();
            sb4.append(this.mResources.getString(R.string.save_ok));
            sb4.append(file.getAbsolutePath());
            show(sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(" File = ");
            sb5.append(file.getAbsolutePath());
            Log.e("JIANG ", sb5.toString());
        }
    }

    public void FinishQRCodeActivity() {
        finish();
    }

    public void show(String str) {
        Toast.makeText(this.mContext, str, 0).show();
    }
}
