package bsdq.bsdq;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PointerIconCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.MyApplication.CheakpwdError;
import bsdq.bsdq.ble.BluetoothLeService;
import bsdq.bsdq.ble.BluetoothLeService.LocalBinder;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.DeviceTable;
import bsdq.bsdq.db.MyDevice;
import bsdq.bsdq.db.RecordDBTable;
import bsdq.bsdq.ui.MyDialog1;
import bsdq.bsdq.ui.MyDialogcheak;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DeviceActivity extends MyBasicActivity {
    public Handler bleHandler = new Handler(new Callback() {
        public boolean handleMessage(Message message) {
            StringBuilder sb = new StringBuilder();
            sb.append("what= ");
            sb.append(message.what);
            Log.e("Message", sb.toString());
            switch (message.what) {
                case 0:
                    DeviceActivity.this.mMyAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    DeviceActivity.this.mMyAdapter.notifyDataSetChanged();
                    break;
                case 4:
                    DeviceActivity.this.mMyAdapter.notifyDataSetChanged();
                    break;
                case 100:
                    Bundle data = message.getData();
                    if (data != null) {
                        String string = data.getString("addr");
                        Toast.makeText(DeviceActivity.this.mContext, DeviceActivity.this.mResources.getString(R.string.error6), 0).show();
                        MyDialogcheak myDialogcheak = new MyDialogcheak(DeviceActivity.this.mContext, R.style.MyDialog, string);
                        myDialogcheak.setMsgHandle(DeviceActivity.this.bleHandler);
                        myDialogcheak.show();
                        break;
                    }
                    break;
                case 101:
                    Bundle data2 = message.getData();
                    if (data2 != null) {
                        data2.getString("addr");
                        Toast.makeText(DeviceActivity.this.mContext, DeviceActivity.this.mResources.getString(R.string.error7), 0).show();
                        break;
                    }
                    break;
                case 103:
                    Log.e("103", "103");
                    Bundle data3 = message.getData();
                    if (data3 != null) {
                        String string2 = data3.getString("deviceAddr");
                        Date date = new Date();
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(date);
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(instance.getTimeInMillis()));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(RecordDBTable.TIME, Long.valueOf(instance.getTimeInMillis()));
                        contentValues.put("addr", string2);
                        DeviceActivity.this.mDBAdapter.insert(RecordDBTable.DB_TABLE, contentValues);
                        break;
                    }
                    break;
                case 401:
                    Toast.makeText(DeviceActivity.this.mContext, DeviceActivity.this.mResources.getString(R.string.pwd_false), 0).show();
                    break;
                case 402:
                    Toast.makeText(DeviceActivity.this.mContext, DeviceActivity.this.mResources.getString(R.string.pwd_true), 0).show();
                    break;
                case PointerIconCompat.TYPE_CONTEXT_MENU /*1001*/:
                    DeviceActivity.this.getData();
                    if (DeviceActivity.this.mMyAdapter != null) {
                        DeviceActivity.this.mMyAdapter.setData(DeviceActivity.this.myApplication.myDevices);
                        DeviceActivity.this.mMyAdapter.notifyDataSetChanged();
                        break;
                    }
                    break;
                case 4011:
                    Bundle data4 = message.getData();
                    String string3 = data4.getString("addr");
                    boolean z = data4.getBoolean("remember");
                    String string4 = data4.getString(DeviceTable.PWD);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("");
                    sb2.append(string3);
                    sb2.append(" ");
                    sb2.append(z);
                    sb2.append(" ");
                    sb2.append(string4);
                    Log.e(" 1 ", sb2.toString());
                    if (z) {
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(DeviceTable.PWD, string4);
                        String[] strArr = {string3};
                        DeviceActivity.this.mDBAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues2, "addr =? ", strArr);
                    }
                    DeviceActivity.this.myApplication.setcheck(string3, string4);
                    break;
            }
            return false;
        }
    });
    private ListView device_list;
    private BluetoothAdapter mBluetoothAdapter;
    /* access modifiers changed from: private */
    public BluetoothLeService mBluetoothLeService;
    public CheakpwdError mCheakpwdError = new CheakpwdError() {
        public void cheakpwdError(String str) {
            Message message = new Message();
            message.what = 100;
            Bundle bundle = new Bundle();
            bundle.putString("addr", str);
            message.setData(bundle);
            DeviceActivity.this.bleHandler.sendMessage(message);
        }

        public void connError(String str) {
            Message message = new Message();
            message.what = 101;
            Bundle bundle = new Bundle();
            bundle.putString("addr", str);
            message.setData(bundle);
            DeviceActivity.this.bleHandler.sendMessage(message);
        }
    };
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public DBAdapter mDBAdapter;
    /* access modifiers changed from: private */
    public MyAdapter mMyAdapter;
    public Resources mResources;
    public MyApplication myApplication;
    private ServiceConnection sc;
    private TextView tv_add;

    public class DevcieItem {
        public String addr = "";
        public ImageView img_battery;
        public ImageView img_conn;
        public ProgressBar progressBar;
        public TextView tv_mac;
        public TextView tv_manage;
        public TextView tv_name;
        public TextView tv_open;

        public DevcieItem() {
        }
    }

    public class MyAdapter extends BaseAdapter {
        private ArrayList<MyDevice> mArrayList = new ArrayList<>();
        private LayoutInflater mInflater;

        public long getItemId(int i) {
            return 0;
        }

        public MyAdapter(Context context) {
            this.mInflater = DeviceActivity.this.getLayoutInflater();
        }

        public void setData(ArrayList<MyDevice> arrayList) {
            this.mArrayList.clear();
            this.mArrayList.addAll(arrayList);
        }

        public void addData(MyDevice myDevice) {
            this.mArrayList.add(myDevice);
        }

        public int getCount() {
            return this.mArrayList.size();
        }

        public Object getItem(int i) {
            return Integer.valueOf(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = this.mInflater.inflate(R.layout.list_item, null);
            MyDevice myDevice = (MyDevice) this.mArrayList.get(i);
            DevcieItem devcieItem = new DevcieItem();
            devcieItem.img_battery = (ImageView) inflate.findViewById(R.id.img_battery);
            devcieItem.tv_name = (TextView) inflate.findViewById(R.id.tv_name);
            devcieItem.tv_mac = (TextView) inflate.findViewById(R.id.tv_mac);
            devcieItem.img_conn = (ImageView) inflate.findViewById(R.id.img_conn);
            devcieItem.progressBar = (ProgressBar) inflate.findViewById(R.id.progressBar);
            devcieItem.tv_open = (TextView) inflate.findViewById(R.id.tv_open);
            if (myDevice.auto) {
                devcieItem.tv_open.setText(DeviceActivity.this.mResources.getString(R.string.auto));
            } else {
                devcieItem.tv_open.setText(DeviceActivity.this.mResources.getString(R.string.open));
            }
            devcieItem.tv_manage = (TextView) inflate.findViewById(R.id.tv_manage);
            devcieItem.tv_manage.setOnClickListener(new MyOnClickListener().setType(i));
            if (myDevice != null) {
                devcieItem.tv_name.setText(myDevice.name);
                devcieItem.tv_mac.setText(myDevice.addr);
                devcieItem.addr = myDevice.addr;
                if (DeviceActivity.this.myApplication.mBluetoothLeService == null || !DeviceActivity.this.myApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(myDevice.addr)) {
                    devcieItem.img_conn.setVisibility(0);
                    devcieItem.img_conn.setImageResource(R.mipmap.ic_disconn);
                    devcieItem.progressBar.setVisibility(4);
                    devcieItem.img_battery.setImageResource(R.mipmap.ic_battery);
                } else {
                    int connectionState = DeviceActivity.this.myApplication.getConnectionState(myDevice.addr);
                    if (DeviceActivity.this.myApplication.isConn(myDevice.addr)) {
                        int battery = DeviceActivity.this.myApplication.getBattery(myDevice.addr);
                        if (battery > 75) {
                            devcieItem.img_battery.setImageResource(R.mipmap.ic_battery4);
                        } else if (battery > 50) {
                            devcieItem.img_battery.setImageResource(R.mipmap.ic_battery3);
                        } else if (battery > 25) {
                            devcieItem.img_battery.setImageResource(R.mipmap.ic_battery2);
                        } else if (battery > 0) {
                            devcieItem.img_battery.setImageResource(R.mipmap.ic_battery1);
                        } else {
                            devcieItem.img_battery.setImageResource(R.mipmap.ic_battery);
                        }
                    } else {
                        devcieItem.img_conn.setVisibility(0);
                        devcieItem.img_conn.setImageResource(R.mipmap.ic_disconn);
                        devcieItem.progressBar.setVisibility(4);
                        devcieItem.img_battery.setImageResource(R.mipmap.ic_battery);
                    }
                    if (connectionState == 0) {
                        devcieItem.img_conn.setVisibility(0);
                        devcieItem.img_conn.setImageResource(R.mipmap.ic_disconn);
                        devcieItem.progressBar.setVisibility(4);
                    } else if (connectionState == 2) {
                        devcieItem.img_conn.setVisibility(0);
                        devcieItem.img_conn.setImageResource(R.mipmap.ic_conn);
                        devcieItem.progressBar.setVisibility(4);
                    } else if (connectionState == 1) {
                        devcieItem.img_conn.setVisibility(4);
                        devcieItem.progressBar.setVisibility(0);
                    }
                }
            }
            devcieItem.tv_open.setTag(Integer.valueOf(i));
            devcieItem.img_conn.setTag(Integer.valueOf(i));
            devcieItem.img_conn.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    int intValue = ((Integer) view.getTag()).intValue();
                    if (DeviceActivity.this.myApplication.myDevices.size() > intValue) {
                        MyDevice myDevice = (MyDevice) DeviceActivity.this.myApplication.myDevices.get(intValue);
                        if (myDevice != null) {
                            if (DeviceActivity.this.myApplication.macDevices.containsKey(myDevice.addr)) {
                                myDevice = (MyDevice) DeviceActivity.this.myApplication.macDevices.get(myDevice.addr);
                            }
                            if (DeviceActivity.this.myApplication.isConn(myDevice.addr)) {
                                DeviceActivity.this.myApplication.disconn(myDevice.addr);
                                return;
                            }
                            StringBuilder sb = new StringBuilder();
                            sb.append("pwd = ");
                            sb.append(myDevice.pwd);
                            Log.e("-", sb.toString());
                            DeviceActivity.this.myApplication.conn(myDevice.addr, myDevice.pwd, myDevice.mechanical_code, myDevice.auto);
                        }
                    }
                }
            });
            devcieItem.tv_open.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Log.e("onClick", "tv_open ");
                    int intValue = ((Integer) view.getTag()).intValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append("tv_open ");
                    sb.append(intValue);
                    Log.e("onClick", sb.toString());
                    if (DeviceActivity.this.myApplication.myDevices.size() > intValue) {
                        MyDevice myDevice = (MyDevice) DeviceActivity.this.myApplication.myDevices.get(intValue);
                        if (myDevice != null) {
                            if (DeviceActivity.this.myApplication.macDevices.containsKey(myDevice.addr)) {
                                myDevice = (MyDevice) DeviceActivity.this.myApplication.macDevices.get(myDevice.addr);
                            }
                            if (DeviceActivity.this.myApplication.isConn(myDevice.addr)) {
                                if (DeviceActivity.this.myApplication.ischeakpwd(myDevice.addr)) {
                                    if (!myDevice.auto) {
                                        DeviceActivity.this.myApplication.open(myDevice.addr, true, false);
                                    }
                                } else if (DeviceActivity.this.myApplication.mCheakpwdError != null) {
                                    DeviceActivity.this.myApplication.mCheakpwdError.cheakpwdError(myDevice.addr);
                                }
                            } else if (DeviceActivity.this.myApplication.mCheakpwdError != null) {
                                DeviceActivity.this.myApplication.mCheakpwdError.connError(myDevice.addr);
                            }
                        }
                    }
                }
            });
            inflate.setTag(devcieItem);
            return inflate;
        }
    }

    public class MyOnClickListener implements OnClickListener {
        private int position = 0;

        public MyOnClickListener() {
        }

        public MyOnClickListener setType(int i) {
            this.position = i;
            return this;
        }

        public void onClick(View view) {
            StringBuilder sb = new StringBuilder();
            sb.append("tv_open ");
            sb.append(this.position);
            Log.e("onClick", sb.toString());
            if (DeviceActivity.this.myApplication.myDevices.size() > this.position) {
                MyDevice myDevice = (MyDevice) DeviceActivity.this.myApplication.myDevices.get(this.position);
                if (myDevice != null) {
                    if (DeviceActivity.this.myApplication.macDevices.containsKey(myDevice.addr)) {
                        myDevice = (MyDevice) DeviceActivity.this.myApplication.macDevices.get(myDevice.addr);
                    }
                    if (DeviceActivity.this.myApplication.isConn(myDevice.addr)) {
                        if (DeviceActivity.this.myApplication.ischeakpwd(myDevice.addr)) {
                            Intent intent = new Intent(DeviceActivity.this, ManageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("pos", this.position);
                            intent.putExtra("data", bundle);
                            DeviceActivity.this.startActivity(intent);
                        } else if (DeviceActivity.this.myApplication.mCheakpwdError != null) {
                            DeviceActivity.this.myApplication.mCheakpwdError.cheakpwdError(myDevice.addr);
                        }
                    } else if (DeviceActivity.this.myApplication.mCheakpwdError != null) {
                        DeviceActivity.this.myApplication.mCheakpwdError.connError(myDevice.addr);
                    }
                } else {
                    Log.e("onClick", "mMyDevice == null ");
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_device);
        this.mContext = this;
        super.setTintColor(-16772059);
        this.myApplication = (MyApplication) getApplication();
        this.myApplication.mCheakpwdError = this.mCheakpwdError;
        this.mResources = getResources();
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        init();
        setListener();
        initialize();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        getData();
        if (this.mMyAdapter != null) {
            this.mMyAdapter.setData(this.myApplication.myDevices);
            this.mMyAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (4 != i) {
            return super.onKeyDown(i, keyEvent);
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(268435456);
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
        return true;
    }

    public void getData() {
        this.myApplication.myDevices.clear();
        Cursor queryAllData = this.mDBAdapter.queryAllData(DeviceTable.DB_TABLE);
        while (queryAllData.moveToNext()) {
            String string = queryAllData.getString(queryAllData.getColumnIndex("name"));
            String string2 = queryAllData.getString(queryAllData.getColumnIndex("addr"));
            String string3 = queryAllData.getString(queryAllData.getColumnIndex(DeviceTable.MECHANICAL_CODE));
            String string4 = queryAllData.getString(queryAllData.getColumnIndex(DeviceTable.PWD));
            String string5 = queryAllData.getString(queryAllData.getColumnIndex(DeviceTable.DEVICE_NAME));
            boolean z = queryAllData.getInt(queryAllData.getColumnIndex(DeviceTable.AUTO)) != 0;
            MyDevice myDevice = new MyDevice();
            myDevice.addr = string2;
            myDevice.name = string;
            myDevice.mechanical_code = string3;
            myDevice.pwd = string4;
            myDevice.auto = z;
            myDevice.devicename = string5;
            this.myApplication.myDevices.add(myDevice);
            this.myApplication.macDevices.put(string2, myDevice);
            StringBuilder sb = new StringBuilder();
            sb.append("name=");
            sb.append(string);
            sb.append(" ");
            sb.append(string2);
            Log.e("--", sb.toString());
        }
    }

    private void init() {
        this.tv_add = (TextView) findViewById(R.id.tv_add);
        this.device_list = (ListView) findViewById(R.id.device_list);
        this.mMyAdapter = new MyAdapter(this.mContext);
        this.mMyAdapter.setData(this.myApplication.myDevices);
        this.device_list.setAdapter(this.mMyAdapter);
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
                DeviceActivity.this.mBluetoothLeService = ((LocalBinder) iBinder).getService();
                if (DeviceActivity.this.mBluetoothLeService != null) {
                    DeviceActivity.this.myApplication.mBluetoothLeService = DeviceActivity.this.mBluetoothLeService;
                    DeviceActivity.this.mBluetoothLeService.scanLeDevice(true);
                    DeviceActivity.this.mBluetoothLeService.setOperateHandler(DeviceActivity.this.bleHandler);
                }
            }
        };
        getApplicationContext().bindService(new Intent(getApplicationContext(), BluetoothLeService.class), this.sc, 1);
    }

    private void setListener() {
        this.tv_add.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DeviceActivity.this.startActivity(new Intent(DeviceActivity.this, AdddeviceActiviy.class));
            }
        });
        this.device_list.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                DeviceActivity.this.showNormalDialog(((DevcieItem) view.getTag()).addr);
                return false;
            }
        });
    }

    /* access modifiers changed from: private */
    public void showNormalDialog(final String str) {
        Log.e("--", "showNormalDialog");
        final MyDialog1 myDialog1 = new MyDialog1(this.mContext, R.style.MyDialog, R.layout.activity_pop7);
        myDialog1.setShowMsg(this.mResources.getString(R.string.del_device), this.mResources.getString(R.string.sure_deldevice));
        myDialog1.setRefreshListener(new OnClickListener() {
            public void onClick(View view) {
                DeviceActivity.this.myApplication.disconn(str);
                DeviceActivity.this.myApplication.macDevices.remove(str);
                DeviceActivity.this.myApplication.mBluetoothLeService.mDevices.remove(str);
                DeviceActivity.this.myApplication.noshowDevices.put(str, str);
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(str);
                String[] strArr = {sb.toString()};
                DeviceActivity.this.mDBAdapter.deleteOneData(DeviceTable.DB_TABLE, "addr =? ", strArr);
                DeviceActivity.this.bleHandler.sendEmptyMessageDelayed(PointerIconCompat.TYPE_CONTEXT_MENU, 500);
                myDialog1.mydismiss();
            }
        });
        myDialog1.show();
    }
}
