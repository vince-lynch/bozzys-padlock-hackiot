package bsdq.bsdq;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import bsdq.bsdq.ble.BluetoothLeService;
import bsdq.bsdq.ble.MyBluetoothGatt;
import bsdq.bsdq.db.MyDevice;
import bsdq.bsdq.db.User;
import java.util.ArrayList;
import java.util.Hashtable;

public class MyApplication extends Application {
    public boolean isOpencheckpwd = true;
    public boolean isfrist = true;
    public BluetoothLeService mBluetoothLeService;
    public CheakpwdError mCheakpwdError;
    public MyDeviceState mMyDeviceState;
    public User mUser;
    public Hashtable<String, MyDevice> macDevices = new Hashtable<>();
    public ArrayList<MyDevice> myDevices = new ArrayList<>();
    public Hashtable<String, String> noshowDevices = new Hashtable<>();
    public String pwd1 = "123456";
    public String pwd2 = "123456";
    public int pwdtype = 0;
    public SharedPreferences sett;
    public SharedPreferences setting;

    public interface CheakpwdError {
        void cheakpwdError(String str);

        void connError(String str);
    }

    public interface MyDeviceState {
        void getBattery(int i);

        void getLockstate(String str, boolean z, boolean z2);

        void resetAuto(String str, boolean z, boolean z2);

        void resetDevicePwd(String str, String str2, boolean z);

        void resetPwd(String str, String str2, boolean z);
    }

    public void onCreate() {
        this.sett = getSharedPreferences("bsdq", 0);
        this.isOpencheckpwd = this.sett.getBoolean("isOpencheckpwd", true);
        super.onCreate();
    }

    public boolean isConn(String str) {
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
            if (myBluetoothGatt != null && myBluetoothGatt.mConnectionState == 2) {
                return true;
            }
        }
        return false;
    }

    public boolean ischeakpwd(String str) {
        if (!isConn(str) && this.mCheakpwdError != null) {
            this.mCheakpwdError.connError(str);
        }
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
            if (myBluetoothGatt != null && myBluetoothGatt.mConnectionState == 2 && myBluetoothGatt.pwdIsTrue) {
                return true;
            }
        }
        return false;
    }

    public int getConnectionState(String str) {
        if (this.mBluetoothLeService == null || !this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            return 0;
        }
        MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
        if (myBluetoothGatt == null) {
            return 0;
        }
        return myBluetoothGatt.mConnectionState;
    }

    public void conn(String str, String str2, String str3, boolean z) {
        if (this.mBluetoothLeService != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(" conn addr");
            sb.append(str);
            Log.e("11", sb.toString());
            this.mBluetoothLeService.connBLE(str, str2, str3, z);
        }
    }

    public void disconn(String str) {
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
            if (myBluetoothGatt != null && myBluetoothGatt.mConnectionState == 2) {
                StringBuilder sb = new StringBuilder();
                sb.append(" disconn addr");
                sb.append(str);
                Log.e("11", sb.toString());
                myBluetoothGatt.stopLEService();
            }
        }
    }

    public int getBattery(String str) {
        if (isConn(str)) {
            return ((MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str)).battery;
        }
        return 0;
    }

    public void open(String str, boolean z, boolean z2) {
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
            if (myBluetoothGatt != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.open(z, z2);
            }
        }
    }

    public void resetPwd(String str, String str2) {
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
            if (myBluetoothGatt != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.resetPWD(myBluetoothGatt.getPwd(), str2);
            }
        }
    }

    public void resetdevciePwd(String str, String str2) {
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
            if (myBluetoothGatt != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.resetDevicePWD(myBluetoothGatt.getMachinePwd(), str2);
            }
        }
    }

    public void setParam(String str, boolean z, byte b) {
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
            if (myBluetoothGatt != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.setParam(z, b);
            }
        }
    }

    public void setcheck(String str, String str2) {
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) this.mBluetoothLeService.MyBluetoothGatts.get(str);
            if (myBluetoothGatt != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.setPWD(str2);
            }
        }
    }
}
