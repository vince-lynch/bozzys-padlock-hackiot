package bsdq.bsdq;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.consmart.ble.AES2;

public class HomeActivity extends TabActivity {
    public static TabHost mTabHost;
    public byte[] aa = {-48, -7, -12, -116, 89, -94, 105, 29, 32, 83, -53, -38, Byte.MIN_VALUE, -124, 67, -109};
    private RelativeLayout home_activity;
    private ImageView img_home;
    private ImageView img_record;
    private ImageView img_user;
    boolean isopenGPS = false;
    public Context mContext;
    public Resources mResources;
    private SystemBarTintManager mTintManager;
    private RadioGroup main_radio;
    public MyApplication myApplication;
    private OnClickListener myOnClickListener = new OnClickListener() {
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.home_activity) {
                HomeActivity.mTabHost.setCurrentTab(0);
            } else if (id == R.id.record_activity) {
                HomeActivity.mTabHost.setCurrentTab(1);
            } else if (id == R.id.user_activity) {
                HomeActivity.mTabHost.setCurrentTab(2);
            }
            HomeActivity.this.settabbg();
        }
    };
    private RelativeLayout record_activity;
    private TextView tx_home;
    private TextView tx_record;
    private TextView tx_user;
    private RelativeLayout user_activity;

    public static TabHost getmTabHost() {
        return mTabHost;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.e("--", "onResume");
        if (VERSION.SDK_INT >= 26 && !Tool.isOPen(getApplicationContext()) && !this.isopenGPS) {
            Tool.openGPS(this);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.open_gps_msg), 1).show();
            this.isopenGPS = true;
        }
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        if (VERSION.SDK_INT >= 19) {
            getWindow().addFlags(67108864);
        }
        this.mContext = this;
        this.mResources = getResources();
        if (VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 1);
            Toast.makeText(this.mContext, this.mResources.getString(R.string.openpermission), 1).show();
        }
        AES2.setKey(this.aa);
        this.myApplication = (MyApplication) getApplication();
        this.mTintManager = new SystemBarTintManager(this);
        this.mTintManager.setStatusBarTintEnabled(true);
        this.mTintManager.setNavigationBarTintEnabled(true);
        this.mTintManager.setTintAlpha(255.0f);
        setContentView(R.layout.activity_home);
        setTintColor(-16772059);
        mTabHost = getTabHost();
        mTabHost.getTabWidget().setStripEnabled(false);
        mTabHost.addTab(mTabHost.newTabSpec("TAG1").setIndicator("1").setContent(new Intent(this, DeviceActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("TAG2").setIndicator("2").setContent(new Intent(this, RecordActiviy.class)));
        mTabHost.addTab(mTabHost.newTabSpec("TAG3").setIndicator("3").setContent(new Intent(this, UserActivity.class)));
        mTabHost.setCurrentTab(0);
        this.home_activity = (RelativeLayout) findViewById(R.id.home_activity);
        this.record_activity = (RelativeLayout) findViewById(R.id.record_activity);
        this.user_activity = (RelativeLayout) findViewById(R.id.user_activity);
        this.img_home = (ImageView) findViewById(R.id.img_home);
        this.img_record = (ImageView) findViewById(R.id.img_record);
        this.img_user = (ImageView) findViewById(R.id.img_user);
        this.tx_home = (TextView) findViewById(R.id.tx_home);
        this.tx_record = (TextView) findViewById(R.id.tx_record);
        this.tx_user = (TextView) findViewById(R.id.tx_user);
        settabbg();
        setListener();
    }

    public void setTintColor(int i) {
        this.mTintManager.setTintColor(i);
    }

    public void setListener() {
        this.home_activity.setOnClickListener(this.myOnClickListener);
        this.record_activity.setOnClickListener(this.myOnClickListener);
        this.user_activity.setOnClickListener(this.myOnClickListener);
    }

    public void settabbg() {
        int currentTab = mTabHost.getCurrentTab();
        this.img_home.setImageResource(R.mipmap.ic_lock_w);
        this.img_record.setImageResource(R.mipmap.ic_record_w);
        this.img_user.setImageResource(R.mipmap.ic_user_w);
        this.tx_home.setTextColor(-1);
        this.tx_record.setTextColor(-1);
        this.tx_user.setTextColor(-1);
        switch (currentTab) {
            case 0:
                this.img_home.setImageResource(R.mipmap.ic_lock_blue);
                this.tx_home.setTextColor(-15752243);
                return;
            case 1:
                this.img_record.setImageResource(R.mipmap.ic_record_blue);
                this.tx_record.setTextColor(-15752243);
                return;
            case 2:
                this.img_user.setImageResource(R.mipmap.ic_user_blue);
                this.tx_user.setTextColor(-15752243);
                return;
            default:
                return;
        }
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

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        StringBuilder sb = new StringBuilder();
        sb.append("requestCode=");
        sb.append(i);
        sb.append(" resultCode=");
        sb.append(i2);
        Log.e("--", sb.toString());
    }
}
