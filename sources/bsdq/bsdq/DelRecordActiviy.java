package bsdq.bsdq;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.DeviceTable;
import bsdq.bsdq.db.MyDevice;
import java.util.ArrayList;

public class DelRecordActiviy extends MyBasicActivity {
    private Context mContext;
    private DBAdapter mDBAdapter;
    private MyRecordAdapter mMyRecordAdapter;
    private Resources mResources;
    /* access modifiers changed from: private */
    public MyApplication myApplication;
    private ListView record_list;
    private RelativeLayout rel_back;

    public class MyItem {
        public TextView tv_mac;
        public TextView tv_name;

        public MyItem() {
        }
    }

    public class MyRecordAdapter extends BaseAdapter {
        private ArrayList<MyDevice> data = new ArrayList<>();
        private LayoutInflater mInflater;

        public long getItemId(int i) {
            return (long) i;
        }

        public void setData(ArrayList<MyDevice> arrayList) {
            this.data.clear();
            this.data.addAll(arrayList);
        }

        public void addData(MyDevice myDevice) {
            this.data.add(myDevice);
        }

        public MyRecordAdapter(Context context) {
            this.mInflater = DelRecordActiviy.this.getLayoutInflater();
        }

        public int getCount() {
            return this.data.size();
        }

        public Object getItem(int i) {
            return Integer.valueOf(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = this.mInflater.inflate(R.layout.recordlist_item, null);
            MyDevice myDevice = (MyDevice) DelRecordActiviy.this.myApplication.myDevices.get(i);
            MyItem myItem = new MyItem();
            myItem.tv_name = (TextView) inflate.findViewById(R.id.tv_name);
            myItem.tv_mac = (TextView) inflate.findViewById(R.id.tv_mac);
            if (myDevice != null) {
                myItem.tv_name.setText(myDevice.name);
                myItem.tv_mac.setText(myDevice.addr);
            }
            inflate.setTag(myDevice);
            return inflate;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_del_record);
        super.setTintColor(-16772059);
        this.mContext = this;
        this.myApplication = (MyApplication) getApplication();
        this.mResources = getResources();
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        getData();
        init();
        setListener();
    }

    private void init() {
        this.record_list = (ListView) findViewById(R.id.record_list);
        this.mMyRecordAdapter = new MyRecordAdapter(this.mContext);
        this.mMyRecordAdapter.setData(this.myApplication.myDevices);
        this.record_list.setAdapter(this.mMyRecordAdapter);
        this.rel_back = (RelativeLayout) findViewById(R.id.rel_back);
    }

    private void setListener() {
        this.record_list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                MyDevice myDevice = (MyDevice) view.getTag();
                if (myDevice != null) {
                    Intent intent = new Intent(DelRecordActiviy.this, OpenDataActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", myDevice.name);
                    bundle.putString("addr", myDevice.addr);
                    intent.putExtra("data", bundle);
                    DelRecordActiviy.this.startActivity(intent);
                }
            }
        });
        this.rel_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DelRecordActiviy.this.finish();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        if (this.mMyRecordAdapter == null) {
            this.mMyRecordAdapter.setData(this.myApplication.myDevices);
            this.mMyRecordAdapter.notifyDataSetChanged();
        }
        super.onResume();
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
        }
    }
}
