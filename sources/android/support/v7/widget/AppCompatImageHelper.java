package android.support.v7.widget;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.ImageView;

@RestrictTo({Scope.LIBRARY_GROUP})
public class AppCompatImageHelper {
    private final ImageView mView;

    public AppCompatImageHelper(ImageView imageView) {
        this.mView = imageView;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0043  */
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        TintTypedArray tintTypedArray;
        TintTypedArray tintTypedArray2 = null;
        try {
            Drawable drawable = this.mView.getDrawable();
            if (drawable == null) {
                tintTypedArray = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attributeSet, R.styleable.AppCompatImageView, i, 0);
                try {
                    int resourceId = tintTypedArray.getResourceId(R.styleable.AppCompatImageView_srcCompat, -1);
                    if (resourceId != -1) {
                        drawable = AppCompatResources.getDrawable(this.mView.getContext(), resourceId);
                        if (drawable != null) {
                            this.mView.setImageDrawable(drawable);
                        }
                    }
                    tintTypedArray2 = tintTypedArray;
                } catch (Throwable th) {
                    th = th;
                    if (tintTypedArray != null) {
                        tintTypedArray.recycle();
                    }
                    throw th;
                }
            }
            if (drawable != null) {
                DrawableUtils.fixDrawable(drawable);
            }
            if (tintTypedArray2 != null) {
                tintTypedArray2.recycle();
            }
        } catch (Throwable th2) {
            th = th2;
            tintTypedArray = tintTypedArray2;
            if (tintTypedArray != null) {
            }
            throw th;
        }
    }

    public void setImageResource(int i) {
        if (i != 0) {
            Drawable drawable = AppCompatResources.getDrawable(this.mView.getContext(), i);
            if (drawable != null) {
                DrawableUtils.fixDrawable(drawable);
            }
            this.mView.setImageDrawable(drawable);
            return;
        }
        this.mView.setImageDrawable(null);
    }

    /* access modifiers changed from: 0000 */
    public boolean hasOverlappingRendering() {
        return VERSION.SDK_INT < 21 || !(this.mView.getBackground() instanceof RippleDrawable);
    }
}
