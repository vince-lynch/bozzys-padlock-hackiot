package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class GenericMultipleBarcodeReader implements MultipleBarcodeReader {
    private static final int MAX_DEPTH = 4;
    private static final int MIN_DIMENSION_TO_RECUR = 100;
    private final Reader delegate;

    public GenericMultipleBarcodeReader(Reader reader) {
        this.delegate = reader;
    }

    public Result[] decodeMultiple(BinaryBitmap binaryBitmap) throws NotFoundException {
        return decodeMultiple(binaryBitmap, null);
    }

    public Result[] decodeMultiple(BinaryBitmap binaryBitmap, Map<DecodeHintType, ?> map) throws NotFoundException {
        ArrayList arrayList = new ArrayList();
        doDecodeMultiple(binaryBitmap, map, arrayList, 0, 0, 0);
        if (!arrayList.isEmpty()) {
            return (Result[]) arrayList.toArray(new Result[arrayList.size()]);
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private void doDecodeMultiple(BinaryBitmap binaryBitmap, Map<DecodeHintType, ?> map, List<Result> list, int i, int i2, int i3) {
        int i4;
        boolean z;
        float f;
        int i5;
        int i6;
        BinaryBitmap binaryBitmap2 = binaryBitmap;
        int i7 = i;
        int i8 = i2;
        int i9 = i3;
        if (i9 <= 4) {
            try {
                Result decode = this.delegate.decode(binaryBitmap2, map);
                Iterator it = list.iterator();
                while (true) {
                    i4 = 0;
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    } else if (((Result) it.next()).getText().equals(decode.getText())) {
                        z = true;
                        break;
                    } else {
                        List<Result> list2 = list;
                        Map<DecodeHintType, ?> map2 = map;
                    }
                }
                if (!z) {
                    list.add(translateResultPoints(decode, i7, i8));
                } else {
                    List<Result> list3 = list;
                }
                ResultPoint[] resultPoints = decode.getResultPoints();
                if (resultPoints != null && resultPoints.length != 0) {
                    int width = binaryBitmap.getWidth();
                    int height = binaryBitmap.getHeight();
                    float f2 = (float) width;
                    float f3 = (float) height;
                    int length = resultPoints.length;
                    float f4 = f3;
                    float f5 = 0.0f;
                    float f6 = 0.0f;
                    float f7 = f2;
                    int i10 = 0;
                    while (i10 < length) {
                        float f8 = f4;
                        int i11 = height;
                        int i12 = width;
                        float f9 = f6;
                        ResultPoint resultPoint = resultPoints[i10];
                        if (resultPoint != null) {
                            float x = resultPoint.getX();
                            float y = resultPoint.getY();
                            if (x < f7) {
                                f7 = x;
                            }
                            if (y < f8) {
                                f8 = y;
                            }
                            if (x > f5) {
                                f5 = x;
                            }
                            if (y > f9) {
                                f4 = f8;
                                f6 = y;
                                i10++;
                                height = i11;
                                width = i12;
                                Map<DecodeHintType, ?> map3 = map;
                                i4 = 0;
                            }
                        }
                        f6 = f9;
                        f4 = f8;
                        i10++;
                        height = i11;
                        width = i12;
                        Map<DecodeHintType, ?> map32 = map;
                        i4 = 0;
                    }
                    if (f7 > 100.0f) {
                        BinaryBitmap crop = binaryBitmap2.crop(i4, i4, (int) f7, height);
                        f = f4;
                        i6 = height;
                        i5 = width;
                        doDecodeMultiple(crop, map, list, i, i2, i9 + 1);
                    } else {
                        f = f4;
                        i6 = height;
                        i5 = width;
                    }
                    if (f > 100.0f) {
                        doDecodeMultiple(binaryBitmap2.crop(0, 0, i5, (int) f), map, list, i, i2, i9 + 1);
                    }
                    if (f5 < ((float) (i5 - 100))) {
                        int i13 = (int) f5;
                        BinaryBitmap crop2 = binaryBitmap2.crop(i13, 0, i5 - i13, i6);
                        doDecodeMultiple(crop2, map, list, i7 + i13, i2, i9 + 1);
                    }
                    float f10 = f6;
                    if (f10 < ((float) (i6 - 100))) {
                        int i14 = (int) f10;
                        BinaryBitmap crop3 = binaryBitmap2.crop(0, i14, i5, i6 - i14);
                        doDecodeMultiple(crop3, map, list, i, i8 + i14, 1 + i9);
                    }
                }
            } catch (ReaderException unused) {
            }
        }
    }

    private static Result translateResultPoints(Result result, int i, int i2) {
        ResultPoint[] resultPoints = result.getResultPoints();
        if (resultPoints == null) {
            return result;
        }
        ResultPoint[] resultPointArr = new ResultPoint[resultPoints.length];
        for (int i3 = 0; i3 < resultPoints.length; i3++) {
            ResultPoint resultPoint = resultPoints[i3];
            if (resultPoint != null) {
                resultPointArr[i3] = new ResultPoint(resultPoint.getX() + ((float) i), resultPoint.getY() + ((float) i2));
            }
        }
        Result result2 = new Result(result.getText(), result.getRawBytes(), resultPointArr, result.getBarcodeFormat());
        result2.putAllMetadata(result.getResultMetadata());
        return result2;
    }
}
