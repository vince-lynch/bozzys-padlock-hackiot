package bsdq.bsdq.Gesture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.View.MeasureSpec;

public class GestureRound extends View {
    private float EXCIRCLE_RADIUS;
    private int GRSTURE_STATE;
    private float X_RADIUS;
    private float Y_RAFIUS;
    private GestureData gestureData;
    private Paint mPaint = new Paint(1);

    public GestureRound(Context context, GestureData gestureData2) {
        super(context);
        this.gestureData = gestureData2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        this.EXCIRCLE_RADIUS = ((float) ((size < size2 ? size : size2) / 2)) - this.gestureData.getBorderWidth();
        this.X_RADIUS = (float) (size / 2);
        this.Y_RAFIUS = (float) (size2 / 2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (this.GRSTURE_STATE) {
            case 0:
                this.mPaint.setStyle(Style.STROKE);
                this.mPaint.setStrokeWidth(this.gestureData.getBorderWidth());
                this.mPaint.setColor(this.gestureData.getDefaultBorderColor());
                canvas.drawCircle(this.X_RADIUS, this.Y_RAFIUS, this.EXCIRCLE_RADIUS, this.mPaint);
                this.mPaint.setStyle(Style.FILL);
                this.mPaint.setColor(this.gestureData.getDefaultFilletColor());
                canvas.drawCircle(this.X_RADIUS, this.Y_RAFIUS, this.EXCIRCLE_RADIUS * this.gestureData.getFilletWidth(), this.mPaint);
                return;
            case 1:
                this.mPaint.setStyle(Style.STROKE);
                this.mPaint.setStrokeWidth(this.gestureData.getBorderWidth());
                this.mPaint.setColor(this.gestureData.getSelectBorderColor());
                canvas.drawCircle(this.X_RADIUS, this.Y_RAFIUS, this.EXCIRCLE_RADIUS, this.mPaint);
                this.mPaint.setStyle(Style.FILL);
                this.mPaint.setColor(this.gestureData.getSelectFilletColor());
                canvas.drawCircle(this.X_RADIUS, this.Y_RAFIUS, this.EXCIRCLE_RADIUS * this.gestureData.getFilletWidth(), this.mPaint);
                return;
            case 2:
                this.mPaint.setStyle(Style.STROKE);
                this.mPaint.setStrokeWidth(this.gestureData.getBorderWidth());
                this.mPaint.setColor(this.gestureData.getCorrectBorderColor());
                canvas.drawCircle(this.X_RADIUS, this.Y_RAFIUS, this.EXCIRCLE_RADIUS, this.mPaint);
                this.mPaint.setStyle(Style.FILL);
                this.mPaint.setColor(this.gestureData.getCorrectFilletColor());
                canvas.drawCircle(this.X_RADIUS, this.Y_RAFIUS, this.EXCIRCLE_RADIUS * this.gestureData.getFilletWidth(), this.mPaint);
                return;
            case 3:
                this.mPaint.setStyle(Style.STROKE);
                this.mPaint.setStrokeWidth(this.gestureData.getBorderWidth());
                this.mPaint.setColor(this.gestureData.getErrorBorderColor());
                canvas.drawCircle(this.X_RADIUS, this.Y_RAFIUS, this.EXCIRCLE_RADIUS, this.mPaint);
                this.mPaint.setStyle(Style.FILL);
                this.mPaint.setColor(this.gestureData.getErrorFilletColor());
                canvas.drawCircle(this.X_RADIUS, this.Y_RAFIUS, this.EXCIRCLE_RADIUS * this.gestureData.getFilletWidth(), this.mPaint);
                return;
            default:
                return;
        }
    }

    public void setState(int i) {
        this.GRSTURE_STATE = i;
        invalidate();
    }
}
