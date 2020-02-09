package bsdq.bsdq.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.MyApplication;
import bsdq.bsdq.R;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.DeviceTable;

public class MyDialogConnPWD extends Dialog {
    public Context context;
    public DBAdapter dbAdapter;
    public EditText et_1;
    public EditText et_2;
    public Resources mResources;
    public Handler msgHandle;
    public MyApplication myApplication;
    public String t = "";
    public TextView tv_close;
    public TextView tv_refresh;
    public TextView tv_title;

    public MyDialogConnPWD(Context context2, int i) {
        super(context2, i);
        this.context = context2;
        this.mResources = context2.getResources();
    }

    public void setT(String str) {
        this.t = str;
    }

    public MyDialogConnPWD setMsgHandle(Handler handler) {
        this.msgHandle = handler;
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_resetnumpwd);
        this.dbAdapter = DBAdapter.init(this.context);
        this.dbAdapter.open();
        this.et_1 = (EditText) findViewById(R.id.et_1);
        this.et_2 = (EditText) findViewById(R.id.et_2);
        this.tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        this.tv_close = (TextView) findViewById(R.id.tv_close);
        this.tv_title = (TextView) findViewById(R.id.tv_title);
        if ("".equals(this.t)) {
            this.tv_title.setText(this.mResources.getString(R.string.set_conn_pwd));
        } else {
            this.tv_title.setText(this.t);
        }
        this.tv_refresh.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String obj = MyDialogConnPWD.this.et_1.getEditableText().toString();
                String obj2 = MyDialogConnPWD.this.et_2.getEditableText().toString();
                if (!obj.equals(obj2)) {
                    Toast.makeText(MyDialogConnPWD.this.context, MyDialogConnPWD.this.mResources.getString(R.string.dissimilarity), 1).show();
                } else if (obj.length() == 6 && obj2.length() == 6) {
                    if (MyDialogConnPWD.this.msgHandle != null) {
                        Message message = new Message();
                        message.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putString(DeviceTable.PWD, obj);
                        message.setData(bundle);
                        MyDialogConnPWD.this.msgHandle.sendMessage(message);
                    }
                    MyDialogConnPWD.this.mydismiss();
                } else {
                    Toast.makeText(MyDialogConnPWD.this.context, MyDialogConnPWD.this.mResources.getString(R.string.error4), 1).show();
                }
            }
        });
        this.tv_close.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MyDialogConnPWD.this.mydismiss();
            }
        });
    }

    public void mydismiss() {
        dismiss();
    }
}
