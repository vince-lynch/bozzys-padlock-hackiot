package com.google.zxing.aztec;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.aztec.decoder.Decoder;
import com.google.zxing.aztec.detector.Detector;
import com.google.zxing.common.DecoderResult;
import java.util.List;
import java.util.Map;

public final class AztecReader implements Reader {
    public void reset() {
    }

    public Result decode(BinaryBitmap binaryBitmap) throws NotFoundException, FormatException {
        return decode(binaryBitmap, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0031  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0084  */
    public Result decode(BinaryBitmap binaryBitmap, Map<DecodeHintType, ?> map) throws NotFoundException, FormatException {
        ResultPoint[] resultPointArr;
        Throwable th;
        List byteSegments;
        String eCLevel;
        ResultPoint[] resultPointArr2;
        Detector detector = new Detector(binaryBitmap.getBlackMatrix());
        DecoderResult decoderResult = null;
        try {
            AztecDetectorResult detect = detector.detect(false);
            resultPointArr2 = detect.getPoints();
            try {
                DecoderResult decode = new Decoder().decode(detect);
                resultPointArr = resultPointArr2;
                th = null;
                decoderResult = decode;
                e = null;
            } catch (NotFoundException e) {
                e = e;
                resultPointArr = resultPointArr2;
                th = null;
                if (decoderResult == null) {
                }
                if (map != null) {
                }
                Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), resultPointArr, BarcodeFormat.AZTEC);
                byteSegments = decoderResult.getByteSegments();
                if (byteSegments != null) {
                }
                eCLevel = decoderResult.getECLevel();
                if (eCLevel != null) {
                }
                return result;
            } catch (FormatException e2) {
                e = e2;
                resultPointArr = resultPointArr2;
                th = e;
                e = null;
                if (decoderResult == null) {
                }
                if (map != null) {
                }
                Result result2 = new Result(decoderResult.getText(), decoderResult.getRawBytes(), resultPointArr, BarcodeFormat.AZTEC);
                byteSegments = decoderResult.getByteSegments();
                if (byteSegments != null) {
                }
                eCLevel = decoderResult.getECLevel();
                if (eCLevel != null) {
                }
                return result2;
            }
        } catch (NotFoundException e3) {
            e = e3;
            resultPointArr2 = null;
            resultPointArr = resultPointArr2;
            th = null;
            if (decoderResult == null) {
            }
            if (map != null) {
            }
            Result result22 = new Result(decoderResult.getText(), decoderResult.getRawBytes(), resultPointArr, BarcodeFormat.AZTEC);
            byteSegments = decoderResult.getByteSegments();
            if (byteSegments != null) {
            }
            eCLevel = decoderResult.getECLevel();
            if (eCLevel != null) {
            }
            return result22;
        } catch (FormatException e4) {
            e = e4;
            resultPointArr2 = null;
            resultPointArr = resultPointArr2;
            th = e;
            e = null;
            if (decoderResult == null) {
            }
            if (map != null) {
            }
            Result result222 = new Result(decoderResult.getText(), decoderResult.getRawBytes(), resultPointArr, BarcodeFormat.AZTEC);
            byteSegments = decoderResult.getByteSegments();
            if (byteSegments != null) {
            }
            eCLevel = decoderResult.getECLevel();
            if (eCLevel != null) {
            }
            return result222;
        }
        if (decoderResult == null) {
            try {
                AztecDetectorResult detect2 = detector.detect(true);
                resultPointArr = detect2.getPoints();
                decoderResult = new Decoder().decode(detect2);
            } catch (FormatException | NotFoundException e5) {
                if (e != null) {
                    throw e;
                } else if (th != null) {
                    throw th;
                } else {
                    throw e5;
                }
            }
        }
        if (map != null) {
            ResultPointCallback resultPointCallback = (ResultPointCallback) map.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
            if (resultPointCallback != null) {
                for (ResultPoint foundPossibleResultPoint : resultPointArr) {
                    resultPointCallback.foundPossibleResultPoint(foundPossibleResultPoint);
                }
            }
        }
        Result result2222 = new Result(decoderResult.getText(), decoderResult.getRawBytes(), resultPointArr, BarcodeFormat.AZTEC);
        byteSegments = decoderResult.getByteSegments();
        if (byteSegments != null) {
            result2222.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
        }
        eCLevel = decoderResult.getECLevel();
        if (eCLevel != null) {
            result2222.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, eCLevel);
        }
        return result2222;
    }
}
