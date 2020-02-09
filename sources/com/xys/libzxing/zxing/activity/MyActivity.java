package com.xys.libzxing.zxing.activity;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;

public class MyActivity extends Activity {
    private SystemBarTintManager mTintManager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        if (VERSION.SDK_INT >= 19) {
            getWindow().addFlags(67108864);
        }
        requestWindowFeature(1);
        super.onCreate(bundle);
        this.mTintManager = new SystemBarTintManager(this);
        this.mTintManager.setStatusBarTintEnabled(true);
        this.mTintManager.setNavigationBarTintEnabled(true);
        this.mTintManager.setTintAlpha(255.0f);
        this.mTintManager.setTintColor(-16643815);
    }

    public void setTintColor(int i) {
        this.mTintManager.setTintColor(i);
    }
}
