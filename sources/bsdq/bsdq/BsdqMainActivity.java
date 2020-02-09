package bsdq.bsdq;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.Gesture.GestureCustomView;
import bsdq.bsdq.Gesture.GestureCustomView.OnResultListener;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.UserTable;
import bsdq.bsdq.ui.MyDialog;

public class BsdqMainActivity extends MyBasicActivity {
    public String cache = "-1";
    /* access modifiers changed from: private */
    public GestureCustomView customView;
    /* access modifiers changed from: private */
    public boolean isresetpwd = false;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public DBAdapter mDBAdapter;
    /* access modifiers changed from: private */
    public Resources mResources;
    /* access modifiers changed from: private */
    public MyApplication myApplication;
    private TextView tv_getpwd;
    /* access modifiers changed from: private */
    public TextView tv_top;

    /* access modifiers changed from: protected */
    @RequiresApi(api = 21)
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bsdq_main);
        this.mContext = this;
        super.setTintColor(-16772059);
        this.myApplication = (MyApplication) getApplication();
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        DBAdapter dBAdapter = this.mDBAdapter;
        DBAdapter.init(this.mContext);
        this.mResources = getResources();
        this.isresetpwd = getIntent().getBooleanExtra("isresetpwd", false);
        StringBuilder sb = new StringBuilder();
        sb.append("isresetpwd=");
        sb.append(this.isresetpwd);
        Log.e("isresetpwd", sb.toString());
        this.tv_getpwd = (TextView) findViewById(R.id.tv_getpwd);
        this.tv_top = (TextView) findViewById(R.id.tv_top);
        if (this.myApplication.isfrist) {
            this.tv_top.setText(this.mResources.getString(R.string.inputpwd10));
        }
        if (this.isresetpwd) {
            this.tv_getpwd.setText(this.mResources.getString(R.string.cancle));
        } else {
            this.tv_getpwd.setText(this.mResources.getString(R.string.phrase));
        }
        if (this.myApplication.isOpencheckpwd || this.myApplication.isfrist) {
            this.customView = (GestureCustomView) findViewById(R.id.GCV);
            this.customView.setOnResultListener(new OnResultListener() {
                public void Result(String str) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("result=");
                    sb.append(str);
                    Log.e("--", sb.toString());
                    if (BsdqMainActivity.this.myApplication.isfrist) {
                        if ("-1".equals(BsdqMainActivity.this.cache)) {
                            BsdqMainActivity.this.cache = str;
                            BsdqMainActivity.this.tv_top.setText(BsdqMainActivity.this.mResources.getString(R.string.inputpwd11));
                        } else if (BsdqMainActivity.this.cache.equals(str)) {
                            BsdqMainActivity.this.myApplication.mUser.pwd1 = str;
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(UserTable.PWD1, str);
                            contentValues.put(UserTable.PWDTYPE, Integer.valueOf(0));
                            BsdqMainActivity.this.mDBAdapter.upDataforTable(UserTable.DB_TABLE, contentValues, null, null);
                            if (BsdqMainActivity.this.myApplication.setting != null) {
                                Editor edit = BsdqMainActivity.this.myApplication.setting.edit();
                                edit.putBoolean("isfrist", false);
                                edit.commit();
                            }
                            BsdqMainActivity.this.startActivity(new Intent(BsdqMainActivity.this, HomeActivity.class));
                            BsdqMainActivity.this.finish();
                        } else {
                            Context access$400 = BsdqMainActivity.this.mContext;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("");
                            sb2.append(BsdqMainActivity.this.mResources.getString(R.string.dissimilarity));
                            Toast.makeText(access$400, sb2.toString(), 0).show();
                            BsdqMainActivity.this.cache = "-1";
                            BsdqMainActivity.this.customView.setFristData(null);
                            BsdqMainActivity.this.tv_top.setText(BsdqMainActivity.this.mResources.getString(R.string.inputpwd10));
                        }
                    } else if (BsdqMainActivity.this.customView.cheakPWD(str)) {
                        BsdqMainActivity.this.startActivity(new Intent(BsdqMainActivity.this, HomeActivity.class));
                    } else {
                        Context access$4002 = BsdqMainActivity.this.mContext;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("");
                        sb3.append(BsdqMainActivity.this.mResources.getString(R.string.invalid_pwd));
                        Toast.makeText(access$4002, sb3.toString(), 0).show();
                    }
                }
            });
            if (this.myApplication.isfrist) {
                this.customView.setAgain(true);
                this.tv_getpwd.setVisibility(8);
            } else {
                this.customView.setAgain(false);
                this.customView.setFristData(this.myApplication.mUser.pwd1);
                this.tv_getpwd.setVisibility(0);
            }
            if (this.isresetpwd) {
                this.tv_getpwd.setVisibility(0);
            }
            this.tv_getpwd.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (BsdqMainActivity.this.isresetpwd) {
                        BsdqMainActivity.this.finish();
                        return;
                    }
                    if (BsdqMainActivity.this.myApplication.mUser.question.equals("")) {
                        Context access$400 = BsdqMainActivity.this.mContext;
                        StringBuilder sb = new StringBuilder();
                        sb.append("");
                        sb.append(BsdqMainActivity.this.mResources.getString(R.string.noprompt));
                        Toast.makeText(access$400, sb.toString(), 0).show();
                    } else {
                        MyDialog myDialog = new MyDialog(BsdqMainActivity.this, R.style.MyDialog, R.layout.activity_pop1, BsdqMainActivity.this.myApplication.mUser.question, BsdqMainActivity.this.myApplication.mUser.anwser);
                        myDialog.setType(1);
                        myDialog.show();
                    }
                }
            });
            return;
        }
        startActivity(new Intent(this, HomeActivity.class));
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (4 == i) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }
}
