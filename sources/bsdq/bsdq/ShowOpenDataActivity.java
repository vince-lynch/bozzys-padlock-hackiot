package bsdq.bsdq;

import android.content.ContentValues;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.RecordDBTable;
import bsdq.bsdq.db.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowOpenDataActivity extends MyBasicActivity {
    private String addr = "";
    private ListView device_list;
    private ImageView img_back;
    private Context mContext;
    /* access modifiers changed from: private */
    public DBAdapter mDBAdapter;
    private Handler mHandler = new Handler(new Callback() {
        public boolean handleMessage(Message message) {
            ShowOpenDataActivity.this.upDatalist();
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
            this.mInflater = ShowOpenDataActivity.this.getLayoutInflater();
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
                sb.append(ShowOpenDataActivity.this.mResources.getString(R.string.time));
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
        this.tv_clear.setVisibility(8);
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
                ShowOpenDataActivity.this.finish();
            }
        });
        this.tv_clear.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Date date = new Date();
                Calendar instance = Calendar.getInstance();
                instance.setTime(date);
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(instance.getTimeInMillis()));
                ContentValues contentValues = new ContentValues();
                contentValues.put(RecordDBTable.TIME, Long.valueOf(instance.getTimeInMillis()));
                contentValues.put("addr", "123455");
                ShowOpenDataActivity.this.mDBAdapter.insert(RecordDBTable.DB_TABLE, contentValues);
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
