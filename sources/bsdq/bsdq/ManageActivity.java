package bsdq.bsdq;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.MyApplication.MyDeviceState;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.DeviceTable;
import bsdq.bsdq.db.MyDevice;
import bsdq.bsdq.db.RecordDBTable;
import bsdq.bsdq.ui.MyDialog1;
import bsdq.bsdq.ui.MyDialogConnPWD;
import bsdq.bsdq.ui.MyDialogsetname;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ManageActivity extends MyBasicActivity {
    /* access modifiers changed from: private */
    public String addr = "";
    public Context mContext;
    public DBAdapter mDBAdapter;
    public Handler mHandler = new Handler(new Callback() {
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    ManageActivity.this.finish();
                    break;
                case 1:
                    Cursor queryDevice = ManageActivity.this.mDBAdapter.queryDevice(ManageActivity.this.addr);
                    if (queryDevice.moveToNext()) {
                        ManageActivity.this.name = queryDevice.getString(queryDevice.getColumnIndex("name"));
                    }
                    TextView access$200 = ManageActivity.this.tv_name;
                    StringBuilder sb = new StringBuilder();
                    sb.append(ManageActivity.this.mResources.getString(R.string.name));
                    sb.append(ManageActivity.this.name);
                    access$200.setText(sb.toString());
                    break;
                case 2:
                    Bundle data = message.getData();
                    if (data != null) {
                        String string = data.getString(DeviceTable.PWD);
                        String str = DeviceTable.PWD;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("pwd=");
                        sb2.append(string);
                        sb2.append(" ");
                        sb2.append(ManageActivity.this.addr);
                        Log.e(str, sb2.toString());
                        ManageActivity.this.myApplication.resetPwd(ManageActivity.this.addr, string);
                        break;
                    }
                    break;
                case 3:
                    Toast.makeText(ManageActivity.this.mContext, ManageActivity.this.mResources.getString(R.string.pwd_modify_true), 1).show();
                    break;
                case 4:
                    Toast.makeText(ManageActivity.this.mContext, ManageActivity.this.mResources.getString(R.string.pwd_modify_false), 1).show();
                    break;
            }
            return false;
        }
    });
    public MyDeviceState mMyDeviceState = new MyDeviceState() {
        public void getBattery(int i) {
        }

        public void resetAuto(String str, boolean z, boolean z2) {
            ManageActivity.this.mHandler.sendEmptyMessage(3);
            String[] strArr = {str};
            ContentValues contentValues = new ContentValues();
            contentValues.put(DeviceTable.AUTO, Boolean.valueOf(z));
            ManageActivity.this.mDBAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues, "addr =? ", strArr);
        }

        public void resetPwd(String str, String str2, boolean z) {
            StringBuilder sb = new StringBuilder();
            sb.append("resetPwd ");
            sb.append(str);
            sb.append(" ");
            sb.append(str2);
            sb.append(" ");
            sb.append(z);
            Log.e("resetPwd", sb.toString());
            if (z) {
                ManageActivity.this.mHandler.sendEmptyMessage(3);
                String[] strArr = {str};
                ContentValues contentValues = new ContentValues();
                contentValues.put(DeviceTable.PWD, str2);
                ManageActivity.this.mDBAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues, "addr =? ", strArr);
                return;
            }
            ManageActivity.this.mHandler.sendEmptyMessage(4);
        }

        public void getLockstate(String str, boolean z, boolean z2) {
            Log.e("Lockstate", "getLockstate");
            if (z) {
                Date date = new Date();
                Calendar instance = Calendar.getInstance();
                instance.setTime(date);
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(instance.getTimeInMillis()));
                ContentValues contentValues = new ContentValues();
                contentValues.put(RecordDBTable.TIME, Long.valueOf(instance.getTimeInMillis()));
                contentValues.put("addr", str);
                ManageActivity.this.mDBAdapter.insert(RecordDBTable.DB_TABLE, contentValues);
            }
        }

        public void resetDevicePwd(String str, String str2, boolean z) {
            StringBuilder sb = new StringBuilder();
            sb.append("resetDevicePwd ");
            sb.append(str);
            sb.append(" ");
            sb.append(str2);
            sb.append(" ");
            sb.append(z);
            Log.e("Pwd", sb.toString());
            if (z) {
                ManageActivity.this.mHandler.sendEmptyMessage(3);
                String[] strArr = {str};
                ContentValues contentValues = new ContentValues();
                contentValues.put(DeviceTable.MECHANICAL_CODE, str2);
                ManageActivity.this.mDBAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues, "addr =? ", strArr);
                return;
            }
            ManageActivity.this.mHandler.sendEmptyMessage(4);
        }
    };
    public Resources mResources;
    /* access modifiers changed from: private */
    public MyApplication myApplication;
    /* access modifiers changed from: private */
    public MyDevice myDevice;
    private OnClickListener myOnClickListener = new OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rel2 /*2131165320*/:
                    MyDialogsetname myDialogsetname = new MyDialogsetname(ManageActivity.this.mContext, R.style.MyDialog, ManageActivity.this.addr);
                    myDialogsetname.setMsgHandle(ManageActivity.this.mHandler);
                    myDialogsetname.show();
                    return;
                case R.id.rel3 /*2131165321*/:
                    MyDialogConnPWD myDialogConnPWD = new MyDialogConnPWD(ManageActivity.this.mContext, R.style.MyDialog);
                    myDialogConnPWD.setMsgHandle(ManageActivity.this.mHandler);
                    myDialogConnPWD.show();
                    return;
                case R.id.rel4 /*2131165322*/:
                    final MyDialog1 myDialog1 = new MyDialog1(ManageActivity.this.mContext, R.style.MyDialog, R.layout.activity_pop7);
                    if (ManageActivity.this.myDevice.auto) {
                        myDialog1.setBtnMsg(ManageActivity.this.mResources.getString(R.string.mystop), ManageActivity.this.mResources.getString(R.string.close));
                    } else {
                        myDialog1.setBtnMsg(ManageActivity.this.mResources.getString(R.string.myOpen), ManageActivity.this.mResources.getString(R.string.close));
                    }
                    myDialog1.setShowMsg(ManageActivity.this.mResources.getString(R.string.automatic), ManageActivity.this.mResources.getString(R.string.automatic_unlocking));
                    myDialog1.setRefreshListener(new OnClickListener() {
                        public void onClick(View view) {
                            Log.e("open", "open");
                            if (ManageActivity.this.myDevice.auto) {
                                ManageActivity.this.myApplication.setParam(ManageActivity.this.addr, false, 0);
                                String[] strArr = {ManageActivity.this.addr};
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DeviceTable.AUTO, Boolean.valueOf(false));
                                ManageActivity.this.mDBAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues, "addr =? ", strArr);
                            } else {
                                ManageActivity.this.myApplication.setParam(ManageActivity.this.addr, true, 0);
                                String[] strArr2 = {ManageActivity.this.addr};
                                ContentValues contentValues2 = new ContentValues();
                                contentValues2.put(DeviceTable.AUTO, Boolean.valueOf(true));
                                ManageActivity.this.mDBAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues2, "addr =? ", strArr2);
                            }
                            myDialog1.mydismiss();
                        }
                    });
                    myDialog1.setCloseListener(new OnClickListener() {
                        public void onClick(View view) {
                            Log.e("Close", "Close");
                            myDialog1.mydismiss();
                        }
                    });
                    myDialog1.show();
                    return;
                case R.id.rel5 /*2131165323*/:
                    Intent intent = new Intent(ManageActivity.this, SetDevicePWDAcivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("addr", ManageActivity.this.addr);
                    intent.putExtra("data", bundle);
                    ManageActivity.this.startActivity(intent);
                    return;
                case R.id.rel6 /*2131165324*/:
                    Intent intent2 = new Intent(ManageActivity.this, QRCodeActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("addr", ManageActivity.this.addr);
                    intent2.putExtra("data", bundle2);
                    ManageActivity.this.startActivity(intent2);
                    return;
                case R.id.rel7 /*2131165325*/:
                    final MyDialog1 myDialog12 = new MyDialog1(ManageActivity.this.mContext, R.style.MyDialog, R.layout.activity_pop7);
                    myDialog12.setShowMsg(ManageActivity.this.mResources.getString(R.string.del_device), ManageActivity.this.mResources.getString(R.string.sure_deldevice));
                    myDialog12.setRefreshListener(new OnClickListener() {
                        public void onClick(View view) {
                            ManageActivity.this.myApplication.disconn(ManageActivity.this.addr);
                            ManageActivity.this.myApplication.macDevices.remove(ManageActivity.this.addr);
                            StringBuilder sb = new StringBuilder();
                            sb.append("");
                            sb.append(ManageActivity.this.addr);
                            String[] strArr = {sb.toString()};
                            long deleteOneData = ManageActivity.this.mDBAdapter.deleteOneData(DeviceTable.DB_TABLE, "addr =? ", strArr);
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("i=");
                            sb2.append(deleteOneData);
                            Log.e("i", sb2.toString());
                            ManageActivity.this.mHandler.sendEmptyMessage(0);
                            myDialog12.mydismiss();
                        }
                    });
                    myDialog12.show();
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public String name = "";
    private int pos = 0;
    private RelativeLayout rel2;
    private RelativeLayout rel3;
    private RelativeLayout rel4;
    private RelativeLayout rel5;
    private RelativeLayout rel6;
    private RelativeLayout rel7;
    private TextView tv_closemanage;
    /* access modifiers changed from: private */
    public TextView tv_name;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_manage);
        super.setTintColor(-16772059);
        this.myApplication = (MyApplication) getApplication();
        this.myApplication.mMyDeviceState = this.mMyDeviceState;
        this.mContext = this;
        this.mResources = getResources();
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        Bundle bundleExtra = getIntent().getBundleExtra("data");
        if (bundleExtra != null) {
            this.pos = bundleExtra.getInt("pos", 0);
            if (this.myApplication.myDevices.size() > this.pos) {
                this.myDevice = (MyDevice) this.myApplication.myDevices.get(this.pos);
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(this.myDevice.name);
                this.name = sb.toString();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("");
                sb2.append(this.myDevice.addr);
                this.addr = sb2.toString();
            }
        }
        init();
        setListener();
    }

    private void init() {
        this.rel2 = (RelativeLayout) findViewById(R.id.rel2);
        this.rel3 = (RelativeLayout) findViewById(R.id.rel3);
        this.rel4 = (RelativeLayout) findViewById(R.id.rel4);
        this.rel5 = (RelativeLayout) findViewById(R.id.rel5);
        this.rel6 = (RelativeLayout) findViewById(R.id.rel6);
        this.rel7 = (RelativeLayout) findViewById(R.id.rel7);
        this.tv_closemanage = (TextView) findViewById(R.id.tv_closemanage);
        this.tv_name = (TextView) findViewById(R.id.tv_name);
        if (this.myDevice != null) {
            TextView textView = this.tv_name;
            StringBuilder sb = new StringBuilder();
            sb.append(this.mResources.getString(R.string.name));
            sb.append(this.name);
            textView.setText(sb.toString());
        }
    }

    private void setListener() {
        this.tv_closemanage.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ManageActivity.this.finish();
            }
        });
        this.rel7.setOnClickListener(this.myOnClickListener);
        this.rel2.setOnClickListener(this.myOnClickListener);
        this.rel3.setOnClickListener(this.myOnClickListener);
        this.rel4.setOnClickListener(this.myOnClickListener);
        this.rel5.setOnClickListener(this.myOnClickListener);
        this.rel6.setOnClickListener(this.myOnClickListener);
    }
}
