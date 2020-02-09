package com.consmart.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import java.util.Hashtable;
import java.util.UUID;

public class BleController {
    private static BleController mBleController;
    private Hashtable<String, String> autoConnection = new Hashtable<>();
    private Context context;
    private boolean isOpenScan = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            String name = bluetoothDevice.getName();
            if (BleController.this.name != null || "".equals(BleController.this.name)) {
                if (BleController.this.name.equals(name.substring(0, BleController.this.name.length())) && BleController.this.mMyLeScanCallback != null) {
                    BleController.this.mMyLeScanCallback.onLeScan(bluetoothDevice, i);
                }
            } else if (BleController.this.mMyLeScanCallback != null) {
                BleController.this.mMyLeScanCallback.onLeScan(bluetoothDevice, i);
            }
        }
    };
    private Hashtable<String, MyBluetoothGatt> mMyBluetoothGatts = new Hashtable<>();
    /* access modifiers changed from: private */
    public MyLeScanCallback mMyLeScanCallback;
    /* access modifiers changed from: private */
    public String name;
    private Handler scanHandler = new Handler();
    private Runnable scanRunnable = new Runnable() {
        public void run() {
        }
    };
    private UUID[] serviceUuids;

    public interface MyLeScanCallback {
        void onLeScan(BluetoothDevice bluetoothDevice, int i);
    }

    private BleController(Context context2) {
        this.mBluetoothAdapter = ((BluetoothManager) context2.getSystemService("bluetooth")).getAdapter();
    }

    public static BleController initialization(Context context2) {
        if (context2 == null) {
            return null;
        }
        if (mBleController == null) {
            mBleController = new BleController(context2);
        }
        return mBleController;
    }

    public void removeMyBluetoothGatt(String str) {
        if (this.mMyBluetoothGatts.containsKey(str)) {
            this.mMyBluetoothGatts.remove(str);
        }
    }

    public void setScanLeDeviceType(UUID[] uuidArr, String str) {
        this.serviceUuids = uuidArr;
        this.name = str;
    }

    public int scanLeDevice() {
        if (this.mBluetoothAdapter == null || !this.mBluetoothAdapter.isEnabled()) {
            return 0;
        }
        if (this.serviceUuids == null) {
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
            Log.e("", "--->> 1开启成功");
        } else {
            this.mBluetoothAdapter.startLeScan(this.serviceUuids, this.mLeScanCallback);
        }
        return 1;
    }

    public void stopScanLeDevice() {
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }
    }

    public void setMyLeScanCallback(MyLeScanCallback myLeScanCallback) {
        this.mMyLeScanCallback = myLeScanCallback;
    }

    public void addAutoConnection(String str) {
        this.autoConnection.put(str, str);
    }

    public MyBluetoothGatt ConnectGatt(String str, MyBluetoothGattCallback myBluetoothGattCallback) {
        MyBluetoothGatt myBluetoothGatt = new MyBluetoothGatt(this.context, this.mBluetoothAdapter);
        myBluetoothGatt.setMyBluetoothGattCallback(myBluetoothGattCallback);
        myBluetoothGatt.connectGatt(str);
        return myBluetoothGatt;
    }
}
