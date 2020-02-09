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
import bsdq.bsdq.db.DeviceTable;

public class MyDialogsetname extends Dialog {
    public String addr = "";
    public Context context;
    public DBAdapter dbAdapter;
    public EditText et_name;
    public Resources mResources;
    public Handler msgHandle;
    public MyApplication myApplication;
    public int r1 = R.layout.activity_pop1;
    public TextView tv_close;
    public TextView tv_refresh;

    public MyDialogsetname(Context context2, int i, String str) {
        super(context2, i);
        this.context = context2;
        this.mResources = context2.getResources();
        this.addr = str;
    }

    public MyDialogsetname setMsgHandle(Handler handler) {
        this.msgHandle = handler;
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_popsetname);
        this.dbAdapter = DBAdapter.init(this.context);
        this.dbAdapter.open();
        this.et_name = (EditText) findViewById(R.id.et_1);
        this.tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        this.tv_close = (TextView) findViewById(R.id.tv_close);
        this.tv_refresh.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String obj = MyDialogsetname.this.et_name.getEditableText().toString();
                if ("".equals(obj) || obj.length() < 1) {
                    Toast.makeText(MyDialogsetname.this.context, MyDialogsetname.this.mResources.getString(R.string.set_name_error), 1).show();
                    return;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", obj);
                String[] strArr = {MyDialogsetname.this.addr};
                MyDialogsetname.this.dbAdapter.upDataforTable(DeviceTable.DB_TABLE, contentValues, "addr =? ", strArr);
                if (MyDialogsetname.this.msgHandle != null) {
                    MyDialogsetname.this.msgHandle.sendEmptyMessageDelayed(1, 500);
                }
                MyDialogsetname.this.mydismiss();
            }
        });
        this.tv_close.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MyDialogsetname.this.mydismiss();
            }
        });
    }

    public void mydismiss() {
        dismiss();
    }
}
