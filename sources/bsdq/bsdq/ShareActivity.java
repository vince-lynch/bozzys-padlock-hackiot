package bsdq.bsdq;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ShareActivity extends MyBasicActivity {
    private ImageView img_back;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_share);
        super.setTintColor(-16772059);
        this.img_back = (ImageView) findViewById(R.id.img_back);
        this.img_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ShareActivity.this.finish();
            }
        });
    }
}
