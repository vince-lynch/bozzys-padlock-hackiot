package bsdq.bsdq.ui;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.HomeActivity;
import bsdq.bsdq.MyApplication;
import bsdq.bsdq.R;
import bsdq.bsdq.db.DBAdapter;
import bsdq.bsdq.db.UserTable;

public class MyDialog extends Dialog {
    public String a = "";
    public Context context;
    public DBAdapter dbAdapter;
    public EditText et_anwser;
    public EditText et_question;
    public Resources mResources;
    public Handler msgHandle;
    public MyApplication myApplication;
    public String q = "";
    public int r1 = R.layout.activity_pop1;
    public TextView tv_close;
    public TextView tv_refresh;
    public int type = 0;

    public MyDialog(Context context2, int i, int i2, String str, String str2) {
        super(context2, i);
        this.context = context2;
        this.r1 = i2;
        this.q = str;
        this.a = str2;
        this.mResources = context2.getResources();
    }

    public MyDialog(Context context2, int i) {
        super(context2);
        this.context = context2;
        this.r1 = i;
    }

    public void setType(int i) {
        this.type = i;
    }

    public MyDialog setMsgHandle(Handler handler) {
        this.msgHandle = handler;
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(this.r1);
        this.dbAdapter = DBAdapter.init(this.context);
        this.dbAdapter.open();
        this.et_question = (EditText) findViewById(R.id.et_question);
        this.et_anwser = (EditText) findViewById(R.id.et_anwser);
        this.tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        this.tv_close = (TextView) findViewById(R.id.tv_close);
        this.et_question.setText(this.q);
        this.tv_refresh.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String obj = MyDialog.this.et_question.getEditableText().toString();
                String obj2 = MyDialog.this.et_anwser.getEditableText().toString();
                StringBuilder sb = new StringBuilder();
                sb.append("i=");
                sb.append(MyDialog.this.a);
                Log.e("00", sb.toString());
                if (MyDialog.this.type == 1) {
                    if (obj2.equals(MyDialog.this.a)) {
                        MyDialog.this.context.startActivity(new Intent(MyDialog.this.context, HomeActivity.class));
                    } else {
                        Toast.makeText(MyDialog.this.context, MyDialog.this.mResources.getString(R.string.error2), 1).show();
                    }
                } else if (obj == null || obj.length() < 1 || obj2 == null || obj2.length() < 1) {
                    Toast.makeText(MyDialog.this.context, MyDialog.this.mResources.getString(R.string.error3), 1).show();
                    return;
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(UserTable.QUESTION, obj);
                    contentValues.put(UserTable.ANWSER, obj2);
                    MyDialog.this.dbAdapter.upDataforTable(UserTable.DB_TABLE, contentValues, null, null);
                    if (MyDialog.this.msgHandle != null) {
                        MyDialog.this.msgHandle.sendEmptyMessage(1);
                    }
                }
                MyDialog.this.mydismiss();
            }
        });
        this.tv_close.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MyDialog.this.mydismiss();
            }
        });
    }

    public void mydismiss() {
        dismiss();
    }
}
