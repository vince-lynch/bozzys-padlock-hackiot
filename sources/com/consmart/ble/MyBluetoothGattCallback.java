package com.consmart.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public interface MyBluetoothGattCallback {
    void onCharacteristicChanged(MyBluetoothGatt myBluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic);

    void onCharacteristicRead(MyBluetoothGatt myBluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i);

    void onCharacteristicWrite(MyBluetoothGatt myBluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i);

    void onConnectionStateChange(MyBluetoothGatt myBluetoothGatt, int i, int i2);

    void onDescriptorWrite(MyBluetoothGatt myBluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i);

    void onReadRemoteRssi(MyBluetoothGatt myBluetoothGatt, int i, int i2);

    void onServicesDiscovered(MyBluetoothGatt myBluetoothGatt, int i);

    void oncheck();
}
