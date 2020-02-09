package bsdq.bsdq;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;

public class SetDevicePWDAcivity extends MyBasicActivity {
    /* access modifiers changed from: private */
    public String addr = "";
    private ImageView img_shang;
    private ImageView img_xia;
    private ImageView img_you;
    private ImageView img_zuo;
    /* access modifiers changed from: private */
    public String kong = "  ";
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Resources mResources;
    /* access modifiers changed from: private */
    public MyApplication myApplication;
    private OnClickListener myOnClickListener = new OnClickListener() {
        public void onClick(View view) {
            if (SetDevicePWDAcivity.this.num == 6) {
                Toast.makeText(SetDevicePWDAcivity.this.mContext, SetDevicePWDAcivity.this.mResources.getString(R.string.error4), 0).show();
            }
            int id = view.getId();
            if (id != R.id.img_shang) {
                switch (id) {
                    case R.id.img_xia /*2131165285*/:
                        SetDevicePWDAcivity.this.pwds.add("2");
                        break;
                    case R.id.img_you /*2131165286*/:
                        SetDevicePWDAcivity.this.pwds.add("4");
                        break;
                    case R.id.img_zuo /*2131165287*/:
                        SetDevicePWDAcivity.this.pwds.add("3");
                        break;
                }
            } else {
                SetDevicePWDAcivity.this.pwds.add("1");
            }
            if (SetDevicePWDAcivity.this.num == 0) {
                SetDevicePWDAcivity.this.spwd.append("*");
            } else {
                SetDevicePWDAcivity.this.spwd.append(SetDevicePWDAcivity.this.kong);
                SetDevicePWDAcivity.this.spwd.append("*");
            }
            SetDevicePWDAcivity.this.num = SetDevicePWDAcivity.this.num + 1;
            SetDevicePWDAcivity.this.tv_device_pwd.setText(SetDevicePWDAcivity.this.spwd.toString());
        }
    };
    /* access modifiers changed from: private */
    public int num = 0;
    /* access modifiers changed from: private */
    public ArrayList<String> pwds = new ArrayList<>();
    /* access modifiers changed from: private */
    public StringBuffer spwd = new StringBuffer();
    private TextView tv_cancle;
    /* access modifiers changed from: private */
    public TextView tv_device_pwd;
    private TextView tv_ok;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_resetdevicepwd);
        super.setTintColor(-16772059);
        this.myApplication = (MyApplication) getApplication();
        this.mContext = this;
        this.mResources = getResources();
        Bundle bundleExtra = getIntent().getBundleExtra("data");
        if (bundleExtra != null) {
            this.addr = bundleExtra.getString("addr");
        }
        init();
        setListener();
    }

    private void init() {
        this.img_shang = (ImageView) findViewById(R.id.img_shang);
        this.img_xia = (ImageView) findViewById(R.id.img_xia);
        this.img_zuo = (ImageView) findViewById(R.id.img_zuo);
        this.img_you = (ImageView) findViewById(R.id.img_you);
        this.tv_ok = (TextView) findViewById(R.id.tv_ok);
        this.tv_cancle = (TextView) findViewById(R.id.tv_cancle);
        this.tv_device_pwd = (TextView) findViewById(R.id.tv_device_pwd);
        this.tv_device_pwd.setText("");
    }

    private void setListener() {
        this.tv_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (SetDevicePWDAcivity.this.pwds.size() != 6) {
                    Toast.makeText(SetDevicePWDAcivity.this.mContext, SetDevicePWDAcivity.this.mResources.getString(R.string.error4), 0).show();
                    return;
                }
                String str = "";
                Iterator it = SetDevicePWDAcivity.this.pwds.iterator();
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(str2);
                    str = sb.toString();
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append("resetdevciePwd ");
                sb2.append(str);
                sb2.append(" ");
                sb2.append(SetDevicePWDAcivity.this.addr);
                Log.e("a", sb2.toString());
                SetDevicePWDAcivity.this.myApplication.resetdevciePwd(SetDevicePWDAcivity.this.addr, str);
                SetDevicePWDAcivity.this.finish();
            }
        });
        this.tv_cancle.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SetDevicePWDAcivity.this.finish();
            }
        });
        this.img_shang.setOnClickListener(this.myOnClickListener);
        this.img_xia.setOnClickListener(this.myOnClickListener);
        this.img_zuo.setOnClickListener(this.myOnClickListener);
        this.img_you.setOnClickListener(this.myOnClickListener);
    }
}
