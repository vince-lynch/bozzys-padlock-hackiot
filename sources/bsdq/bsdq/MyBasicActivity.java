package bsdq.bsdq;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;

public class MyBasicActivity extends Activity {
    private SystemBarTintManager mTintManager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        if (VERSION.SDK_INT >= 19) {
            getWindow().addFlags(67108864);
        }
        this.mTintManager = new SystemBarTintManager(this);
        this.mTintManager.setStatusBarTintEnabled(true);
        this.mTintManager.setNavigationBarTintEnabled(true);
        this.mTintManager.setTintAlpha(255.0f);
    }

    public void setTintColor(int i) {
        this.mTintManager.setTintColor(i);
    }
}
