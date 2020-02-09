package com.consmart.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import bsdq.bsdq.R;
import com.consmart.ble.BleController.MyLeScanCallback;
import java.util.UUID;

public class MainActivity extends Activity {
    public BleController mBleController;
    private Context mContext;
    private MyLeScanCallback mMyLeScanCallback = new MyLeScanCallback() {
        public void onLeScan(BluetoothDevice bluetoothDevice, int i) {
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.bool.abc_action_bar_embed_tabs);
        this.mContext = getApplicationContext();
        this.mBleController = BleController.initialization(this.mContext);
        new UUID[1][0] = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
        this.mBleController.setScanLeDeviceType(null, "123456");
        this.mBleController.setMyLeScanCallback(this.mMyLeScanCallback);
        this.mBleController.scanLeDevice();
    }
}
