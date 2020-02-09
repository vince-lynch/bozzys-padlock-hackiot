package bsdq.bsdq;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bsdq.bsdq.ui.MyDialog;
import java.util.ArrayList;
import java.util.HashMap;

public class NumPwdActivity extends MyBasicActivity {
    private ImageView img_pwd1;
    private ImageView img_pwd2;
    private ImageView img_pwd3;
    private ImageView img_pwd4;
    private ImageView img_pwd5;
    private ImageView img_pwd6;
    /* access modifiers changed from: private */
    public Context mContext;
    private Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public Resources mResources;
    /* access modifiers changed from: private */
    public MyApplication myApplication;
    private OnClickListener myOnClickListener = new OnClickListener() {
        public void onClick(View view) {
            int i;
            if (NumPwdActivity.this.num > 6) {
                NumPwdActivity.this.num = 6;
                return;
            }
            switch (view.getId()) {
                case R.id.rel_num1 /*2131165330*/:
                    i = 1;
                    break;
                case R.id.rel_num2 /*2131165333*/:
                    i = 2;
                    break;
                case R.id.rel_num3 /*2131165334*/:
                    i = 3;
                    break;
                case R.id.rel_num4 /*2131165335*/:
                    i = 4;
                    break;
                case R.id.rel_num5 /*2131165336*/:
                    i = 5;
                    break;
                case R.id.rel_num6 /*2131165337*/:
                    i = 6;
                    break;
                case R.id.rel_num7 /*2131165338*/:
                    i = 7;
                    break;
                case R.id.rel_num8 /*2131165339*/:
                    i = 8;
                    break;
                case R.id.rel_num9 /*2131165340*/:
                    i = 9;
                    break;
                default:
                    i = 0;
                    break;
            }
            NumPwdActivity.this.num = NumPwdActivity.this.num + 1;
            NumPwdActivity.this.reset(NumPwdActivity.this.num);
            NumPwdActivity.this.nums.put(Integer.valueOf(NumPwdActivity.this.num), Integer.valueOf(i));
            if (NumPwdActivity.this.num == 5) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i2 = 0; i2 < 6; i2++) {
                    if (NumPwdActivity.this.nums.containsKey(Integer.valueOf(i2))) {
                        stringBuffer.append(((Integer) NumPwdActivity.this.nums.get(Integer.valueOf(i2))).intValue());
                    }
                }
                if (NumPwdActivity.this.myApplication.mUser.pwd2.equals(stringBuffer.toString())) {
                    NumPwdActivity.this.startActivity(new Intent(NumPwdActivity.this, HomeActivity.class));
                    NumPwdActivity.this.finish();
                    return;
                }
                Toast.makeText(NumPwdActivity.this.mContext, NumPwdActivity.this.mResources.getString(R.string.invalid_pwd), 1).show();
                NumPwdActivity.this.reset(NumPwdActivity.this.num);
                NumPwdActivity.this.num = -1;
                NumPwdActivity.this.reset(NumPwdActivity.this.num);
            }
        }
    };
    /* access modifiers changed from: private */
    public int num = -1;
    /* access modifiers changed from: private */
    public HashMap<Integer, Integer> nums = new HashMap<>();
    private ArrayList<ImageView> pwds = new ArrayList<>();
    private RelativeLayout rel_num0;
    private RelativeLayout rel_num1;
    private RelativeLayout rel_num2;
    private RelativeLayout rel_num3;
    private RelativeLayout rel_num4;
    private RelativeLayout rel_num5;
    private RelativeLayout rel_num6;
    private RelativeLayout rel_num7;
    private RelativeLayout rel_num8;
    private RelativeLayout rel_num9;
    private TextView tv_cancle;
    private TextView tv_phrase;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_num_pwd);
        super.setTintColor(-16772059);
        this.myApplication = (MyApplication) getApplication();
        this.mResources = getResources();
        this.mContext = this;
        init();
        setListener();
    }

    private void init() {
        this.rel_num1 = (RelativeLayout) findViewById(R.id.rel_num1);
        this.rel_num2 = (RelativeLayout) findViewById(R.id.rel_num2);
        this.rel_num3 = (RelativeLayout) findViewById(R.id.rel_num3);
        this.rel_num4 = (RelativeLayout) findViewById(R.id.rel_num4);
        this.rel_num5 = (RelativeLayout) findViewById(R.id.rel_num5);
        this.rel_num6 = (RelativeLayout) findViewById(R.id.rel_num6);
        this.rel_num7 = (RelativeLayout) findViewById(R.id.rel_num7);
        this.rel_num8 = (RelativeLayout) findViewById(R.id.rel_num8);
        this.rel_num9 = (RelativeLayout) findViewById(R.id.rel_num9);
        this.rel_num0 = (RelativeLayout) findViewById(R.id.rel_num0);
        this.img_pwd1 = (ImageView) findViewById(R.id.img_pwd1);
        this.img_pwd2 = (ImageView) findViewById(R.id.img_pwd2);
        this.img_pwd3 = (ImageView) findViewById(R.id.img_pwd3);
        this.img_pwd4 = (ImageView) findViewById(R.id.img_pwd4);
        this.img_pwd5 = (ImageView) findViewById(R.id.img_pwd5);
        this.img_pwd6 = (ImageView) findViewById(R.id.img_pwd6);
        this.pwds.clear();
        this.pwds.add(this.img_pwd1);
        this.pwds.add(this.img_pwd2);
        this.pwds.add(this.img_pwd3);
        this.pwds.add(this.img_pwd4);
        this.pwds.add(this.img_pwd5);
        this.pwds.add(this.img_pwd6);
        reset(this.num);
        this.tv_cancle = (TextView) findViewById(R.id.tv_cancle);
        this.tv_phrase = (TextView) findViewById(R.id.tv_phrase);
    }

    /* access modifiers changed from: private */
    public void reset(int i) {
        for (int i2 = 0; i2 < this.pwds.size(); i2++) {
            ((ImageView) this.pwds.get(i2)).setVisibility(8);
        }
        int i3 = i + 1;
        if (this.pwds.size() >= i3) {
            for (int i4 = 0; i4 < i3; i4++) {
                ((ImageView) this.pwds.get(i4)).setVisibility(0);
            }
        }
    }

    private void setListener() {
        this.rel_num1.setOnClickListener(this.myOnClickListener);
        this.rel_num2.setOnClickListener(this.myOnClickListener);
        this.rel_num3.setOnClickListener(this.myOnClickListener);
        this.rel_num4.setOnClickListener(this.myOnClickListener);
        this.rel_num5.setOnClickListener(this.myOnClickListener);
        this.rel_num6.setOnClickListener(this.myOnClickListener);
        this.rel_num7.setOnClickListener(this.myOnClickListener);
        this.rel_num8.setOnClickListener(this.myOnClickListener);
        this.rel_num9.setOnClickListener(this.myOnClickListener);
        this.rel_num0.setOnClickListener(this.myOnClickListener);
        this.tv_cancle.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (NumPwdActivity.this.num <= -1) {
                    NumPwdActivity.this.num = -1;
                    NumPwdActivity.this.nums.clear();
                    NumPwdActivity.this.reset(NumPwdActivity.this.num);
                    return;
                }
                if (NumPwdActivity.this.nums.size() > NumPwdActivity.this.num) {
                    NumPwdActivity.this.nums.remove(Integer.valueOf(NumPwdActivity.this.num));
                }
                NumPwdActivity.this.num = NumPwdActivity.this.num - 1;
                NumPwdActivity.this.reset(NumPwdActivity.this.num);
            }
        });
        this.tv_phrase.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (NumPwdActivity.this.myApplication.mUser.question.equals("")) {
                    Context access$400 = NumPwdActivity.this.mContext;
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    sb.append(NumPwdActivity.this.mResources.getString(R.string.noprompt));
                    Toast.makeText(access$400, sb.toString(), 0).show();
                    return;
                }
                MyDialog myDialog = new MyDialog(NumPwdActivity.this, R.style.MyDialog, R.layout.activity_pop1, NumPwdActivity.this.myApplication.mUser.question, NumPwdActivity.this.myApplication.mUser.question);
                myDialog.setType(1);
                myDialog.show();
            }
        });
    }
}
