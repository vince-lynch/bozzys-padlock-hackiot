package com.consmart.ble;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import bsdq.bsdq.ble.DeviceUUID;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

@TargetApi(18)
public class MyBluetoothGatt {
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final int INT_PHOTOGRAPH = 2;
    public static final int SREVICE_UPDATA = 5;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public BluetoothAdapter bluetoothAdapter;
    /* access modifiers changed from: private */
    public byte[] checkdata = {1, 1, 1, 1};
    public Context context;
    /* access modifiers changed from: private */
    public boolean isCheck = false;
    public BleController mBleController;
    /* access modifiers changed from: private */
    public BluetoothGatt mBluetoothGatt;
    public int mConnectionState = 0;
    private BluetoothGattCallback mGattCallback;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private BluetoothDevice mLEdevice;
    public MyBluetoothGatt mMyBluetoothGatt;
    public MyBluetoothGattCallback mMyBluetoothGattCallback;
    public Resources mResources;

    public void setMyBluetoothGattCallback(MyBluetoothGattCallback myBluetoothGattCallback) {
        this.mMyBluetoothGattCallback = myBluetoothGattCallback;
    }

    public MyBluetoothGatt(Context context2, BluetoothAdapter bluetoothAdapter2) {
        this.context = context2;
        this.bluetoothAdapter = bluetoothAdapter2;
        this.mMyBluetoothGatt = this;
        this.mBleController = BleController.initialization(this.context);
        this.mHandler = new Handler();
        this.mGattCallback = new BluetoothGattCallback() {
            public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
                MyBluetoothGatt.this.mConnectionState = i;
                if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                    MyBluetoothGatt.this.mMyBluetoothGattCallback.onConnectionStateChange(MyBluetoothGatt.this.mMyBluetoothGatt, i, i2);
                }
                if (i2 == 2) {
                    bluetoothGatt.discoverServices();
                }
                if (i2 == 0) {
                    MyBluetoothGatt.this.mBleController.removeMyBluetoothGatt(bluetoothGatt.getDevice().getAddress());
                }
            }

            public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
                if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                    MyBluetoothGatt.this.mMyBluetoothGattCallback.onServicesDiscovered(MyBluetoothGatt.this.mMyBluetoothGatt, i);
                }
                if (bluetoothGatt != null) {
                    MyBluetoothGatt.this.mBluetoothGatt = bluetoothGatt;
                }
                if (i == 0) {
                    MyBluetoothGatt.this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            String str = DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_MUSICCHECK_UUID;
                            Log.e("", "--->>设置验证");
                            MyBluetoothGatt.this.setCharacteristicNotify("0000fff0-0000-1000-8000-00805f9b34fb", str);
                        }
                    }, 100);
                }
            }

            public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
                if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                    MyBluetoothGatt.this.mMyBluetoothGattCallback.onDescriptorWrite(MyBluetoothGatt.this.mMyBluetoothGatt, bluetoothGattDescriptor, i);
                }
                StringBuilder sb = new StringBuilder("descriptor -");
                sb.append(bluetoothGattDescriptor.getCharacteristic().getUuid().toString());
                Log.e("", sb.toString());
                if (i == 0 && DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_MUSICCHECK_UUID.equalsIgnoreCase(bluetoothGattDescriptor.getCharacteristic().getUuid().toString())) {
                    MyBluetoothGatt.this.check();
                }
                super.onDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i);
            }

            public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                    MyBluetoothGatt.this.mMyBluetoothGattCallback.onCharacteristicChanged(MyBluetoothGatt.this.mMyBluetoothGatt, bluetoothGattCharacteristic);
                }
                if (DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_MUSICCHECK_UUID.equalsIgnoreCase(bluetoothGattCharacteristic.getUuid().toString())) {
                    byte[] value = bluetoothGattCharacteristic.getValue();
                    if (value != null && value.length == 18 && 102 == (value[0] & 255) && 187 == (value[17] & 255) && value[1] == MyBluetoothGatt.this.checkdata[0] && value[2] == MyBluetoothGatt.this.checkdata[1] && value[3] == MyBluetoothGatt.this.checkdata[2] && value[4] == MyBluetoothGatt.this.checkdata[3]) {
                        MyBluetoothGatt.this.isCheck = true;
                        Log.e("", "验证成功！");
                        if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                            MyBluetoothGatt.this.mMyBluetoothGattCallback.oncheck();
                        }
                    }
                }
            }

            public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                    MyBluetoothGatt.this.mMyBluetoothGattCallback.onCharacteristicWrite(MyBluetoothGatt.this.mMyBluetoothGatt, bluetoothGattCharacteristic, i);
                }
                super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
            }

            public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                    MyBluetoothGatt.this.mMyBluetoothGattCallback.onCharacteristicRead(MyBluetoothGatt.this.mMyBluetoothGatt, bluetoothGattCharacteristic, i);
                }
            }

            public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
                if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                    MyBluetoothGatt.this.mMyBluetoothGattCallback.onReadRemoteRssi(MyBluetoothGatt.this.mMyBluetoothGatt, i, i2);
                }
            }
        };
    }

    private BluetoothGattCallback getmGattCallback() {
        return this.mGattCallback;
    }

    public BluetoothDevice getmLEdevice() {
        return this.mLEdevice;
    }

    @SuppressLint({"NewApi"})
    public void connectGatt(String str) {
        if (this.bluetoothAdapter != null) {
            this.mLEdevice = this.bluetoothAdapter.getRemoteDevice(str);
            this.mBluetoothGatt = this.mLEdevice.connectGatt(this.context, false, this.mGattCallback);
        }
    }

    public void stopLEService() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        }
    }

    public ArrayList<BluetoothGattService> getBluetoothGattService() {
        return this.mBluetoothGatt != null ? (ArrayList) this.mBluetoothGatt.getServices() : new ArrayList<>();
    }

    public BluetoothGattService getService(UUID uuid) {
        if (this.mBluetoothGatt != null) {
            return this.mBluetoothGatt.getService(uuid);
        }
        return null;
    }

    public boolean readRSSI() {
        if (this.mBluetoothGatt == null) {
            return false;
        }
        return this.mBluetoothGatt.readRemoteRssi();
    }

    private void setMsg(String str, int i) {
        Message message = new Message();
        message.what = i;
        Bundle bundle = new Bundle();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        bundle.putString("deviceAddr", sb.toString());
        message.setData(bundle);
        this.mHandler.sendMessage(message);
    }

    public synchronized boolean writeCharacteristic(String str, String str2, byte[] bArr) {
        if (!this.isCheck) {
            return false;
        }
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
        characteristic.setWriteType(2);
        characteristic.setValue(bArr);
        return this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private synchronized boolean writeCharacteristic(String str, String str2, byte[] bArr, boolean z) {
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
        characteristic.setWriteType(2);
        characteristic.setValue(bArr);
        return this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public synchronized boolean readCharacteristic(String str, String str2) {
        if (!this.isCheck) {
            return false;
        }
        if (this.mBluetoothGatt == null) {
            return false;
        }
        UUID fromString = UUID.fromString(str);
        UUID fromString2 = UUID.fromString(str2);
        BluetoothGattService service = this.mBluetoothGatt.getService(fromString);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(fromString2);
        if (characteristic == null) {
            return false;
        }
        return this.mBluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean setCharacteristicNotify(String str, String str2, boolean z) {
        if (!this.isCheck) {
            return false;
        }
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
        this.mBluetoothGatt.setCharacteristicNotification(characteristic, z);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return this.mBluetoothGatt.writeDescriptor(descriptor);
    }

    /* access modifiers changed from: private */
    public boolean setCharacteristicNotify(String str, String str2) {
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
        this.mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor == null) {
            return false;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return this.mBluetoothGatt.writeDescriptor(descriptor);
    }

    /* access modifiers changed from: private */
    public void check() {
        Log.e("", "验证.....");
        Random random = new Random();
        byte[] bArr = {-86, -81, -86, 70, -21, 28, -21, 15, -7, 68, 73, 118, 53, -42, 123, 64, 4, 85};
        for (int i = 0; i < this.checkdata.length; i++) {
            int nextInt = random.nextInt(200);
            if (nextInt == 0) {
                nextInt = 1;
            }
            this.checkdata[i] = (byte) (nextInt & 255);
        }
        try {
            byte[] Encrypt = AES.Encrypt(this.checkdata);
            int i2 = 0;
            while (i2 < Encrypt.length) {
                int i3 = i2 + 1;
                bArr[i3] = Encrypt[i2];
                i2 = i3;
            }
            writeCharacteristic("0000fff0-0000-1000-8000-00805f9b34fb", "0000fff5-0000-1000-8000-00805f9b34fb", bArr, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
