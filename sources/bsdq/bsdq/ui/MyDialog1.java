package bsdq.bsdq.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import bsdq.bsdq.MyApplication;
import bsdq.bsdq.R;
import bsdq.bsdq.db.DBAdapter;

public class MyDialog1 extends Dialog {
    public String cancle;
    private OnClickListener clistener;
    public Context context;
    public DBAdapter dbAdapter;
    public String m;
    public Resources mResources;
    public TextView msg;
    public Handler msgHandle;
    public MyApplication myApplication;
    public String ok;
    public int r1 = R.layout.activity_pop7;
    private OnClickListener rlistener;
    public String t;
    public TextView title;
    public TextView tv_close;
    public TextView tv_refresh;

    public MyDialog1 setRefreshListener(OnClickListener onClickListener) {
        this.rlistener = onClickListener;
        return this;
    }

    public MyDialog1 setCloseListener(OnClickListener onClickListener) {
        this.clistener = onClickListener;
        return this;
    }

    public MyDialog1(Context context2, int i, int i2) {
        super(context2, i);
        this.context = context2;
        this.r1 = i2;
        this.mResources = context2.getResources();
    }

    public MyDialog1(Context context2, int i) {
        super(context2);
        this.context = context2;
        this.r1 = i;
    }

    public MyDialog1 setShowMsg(String str, String str2) {
        this.t = str;
        this.m = str2;
        return this;
    }

    public MyDialog1 setBtnMsg(String str, String str2) {
        this.ok = str;
        this.cancle = str2;
        return this;
    }

    public MyDialog1 setMsgHandle(Handler handler) {
        this.msgHandle = handler;
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.e("MyDialog", "MyDialog onCreate");
        setContentView(this.r1);
        this.tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        this.tv_close = (TextView) findViewById(R.id.tv_close);
        if (this.ok != null && !"".equals(this.ok)) {
            this.tv_refresh.setText(this.ok);
        }
        if (this.cancle != null && !"".equals(this.cancle)) {
            this.tv_close.setText(this.cancle);
        }
        this.title = (TextView) findViewById(R.id.tv_title);
        this.msg = (TextView) findViewById(R.id.tv_msg);
        this.title.setText(this.t);
        this.msg.setText(this.m);
        if (this.rlistener != null) {
            this.tv_refresh.setOnClickListener(this.rlistener);
        } else {
            this.tv_refresh.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    MyDialog1.this.mydismiss();
                }
            });
        }
        if (this.clistener != null) {
            this.tv_close.setOnClickListener(this.clistener);
        } else {
            this.tv_close.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    MyDialog1.this.mydismiss();
                }
            });
        }
    }

    public void mydismiss() {
        dismiss();
    }
}
