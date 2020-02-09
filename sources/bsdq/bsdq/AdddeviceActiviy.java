package bsdq.bsdq;

import android.bluetooth.BluetoothDevice;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.DeviceTable;
import bsdq.bsdq.db.MyDevice;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class AdddeviceActiviy extends MyBasicActivity {
    public HashMap<String, MyDevice> DBdatas = new HashMap<>();
    /* access modifiers changed from: private */
    public int QRCODE_RETURN = 101;
    public ImageView img_rqcode;
    private Context mContext;
    /* access modifiers changed from: private */
    public DBAdapter mDBAdapter;
    public Handler mHandler = new Handler(new Callback() {
        public boolean handleMessage(Message message) {
            Log.e("--", "--handleMessage--");
            AdddeviceActiviy.this.getData();
            AdddeviceActiviy.this.mMyScanAdapter.setData(AdddeviceActiviy.this.myDevices);
            AdddeviceActiviy.this.mMyScanAdapter.notifyDataSetChanged();
            return false;
        }
    });
    /* access modifiers changed from: private */
    public MyScanAdapter mMyScanAdapter;
    private Resources mResources;
    public MyApplication myApplication;
    public ArrayList<MyDevice> myDevices = new ArrayList<>();
    private ListView scandevice_list;
    public TextView tv_close;
    public TextView tv_refresh;

    public interface AddDevice {
        void add(int i);
    }

    public class DeviceItem {
        public TextView tv_add;
        public TextView tv_mac;
        public TextView tv_name;

        public DeviceItem() {
        }
    }

    public class MyScanAdapter extends BaseAdapter {
        private ArrayList<MyDevice> data = new ArrayList<>();
        /* access modifiers changed from: private */
        public AddDevice mAddDevice;
        private LayoutInflater mInflater;

        public long getItemId(int i) {
            return (long) i;
        }

        public void setData(ArrayList<MyDevice> arrayList) {
            this.data.clear();
            this.data.addAll(arrayList);
        }

        public void setmAddDeviceListener(AddDevice addDevice) {
            this.mAddDevice = addDevice;
        }

        public void addData(MyDevice myDevice) {
            this.data.add(myDevice);
        }

        public MyScanAdapter(Context context) {
            this.mInflater = AdddeviceActiviy.this.getLayoutInflater();
        }

        public int getCount() {
            return this.data.size();
        }

        public Object getItem(int i) {
            return Integer.valueOf(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = this.mInflater.inflate(R.layout.scanlist_item, null);
            MyDevice myDevice = (MyDevice) this.data.get(i);
            DeviceItem deviceItem = new DeviceItem();
            deviceItem.tv_name = (TextView) inflate.findViewById(R.id.tv_name);
            deviceItem.tv_mac = (TextView) inflate.findViewById(R.id.tv_mac);
            deviceItem.tv_add = (TextView) inflate.findViewById(R.id.tv_add);
            deviceItem.tv_add.setTag(Integer.valueOf(i));
            inflate.setTag(deviceItem);
            deviceItem.tv_name.setText(myDevice.name);
            deviceItem.tv_mac.setText(myDevice.addr);
            deviceItem.tv_add.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    int intValue = ((Integer) view.getTag()).intValue();
                    Log.e("99", "11");
                    if (MyScanAdapter.this.mAddDevice != null) {
                        MyScanAdapter.this.mAddDevice.add(intValue);
                    }
                }
            });
            return inflate;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_adddevice);
        super.setTintColor(-16772059);
        this.mContext = this;
        this.mResources = getResources();
        this.myApplication = (MyApplication) getApplication();
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        init();
        setListener();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        if (this.mMyScanAdapter != null) {
            getData();
            this.mMyScanAdapter.setData(this.myDevices);
            this.mMyScanAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    private void init() {
        this.tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        this.tv_close = (TextView) findViewById(R.id.tv_close);
        this.img_rqcode = (ImageView) findViewById(R.id.img_rqcode);
        this.scandevice_list = (ListView) findViewById(R.id.scandevice_list);
        this.mMyScanAdapter = new MyScanAdapter(this.mContext);
        getData();
        this.mMyScanAdapter.setData(this.myDevices);
        this.scandevice_list.setAdapter(this.mMyScanAdapter);
        this.mMyScanAdapter.setmAddDeviceListener(new AddDevice() {
            public void add(int i) {
                if (AdddeviceActiviy.this.myDevices.size() > i) {
                    MyDevice myDevice = (MyDevice) AdddeviceActiviy.this.myDevices.get(i);
                    if (myDevice != null) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", myDevice.name);
                        contentValues.put(DeviceTable.DEVICE_NAME, myDevice.name);
                        contentValues.put("addr", myDevice.addr);
                        contentValues.put(DeviceTable.PWD, "123412");
                        contentValues.put(DeviceTable.MECHANICAL_CODE, "123412");
                        contentValues.put(DeviceTable.AUTO, Boolean.valueOf(false));
                        AdddeviceActiviy.this.mDBAdapter.insert(DeviceTable.DB_TABLE, contentValues);
                        AdddeviceActiviy.this.mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                }
            }
        });
    }

    public void getData() {
        this.DBdatas.clear();
        Cursor queryAllData = this.mDBAdapter.queryAllData(DeviceTable.DB_TABLE);
        while (queryAllData.moveToNext()) {
            String string = queryAllData.getString(queryAllData.getColumnIndex("name"));
            String string2 = queryAllData.getString(queryAllData.getColumnIndex("addr"));
            String string3 = queryAllData.getString(queryAllData.getColumnIndex(DeviceTable.MECHANICAL_CODE));
            String string4 = queryAllData.getString(queryAllData.getColumnIndex(DeviceTable.PWD));
            boolean z = queryAllData.getInt(queryAllData.getColumnIndex(DeviceTable.AUTO)) != 0;
            String string5 = queryAllData.getString(queryAllData.getColumnIndex(DeviceTable.DEVICE_NAME));
            MyDevice myDevice = new MyDevice();
            myDevice.addr = string2;
            myDevice.name = string;
            myDevice.mechanical_code = string3;
            myDevice.pwd = string4;
            myDevice.auto = z;
            myDevice.devicename = string5;
            this.DBdatas.put(myDevice.addr, myDevice);
            StringBuilder sb = new StringBuilder();
            sb.append("name=");
            sb.append(string);
            Log.e("--", sb.toString());
        }
        this.myDevices.clear();
        if (this.myApplication.mBluetoothLeService != null) {
            for (String str : this.myApplication.mBluetoothLeService.mDevices.keySet()) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) this.myApplication.mBluetoothLeService.mDevices.get(str);
                if (bluetoothDevice != null && !this.DBdatas.containsKey(bluetoothDevice.getAddress()) && !this.myApplication.noshowDevices.containsKey(bluetoothDevice.getAddress())) {
                    MyDevice myDevice2 = new MyDevice();
                    myDevice2.name = bluetoothDevice.getName();
                    myDevice2.addr = bluetoothDevice.getAddress();
                    this.myDevices.add(myDevice2);
                }
            }
        }
    }

    private void setListener() {
        this.tv_refresh.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AdddeviceActiviy.this.myApplication.noshowDevices.clear();
                AdddeviceActiviy.this.mHandler.sendEmptyMessage(0);
            }
        });
        this.tv_close.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AdddeviceActiviy.this.finish();
            }
        });
        this.img_rqcode.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AdddeviceActiviy.this.startActivityForResult(new Intent(AdddeviceActiviy.this, CaptureActivity.class), AdddeviceActiviy.this.QRCODE_RETURN);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == this.QRCODE_RETURN && intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String string = extras.getString("result");
                Log.e("--ada", string);
                try {
                    JSONObject jSONObject = new JSONObject(string);
                    MyDevice myDevice = new MyDevice();
                    myDevice.name = jSONObject.getString("K");
                    myDevice.pwd = jSONObject.getString("P");
                    myDevice.addr = jSONObject.getString("T");
                    myDevice.devicename = jSONObject.getString("N");
                    myDevice.mechanical_code = jSONObject.getString("M");
                    if (this.myApplication.isConn(myDevice.addr)) {
                        this.myApplication.setcheck(myDevice.addr, myDevice.pwd);
                    }
                    if (this.myApplication.macDevices.containsKey(myDevice.addr)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", myDevice.name);
                        contentValues.put("addr", myDevice.addr);
                        contentValues.put(DeviceTable.PWD, myDevice.pwd);
                        contentValues.put(DeviceTable.DEVICE_NAME, myDevice.devicename);
                        contentValues.put(DeviceTable.MECHANICAL_CODE, myDevice.mechanical_code);
                        contentValues.put(DeviceTable.AUTO, Boolean.valueOf(false));
                        StringBuilder sb = new StringBuilder();
                        sb.append("");
                        sb.append(myDevice.addr);
                        String[] strArr = {sb.toString()};
                        this.mDBAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues, "addr =?", strArr);
                        this.myApplication.macDevices.put(myDevice.addr, myDevice);
                        return;
                    }
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("name", myDevice.name);
                    contentValues2.put("addr", myDevice.addr);
                    contentValues2.put(DeviceTable.PWD, myDevice.pwd);
                    contentValues2.put(DeviceTable.DEVICE_NAME, myDevice.devicename);
                    contentValues2.put(DeviceTable.MECHANICAL_CODE, myDevice.mechanical_code);
                    contentValues2.put(DeviceTable.AUTO, Boolean.valueOf(false));
                    if (this.mDBAdapter.insert(DeviceTable.DB_TABLE, contentValues2) >= 1) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("");
                        sb2.append(myDevice.addr);
                        String[] strArr2 = {sb2.toString()};
                        this.mDBAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues2, "addr =?", strArr2);
                    }
                    this.myApplication.macDevices.put(myDevice.addr, myDevice);
                } catch (JSONException e) {
                    Toast.makeText(this, this.mResources.getString(R.string.dataerror), 0).show();
                    e.printStackTrace();
                }
            }
        }
    }
}
