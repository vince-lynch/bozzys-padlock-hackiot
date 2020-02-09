package bsdq.bsdq.Gesture;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import bsdq.bsdq.R;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GestureCustomView extends RelativeLayout {
    /* access modifiers changed from: private */
    public int COLUMN_NUMBER;
    private int ID_NUMBER;
    /* access modifiers changed from: private */
    public int LINE_NUMBER;
    private int ROUND_NUMBER;
    private String TAG;
    private Context context;
    private String fristData;
    private int gestureBetweenWidth;
    private GestureData gestureData;
    private int gestureViewWidth;
    /* access modifiers changed from: private */
    public GestureRound[][] gestureViews;
    private boolean isAgain;
    /* access modifiers changed from: private */
    public boolean isClick;
    private boolean isRound;
    /* access modifiers changed from: private */
    public List<GestureRound> list;
    /* access modifiers changed from: private */
    public float moveX;
    /* access modifiers changed from: private */
    public float moveY;
    private StringBuffer orderData;
    private Paint paintLine;
    /* access modifiers changed from: private */
    public Path pathLine;
    private OnResultListener resultListener;
    private float startX;
    private float startY;

    public interface OnResultListener {
        void Result(String str);
    }

    public GestureCustomView(Context context2) {
        this(context2, null);
    }

    public GestureCustomView(Context context2, AttributeSet attributeSet) {
        this(context2, attributeSet, 0);
    }

    public GestureCustomView(Context context2, AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
        this.TAG = GestureCustomView.class.getSimpleName();
        this.ID_NUMBER = 130059;
        this.isAgain = true;
        this.isClick = true;
        TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, R.styleable.Gesture_Styles);
        this.gestureData = new GestureData();
        this.gestureData.setLine_Number(obtainStyledAttributes.getInt(17, getResources().getInteger(R.integer.Line_Number)));
        this.gestureData.setColumn_Number(obtainStyledAttributes.getInt(2, getResources().getInteger(R.integer.Column_Number)));
        this.gestureData.setBorderWidth(obtainStyledAttributes.getFloat(1, Float.parseFloat(getResources().getString(R.string.BorderWidth))));
        this.gestureData.setFilletWidth(obtainStyledAttributes.getFloat(15, Float.parseFloat(getResources().getString(R.string.FilletWidth))));
        this.gestureData.setBetweenWidth(obtainStyledAttributes.getFloat(0, Float.parseFloat(getResources().getString(R.string.BetweenWidth))));
        this.gestureData.setAgain(obtainStyledAttributes.getBoolean(23, getResources().getBoolean(R.bool.isAgain)));
        this.gestureData.setRound_Number(obtainStyledAttributes.getInt(18, getResources().getInteger(R.integer.Round_Number)));
        this.gestureData.setDelay(obtainStyledAttributes.getInt(10, getResources().getInteger(R.integer.Delay)));
        this.gestureData.setLineWidth(obtainStyledAttributes.getFloat(16, Float.parseFloat(getResources().getString(R.string.LineWidth))));
        this.gestureData.setDefaultBorderColor(obtainStyledAttributes.getColor(7, getResources().getColor(R.color.DefaultBorderColor)));
        this.gestureData.setDefaultFilletColor(obtainStyledAttributes.getColor(9, getResources().getColor(R.color.DefaultFilletColor)));
        this.gestureData.setDefaultExcircleColor(obtainStyledAttributes.getColor(8, getResources().getColor(R.color.DefaultExcircleColor)));
        this.gestureData.setSelectBorderColor(obtainStyledAttributes.getColor(19, getResources().getColor(R.color.SelectBorderColor)));
        this.gestureData.setSelectFilletColor(obtainStyledAttributes.getColor(21, getResources().getColor(R.color.SelectFilletColor)));
        this.gestureData.setSelectExcircleColor(obtainStyledAttributes.getColor(20, getResources().getColor(R.color.SelectExcircleColor)));
        this.gestureData.setSelectLineColor(obtainStyledAttributes.getColor(22, getResources().getColor(R.color.SelectLineColor)));
        this.gestureData.setErrorBorderColor(obtainStyledAttributes.getColor(11, getResources().getColor(R.color.ErrorBorderColor)));
        this.gestureData.setErrorFilletColor(obtainStyledAttributes.getColor(13, getResources().getColor(R.color.ErrorFilletColor)));
        this.gestureData.setErrorExcircleColor(obtainStyledAttributes.getColor(12, getResources().getColor(R.color.ErrorExcircleColor)));
        this.gestureData.setErrorLineColor(obtainStyledAttributes.getColor(14, getResources().getColor(R.color.ErrorLineColor)));
        this.gestureData.setCorrectBorderColor(obtainStyledAttributes.getColor(3, getResources().getColor(R.color.CorrectBorderColor)));
        this.gestureData.setCorrectFilletColor(obtainStyledAttributes.getColor(5, getResources().getColor(R.color.CorrectFilletColor)));
        this.gestureData.setCorrectExcircleColor(obtainStyledAttributes.getColor(4, getResources().getColor(R.color.CorrectExcircleColor)));
        this.gestureData.setCorrectLineColor(obtainStyledAttributes.getColor(6, getResources().getColor(R.color.CorrectLineColor)));
        this.context = context2;
        this.paintLine = new Paint(1);
        this.paintLine.setStyle(Style.STROKE);
        this.paintLine.setStrokeCap(Cap.ROUND);
        this.paintLine.setStrokeJoin(Join.ROUND);
        this.pathLine = new Path();
        this.list = new ArrayList();
        this.ROUND_NUMBER = this.gestureData.getRound_Number();
        this.isAgain = this.gestureData.isAgain();
        this.LINE_NUMBER = this.gestureData.getLine_Number();
        this.COLUMN_NUMBER = this.gestureData.getColumn_Number();
        this.gestureViews = (GestureRound[][]) Array.newInstance(GestureRound.class, new int[]{this.LINE_NUMBER, this.COLUMN_NUMBER});
        this.orderData = new StringBuffer();
        this.paintLine.setAntiAlias(true);
    }

    @TargetApi(21)
    public GestureCustomView(Context context2, AttributeSet attributeSet, int i, int i2) {
        super(context2, attributeSet, i, i2);
        this.TAG = GestureCustomView.class.getSimpleName();
        this.ID_NUMBER = 130059;
        this.isAgain = true;
        this.isClick = true;
        TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, R.styleable.Gesture_Styles);
        this.gestureData = new GestureData();
        this.gestureData.setLine_Number(obtainStyledAttributes.getInt(17, getResources().getInteger(R.integer.Line_Number)));
        this.gestureData.setColumn_Number(obtainStyledAttributes.getInt(2, getResources().getInteger(R.integer.Column_Number)));
        this.gestureData.setBorderWidth(obtainStyledAttributes.getFloat(1, Float.parseFloat(getResources().getString(R.string.BorderWidth))));
        this.gestureData.setFilletWidth(obtainStyledAttributes.getFloat(15, Float.parseFloat(getResources().getString(R.string.FilletWidth))));
        this.gestureData.setBetweenWidth(obtainStyledAttributes.getFloat(0, Float.parseFloat(getResources().getString(R.string.BetweenWidth))));
        this.gestureData.setAgain(obtainStyledAttributes.getBoolean(23, getResources().getBoolean(R.bool.isAgain)));
        this.gestureData.setRound_Number(obtainStyledAttributes.getInt(18, getResources().getInteger(R.integer.Round_Number)));
        this.gestureData.setDelay(obtainStyledAttributes.getInt(10, getResources().getInteger(R.integer.Delay)));
        this.gestureData.setLineWidth(obtainStyledAttributes.getFloat(16, Float.parseFloat(getResources().getString(R.string.LineWidth))));
        this.gestureData.setDefaultBorderColor(obtainStyledAttributes.getColor(7, getResources().getColor(R.color.DefaultBorderColor)));
        this.gestureData.setDefaultFilletColor(obtainStyledAttributes.getColor(9, getResources().getColor(R.color.DefaultFilletColor)));
        this.gestureData.setDefaultExcircleColor(obtainStyledAttributes.getColor(8, getResources().getColor(R.color.DefaultExcircleColor)));
        this.gestureData.setSelectBorderColor(obtainStyledAttributes.getColor(19, getResources().getColor(R.color.SelectBorderColor)));
        this.gestureData.setSelectFilletColor(obtainStyledAttributes.getColor(21, getResources().getColor(R.color.SelectFilletColor)));
        this.gestureData.setSelectExcircleColor(obtainStyledAttributes.getColor(20, getResources().getColor(R.color.SelectExcircleColor)));
        this.gestureData.setSelectLineColor(obtainStyledAttributes.getColor(22, getResources().getColor(R.color.SelectLineColor)));
        this.gestureData.setErrorBorderColor(obtainStyledAttributes.getColor(11, getResources().getColor(R.color.ErrorBorderColor)));
        this.gestureData.setErrorFilletColor(obtainStyledAttributes.getColor(13, getResources().getColor(R.color.ErrorFilletColor)));
        this.gestureData.setErrorExcircleColor(obtainStyledAttributes.getColor(12, getResources().getColor(R.color.ErrorExcircleColor)));
        this.gestureData.setErrorLineColor(obtainStyledAttributes.getColor(14, getResources().getColor(R.color.ErrorLineColor)));
        this.gestureData.setCorrectBorderColor(obtainStyledAttributes.getColor(3, getResources().getColor(R.color.CorrectBorderColor)));
        this.gestureData.setCorrectFilletColor(obtainStyledAttributes.getColor(5, getResources().getColor(R.color.CorrectFilletColor)));
        this.gestureData.setCorrectExcircleColor(obtainStyledAttributes.getColor(4, getResources().getColor(R.color.CorrectExcircleColor)));
        this.gestureData.setCorrectLineColor(obtainStyledAttributes.getColor(6, getResources().getColor(R.color.CorrectLineColor)));
        this.context = context2;
        this.paintLine = new Paint(1);
        this.paintLine.setStyle(Style.STROKE);
        this.paintLine.setStrokeCap(Cap.ROUND);
        this.paintLine.setStrokeJoin(Join.ROUND);
        this.pathLine = new Path();
        this.list = new ArrayList();
        this.ROUND_NUMBER = this.gestureData.getRound_Number();
        this.isAgain = this.gestureData.isAgain();
        this.LINE_NUMBER = this.gestureData.getLine_Number();
        this.COLUMN_NUMBER = this.gestureData.getColumn_Number();
        this.gestureViews = (GestureRound[][]) Array.newInstance(GestureRound.class, new int[]{this.LINE_NUMBER, this.COLUMN_NUMBER});
        this.orderData = new StringBuffer();
        this.paintLine.setAntiAlias(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = MeasureSpec.getSize(i2);
        int size2 = MeasureSpec.getSize(i);
        if (size2 < size) {
            size = size2;
        }
        this.gestureViewWidth = (int) ((((float) (this.COLUMN_NUMBER * size)) * 0.8f) / ((float) (((this.COLUMN_NUMBER + 1) * this.COLUMN_NUMBER) + 1)));
        this.gestureBetweenWidth = (int) (((float) this.gestureViewWidth) * this.gestureData.getBetweenWidth());
        this.paintLine.setStrokeWidth(((float) this.gestureViewWidth) * this.gestureData.getLineWidth());
        int i3 = 0;
        while (i3 < this.LINE_NUMBER) {
            int i4 = 0;
            while (i4 < this.COLUMN_NUMBER) {
                int i5 = i3 + i4 + ((this.COLUMN_NUMBER - 1) * i3) + this.ID_NUMBER + 1;
                GestureRound gestureRound = new GestureRound(this.context, this.gestureData);
                gestureRound.setId(i5);
                LayoutParams layoutParams = new LayoutParams(this.gestureViewWidth, this.gestureViewWidth);
                layoutParams.setMargins(this.COLUMN_NUMBER == 0 ? this.gestureBetweenWidth : 0, this.LINE_NUMBER == 0 ? this.gestureBetweenWidth : 0, i4 == this.COLUMN_NUMBER - 1 ? 0 : this.gestureBetweenWidth, i3 == this.LINE_NUMBER - 1 ? 0 : this.gestureBetweenWidth);
                layoutParams.addRule(1, i4 != 0 ? i5 - 1 : 0);
                layoutParams.addRule(3, i3 != 0 ? i5 - this.COLUMN_NUMBER : 0);
                gestureRound.setLayoutParams(layoutParams);
                this.gestureViews[i3][i4] = gestureRound;
                addView(gestureRound);
                i4++;
            }
            i3++;
        }
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
        if (this.isRound) {
            if (this.pathLine != null) {
                canvas.drawPath(this.pathLine, this.paintLine);
            }
            if (this.moveX != 0.0f && this.moveY != 0.0f) {
                canvas.drawLine(this.startX, this.startY, this.moveX, this.moveY, this.paintLine);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case 0:
                GestureRound round = getRound(x, y);
                if (round != null && this.isClick) {
                    this.isClick = false;
                    this.paintLine.setColor(this.gestureData.getSelectLineColor());
                    round.setState(1);
                    this.isRound = true;
                    this.list.add(round);
                    getStartPosition(round);
                    this.pathLine.moveTo(this.startX, this.startY);
                    this.orderData.append(round.getId() - this.ID_NUMBER);
                    break;
                } else {
                    this.isRound = false;
                    break;
                }
                break;
            case 1:
                if (this.isRound) {
                    this.moveX = this.startX;
                    this.moveY = this.startY;
                    if (this.list.size() >= this.ROUND_NUMBER) {
                        if (this.isAgain) {
                            if (this.fristData == null) {
                                this.fristData = this.orderData.toString();
                            } else if (this.fristData.equals(this.orderData.toString())) {
                                changeMode(2);
                                this.paintLine.setColor(this.gestureData.getCorrectLineColor());
                            } else {
                                changeMode(3);
                                this.paintLine.setColor(this.gestureData.getErrorLineColor());
                            }
                        } else if (this.fristData.equals(this.orderData.toString())) {
                            changeMode(2);
                            this.paintLine.setColor(this.gestureData.getCorrectLineColor());
                        } else {
                            changeMode(3);
                            this.paintLine.setColor(this.gestureData.getErrorLineColor());
                        }
                        this.resultListener.Result(this.orderData.toString());
                    } else {
                        Toast.makeText(this.context, getResources().getString(R.string.pwdlenerror), 0).show();
                    }
                    this.orderData.delete(0, this.orderData.length());
                    restoreState();
                    break;
                }
                break;
            case 2:
                if (this.isRound) {
                    this.moveX = x;
                    this.moveY = y;
                    GestureRound round2 = getRound(this.moveX, this.moveY);
                    if (round2 != null && !this.list.contains(round2)) {
                        round2.setState(1);
                        getStartPosition(round2);
                        this.list.add(round2);
                        this.orderData.append(round2.getId() - this.ID_NUMBER);
                        this.pathLine.lineTo((float) ((round2.getLeft() + round2.getRight()) / 2), (float) ((round2.getTop() + round2.getBottom()) / 2));
                        break;
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    private void changeMode(int i) {
        for (GestureRound state : this.list) {
            state.setState(i);
        }
    }

    private void getStartPosition(GestureRound gestureRound) {
        this.startX = (float) ((gestureRound.getLeft() + gestureRound.getRight()) / 2);
        this.startY = (float) ((gestureRound.getTop() + gestureRound.getBottom()) / 2);
    }

    private void restoreState() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                GestureCustomView.this.pathLine.reset();
                GestureCustomView.this.moveX = 0.0f;
                GestureCustomView.this.moveY = 0.0f;
                for (int i = 0; i < GestureCustomView.this.COLUMN_NUMBER; i++) {
                    for (int i2 = 0; i2 < GestureCustomView.this.LINE_NUMBER; i2++) {
                        GestureRound gestureRound = GestureCustomView.this.gestureViews[i2][i];
                        if (GestureCustomView.this.list.contains(gestureRound)) {
                            gestureRound.setState(0);
                        }
                    }
                }
                GestureCustomView.this.list.clear();
                GestureCustomView.this.invalidate();
                GestureCustomView.this.isClick = true;
            }
        }, (long) this.gestureData.getDelay());
    }

    private GestureRound getRound(float f, float f2) {
        for (int i = 0; i < this.COLUMN_NUMBER; i++) {
            for (int i2 = 0; i2 < this.LINE_NUMBER; i2++) {
                if (judgeRound(this.gestureViews[i2][i], f, f2)) {
                    return this.gestureViews[i2][i];
                }
            }
        }
        return null;
    }

    private boolean judgeRound(GestureRound gestureRound, float f, float f2) {
        float f3 = ((float) this.gestureViewWidth) * 0.2f;
        return f >= ((float) gestureRound.getLeft()) + f3 && f <= ((float) gestureRound.getRight()) - f3 && f2 >= ((float) gestureRound.getTop()) + f3 && f2 <= ((float) gestureRound.getBottom()) - f3;
    }

    public void setAgain(boolean z) {
        this.isAgain = z;
    }

    public void setFristData(String str) {
        this.fristData = str;
    }

    public boolean cheakPWD(String str) {
        if (this.fristData != null && this.fristData.equals(str)) {
            return true;
        }
        return false;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.resultListener = onResultListener;
    }
}
