package bsdq.bsdq;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import bsdq.bsdq.ble.BluetoothLeService;
import bsdq.bsdq.ble.BluetoothLeService.LocalBinder;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.User;
import bsdq.bsdq.db.UserTable;

public class FristActivity extends MyBasicActivity {
    private BluetoothAdapter mBluetoothAdapter;
    /* access modifiers changed from: private */
    public BluetoothLeService mBluetoothLeService;
    private Context mContext;
    private DBAdapter mDBAdapter;
    private Handler mHandler = new Handler();
    private Resources mResources;
    /* access modifiers changed from: private */
    public MyApplication myApplication;
    private ServiceConnection sc;
    private SharedPreferences setting;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_frist);
        super.setTintColor(-16772059);
        this.myApplication = (MyApplication) getApplication();
        this.setting = getSharedPreferences("bsdq", 0);
        this.myApplication.setting = this.setting;
        this.mContext = this;
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        this.myApplication.isfrist = this.setting.getBoolean("isfrist", true);
        Cursor queryAllData = this.mDBAdapter.queryAllData(UserTable.DB_TABLE);
        User user = new User();
        if (queryAllData.moveToNext()) {
            user.name = queryAllData.getString(queryAllData.getColumnIndex("name"));
            user.pwd1 = queryAllData.getString(queryAllData.getColumnIndex(UserTable.PWD1));
            user.pwd2 = queryAllData.getString(queryAllData.getColumnIndex(UserTable.PWD2));
            user.pwdtype = queryAllData.getInt(queryAllData.getColumnIndex(UserTable.PWDTYPE));
            user.question = queryAllData.getString(queryAllData.getColumnIndex(UserTable.QUESTION));
            user.anwser = queryAllData.getString(queryAllData.getColumnIndex(UserTable.ANWSER));
        }
        this.myApplication.mUser = user;
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (FristActivity.this.myApplication.mUser.pwdtype == 0) {
                    FristActivity.this.startActivity(new Intent(FristActivity.this, BsdqMainActivity.class));
                    return;
                }
                FristActivity.this.startActivity(new Intent(FristActivity.this, NumPwdActivity.class));
            }
        }, 1500);
    }

    public void initialize() {
        this.mBluetoothAdapter = ((BluetoothManager) getSystemService("bluetooth")).getAdapter();
        boolean isEnabled = this.mBluetoothAdapter.isEnabled();
        if (this.mBluetoothAdapter == null || !isEnabled) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
            Toast.makeText(this.mContext, this.mResources.getText(R.string.open_bluetooth), 1).show();
        }
        this.sc = new ServiceConnection() {
            public void onServiceDisconnected(ComponentName componentName) {
            }

            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                FristActivity.this.mBluetoothLeService = ((LocalBinder) iBinder).getService();
                if (FristActivity.this.mBluetoothLeService != null) {
                    FristActivity.this.myApplication.mBluetoothLeService = FristActivity.this.mBluetoothLeService;
                    FristActivity.this.mBluetoothLeService.scanLeDevice(true);
                }
            }
        };
        getApplicationContext().bindService(new Intent(getApplicationContext(), BluetoothLeService.class), this.sc, 1);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        StringBuilder sb = new StringBuilder();
        sb.append("requestCode=");
        sb.append(i);
        sb.append(" resultCode=");
        sb.append(i2);
        Log.e("--", sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(" mBluetoothAdapter.isEnabled()=");
        sb2.append(this.mBluetoothAdapter.isEnabled());
        Log.e("--", sb2.toString());
    }
}
