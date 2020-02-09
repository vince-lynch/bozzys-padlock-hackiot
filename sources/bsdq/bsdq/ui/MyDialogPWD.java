package bsdq.bsdq.ui;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.MyApplication;
import bsdq.bsdq.R;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.UserTable;

public class MyDialogPWD extends Dialog {
    public String a = "";
    public Context context;
    public DBAdapter dbAdapter;
    public EditText et_1;
    public EditText et_2;
    public Resources mResources;
    public Handler msgHandle;
    public MyApplication myApplication;
    public String q = "";
    public int r1 = R.layout.activity_pop1;
    public TextView tv_close;
    public TextView tv_refresh;
    public int type = 0;

    public MyDialogPWD(Context context2, int i, int i2, String str, String str2) {
        super(context2, i);
        this.context = context2;
        this.r1 = i2;
        this.q = str;
        this.a = str2;
        this.mResources = context2.getResources();
    }

    public MyDialogPWD(Context context2, int i) {
        super(context2);
        this.context = context2;
        this.r1 = i;
    }

    public void setType(int i) {
        this.type = i;
    }

    public MyDialogPWD setMsgHandle(Handler handler) {
        this.msgHandle = handler;
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(this.r1);
        this.dbAdapter = DBAdapter.init(this.context);
        this.dbAdapter.open();
        this.et_1 = (EditText) findViewById(R.id.et_1);
        this.et_2 = (EditText) findViewById(R.id.et_2);
        this.tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        this.tv_close = (TextView) findViewById(R.id.tv_close);
        this.tv_refresh.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String obj = MyDialogPWD.this.et_1.getEditableText().toString();
                String obj2 = MyDialogPWD.this.et_2.getEditableText().toString();
                if (!obj.equals(obj2)) {
                    Toast.makeText(MyDialogPWD.this.context, MyDialogPWD.this.mResources.getString(R.string.dissimilarity), 1).show();
                } else if (obj.length() == 6 && obj2.length() == 6) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(UserTable.PWD2, obj);
                    contentValues.put(UserTable.PWDTYPE, Integer.valueOf(1));
                    MyDialogPWD.this.dbAdapter.upDataforTable(UserTable.DB_TABLE, contentValues, null, null);
                    if (MyDialogPWD.this.msgHandle != null) {
                        MyDialogPWD.this.msgHandle.sendEmptyMessage(1);
                    }
                    MyDialogPWD.this.mydismiss();
                } else {
                    Toast.makeText(MyDialogPWD.this.context, MyDialogPWD.this.mResources.getString(R.string.error4), 1).show();
                }
            }
        });
        this.tv_close.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MyDialogPWD.this.mydismiss();
            }
        });
    }

    public void mydismiss() {
        dismiss();
    }
}
