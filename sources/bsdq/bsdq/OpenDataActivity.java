package bsdq.bsdq;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
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
import android.widget.TextView;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.RecordDBTable;
import bsdq.bsdq.db.Time;
import bsdq.bsdq.ui.MyDialog1;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OpenDataActivity extends MyBasicActivity {
    /* access modifiers changed from: private */
    public String addr = "";
    private ListView device_list;
    private ImageView img_back;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public DBAdapter mDBAdapter;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler(new Callback() {
        public boolean handleMessage(Message message) {
            OpenDataActivity.this.upDatalist();
            return false;
        }
    });
    private ArrayList<Time> mListStr = new ArrayList<>();
    private MyAdapter mMyAdapter;
    /* access modifiers changed from: private */
    public Resources mResources;
    private String name = "";
    private TextView tv_clear;

    public class MyAdapter extends BaseAdapter {
        private ArrayList<Time> mArrayList = new ArrayList<>();
        private LayoutInflater mInflater;

        public long getItemId(int i) {
            return 0;
        }

        public MyAdapter(Context context) {
            this.mInflater = OpenDataActivity.this.getLayoutInflater();
        }

        public void setData(ArrayList<Time> arrayList) {
            this.mArrayList.clear();
            this.mArrayList.addAll(arrayList);
        }

        public void addData(Time time) {
            this.mArrayList.add(time);
        }

        public int getCount() {
            return this.mArrayList.size();
        }

        public Object getItem(int i) {
            return Integer.valueOf(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = this.mInflater.inflate(R.layout.opendata_item, null);
            TextView textView = (TextView) inflate.findViewById(R.id.tv_time);
            Time time = (Time) this.mArrayList.get(i);
            if (time != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(OpenDataActivity.this.mResources.getString(R.string.time));
                sb.append(" ");
                sb.append(time.stime);
                textView.setText(sb.toString());
            }
            inflate.setTag(time);
            return inflate;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_opendata);
        this.mContext = this;
        this.mResources = getResources();
        super.setTintColor(-16772059);
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        Bundle bundleExtra = getIntent().getBundleExtra("data");
        if (bundleExtra != null) {
            this.addr = bundleExtra.getString("addr");
            this.name = bundleExtra.getString("name");
        }
        getData();
        init();
        setListener();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    private void init() {
        this.img_back = (ImageView) findViewById(R.id.img_back);
        this.tv_clear = (TextView) findViewById(R.id.tv_clear);
        this.device_list = (ListView) findViewById(R.id.data_list);
        this.mMyAdapter = new MyAdapter(this.mContext);
        this.mMyAdapter.setData(this.mListStr);
        this.device_list.setAdapter(this.mMyAdapter);
    }

    public void upDatalist() {
        getData();
        if (this.mMyAdapter != null) {
            this.mMyAdapter.setData(this.mListStr);
            this.mMyAdapter.notifyDataSetChanged();
        }
    }

    private void setListener() {
        this.img_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                OpenDataActivity.this.finish();
            }
        });
        this.tv_clear.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                final MyDialog1 myDialog1 = new MyDialog1(OpenDataActivity.this.mContext, R.style.MyDialog, R.layout.activity_pop7);
                myDialog1.setShowMsg(OpenDataActivity.this.mResources.getString(R.string.del_record), OpenDataActivity.this.mResources.getString(R.string.sure_delall_record));
                myDialog1.setRefreshListener(new OnClickListener() {
                    public void onClick(View view) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("");
                        sb.append(OpenDataActivity.this.addr);
                        String[] strArr = {sb.toString()};
                        long deleteOneData = OpenDataActivity.this.mDBAdapter.deleteOneData(RecordDBTable.DB_TABLE, "addr =? ", strArr);
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("i=");
                        sb2.append(deleteOneData);
                        Log.e("i", sb2.toString());
                        OpenDataActivity.this.mHandler.sendEmptyMessage(0);
                        myDialog1.mydismiss();
                    }
                });
                myDialog1.show();
            }
        });
        this.device_list.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                final Time time = (Time) view.getTag();
                if (time != null) {
                    final MyDialog1 myDialog1 = new MyDialog1(OpenDataActivity.this.mContext, R.style.MyDialog, R.layout.activity_pop7);
                    myDialog1.setShowMsg(OpenDataActivity.this.mResources.getString(R.string.del_record), OpenDataActivity.this.mResources.getString(R.string.sure_del_record));
                    myDialog1.setRefreshListener(new OnClickListener() {
                        public void onClick(View view) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("");
                            sb.append(time.id);
                            String[] strArr = {sb.toString()};
                            long deleteOneData = OpenDataActivity.this.mDBAdapter.deleteOneData(RecordDBTable.DB_TABLE, "_id =? ", strArr);
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("i=");
                            sb2.append(deleteOneData);
                            Log.e("i", sb2.toString());
                            OpenDataActivity.this.mHandler.sendEmptyMessage(0);
                            myDialog1.mydismiss();
                        }
                    });
                    myDialog1.show();
                }
                return false;
            }
        });
    }

    public void getData() {
        this.mListStr.clear();
        Cursor queryAllDataOrderbyDesc = this.mDBAdapter.queryAllDataOrderbyDesc(this.addr);
        while (queryAllDataOrderbyDesc.moveToNext()) {
            long j = queryAllDataOrderbyDesc.getLong(queryAllDataOrderbyDesc.getColumnIndex(RecordDBTable.TIME));
            int i = queryAllDataOrderbyDesc.getInt(queryAllDataOrderbyDesc.getColumnIndex("_id"));
            String string = queryAllDataOrderbyDesc.getString(queryAllDataOrderbyDesc.getColumnIndex("addr"));
            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(j));
            StringBuilder sb = new StringBuilder();
            sb.append("time=");
            sb.append(format);
            Log.e("--", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("id=");
            sb2.append(i);
            Log.e("--", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("addr=");
            sb3.append(string);
            Log.e("--", sb3.toString());
            Time time = new Time(i, string, j, format);
            this.mListStr.add(time);
        }
    }
}
