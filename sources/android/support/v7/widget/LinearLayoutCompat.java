package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LinearLayoutCompat extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity;
        public float weight;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = -1;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LinearLayoutCompat_Layout);
            this.weight = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int i, int i2, float f) {
            super(i, i2);
            this.gravity = -1;
            this.weight = f;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
            this.weight = layoutParams.weight;
            this.gravity = layoutParams.gravity;
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    /* access modifiers changed from: 0000 */
    public int getChildrenSkipCount(View view, int i) {
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public int getLocationOffset(View view) {
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public int getNextLocationOffset(View view) {
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public int measureNullChild(int i) {
        return 0;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public LinearLayoutCompat(Context context) {
        this(context, null);
    }

    public LinearLayoutCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LinearLayoutCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.LinearLayoutCompat, i, 0);
        int i2 = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
        if (i2 >= 0) {
            setOrientation(i2);
        }
        int i3 = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
        if (i3 >= 0) {
            setGravity(i3);
        }
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!z) {
            setBaselineAligned(z);
        }
        this.mWeightSum = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
        setDividerDrawable(obtainStyledAttributes.getDrawable(R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = obtainStyledAttributes.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
        obtainStyledAttributes.recycle();
    }

    public void setShowDividers(int i) {
        if (i != this.mShowDividers) {
            requestLayout();
        }
        this.mShowDividers = i;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public void setDividerDrawable(Drawable drawable) {
        if (drawable != this.mDivider) {
            this.mDivider = drawable;
            boolean z = false;
            if (drawable != null) {
                this.mDividerWidth = drawable.getIntrinsicWidth();
                this.mDividerHeight = drawable.getIntrinsicHeight();
            } else {
                this.mDividerWidth = 0;
                this.mDividerHeight = 0;
            }
            if (drawable == null) {
                z = true;
            }
            setWillNotDraw(z);
            requestLayout();
        }
    }

    public void setDividerPadding(int i) {
        this.mDividerPadding = i;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mDivider != null) {
            if (this.mOrientation == 1) {
                drawDividersVertical(canvas);
            } else {
                drawDividersHorizontal(canvas);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawDividersVertical(Canvas canvas) {
        int i;
        int virtualChildCount = getVirtualChildCount();
        for (int i2 = 0; i2 < virtualChildCount; i2++) {
            View virtualChildAt = getVirtualChildAt(i2);
            if (!(virtualChildAt == null || virtualChildAt.getVisibility() == 8 || !hasDividerBeforeChildAt(i2))) {
                drawHorizontalDivider(canvas, (virtualChildAt.getTop() - ((LayoutParams) virtualChildAt.getLayoutParams()).topMargin) - this.mDividerHeight);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View virtualChildAt2 = getVirtualChildAt(virtualChildCount - 1);
            if (virtualChildAt2 == null) {
                i = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
            } else {
                i = virtualChildAt2.getBottom() + ((LayoutParams) virtualChildAt2.getLayoutParams()).bottomMargin;
            }
            drawHorizontalDivider(canvas, i);
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawDividersHorizontal(Canvas canvas) {
        int i;
        int i2;
        int virtualChildCount = getVirtualChildCount();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        for (int i3 = 0; i3 < virtualChildCount; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (!(virtualChildAt == null || virtualChildAt.getVisibility() == 8 || !hasDividerBeforeChildAt(i3))) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (isLayoutRtl) {
                    i2 = virtualChildAt.getRight() + layoutParams.rightMargin;
                } else {
                    i2 = (virtualChildAt.getLeft() - layoutParams.leftMargin) - this.mDividerWidth;
                }
                drawVerticalDivider(canvas, i2);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View virtualChildAt2 = getVirtualChildAt(virtualChildCount - 1);
            if (virtualChildAt2 != null) {
                LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                if (isLayoutRtl) {
                    i = (virtualChildAt2.getLeft() - layoutParams2.leftMargin) - this.mDividerWidth;
                } else {
                    i = virtualChildAt2.getRight() + layoutParams2.rightMargin;
                }
            } else if (isLayoutRtl) {
                i = getPaddingLeft();
            } else {
                i = (getWidth() - getPaddingRight()) - this.mDividerWidth;
            }
            drawVerticalDivider(canvas, i);
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawHorizontalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, i, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + i);
        this.mDivider.draw(canvas);
    }

    /* access modifiers changed from: 0000 */
    public void drawVerticalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(i, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + i, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    public void setBaselineAligned(boolean z) {
        this.mBaselineAligned = z;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    public void setMeasureWithLargestChildEnabled(boolean z) {
        this.mUseLargestChild = z;
    }

    public int getBaseline() {
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        if (getChildCount() > this.mBaselineAlignedChildIndex) {
            View childAt = getChildAt(this.mBaselineAlignedChildIndex);
            int baseline = childAt.getBaseline();
            if (baseline != -1) {
                int i = this.mBaselineChildTop;
                if (this.mOrientation == 1) {
                    int i2 = this.mGravity & 112;
                    if (i2 != 48) {
                        if (i2 == 16) {
                            i += ((((getBottom() - getTop()) - getPaddingTop()) - getPaddingBottom()) - this.mTotalLength) / 2;
                        } else if (i2 == 80) {
                            i = ((getBottom() - getTop()) - getPaddingBottom()) - this.mTotalLength;
                        }
                    }
                }
                return i + ((LayoutParams) childAt.getLayoutParams()).topMargin + baseline;
            } else if (this.mBaselineAlignedChildIndex == 0) {
                return -1;
            } else {
                throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
            }
        } else {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    public void setBaselineAlignedChildIndex(int i) {
        if (i < 0 || i >= getChildCount()) {
            StringBuilder sb = new StringBuilder();
            sb.append("base aligned child index out of range (0, ");
            sb.append(getChildCount());
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        }
        this.mBaselineAlignedChildIndex = i;
    }

    /* access modifiers changed from: 0000 */
    public View getVirtualChildAt(int i) {
        return getChildAt(i);
    }

    /* access modifiers changed from: 0000 */
    public int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    public void setWeightSum(float f) {
        this.mWeightSum = Math.max(0.0f, f);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.mOrientation == 1) {
            measureVertical(i, i2);
        } else {
            measureHorizontal(i, i2);
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasDividerBeforeChildAt(int i) {
        boolean z = false;
        if (i == 0) {
            if ((this.mShowDividers & 1) != 0) {
                z = true;
            }
            return z;
        } else if (i == getChildCount()) {
            if ((this.mShowDividers & 4) != 0) {
                z = true;
            }
            return z;
        } else if ((this.mShowDividers & 2) == 0) {
            return false;
        } else {
            int i2 = i - 1;
            while (true) {
                if (i2 < 0) {
                    break;
                } else if (getChildAt(i2).getVisibility() != 8) {
                    z = true;
                    break;
                } else {
                    i2--;
                }
            }
            return z;
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0337  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x017a  */
    public void measureVertical(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        boolean z;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        int i16;
        LayoutParams layoutParams;
        int i17;
        int i18;
        View view;
        int i19;
        boolean z2;
        int i20;
        int i21;
        int i22;
        int i23;
        int i24 = i;
        int i25 = i2;
        int i26 = 0;
        this.mTotalLength = 0;
        int virtualChildCount = getVirtualChildCount();
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        int i27 = this.mBaselineAlignedChildIndex;
        boolean z3 = this.mUseLargestChild;
        float f = 0.0f;
        int i28 = 0;
        int i29 = Integer.MIN_VALUE;
        int i30 = 0;
        int i31 = 0;
        boolean z4 = false;
        boolean z5 = true;
        boolean z6 = false;
        int i32 = 0;
        while (i31 < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i31);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(i31);
                i12 = virtualChildCount;
                i13 = mode2;
            } else {
                int i33 = i28;
                if (virtualChildAt.getVisibility() == 8) {
                    i31 += getChildrenSkipCount(virtualChildAt, i31);
                    i12 = virtualChildCount;
                    i13 = mode2;
                    i28 = i33;
                } else {
                    if (hasDividerBeforeChildAt(i31)) {
                        this.mTotalLength += this.mDividerHeight;
                    }
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt.getLayoutParams();
                    float f2 = f + layoutParams2.weight;
                    if (mode2 == 1073741824 && layoutParams2.height == 0 && layoutParams2.weight > 0.0f) {
                        int i34 = this.mTotalLength;
                        i16 = i29;
                        this.mTotalLength = Math.max(i34, layoutParams2.topMargin + i34 + layoutParams2.bottomMargin);
                        i14 = i30;
                        view = virtualChildAt;
                        i22 = virtualChildCount;
                        i23 = mode2;
                        i15 = i32;
                        i18 = i33;
                        z4 = true;
                        i17 = i31;
                        layoutParams = layoutParams2;
                    } else {
                        int i35 = i29;
                        if (layoutParams2.height != 0 || layoutParams2.weight <= 0.0f) {
                            i21 = Integer.MIN_VALUE;
                        } else {
                            layoutParams2.height = -2;
                            i21 = 0;
                        }
                        i18 = i33;
                        int i36 = i21;
                        int i37 = i35;
                        View view2 = virtualChildAt;
                        i22 = virtualChildCount;
                        i23 = mode2;
                        i17 = i31;
                        i14 = i30;
                        i15 = i32;
                        layoutParams = layoutParams2;
                        measureChildBeforeLayout(virtualChildAt, i31, i, 0, i2, f2 == 0.0f ? this.mTotalLength : 0);
                        int i38 = i36;
                        if (i38 != Integer.MIN_VALUE) {
                            layoutParams.height = i38;
                        }
                        int measuredHeight = view2.getMeasuredHeight();
                        int i39 = this.mTotalLength;
                        view = view2;
                        this.mTotalLength = Math.max(i39, i39 + measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin + getNextLocationOffset(view));
                        i16 = z3 ? Math.max(measuredHeight, i37) : i37;
                    }
                    if (i27 >= 0 && i27 == i17 + 1) {
                        this.mBaselineChildTop = this.mTotalLength;
                    }
                    if (i17 >= i27 || layoutParams.weight <= 0.0f) {
                        if (mode != 1073741824) {
                            i19 = -1;
                            if (layoutParams.width == -1) {
                                z2 = true;
                                z6 = true;
                                int i40 = layoutParams.leftMargin + layoutParams.rightMargin;
                                int measuredWidth = view.getMeasuredWidth() + i40;
                                int max = Math.max(i18, measuredWidth);
                                int combineMeasuredStates = ViewUtils.combineMeasuredStates(i26, ViewCompat.getMeasuredState(view));
                                boolean z7 = !z5 && layoutParams.width == i19;
                                if (layoutParams.weight <= 0.0f) {
                                    if (!z2) {
                                        i40 = measuredWidth;
                                    }
                                    i20 = Math.max(i14, i40);
                                } else {
                                    i20 = i14;
                                    if (z2) {
                                        measuredWidth = i40;
                                    }
                                    i15 = Math.max(i15, measuredWidth);
                                }
                                z5 = z7;
                                i28 = max;
                                i26 = combineMeasuredStates;
                                i30 = i20;
                                i29 = i16;
                                i32 = i15;
                                i31 = getChildrenSkipCount(view, i17) + i17;
                                f = f2;
                                i31++;
                                mode2 = i13;
                                virtualChildCount = i12;
                                int i41 = i;
                                int i42 = i2;
                            }
                        } else {
                            i19 = -1;
                        }
                        z2 = false;
                        int i402 = layoutParams.leftMargin + layoutParams.rightMargin;
                        int measuredWidth2 = view.getMeasuredWidth() + i402;
                        int max2 = Math.max(i18, measuredWidth2);
                        int combineMeasuredStates2 = ViewUtils.combineMeasuredStates(i26, ViewCompat.getMeasuredState(view));
                        if (!z5) {
                        }
                        if (layoutParams.weight <= 0.0f) {
                        }
                        z5 = z7;
                        i28 = max2;
                        i26 = combineMeasuredStates2;
                        i30 = i20;
                        i29 = i16;
                        i32 = i15;
                        i31 = getChildrenSkipCount(view, i17) + i17;
                        f = f2;
                        i31++;
                        mode2 = i13;
                        virtualChildCount = i12;
                        int i412 = i;
                        int i422 = i2;
                    } else {
                        throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                    }
                }
            }
            i31++;
            mode2 = i13;
            virtualChildCount = i12;
            int i4122 = i;
            int i4222 = i2;
        }
        int i43 = i28;
        int i44 = i29;
        int i45 = virtualChildCount;
        int i46 = mode2;
        int i47 = i32;
        int i48 = i30;
        if (this.mTotalLength > 0) {
            i3 = i45;
            if (hasDividerBeforeChildAt(i3)) {
                this.mTotalLength += this.mDividerHeight;
            }
        } else {
            i3 = i45;
        }
        if (z3) {
            i4 = i46;
            if (i4 == Integer.MIN_VALUE || i4 == 0) {
                this.mTotalLength = 0;
                int i49 = 0;
                while (i49 < i3) {
                    View virtualChildAt2 = getVirtualChildAt(i49);
                    if (virtualChildAt2 == null) {
                        this.mTotalLength += measureNullChild(i49);
                    } else if (virtualChildAt2.getVisibility() == 8) {
                        i49 += getChildrenSkipCount(virtualChildAt2, i49);
                    } else {
                        LayoutParams layoutParams3 = (LayoutParams) virtualChildAt2.getLayoutParams();
                        int i50 = this.mTotalLength;
                        this.mTotalLength = Math.max(i50, i50 + i44 + layoutParams3.topMargin + layoutParams3.bottomMargin + getNextLocationOffset(virtualChildAt2));
                    }
                    i49++;
                }
            }
        } else {
            i4 = i46;
        }
        this.mTotalLength += getPaddingTop() + getPaddingBottom();
        int i51 = i48;
        int i52 = i2;
        int resolveSizeAndState = ViewCompat.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), i52, 0);
        int i53 = (16777215 & resolveSizeAndState) - this.mTotalLength;
        if (z4 || (i53 != 0 && f > 0.0f)) {
            if (this.mWeightSum > 0.0f) {
                f = this.mWeightSum;
            }
            this.mTotalLength = 0;
            float f3 = f;
            int i54 = 0;
            while (i54 < i3) {
                View virtualChildAt3 = getVirtualChildAt(i54);
                if (virtualChildAt3.getVisibility() == 8) {
                    i8 = i4;
                    i7 = i53;
                    int i55 = i;
                } else {
                    LayoutParams layoutParams4 = (LayoutParams) virtualChildAt3.getLayoutParams();
                    float f4 = layoutParams4.weight;
                    if (f4 > 0.0f) {
                        int i56 = (int) ((((float) i53) * f4) / f3);
                        float f5 = f3 - f4;
                        int i57 = i53 - i56;
                        int childMeasureSpec = getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + layoutParams4.leftMargin + layoutParams4.rightMargin, layoutParams4.width);
                        if (layoutParams4.height == 0) {
                            i11 = 1073741824;
                            if (i4 == 1073741824) {
                                if (i56 <= 0) {
                                    i56 = 0;
                                }
                                virtualChildAt3.measure(childMeasureSpec, MeasureSpec.makeMeasureSpec(i56, 1073741824));
                                i26 = ViewUtils.combineMeasuredStates(i26, ViewCompat.getMeasuredState(virtualChildAt3) & InputDeviceCompat.SOURCE_ANY);
                                f3 = f5;
                                i9 = i57;
                            }
                        } else {
                            i11 = 1073741824;
                        }
                        int measuredHeight2 = virtualChildAt3.getMeasuredHeight() + i56;
                        if (measuredHeight2 < 0) {
                            measuredHeight2 = 0;
                        }
                        virtualChildAt3.measure(childMeasureSpec, MeasureSpec.makeMeasureSpec(measuredHeight2, i11));
                        i26 = ViewUtils.combineMeasuredStates(i26, ViewCompat.getMeasuredState(virtualChildAt3) & InputDeviceCompat.SOURCE_ANY);
                        f3 = f5;
                        i9 = i57;
                    } else {
                        i9 = i53;
                        int i58 = i;
                    }
                    i8 = i4;
                    int i59 = layoutParams4.leftMargin + layoutParams4.rightMargin;
                    int measuredWidth3 = virtualChildAt3.getMeasuredWidth() + i59;
                    i43 = Math.max(i43, measuredWidth3);
                    int i60 = measuredWidth3;
                    if (mode != 1073741824) {
                        i7 = i9;
                        i10 = -1;
                        if (layoutParams4.width == -1) {
                            z = true;
                            if (!z) {
                                i59 = i60;
                            }
                            i47 = Math.max(i47, i59);
                            boolean z8 = !z5 && layoutParams4.width == i10;
                            int i61 = this.mTotalLength;
                            this.mTotalLength = Math.max(i61, i61 + virtualChildAt3.getMeasuredHeight() + layoutParams4.topMargin + layoutParams4.bottomMargin + getNextLocationOffset(virtualChildAt3));
                            z5 = z8;
                        }
                    } else {
                        i7 = i9;
                        i10 = -1;
                    }
                    z = false;
                    if (!z) {
                    }
                    i47 = Math.max(i47, i59);
                    if (!z5) {
                    }
                    int i612 = this.mTotalLength;
                    this.mTotalLength = Math.max(i612, i612 + virtualChildAt3.getMeasuredHeight() + layoutParams4.topMargin + layoutParams4.bottomMargin + getNextLocationOffset(virtualChildAt3));
                    z5 = z8;
                }
                i54++;
                i4 = i8;
                i53 = i7;
            }
            i5 = i;
            this.mTotalLength += getPaddingTop() + getPaddingBottom();
            i6 = i47;
        } else {
            i6 = Math.max(i47, i51);
            if (z3 && i4 != 1073741824) {
                for (int i62 = 0; i62 < i3; i62++) {
                    View virtualChildAt4 = getVirtualChildAt(i62);
                    if (!(virtualChildAt4 == null || virtualChildAt4.getVisibility() == 8 || ((LayoutParams) virtualChildAt4.getLayoutParams()).weight <= 0.0f)) {
                        virtualChildAt4.measure(MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredWidth(), 1073741824), MeasureSpec.makeMeasureSpec(i44, 1073741824));
                    }
                }
            }
            i5 = i;
        }
        if (z5 || mode == 1073741824) {
            i6 = i43;
        }
        setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(i6 + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), i5, i26), resolveSizeAndState);
        if (z6) {
            forceUniformWidth(i3, i52);
        }
    }

    private void forceUniformWidth(int i, int i2) {
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.width == -1) {
                    int i4 = layoutParams.height;
                    layoutParams.height = virtualChildAt.getMeasuredHeight();
                    measureChildWithMargins(virtualChildAt, makeMeasureSpec, 0, i2, 0);
                    layoutParams.height = i4;
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0434  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0190  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01de  */
    public void measureHorizontal(int i, int i2) {
        int[] iArr;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        float f;
        int i8;
        boolean z;
        int i9;
        boolean z2;
        boolean z3;
        int i10;
        LayoutParams layoutParams;
        int i11;
        View view;
        int i12;
        boolean z4;
        int i13;
        int i14 = i;
        int i15 = i2;
        this.mTotalLength = 0;
        int virtualChildCount = getVirtualChildCount();
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] iArr2 = this.mMaxAscent;
        int[] iArr3 = this.mMaxDescent;
        iArr2[3] = -1;
        iArr2[2] = -1;
        iArr2[1] = -1;
        iArr2[0] = -1;
        iArr3[3] = -1;
        iArr3[2] = -1;
        iArr3[1] = -1;
        iArr3[0] = -1;
        boolean z5 = this.mBaselineAligned;
        boolean z6 = this.mUseLargestChild;
        int i16 = 1073741824;
        boolean z7 = mode == 1073741824;
        int i17 = 0;
        int i18 = Integer.MIN_VALUE;
        float f2 = 0.0f;
        int i19 = 0;
        boolean z8 = false;
        int i20 = 0;
        int i21 = 0;
        int i22 = 0;
        boolean z9 = true;
        boolean z10 = false;
        while (true) {
            iArr = iArr3;
            i3 = 8;
            if (i17 >= virtualChildCount) {
                break;
            }
            View virtualChildAt = getVirtualChildAt(i17);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(i17);
            } else if (virtualChildAt.getVisibility() == 8) {
                i17 += getChildrenSkipCount(virtualChildAt, i17);
            } else {
                if (hasDividerBeforeChildAt(i17)) {
                    this.mTotalLength += this.mDividerWidth;
                }
                LayoutParams layoutParams2 = (LayoutParams) virtualChildAt.getLayoutParams();
                f2 += layoutParams2.weight;
                if (mode == i16 && layoutParams2.width == 0 && layoutParams2.weight > 0.0f) {
                    if (z7) {
                        this.mTotalLength += layoutParams2.leftMargin + layoutParams2.rightMargin;
                    } else {
                        int i23 = this.mTotalLength;
                        this.mTotalLength = Math.max(i23, layoutParams2.leftMargin + i23 + layoutParams2.rightMargin);
                    }
                    if (z5) {
                        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
                        virtualChildAt.measure(makeMeasureSpec, makeMeasureSpec);
                        i10 = i17;
                        z3 = z6;
                        z2 = z5;
                        layoutParams = layoutParams2;
                        view = virtualChildAt;
                        i11 = -1;
                    } else {
                        i10 = i17;
                        z3 = z6;
                        z2 = z5;
                        layoutParams = layoutParams2;
                        view = virtualChildAt;
                        i12 = 1073741824;
                        i11 = -1;
                        z8 = true;
                        if (mode2 == i12 && layoutParams.height == i11) {
                            z4 = true;
                            z10 = true;
                        } else {
                            z4 = false;
                        }
                        int i24 = layoutParams.topMargin + layoutParams.bottomMargin;
                        int measuredHeight = view.getMeasuredHeight() + i24;
                        int combineMeasuredStates = ViewUtils.combineMeasuredStates(i22, ViewCompat.getMeasuredState(view));
                        if (z2) {
                            int baseline = view.getBaseline();
                            if (baseline != i11) {
                                int i25 = ((((layoutParams.gravity < 0 ? this.mGravity : layoutParams.gravity) & 112) >> 4) & -2) >> 1;
                                iArr2[i25] = Math.max(iArr2[i25], baseline);
                                iArr[i25] = Math.max(iArr[i25], measuredHeight - baseline);
                            }
                        }
                        int max = Math.max(i19, measuredHeight);
                        boolean z11 = !z9 && layoutParams.height == i11;
                        if (layoutParams.weight <= 0.0f) {
                            if (!z4) {
                                i24 = measuredHeight;
                            }
                            i21 = Math.max(i21, i24);
                        } else {
                            int i26 = i21;
                            if (z4) {
                                measuredHeight = i24;
                            }
                            i20 = Math.max(i20, measuredHeight);
                            i21 = i26;
                        }
                        int i27 = i10;
                        i19 = max;
                        i17 = getChildrenSkipCount(view, i27) + i27;
                        i22 = combineMeasuredStates;
                        z9 = z11;
                        i17++;
                        iArr3 = iArr;
                        z6 = z3;
                        z5 = z2;
                        i16 = 1073741824;
                        int i28 = i;
                        int i29 = i2;
                    }
                } else {
                    if (layoutParams2.width != 0 || layoutParams2.weight <= 0.0f) {
                        i13 = Integer.MIN_VALUE;
                    } else {
                        layoutParams2.width = -2;
                        i13 = 0;
                    }
                    i10 = i17;
                    int i30 = i13;
                    z3 = z6;
                    z2 = z5;
                    layoutParams = layoutParams2;
                    i11 = -1;
                    View view2 = virtualChildAt;
                    measureChildBeforeLayout(virtualChildAt, i10, i, f2 == 0.0f ? this.mTotalLength : 0, i2, 0);
                    if (i30 != Integer.MIN_VALUE) {
                        layoutParams.width = i30;
                    }
                    int measuredWidth = view2.getMeasuredWidth();
                    if (z7) {
                        view = view2;
                        this.mTotalLength += layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(view);
                    } else {
                        view = view2;
                        int i31 = this.mTotalLength;
                        this.mTotalLength = Math.max(i31, i31 + measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin + getNextLocationOffset(view));
                    }
                    if (z3) {
                        i18 = Math.max(measuredWidth, i18);
                    }
                }
                i12 = 1073741824;
                if (mode2 == i12) {
                }
                z4 = false;
                int i242 = layoutParams.topMargin + layoutParams.bottomMargin;
                int measuredHeight2 = view.getMeasuredHeight() + i242;
                int combineMeasuredStates2 = ViewUtils.combineMeasuredStates(i22, ViewCompat.getMeasuredState(view));
                if (z2) {
                }
                int max2 = Math.max(i19, measuredHeight2);
                if (!z9) {
                }
                if (layoutParams.weight <= 0.0f) {
                }
                int i272 = i10;
                i19 = max2;
                i17 = getChildrenSkipCount(view, i272) + i272;
                i22 = combineMeasuredStates2;
                z9 = z11;
                i17++;
                iArr3 = iArr;
                z6 = z3;
                z5 = z2;
                i16 = 1073741824;
                int i282 = i;
                int i292 = i2;
            }
            z3 = z6;
            z2 = z5;
            i17++;
            iArr3 = iArr;
            z6 = z3;
            z5 = z2;
            i16 = 1073741824;
            int i2822 = i;
            int i2922 = i2;
        }
        boolean z12 = z6;
        boolean z13 = z5;
        int i32 = i19;
        int i33 = i20;
        int i34 = i21;
        int i35 = i22;
        if (this.mTotalLength > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerWidth;
        }
        if (!(iArr2[1] == -1 && iArr2[0] == -1 && iArr2[2] == -1 && iArr2[3] == -1)) {
            i32 = Math.max(i32, Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))) + Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))));
        }
        if (z12 && (mode == Integer.MIN_VALUE || mode == 0)) {
            this.mTotalLength = 0;
            int i36 = 0;
            while (i36 < virtualChildCount) {
                View virtualChildAt2 = getVirtualChildAt(i36);
                if (virtualChildAt2 == null) {
                    this.mTotalLength += measureNullChild(i36);
                } else if (virtualChildAt2.getVisibility() == i3) {
                    i36 += getChildrenSkipCount(virtualChildAt2, i36);
                } else {
                    LayoutParams layoutParams3 = (LayoutParams) virtualChildAt2.getLayoutParams();
                    if (z7) {
                        this.mTotalLength += layoutParams3.leftMargin + i18 + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt2);
                    } else {
                        int i37 = this.mTotalLength;
                        this.mTotalLength = Math.max(i37, i37 + i18 + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt2));
                    }
                }
                i36++;
                i3 = 8;
            }
        }
        this.mTotalLength += getPaddingLeft() + getPaddingRight();
        int resolveSizeAndState = ViewCompat.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), i, 0);
        int i38 = (16777215 & resolveSizeAndState) - this.mTotalLength;
        if (z8 || (i38 != 0 && f2 > 0.0f)) {
            float f3 = this.mWeightSum > 0.0f ? this.mWeightSum : f2;
            iArr2[3] = -1;
            iArr2[2] = -1;
            iArr2[1] = -1;
            iArr2[0] = -1;
            iArr[3] = -1;
            iArr[2] = -1;
            iArr[1] = -1;
            iArr[0] = -1;
            this.mTotalLength = 0;
            int i39 = i33;
            int i40 = -1;
            float f4 = f3;
            int i41 = 0;
            while (i41 < virtualChildCount) {
                View virtualChildAt3 = getVirtualChildAt(i41);
                if (virtualChildAt3 == null || virtualChildAt3.getVisibility() == 8) {
                    int i42 = i38;
                    int i43 = i2;
                    i7 = i42;
                } else {
                    LayoutParams layoutParams4 = (LayoutParams) virtualChildAt3.getLayoutParams();
                    float f5 = layoutParams4.weight;
                    if (f5 > 0.0f) {
                        int i44 = (int) ((((float) i38) * f5) / f4);
                        f = f4 - f5;
                        i7 = i38 - i44;
                        int childMeasureSpec = getChildMeasureSpec(i2, getPaddingTop() + getPaddingBottom() + layoutParams4.topMargin + layoutParams4.bottomMargin, layoutParams4.height);
                        if (layoutParams4.width == 0) {
                            i9 = 1073741824;
                            if (mode == 1073741824) {
                                if (i44 <= 0) {
                                    i44 = 0;
                                }
                                virtualChildAt3.measure(MeasureSpec.makeMeasureSpec(i44, 1073741824), childMeasureSpec);
                                i35 = ViewUtils.combineMeasuredStates(i35, ViewCompat.getMeasuredState(virtualChildAt3) & ViewCompat.MEASURED_STATE_MASK);
                            }
                        } else {
                            i9 = 1073741824;
                        }
                        int measuredWidth2 = virtualChildAt3.getMeasuredWidth() + i44;
                        if (measuredWidth2 < 0) {
                            measuredWidth2 = 0;
                        }
                        virtualChildAt3.measure(MeasureSpec.makeMeasureSpec(measuredWidth2, i9), childMeasureSpec);
                        i35 = ViewUtils.combineMeasuredStates(i35, ViewCompat.getMeasuredState(virtualChildAt3) & ViewCompat.MEASURED_STATE_MASK);
                    } else {
                        int i45 = i38;
                        int i46 = i2;
                        f = f4;
                        i7 = i45;
                    }
                    if (z7) {
                        this.mTotalLength += virtualChildAt3.getMeasuredWidth() + layoutParams4.leftMargin + layoutParams4.rightMargin + getNextLocationOffset(virtualChildAt3);
                    } else {
                        int i47 = this.mTotalLength;
                        this.mTotalLength = Math.max(i47, virtualChildAt3.getMeasuredWidth() + i47 + layoutParams4.leftMargin + layoutParams4.rightMargin + getNextLocationOffset(virtualChildAt3));
                    }
                    boolean z14 = mode2 != 1073741824 && layoutParams4.height == -1;
                    int i48 = layoutParams4.topMargin + layoutParams4.bottomMargin;
                    int measuredHeight3 = virtualChildAt3.getMeasuredHeight() + i48;
                    i40 = Math.max(i40, measuredHeight3);
                    if (!z14) {
                        i48 = measuredHeight3;
                    }
                    int max3 = Math.max(i39, i48);
                    if (z9) {
                        i8 = -1;
                        if (layoutParams4.height == -1) {
                            z = true;
                            if (z13) {
                                int baseline2 = virtualChildAt3.getBaseline();
                                if (baseline2 != i8) {
                                    int i49 = ((((layoutParams4.gravity < 0 ? this.mGravity : layoutParams4.gravity) & 112) >> 4) & -2) >> 1;
                                    iArr2[i49] = Math.max(iArr2[i49], baseline2);
                                    iArr[i49] = Math.max(iArr[i49], measuredHeight3 - baseline2);
                                    i39 = max3;
                                    z9 = z;
                                    f4 = f;
                                }
                            }
                            i39 = max3;
                            z9 = z;
                            f4 = f;
                        }
                    } else {
                        i8 = -1;
                    }
                    z = false;
                    if (z13) {
                    }
                    i39 = max3;
                    z9 = z;
                    f4 = f;
                }
                i41++;
                i38 = i7;
                int i50 = i;
            }
            i5 = i2;
            this.mTotalLength += getPaddingLeft() + getPaddingRight();
            if (iArr2[1] == -1 && iArr2[0] == -1 && iArr2[2] == -1 && iArr2[3] == -1) {
                i4 = i40;
            } else {
                i4 = Math.max(i40, Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))) + Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))));
            }
            i6 = i39;
        } else {
            i6 = Math.max(i33, i34);
            if (z12 && mode != 1073741824) {
                for (int i51 = 0; i51 < virtualChildCount; i51++) {
                    View virtualChildAt4 = getVirtualChildAt(i51);
                    if (!(virtualChildAt4 == null || virtualChildAt4.getVisibility() == 8 || ((LayoutParams) virtualChildAt4.getLayoutParams()).weight <= 0.0f)) {
                        virtualChildAt4.measure(MeasureSpec.makeMeasureSpec(i18, 1073741824), MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredHeight(), 1073741824));
                    }
                }
            }
            i5 = i2;
        }
        if (!z9 && mode2 != 1073741824) {
            i4 = i6;
        }
        setMeasuredDimension(resolveSizeAndState | (-16777216 & i35), ViewCompat.resolveSizeAndState(Math.max(i4 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight()), i5, i35 << 16));
        if (z10) {
            forceUniformHeight(virtualChildCount, i);
        }
    }

    private void forceUniformHeight(int i, int i2) {
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.height == -1) {
                    int i4 = layoutParams.width;
                    layoutParams.width = virtualChildAt.getMeasuredWidth();
                    measureChildWithMargins(virtualChildAt, i2, 0, makeMeasureSpec, 0);
                    layoutParams.width = i4;
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void measureChildBeforeLayout(View view, int i, int i2, int i3, int i4, int i5) {
        measureChildWithMargins(view, i2, i3, i4, i5);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mOrientation == 1) {
            layoutVertical(i, i2, i3, i4);
        } else {
            layoutHorizontal(i, i2, i3, i4);
        }
    }

    /* access modifiers changed from: 0000 */
    public void layoutVertical(int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int paddingLeft = getPaddingLeft();
        int i7 = i3 - i;
        int paddingRight = i7 - getPaddingRight();
        int paddingRight2 = (i7 - paddingLeft) - getPaddingRight();
        int virtualChildCount = getVirtualChildCount();
        int i8 = this.mGravity & 112;
        int i9 = this.mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i8 == 16) {
            i5 = (((i4 - i2) - this.mTotalLength) / 2) + getPaddingTop();
        } else if (i8 != 80) {
            i5 = getPaddingTop();
        } else {
            i5 = ((getPaddingTop() + i4) - i2) - this.mTotalLength;
        }
        int i10 = 0;
        while (i10 < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i10);
            if (virtualChildAt == null) {
                i5 += measureNullChild(i10);
            } else if (virtualChildAt.getVisibility() != 8) {
                int measuredWidth = virtualChildAt.getMeasuredWidth();
                int measuredHeight = virtualChildAt.getMeasuredHeight();
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                int i11 = layoutParams.gravity;
                if (i11 < 0) {
                    i11 = i9;
                }
                int absoluteGravity = GravityCompat.getAbsoluteGravity(i11, ViewCompat.getLayoutDirection(this)) & 7;
                if (absoluteGravity == 1) {
                    i6 = ((((paddingRight2 - measuredWidth) / 2) + paddingLeft) + layoutParams.leftMargin) - layoutParams.rightMargin;
                } else if (absoluteGravity != 5) {
                    i6 = layoutParams.leftMargin + paddingLeft;
                } else {
                    i6 = (paddingRight - measuredWidth) - layoutParams.rightMargin;
                }
                int i12 = i6;
                if (hasDividerBeforeChildAt(i10)) {
                    i5 += this.mDividerHeight;
                }
                int i13 = i5 + layoutParams.topMargin;
                LayoutParams layoutParams2 = layoutParams;
                setChildFrame(virtualChildAt, i12, i13 + getLocationOffset(virtualChildAt), measuredWidth, measuredHeight);
                i10 += getChildrenSkipCount(virtualChildAt, i10);
                i5 = i13 + measuredHeight + layoutParams2.bottomMargin + getNextLocationOffset(virtualChildAt);
            }
            i10++;
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00bf  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00f6  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x010a  */
    public void layoutHorizontal(int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int paddingTop = getPaddingTop();
        int i16 = i4 - i2;
        int paddingBottom = i16 - getPaddingBottom();
        int paddingBottom2 = (i16 - paddingTop) - getPaddingBottom();
        int virtualChildCount = getVirtualChildCount();
        int i17 = this.mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int i18 = this.mGravity & 112;
        boolean z = this.mBaselineAligned;
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        int absoluteGravity = GravityCompat.getAbsoluteGravity(i17, ViewCompat.getLayoutDirection(this));
        if (absoluteGravity == 1) {
            i5 = (((i3 - i) - this.mTotalLength) / 2) + getPaddingLeft();
        } else if (absoluteGravity != 5) {
            i5 = getPaddingLeft();
        } else {
            i5 = ((getPaddingLeft() + i3) - i) - this.mTotalLength;
        }
        if (isLayoutRtl) {
            i7 = virtualChildCount - 1;
            i6 = -1;
        } else {
            i7 = 0;
            i6 = 1;
        }
        int i19 = 0;
        while (i19 < virtualChildCount) {
            int i20 = i7 + (i6 * i19);
            View virtualChildAt = getVirtualChildAt(i20);
            if (virtualChildAt == null) {
                i5 += measureNullChild(i20);
            } else if (virtualChildAt.getVisibility() != 8) {
                int measuredWidth = virtualChildAt.getMeasuredWidth();
                int measuredHeight = virtualChildAt.getMeasuredHeight();
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (z) {
                    i11 = i19;
                    i10 = virtualChildCount;
                    if (layoutParams.height != -1) {
                        i12 = virtualChildAt.getBaseline();
                        i13 = layoutParams.gravity;
                        if (i13 < 0) {
                            i13 = i18;
                        }
                        i14 = i13 & 112;
                        i9 = i18;
                        if (i14 != 16) {
                            i15 = ((((paddingBottom2 - measuredHeight) / 2) + paddingTop) + layoutParams.topMargin) - layoutParams.bottomMargin;
                        } else if (i14 == 48) {
                            int i21 = layoutParams.topMargin + paddingTop;
                            if (i12 != -1) {
                                i21 += iArr[1] - i12;
                            }
                            i15 = i21;
                        } else if (i14 != 80) {
                            i15 = paddingTop;
                        } else {
                            int i22 = (paddingBottom - measuredHeight) - layoutParams.bottomMargin;
                            if (i12 != -1) {
                                i22 -= iArr2[2] - (virtualChildAt.getMeasuredHeight() - i12);
                            }
                            i15 = i22;
                        }
                        if (hasDividerBeforeChildAt(i20)) {
                            i5 += this.mDividerWidth;
                        }
                        int i23 = layoutParams.leftMargin + i5;
                        View view = virtualChildAt;
                        int i24 = i20;
                        int locationOffset = i23 + getLocationOffset(virtualChildAt);
                        int i25 = i11;
                        i8 = paddingTop;
                        LayoutParams layoutParams2 = layoutParams;
                        setChildFrame(virtualChildAt, locationOffset, i15, measuredWidth, measuredHeight);
                        View view2 = view;
                        i19 = i25 + getChildrenSkipCount(view2, i24);
                        i5 = i23 + measuredWidth + layoutParams2.rightMargin + getNextLocationOffset(view2);
                        i19++;
                        virtualChildCount = i10;
                        i18 = i9;
                        paddingTop = i8;
                    }
                } else {
                    i11 = i19;
                    i10 = virtualChildCount;
                }
                i12 = -1;
                i13 = layoutParams.gravity;
                if (i13 < 0) {
                }
                i14 = i13 & 112;
                i9 = i18;
                if (i14 != 16) {
                }
                if (hasDividerBeforeChildAt(i20)) {
                }
                int i232 = layoutParams.leftMargin + i5;
                View view3 = virtualChildAt;
                int i242 = i20;
                int locationOffset2 = i232 + getLocationOffset(virtualChildAt);
                int i252 = i11;
                i8 = paddingTop;
                LayoutParams layoutParams22 = layoutParams;
                setChildFrame(virtualChildAt, locationOffset2, i15, measuredWidth, measuredHeight);
                View view22 = view3;
                i19 = i252 + getChildrenSkipCount(view22, i242);
                i5 = i232 + measuredWidth + layoutParams22.rightMargin + getNextLocationOffset(view22);
                i19++;
                virtualChildCount = i10;
                i18 = i9;
                paddingTop = i8;
            } else {
                int i26 = i19;
            }
            i8 = paddingTop;
            i10 = virtualChildCount;
            i9 = i18;
            i19++;
            virtualChildCount = i10;
            i18 = i9;
            paddingTop = i8;
        }
    }

    private void setChildFrame(View view, int i, int i2, int i3, int i4) {
        view.layout(i, i2, i3 + i, i4 + i2);
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            requestLayout();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            if ((8388615 & i) == 0) {
                i |= GravityCompat.START;
            }
            if ((i & 112) == 0) {
                i |= 48;
            }
            this.mGravity = i;
            requestLayout();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setHorizontalGravity(int i) {
        int i2 = i & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if ((8388615 & this.mGravity) != i2) {
            this.mGravity = i2 | (this.mGravity & -8388616);
            requestLayout();
        }
    }

    public void setVerticalGravity(int i) {
        int i2 = i & 112;
        if ((this.mGravity & 112) != i2) {
            this.mGravity = i2 | (this.mGravity & -113);
            requestLayout();
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -2);
        }
        if (this.mOrientation == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setClassName(LinearLayoutCompat.class.getName());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(LinearLayoutCompat.class.getName());
        }
    }
}
