package bsdq.bsdq.ble;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import bsdq.bsdq.MyApplication;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.RecordDBTable;
import com.consmart.ble.AES2;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@TargetApi(18)
public class MyBluetoothGatt {
    public static final int INT_PHOTOGRAPH = 2;
    public static final int SREVICE_UPDATA = 5;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public static final byte[] TIME_HEAD = {35, 37, 39, 67, 69, 71};
    public static final byte[] TIME_TAIL = {50, 82, 114, 52, 84, 116};
    public static final String tag = "MyBluetoothGatt";
    public final byte[] Mods = {37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56};
    public int battery = 0;
    public BluetoothAdapter bluetoothAdapter;
    public String cache = "123412";
    public byte cachecw = 0;
    public String cachedevicepwd = "";
    public String cachepwd = "";
    /* access modifiers changed from: private */
    public Handler checkHandler = new Handler();
    /* access modifiers changed from: private */
    public int checkNum = 0;
    public Runnable checkRunnable = new Runnable() {
        public void run() {
            if (MyBluetoothGatt.this.checkNum >= 8) {
                MyBluetoothGatt.this.isMydevice = false;
                MyBluetoothGatt.this.mBluetoothLeService.unlinkBleDevices.put(MyBluetoothGatt.this.mAddr, MyBluetoothGatt.this.mAddr);
                MyBluetoothGatt.this.stopLEService();
                MyBluetoothGatt.this.setMsg(MyBluetoothGatt.this.mAddr, 400);
                Log.e(MyBluetoothGatt.tag, "验证失败！");
                return;
            }
            MyBluetoothGatt.this.setAES();
            MyBluetoothGatt.this.checkHandler.postDelayed(MyBluetoothGatt.this.checkRunnable, 1000);
            MyBluetoothGatt.this.checkNum = MyBluetoothGatt.this.checkNum + 1;
        }
    };
    public byte closeTime = 0;
    public Context context;
    /* access modifiers changed from: private */
    public String devciepwd = "";
    public boolean isAutoClose = false;
    public boolean isMydevice = false;
    public boolean isOpen = false;
    public boolean isdAuto = false;
    public long linktime = 0;
    public String mAddr = "";
    /* access modifiers changed from: private */
    public BluetoothGatt mBluetoothGatt;
    public BluetoothLeService mBluetoothLeService;
    public int mConnectionState = 0;
    public DBAdapter mDBAdapter;
    private BluetoothGattCallback mGattCallback;
    private Handler mHandler = new Handler();
    private BluetoothDevice mLEdevice;
    private Handler mTimeHandler = new Handler(new Callback() {
        public boolean handleMessage(Message message) {
            return false;
        }
    });
    /* access modifiers changed from: private */
    public String machinepwd = "";
    public int modId = -1;
    public MyApplication myApplication;
    public int num = 0;
    public int openNum = 0;
    public BluetoothGattCharacteristic photoCharacteristic;
    public boolean pwdIsTrue = false;
    private byte[] sendsrcAES = {-53, 2, 5, 5, 16, 8, 35, 1, 2, 0, 5, 85, 34, 1, 18, 19, 20, -54};
    public SharedPreferences settings;
    /* access modifiers changed from: private */
    public byte[] srcAES = {2, 5, 5, 16, 8, 35, 1, 2, 0, 5, 85, 34, 1, 18, 19, 20};
    public MediaPlayer waitiingMP;

    public String getPwd() {
        return this.devciepwd;
    }

    public String getMachinePwd() {
        return this.machinepwd;
    }

    public MyBluetoothGatt(Context context2, BluetoothAdapter bluetoothAdapter2, BluetoothLeService bluetoothLeService, Handler handler, String str, String str2, boolean z) {
        this.context = context2;
        this.settings = this.context.getSharedPreferences("setting", 0);
        this.myApplication = (MyApplication) bluetoothLeService.getApplication();
        this.mDBAdapter = DBAdapter.init(context2);
        this.mDBAdapter.open();
        this.devciepwd = str;
        this.machinepwd = str2;
        this.isdAuto = z;
        this.bluetoothAdapter = bluetoothAdapter2;
        this.mBluetoothLeService = bluetoothLeService;
        Log.e(tag, "MyBluetoothGatt 1");
        updataSrc();
        if (handler != null) {
            this.mHandler = handler;
        }
        final Handler handler2 = handler;
        final BluetoothLeService bluetoothLeService2 = bluetoothLeService;
        final String str3 = str;
        final String str4 = str2;
        AnonymousClass2 r0 = new BluetoothGattCallback() {
            private long fastdata = 0;
            private long fasttime = 0;
            boolean flay = true;
            private int timeDataNum = 0;
            private boolean timeflay = false;

            public void onConnectionStateChange(final BluetoothGatt bluetoothGatt, int i, int i2) {
                if (i2 == 2) {
                    if (MyBluetoothGatt.this.mBluetoothGatt != null) {
                        MyBluetoothGatt.this.mConnectionState = 1;
                        handler2.postDelayed(new Runnable() {
                            public void run() {
                                bluetoothGatt.discoverServices();
                            }
                        }, 100);
                    }
                } else if (i2 == 0) {
                    MyBluetoothGatt.this.mConnectionState = 0;
                    StringBuilder sb = new StringBuilder();
                    sb.append("--DISCONNECTED-");
                    sb.append(bluetoothGatt.getDevice().getAddress());
                    Log.e("", sb.toString());
                    bluetoothLeService2.MyBluetoothGatts.remove(bluetoothGatt.getDevice().getAddress());
                    MyBluetoothGatt.this.setMsg(bluetoothGatt.getDevice().getAddress(), 4);
                    try {
                        MyBluetoothGatt.this.checkHandler.removeCallbacks(MyBluetoothGatt.this.checkRunnable);
                        MyBluetoothGatt.this.mBluetoothGatt.close();
                        MyBluetoothGatt.this.mBluetoothGatt = null;
                        if (bluetoothLeService2 != null && MyBluetoothGatt.this.mAddr != null && "null".equals(MyBluetoothGatt.this.mAddr) && !bluetoothLeService2.unlinkBleDevices.containsKey(MyBluetoothGatt.this.mAddr)) {
                            bluetoothLeService2.connBLE(MyBluetoothGatt.this.mAddr, str3, str4, MyBluetoothGatt.this.isdAuto);
                        }
                    } catch (Exception unused) {
                    }
                }
            }

            public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
                if (bluetoothGatt != null) {
                    MyBluetoothGatt.this.mBluetoothGatt = bluetoothGatt;
                }
                if (i == 0) {
                    MyBluetoothGatt.this.mConnectionState = 2;
                    handler2.postDelayed(new Runnable() {
                        public void run() {
                            MyBluetoothGatt.this.setNotify();
                        }
                    }, 100);
                    MyBluetoothGatt.this.checkHandler.postDelayed(MyBluetoothGatt.this.checkRunnable, 500);
                    handler2.postDelayed(new Runnable() {
                        public void run() {
                            MyBluetoothGatt.this.readDeviceBattry();
                        }
                    }, 400);
                    handler2.postDelayed(new Runnable() {
                        public void run() {
                            MyBluetoothGatt.this.readDeviceBattry();
                        }
                    }, 1400);
                    Message message = new Message();
                    message.what = 3;
                    Bundle bundle = new Bundle();
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    sb.append(MyBluetoothGatt.this.mBluetoothGatt.getDevice().getAddress());
                    bundle.putString("deviceAddr", sb.toString());
                    message.setData(bundle);
                    handler2.sendMessageDelayed(message, 200);
                    MyBluetoothGatt.this.num = 0;
                }
            }

            public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
                Log.e("--", "onDescriptorWrite");
                if (!MyBluetoothGatt.this.pwdIsTrue) {
                    MyBluetoothGatt.this.setPWD(MyBluetoothGatt.this.getPwd());
                }
                MyBluetoothGatt.this.checkHandler.postDelayed(MyBluetoothGatt.this.checkRunnable, 2100);
                super.onDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i);
            }

            public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                StringBuilder sb = new StringBuilder();
                sb.append("---++ ");
                sb.append(bluetoothGattCharacteristic.getUuid().toString());
                Log.e("Changed", sb.toString());
                if (DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_DATA_UUID.equals(bluetoothGattCharacteristic.getUuid().toString())) {
                    byte[] value = bluetoothGattCharacteristic.getValue();
                    MyBluetoothGatt.this.mConnectionState = 2;
                    String str = "";
                    for (byte b : value) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str);
                        sb2.append(" ");
                        sb2.append(Integer.toHexString(b & 255));
                        str = sb2.toString();
                    }
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("---++ ");
                    sb3.append(str);
                    Log.e("Changed", sb3.toString());
                    if (value.length == 4 && value[0] == 88 && value[3] == -107) {
                        boolean z = (value[1] & 255) == 240;
                        if (z) {
                            MyBluetoothGatt.this.devciepwd = MyBluetoothGatt.this.cachepwd;
                        }
                        if (MyBluetoothGatt.this.myApplication.mMyDeviceState != null) {
                            MyBluetoothGatt.this.myApplication.mMyDeviceState.resetPwd(MyBluetoothGatt.this.mAddr, MyBluetoothGatt.this.getPwd(), z);
                        }
                    }
                    if (value.length == 4 && value[0] == 96 && value[3] == -107) {
                        boolean z2 = (value[1] & 255) == 240;
                        if (z2) {
                            MyBluetoothGatt.this.machinepwd = MyBluetoothGatt.this.cachedevicepwd;
                        }
                        if (MyBluetoothGatt.this.myApplication.mMyDeviceState != null) {
                            MyBluetoothGatt.this.myApplication.mMyDeviceState.resetDevicePwd(MyBluetoothGatt.this.mAddr, MyBluetoothGatt.this.getMachinePwd(), z2);
                        }
                    }
                    if (value.length == 5 && value[0] == 97 && value[4] == -107) {
                        boolean z3 = (value[1] & 255) == 240;
                        boolean z4 = (value[2] & 255) == 240;
                        if (MyBluetoothGatt.this.myApplication.mMyDeviceState != null) {
                            MyBluetoothGatt.this.myApplication.mMyDeviceState.resetAuto(MyBluetoothGatt.this.mAddr, z3, z4);
                        }
                    }
                    if (value.length == 5 && value[0] == 98 && value[4] == -107) {
                        MyBluetoothGatt.this.isOpen = (value[1] & 255) == 240;
                        byte b2 = value[2] & 255;
                        MyBluetoothGatt.this.setMsg(MyBluetoothGatt.this.mAddr, 103);
                    }
                    if (value.length == 7 && value[0] == 119 && value[6] == 102) {
                        MyBluetoothGatt.this.isOpen = (value[1] & 255) == 240;
                        MyBluetoothGatt.this.isAutoClose = (value[2] & 255) == 240;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("44 ");
                        sb4.append(MyBluetoothGatt.this.isOpen);
                        sb4.append(" ");
                        sb4.append(MyBluetoothGatt.this.openNum);
                        sb4.append(" ");
                        sb4.append(MyBluetoothGatt.this.isAutoClose);
                        Log.e("22", sb4.toString());
                        MyBluetoothGatt.this.readDeviceBattry();
                    }
                    if (value.length == 4 && value[0] == 89 && value[3] == -107) {
                        if (value[1] == -16) {
                            MyBluetoothGatt.this.pwdIsTrue = true;
                            MyBluetoothGatt.this.devciepwd = MyBluetoothGatt.this.cache;
                            MyBluetoothGatt.this.getdata();
                            MyBluetoothGatt.this.setMsg(MyBluetoothGatt.this.mAddr, 402);
                            MyBluetoothGatt.this.setAutoOpen(MyBluetoothGatt.this.isdAuto);
                        } else {
                            MyBluetoothGatt.this.pwdIsTrue = false;
                            MyBluetoothGatt.this.setMsg(MyBluetoothGatt.this.mAddr, 401);
                        }
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("--pwdIsTrue =");
                        sb5.append(MyBluetoothGatt.this.pwdIsTrue);
                        Log.e("--", sb5.toString());
                    }
                    if (value.length == 18 && value[0] == -7 && value[17] == -8) {
                        try {
                            byte[] Encrypt = AES2.Encrypt(MyBluetoothGatt.this.srcAES);
                            if (Encrypt != null) {
                                StringBuilder sb6 = new StringBuilder();
                                sb6.append(" mysrc len = ");
                                sb6.append(Encrypt.length);
                                Log.e("", sb6.toString());
                                int i = 0;
                                boolean z5 = true;
                                while (i < Encrypt.length) {
                                    byte b3 = Encrypt[i];
                                    i++;
                                    if (b3 != value[i]) {
                                        z5 = false;
                                    }
                                }
                                if (z5) {
                                    MyBluetoothGatt.this.checkHandler.removeCallbacks(MyBluetoothGatt.this.checkRunnable);
                                    MyBluetoothGatt.this.isMydevice = true;
                                    if (!MyBluetoothGatt.this.pwdIsTrue) {
                                        MyBluetoothGatt.this.setPWD(MyBluetoothGatt.this.getPwd());
                                    }
                                } else {
                                    MyBluetoothGatt.this.isMydevice = false;
                                    MyBluetoothGatt.this.setMsg(MyBluetoothGatt.this.mAddr, 400);
                                    MyBluetoothGatt.this.stopLEService();
                                }
                                StringBuilder sb7 = new StringBuilder();
                                sb7.append("ischeck = ");
                                sb7.append(z5);
                                Log.e("0", sb7.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                byte[] value = bluetoothGattCharacteristic.getValue();
                if (value != null) {
                    String str = " Write ";
                    for (byte b : value) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append(" ");
                        sb.append(Integer.toHexString(b & 255));
                        str = sb.toString();
                    }
                    Log.e(MyBluetoothGatt.tag, str);
                    if (value.length == 10 && (value[0] & 255) == 255) {
                        boolean z = true;
                        if ((value[1] & 255) == 79 && (value[2] & 255) == 80) {
                            boolean z2 = (value[3] & 255) == 69;
                            if ((value[4] & 255) != 78) {
                                z = false;
                            }
                            if (z2 && z) {
                                Date date = new Date();
                                Calendar instance = Calendar.getInstance();
                                instance.setTime(date);
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(instance.getTimeInMillis()));
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(RecordDBTable.TIME, Long.valueOf(instance.getTimeInMillis()));
                                contentValues.put("addr", MyBluetoothGatt.this.mAddr);
                                MyBluetoothGatt.this.mDBAdapter.insert(RecordDBTable.DB_TABLE, contentValues);
                            }
                        }
                    }
                }
                DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID.equals(bluetoothGattCharacteristic.getUuid().toString());
                super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
            }

            public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                if (DeviceUUID.SLIC_BLE_READ_CHARACTERISTICS_BATTERY_UUID.equals(bluetoothGattCharacteristic.getUuid().toString())) {
                    byte[] value = bluetoothGattCharacteristic.getValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append("BATTERY_UUID =");
                    sb.append(value[0]);
                    Log.e("BATTERY_UUID", sb.toString());
                    MyBluetoothGatt.this.setMsg(MyBluetoothGatt.this.mAddr, 3);
                    if (value != null && value.length > 0) {
                        MyBluetoothGatt.this.battery = value[0] & 255;
                        if (MyBluetoothGatt.this.myApplication.mMyDeviceState != null) {
                            MyBluetoothGatt.this.myApplication.mMyDeviceState.getBattery(MyBluetoothGatt.this.battery);
                        }
                    }
                }
            }
        };
        this.mGattCallback = r0;
    }

    public BluetoothGattCallback getmGattCallback() {
        return this.mGattCallback;
    }

    public void setmGattCallback(BluetoothGattCallback bluetoothGattCallback) {
        this.mGattCallback = bluetoothGattCallback;
    }

    public BluetoothGatt getmBluetoothGatt() {
        return this.mBluetoothGatt;
    }

    public BluetoothDevice getmLEdevice() {
        return this.mLEdevice;
    }

    @SuppressLint({"NewApi"})
    public void connectGatt(String str) {
        if (this.bluetoothAdapter != null) {
            this.linktime = new Date().getTime();
            this.mAddr = str;
            this.mLEdevice = this.bluetoothAdapter.getRemoteDevice(str);
            this.mBluetoothGatt = this.mLEdevice.connectGatt(this.context, false, this.mGattCallback);
            this.mConnectionState = 1;
            setMsg(this.mAddr, 0);
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    if (MyBluetoothGatt.this.mConnectionState == 1) {
                        MyBluetoothGatt.this.mConnectionState = 4;
                        MyBluetoothGatt.this.isMydevice = false;
                        MyBluetoothGatt.this.mBluetoothLeService.unlinkBleDevices.put(MyBluetoothGatt.this.mAddr, MyBluetoothGatt.this.mAddr);
                        MyBluetoothGatt.this.stopLEService();
                        MyBluetoothGatt.this.setMsg(MyBluetoothGatt.this.mAddr, 4);
                    }
                }
            }, 16000);
        }
    }

    public void stopLEService() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothLeService.unlinkBleDevices.put(this.mAddr, this.mAddr);
            this.mBluetoothGatt.disconnect();
        }
    }

    public void setNotify() {
        UUID fromString = UUID.fromString("0000ffd0-0000-1000-8000-00805f9b34fb");
        UUID fromString2 = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_DATA_UUID);
        if (this.mBluetoothGatt != null) {
            BluetoothGattService service = this.mBluetoothGatt.getService(fromString);
            if (service != null) {
                this.photoCharacteristic = service.getCharacteristic(fromString2);
                this.mBluetoothGatt.setCharacteristicNotification(this.photoCharacteristic, true);
                BluetoothGattDescriptor descriptor = this.photoCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                this.mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    public void setMsg(String str, int i) {
        Message message = new Message();
        message.what = i;
        Bundle bundle = new Bundle();
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(str);
        bundle.putString("deviceAddr", sb.toString());
        message.setData(bundle);
        if (this.mHandler != null) {
            this.mHandler.sendMessage(message);
        }
    }

    public synchronized boolean writeCharacteristic(String str, String str2, byte[] bArr) {
        UUID fromString = UUID.fromString(str);
        UUID fromString2 = UUID.fromString(str2);
        if (this.mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = this.mBluetoothGatt.getService(fromString);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(fromString2);
        if (characteristic == null) {
            return false;
        }
        characteristic.setValue(bArr);
        return this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public synchronized void writeCharacteristic(String str, String str2, byte[] bArr, int i) {
        UUID fromString = UUID.fromString(str);
        UUID fromString2 = UUID.fromString(str2);
        if (this.mBluetoothGatt != null) {
            BluetoothGattService service = this.mBluetoothGatt.getService(fromString);
            if (service != null) {
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(fromString2);
                if (characteristic != null) {
                    characteristic.setWriteType(2);
                    characteristic.setValue(bArr);
                    new Thread(new Runnable() {
                        public void run() {
                            boolean writeCharacteristic = MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic);
                            while (!writeCharacteristic) {
                                writeCharacteristic = MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic);
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            }
        }
    }

    public void readDeviceBattry() {
        UUID fromString = UUID.fromString(DeviceUUID.SLIC_BLE_READ_SERVICE_BATTERY_UUID);
        UUID fromString2 = UUID.fromString(DeviceUUID.SLIC_BLE_READ_CHARACTERISTICS_BATTERY_UUID);
        if (this.mBluetoothGatt != null) {
            BluetoothGattService service = this.mBluetoothGatt.getService(fromString);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(fromString2);
                if (characteristic != null) {
                    this.mBluetoothGatt.readCharacteristic(characteristic);
                }
            }
        }
    }

    private void updataSrc() {
        Random random = new Random();
        int i = 0;
        while (i < this.srcAES.length) {
            byte nextInt = (byte) ((random.nextInt(90) + 1) & 255);
            this.srcAES[i] = nextInt;
            i++;
            this.sendsrcAES[i] = nextInt;
        }
    }

    /* access modifiers changed from: private */
    public void setAES() {
        try {
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, this.sendsrcAES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open(boolean z, boolean z2) {
        byte[] bArr = {-2, 79, 80, 69, 78, 0, 0, 0, -16, -3};
        byte[] bArr2 = {-2, 67, 76, 79, 83, 69, 0, 0, -16, -3};
        if (!z2) {
            bArr[8] = 0;
            bArr2[8] = 0;
        }
        if (z) { //public static final String CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID = "0000ffd5-0000-1000-8000-00805f9b34fb";
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr);
        } else {
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr2);
        }
    }

    public void setPWD(String str) {
        if (str == null || str.length() != 6) {
            setMsg(this.mAddr, 401);
            return;
        }
        this.cache = str;
        byte[] bArr = {41, 0, 0, 0, 0, 0, 0, 40};
        int i = 0;
        while (i < str.length()) {
            int i2 = i + 1;
            bArr[i2] = Byte.parseByte(str.substring(i, i2));
            i = i2;
        }
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr);
    }

    public void resetPWD(String str, String str2) {
        if (str.length() == 6 && str2.length() == 6) {
            this.cachepwd = str2;
            byte[] bArr = {40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 41};
            int i = 0;
            while (i < 6) {
                int i2 = i + 1;
                bArr[i2] = Byte.parseByte(str.substring(i, i2));
                bArr[i + 7] = Byte.parseByte(str2.substring(i, i2));
                i = i2;
            }
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr);
        }
    }

    public void getdata() {
        Log.e("getdata", "getdata1");
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{102, -16, 0, 119});
        Log.e("getdata", "getdata2");
    }

    public void setParam(boolean z, byte b) {
        if (this.pwdIsTrue) {
            byte[] bArr = {-3, -16, 0, 0, 0, -4};
            bArr[1] = z ? (byte) -16 : 15;
            bArr[2] = b;
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr);
        }
    }

    public void setAutoOpen(boolean z) {
        if (this.pwdIsTrue) {
            byte[] bArr = {-3, -16, 0, 0, 0, -4};
            if (z) {
                bArr[1] = -16;
            } else {
                bArr[1] = 15;
            }
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr);
        }
    }

    public void resetDevicePWD(String str, String str2) {
        if (str.length() == 6 && str2.length() == 6) {
            this.cachedevicepwd = str2;
            byte[] bArr = {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2};
            int i = 0;
            while (i < 6) {
                int i2 = i + 1;
                bArr[i2] = Byte.parseByte(str.substring(i, i2));
                bArr[i + 7] = Byte.parseByte(str2.substring(i, i2));
                i = i2;
            }
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr);
        }
    }
}
