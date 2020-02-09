package bsdq.bsdq.ble;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import bsdq.bsdq.MyApplication;
import bsdq.bsdq.db.MyDevice;
import java.util.Hashtable;
import java.util.regex.Pattern;

@TargetApi(18)
public class BluetoothLeService<IWindowManager> extends Service {
    public static final String COMPANY_NAME = "^BSDQ-|^LOCK";
    public static final int INT_PHOTOGRAPH = 2;
    public static final int SCAN_PERIOD = 2500;
    private static final int SREVICE_UPDATA = 5;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static final String TAG = "BluetoothLeService";
    public static final boolean isDebug = true;
    private static boolean isDofindService = false;
    private static boolean isFindService = false;
    private static boolean isStatr = false;
    public static String link_Addr = "";
    public static long time;
    private BluetoothDevice LEdevice;
    public Hashtable<String, MyBluetoothGatt> MyBluetoothGatts = new Hashtable<>();
    public BluetoothGattCharacteristic connect_state;
    public Context context;
    public BluetoothGattCharacteristic device_addr;
    public BluetoothGattCharacteristic device_name;
    private boolean isLEenabled = false;
    public int linkNum = 0;
    private AudioManager mAudiomanager;
    private final IBinder mBinder = new LocalBinder();
    public Hashtable<String, BluetoothDevice> mBindingDevices = new Hashtable<>();
    /* access modifiers changed from: private */
    public BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    public Hashtable<String, String> mConnectedDevices = new Hashtable<>();
    private int mConnectionState = 0;
    public String mDeviceAddr;
    public Hashtable<String, BluetoothDevice> mDevices = new Hashtable<>();
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    private BluetoothDevice mLEdevice;
    /* access modifiers changed from: private */
    public LeScanCallback mLeScanCallback = new LeScanCallback() {
        public BluetoothDevice mdevice;
        private int num = 0;

        /* JADX WARNING: Code restructure failed: missing block: B:30:0x00a6, code lost:
            return;
         */
        public synchronized void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            String name = bluetoothDevice.getName();
            if (name != null) {
                if (Pattern.compile(BluetoothLeService.COMPANY_NAME).matcher(name).find()) {
                    if (!BluetoothLeService.this.mDevices.containsKey(bluetoothDevice.getAddress())) {
                        BluetoothLeService.this.mDeviceAddr = bluetoothDevice.getAddress();
                        if (!BluetoothLeService.this.myApplication.noshowDevices.containsKey(bluetoothDevice.getAddress())) {
                            BluetoothLeService.this.mDevices.put(bluetoothDevice.getAddress(), bluetoothDevice);
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append("---");
                        sb.append(bluetoothDevice.getAddress());
                        Log.e("--", sb.toString());
                    }
                    if (BluetoothLeService.this.myApplication.macDevices.containsKey(bluetoothDevice.getAddress())) {
                        final MyDevice myDevice = (MyDevice) BluetoothLeService.this.myApplication.macDevices.get(bluetoothDevice.getAddress());
                        if (myDevice != null) {
                            if (myDevice.auto) {
                                BluetoothLeService.this.mHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        if (!BluetoothLeService.this.unlinkBleDevices.containsKey(bluetoothDevice.getAddress())) {
                                            BluetoothLeService.this.connBLE(bluetoothDevice.getAddress(), myDevice.pwd, myDevice.mechanical_code, myDevice.auto);
                                            BluetoothLeService.this.linkNum++;
                                            if (BluetoothLeService.this.linkNum > 5) {
                                                BluetoothLeService.this.linkNum = 0;
                                            }
                                        }
                                    }
                                }, (long) ((BluetoothLeService.this.linkNum * 30) + 50));
                            }
                        }
                    }
                }
            }
        }
    };
    public Handler mScanHandler;
    /* access modifiers changed from: private */
    public boolean mScanning = false;
    public BluetoothGattCharacteristic manufacturer_name;
    public MyApplication myApplication;
    public BluetoothLeService myBluetoothLeService;
    public Handler openScanHandler;
    private Handler operateHandler;
    public BluetoothGattCharacteristic photoCharacteristic;
    public BluetoothGattCharacteristic power_level;
    /* access modifiers changed from: private */
    public Runnable scanRunnable = new Runnable() {
        public void run() {
            BluetoothLeService.this.scanLeDevice();
            BluetoothLeService.this.openScanHandler.postDelayed(BluetoothLeService.this.scanRunnable, 2000);
        }
    };
    public SharedPreferences settings;
    public Hashtable<String, String> unlinkBleDevices = new Hashtable<>();

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void setOperateHandler(Handler handler) {
        this.operateHandler = handler;
    }

    public void addBindingDevices(String str) {
        if (this.mDevices.containsKey(str)) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) this.mDevices.get(str);
            if (bluetoothDevice != null) {
                this.mBindingDevices.put(str, bluetoothDevice);
            }
        }
    }

    public void onCreate() {
        this.context = this;
        this.openScanHandler = new Handler();
        initialize();
        this.settings = getSharedPreferences("setting", 0);
        this.myApplication = (MyApplication) getApplication();
        this.myBluetoothLeService = this;
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public Hashtable<String, BluetoothDevice> getBluetoothDevice() {
        Hashtable<String, BluetoothDevice> hashtable = new Hashtable<>(this.mDevices);
        for (String remove : this.mBindingDevices.keySet()) {
            hashtable.remove(remove);
        }
        return hashtable;
    }

    public Hashtable<String, BluetoothDevice> getBindingDevice() {
        return new Hashtable<>(this.mBindingDevices);
    }

    private void initialize() {
        this.mBluetoothAdapter = ((BluetoothManager) getSystemService("bluetooth")).getAdapter();
        this.mAudiomanager = (AudioManager) getSystemService("audio");
        isStatr = true;
    }

    public boolean scanLeDevice() {
        if (this.mBluetoothAdapter == null || !this.mBluetoothAdapter.isEnabled() || this.mScanning) {
            return false;
        }
        this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
        this.mScanning = true;
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                BluetoothLeService.this.mBluetoothAdapter.stopLeScan(BluetoothLeService.this.mLeScanCallback);
            }
        }, 2500);
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                BluetoothLeService.this.mScanning = false;
            }
        }, 4500);
        return true;
    }

    public void scanLeDevice(boolean z) {
        this.openScanHandler.postDelayed(this.scanRunnable, 100);
    }

    public void StopScanLeDevice() {
        if (this.mBluetoothAdapter != null && this.mBluetoothAdapter.isEnabled() && this.mScanning) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
            this.mScanning = false;
        }
    }

    public int connBLE(String str, String str2, String str3, boolean z) {
        if (!this.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = new MyBluetoothGatt(this.context, this.mBluetoothAdapter, this.myBluetoothLeService, this.operateHandler, str2, str3, z);
            myBluetoothGatt.connectGatt(str);
            this.MyBluetoothGatts.put(str, myBluetoothGatt);
            return 1;
        }
        MyBluetoothGatt myBluetoothGatt2 = (MyBluetoothGatt) this.MyBluetoothGatts.get(str);
        if (myBluetoothGatt2 == null) {
            MyBluetoothGatt myBluetoothGatt3 = new MyBluetoothGatt(this.context, this.mBluetoothAdapter, this.myBluetoothLeService, this.operateHandler, str2, str3, z);
            myBluetoothGatt3.connectGatt(str);
            this.MyBluetoothGatts.put(str, myBluetoothGatt3);
            return 1;
        } else if (myBluetoothGatt2.mConnectionState == 2) {
            return 0;
        } else {
            myBluetoothGatt2.stopLEService();
            this.MyBluetoothGatts.remove(str);
            MyBluetoothGatt myBluetoothGatt4 = new MyBluetoothGatt(this.context, this.mBluetoothAdapter, this.myBluetoothLeService, this.operateHandler, str2, str3, z);
            myBluetoothGatt4.connectGatt(str);
            this.MyBluetoothGatts.put(str, myBluetoothGatt4);
            return 1;
        }
    }

    public void stopLEService() {
        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        this.mScanning = false;
    }
}
