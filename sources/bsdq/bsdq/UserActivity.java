package bsdq.bsdq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.User;
import bsdq.bsdq.db.UserTable;
import bsdq.bsdq.ui.MyDialog;
import bsdq.bsdq.ui.MyDialogPWD;

public class UserActivity extends MyBasicActivity {
    /* access modifiers changed from: private */
    public ImageView img_open;
    public Context mContext;
    public DBAdapter mDBAdapter;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler(new Callback() {
        public boolean handleMessage(Message message) {
            Log.e("--", "onResume");
            Cursor queryAllData = UserActivity.this.mDBAdapter.queryAllData(UserTable.DB_TABLE);
            User user = new User();
            if (queryAllData.moveToNext()) {
                user.name = queryAllData.getString(queryAllData.getColumnIndex("name"));
                user.pwd1 = queryAllData.getString(queryAllData.getColumnIndex(UserTable.PWD1));
                user.pwd2 = queryAllData.getString(queryAllData.getColumnIndex(UserTable.PWD2));
                user.pwdtype = queryAllData.getInt(queryAllData.getColumnIndex(UserTable.PWDTYPE));
                user.question = queryAllData.getString(queryAllData.getColumnIndex(UserTable.QUESTION));
                user.anwser = queryAllData.getString(queryAllData.getColumnIndex(UserTable.ANWSER));
            }
            UserActivity.this.myApplication.mUser = user;
            if (!UserActivity.this.myApplication.isOpencheckpwd) {
                UserActivity.this.img_open.setImageResource(R.mipmap.ic_close);
            } else {
                UserActivity.this.img_open.setImageResource(R.mipmap.ic_open);
            }
            return false;
        }
    });
    public Resources mResources;
    /* access modifiers changed from: private */
    public MyApplication myApplication;
    private OnClickListener myOnClickListener = new OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rel_resetprompt /*2131165342*/:
                    MyDialog myDialog = new MyDialog(UserActivity.this, R.style.MyDialog, R.layout.activity_pop1, UserActivity.this.myApplication.mUser.question, UserActivity.this.myApplication.mUser.anwser);
                    myDialog.setMsgHandle(UserActivity.this.mHandler);
                    myDialog.show();
                    return;
                case R.id.rel_resetpwd /*2131165343*/:
                    if (UserActivity.this.myApplication.mUser.pwdtype == 0) {
                        UserActivity.this.myApplication.isfrist = true;
                        Intent intent = new Intent(UserActivity.this, BsdqMainActivity.class);
                        intent.putExtra("isresetpwd", true);
                        UserActivity.this.startActivity(intent);
                        return;
                    }
                    MyDialogPWD myDialogPWD = new MyDialogPWD(UserActivity.this, R.style.MyDialog, R.layout.activity_resetnumpwd, UserActivity.this.myApplication.mUser.question, UserActivity.this.myApplication.mUser.anwser);
                    myDialogPWD.setMsgHandle(UserActivity.this.mHandler);
                    myDialogPWD.show();
                    return;
                case R.id.rel_resetrecord /*2131165344*/:
                    UserActivity.this.startActivity(new Intent(UserActivity.this, DelRecordActiviy.class));
                    return;
                case R.id.rel_share /*2131165345*/:
                    UserActivity.this.startActivity(new Intent(UserActivity.this, ShareActivity.class));
                    return;
                default:
                    return;
            }
        }
    };
    private RelativeLayout rel_resetprompt;
    private RelativeLayout rel_resetpwd;
    private RelativeLayout rel_resetrecord;
    private RelativeLayout rel_share;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_user);
        super.setTintColor(-16772059);
        this.mContext = this;
        this.mResources = getResources();
        this.myApplication = (MyApplication) getApplication();
        this.mDBAdapter = DBAdapter.init(this.mContext);
        this.mDBAdapter.open();
        init();
        setListener();
        Log.e("--", "onCreate");
    }

    private void init() {
        this.rel_resetpwd = (RelativeLayout) findViewById(R.id.rel_resetpwd);
        this.rel_resetrecord = (RelativeLayout) findViewById(R.id.rel_resetrecord);
        this.rel_resetprompt = (RelativeLayout) findViewById(R.id.rel_resetprompt);
        this.img_open = (ImageView) findViewById(R.id.img_open);
        this.rel_share = (RelativeLayout) findViewById(R.id.rel_share);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        if (!this.myApplication.isOpencheckpwd) {
            this.img_open.setImageResource(R.mipmap.ic_close);
        } else {
            this.img_open.setImageResource(R.mipmap.ic_open);
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

    private void setListener() {
        this.rel_share.setOnClickListener(this.myOnClickListener);
        this.rel_resetpwd.setOnClickListener(this.myOnClickListener);
        this.rel_resetrecord.setOnClickListener(this.myOnClickListener);
        this.rel_resetprompt.setOnClickListener(this.myOnClickListener);
        this.img_open.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                UserActivity.this.myApplication.isOpencheckpwd = !UserActivity.this.myApplication.isOpencheckpwd;
                if (!UserActivity.this.myApplication.isOpencheckpwd) {
                    UserActivity.this.img_open.setImageResource(R.mipmap.ic_close);
                } else {
                    UserActivity.this.img_open.setImageResource(R.mipmap.ic_open);
                }
                Editor edit = UserActivity.this.myApplication.sett.edit();
                edit.putBoolean("isOpencheckpwd", UserActivity.this.myApplication.isOpencheckpwd);
                edit.commit();
            }
        });
    }
}
