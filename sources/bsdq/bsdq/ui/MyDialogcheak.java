package bsdq.bsdq.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.MyApplication;
import bsdq.bsdq.R;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.DeviceTable;

public class MyDialogcheak extends Dialog {
    public String addr = "";
    public CheckBox cb_rps;
    private OnClickListener clistener;
    public Context context;
    public DBAdapter dbAdapter;
    public EditText et_ps;
    public Resources mResources;
    public Handler msgHandle;
    public MyApplication myApplication;
    public int r1 = R.layout.activity_pop2;
    private OnClickListener rlistener;
    public TextView tv_close;
    public TextView tv_refresh;

    public MyDialogcheak setRefreshListener(OnClickListener onClickListener) {
        this.rlistener = onClickListener;
        return this;
    }

    public MyDialogcheak setCloseListener(OnClickListener onClickListener) {
        this.clistener = onClickListener;
        return this;
    }

    public MyDialogcheak(Context context2, int i, String str) {
        super(context2, i);
        this.context = context2;
        this.mResources = context2.getResources();
        this.addr = str;
    }

    public MyDialogcheak setMsgHandle(Handler handler) {
        this.msgHandle = handler;
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pop2);
        this.tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        this.tv_close = (TextView) findViewById(R.id.tv_close);
        this.et_ps = (EditText) findViewById(R.id.et_ps);
        this.cb_rps = (CheckBox) findViewById(R.id.cb_rps);
        if (this.rlistener != null) {
            this.tv_refresh.setOnClickListener(this.rlistener);
        } else {
            this.tv_refresh.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    boolean isChecked = MyDialogcheak.this.cb_rps.isChecked();
                    String obj = MyDialogcheak.this.et_ps.getText().toString();
                    if (obj.length() != 6) {
                        Toast.makeText(MyDialogcheak.this.context, MyDialogcheak.this.mResources.getString(R.string.error5), 0).show();
                        return;
                    }
                    if (MyDialogcheak.this.msgHandle != null) {
                        Message message = new Message();
                        message.what = 4011;
                        Bundle bundle = new Bundle();
                        bundle.putString("addr", MyDialogcheak.this.addr);
                        bundle.putString(DeviceTable.PWD, obj);
                        bundle.putBoolean("remember", isChecked);
                        message.setData(bundle);
                        MyDialogcheak.this.msgHandle.sendMessage(message);
                    }
                    MyDialogcheak.this.mydismiss();
                }
            });
        }
        if (this.clistener != null) {
            this.tv_close.setOnClickListener(this.clistener);
        } else {
            this.tv_close.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    MyDialogcheak.this.mydismiss();
                }
            });
        }
    }

    public void mydismiss() {
        dismiss();
    }
}
