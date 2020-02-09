package bsdq.bsdq.Gesture;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GestureData implements Parcelable {
    public static final Creator<GestureData> CREATOR = new Creator<GestureData>() {
        public GestureData createFromParcel(Parcel parcel) {
            return new GestureData(parcel);
        }

        public GestureData[] newArray(int i) {
            return new GestureData[i];
        }
    };
    private float BetweenWidth;
    private float BorderWidth;
    private int Column_Number;
    private int CorrectBorderColor;
    private int CorrectExcircleColor;
    private int CorrectFilletColor;
    private int CorrectLineColor;
    private int DefaultBorderColor;
    private int DefaultExcircleColor;
    private int DefaultFilletColor;
    private int Delay;
    private int ErrorBorderColor;
    private int ErrorExcircleColor;
    private int ErrorFilletColor;
    private int ErrorLineColor;
    private float FilletWidth;
    private float LineWidth;
    private int Line_Number;
    private int Round_Number;
    private int SelectBorderColor;
    private int SelectExcircleColor;
    private int SelectFilletColor;
    private int SelectLineColor;
    private boolean isAgain;

    public int describeContents() {
        return 0;
    }

    public int getDefaultBorderColor() {
        return this.DefaultBorderColor;
    }

    public void setDefaultBorderColor(int i) {
        this.DefaultBorderColor = i;
    }

    public int getDefaultFilletColor() {
        return this.DefaultFilletColor;
    }

    public void setDefaultFilletColor(int i) {
        this.DefaultFilletColor = i;
    }

    public int getDefaultExcircleColor() {
        return this.DefaultExcircleColor;
    }

    public void setDefaultExcircleColor(int i) {
        this.DefaultExcircleColor = i;
    }

    public int getSelectBorderColor() {
        return this.SelectBorderColor;
    }

    public void setSelectBorderColor(int i) {
        this.SelectBorderColor = i;
    }

    public int getSelectFilletColor() {
        return this.SelectFilletColor;
    }

    public void setSelectFilletColor(int i) {
        this.SelectFilletColor = i;
    }

    public int getSelectExcircleColor() {
        return this.SelectExcircleColor;
    }

    public void setSelectExcircleColor(int i) {
        this.SelectExcircleColor = i;
    }

    public int getSelectLineColor() {
        return this.SelectLineColor;
    }

    public void setSelectLineColor(int i) {
        this.SelectLineColor = i;
    }

    public int getErrorBorderColor() {
        return this.ErrorBorderColor;
    }

    public void setErrorBorderColor(int i) {
        this.ErrorBorderColor = i;
    }

    public int getErrorFilletColor() {
        return this.ErrorFilletColor;
    }

    public void setErrorFilletColor(int i) {
        this.ErrorFilletColor = i;
    }

    public int getErrorExcircleColor() {
        return this.ErrorExcircleColor;
    }

    public void setErrorExcircleColor(int i) {
        this.ErrorExcircleColor = i;
    }

    public int getErrorLineColor() {
        return this.ErrorLineColor;
    }

    public void setErrorLineColor(int i) {
        this.ErrorLineColor = i;
    }

    public int getCorrectBorderColor() {
        return this.CorrectBorderColor;
    }

    public void setCorrectBorderColor(int i) {
        this.CorrectBorderColor = i;
    }

    public int getCorrectFilletColor() {
        return this.CorrectFilletColor;
    }

    public void setCorrectFilletColor(int i) {
        this.CorrectFilletColor = i;
    }

    public int getCorrectExcircleColor() {
        return this.CorrectExcircleColor;
    }

    public void setCorrectExcircleColor(int i) {
        this.CorrectExcircleColor = i;
    }

    public int getCorrectLineColor() {
        return this.CorrectLineColor;
    }

    public void setCorrectLineColor(int i) {
        this.CorrectLineColor = i;
    }

    public float getBorderWidth() {
        return this.BorderWidth;
    }

    public void setBorderWidth(float f) {
        this.BorderWidth = f;
    }

    public float getFilletWidth() {
        return this.FilletWidth;
    }

    public void setFilletWidth(float f) {
        this.FilletWidth = f;
    }

    public float getBetweenWidth() {
        return this.BetweenWidth;
    }

    public void setBetweenWidth(float f) {
        this.BetweenWidth = f;
    }

    public float getLineWidth() {
        return this.LineWidth;
    }

    public void setLineWidth(float f) {
        this.LineWidth = f;
    }

    public int getRound_Number() {
        return this.Round_Number;
    }

    public void setRound_Number(int i) {
        this.Round_Number = i;
    }

    public boolean isAgain() {
        return this.isAgain;
    }

    public void setAgain(boolean z) {
        this.isAgain = z;
    }

    public int getDelay() {
        return this.Delay;
    }

    public void setDelay(int i) {
        this.Delay = i;
    }

    public int getLine_Number() {
        return this.Line_Number;
    }

    public void setLine_Number(int i) {
        this.Line_Number = i;
    }

    public int getColumn_Number() {
        return this.Column_Number;
    }

    public void setColumn_Number(int i) {
        this.Column_Number = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.DefaultBorderColor);
        parcel.writeInt(this.DefaultFilletColor);
        parcel.writeInt(this.DefaultExcircleColor);
        parcel.writeInt(this.SelectBorderColor);
        parcel.writeInt(this.SelectFilletColor);
        parcel.writeInt(this.SelectExcircleColor);
        parcel.writeInt(this.SelectLineColor);
        parcel.writeInt(this.ErrorBorderColor);
        parcel.writeInt(this.ErrorFilletColor);
        parcel.writeInt(this.ErrorExcircleColor);
        parcel.writeInt(this.ErrorLineColor);
        parcel.writeInt(this.CorrectBorderColor);
        parcel.writeInt(this.CorrectFilletColor);
        parcel.writeInt(this.CorrectExcircleColor);
        parcel.writeInt(this.CorrectLineColor);
        parcel.writeFloat(this.BorderWidth);
        parcel.writeFloat(this.FilletWidth);
        parcel.writeFloat(this.BetweenWidth);
        parcel.writeFloat(this.LineWidth);
        parcel.writeInt(this.Round_Number);
        parcel.writeByte(this.isAgain ? (byte) 1 : 0);
        parcel.writeInt(this.Delay);
        parcel.writeInt(this.Line_Number);
        parcel.writeInt(this.Column_Number);
    }

    public GestureData() {
    }

    protected GestureData(Parcel parcel) {
        this.DefaultBorderColor = parcel.readInt();
        this.DefaultFilletColor = parcel.readInt();
        this.DefaultExcircleColor = parcel.readInt();
        this.SelectBorderColor = parcel.readInt();
        this.SelectFilletColor = parcel.readInt();
        this.SelectExcircleColor = parcel.readInt();
        this.SelectLineColor = parcel.readInt();
        this.ErrorBorderColor = parcel.readInt();
        this.ErrorFilletColor = parcel.readInt();
        this.ErrorExcircleColor = parcel.readInt();
        this.ErrorLineColor = parcel.readInt();
        this.CorrectBorderColor = parcel.readInt();
        this.CorrectFilletColor = parcel.readInt();
        this.CorrectExcircleColor = parcel.readInt();
        this.CorrectLineColor = parcel.readInt();
        this.BorderWidth = parcel.readFloat();
        this.FilletWidth = parcel.readFloat();
        this.BetweenWidth = parcel.readFloat();
        this.LineWidth = parcel.readFloat();
        this.Round_Number = parcel.readInt();
        this.isAgain = parcel.readByte() != 0;
        this.Delay = parcel.readInt();
        this.Line_Number = parcel.readInt();
        this.Column_Number = parcel.readInt();
    }
}
