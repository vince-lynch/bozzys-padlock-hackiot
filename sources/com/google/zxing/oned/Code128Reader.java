package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.Map;

public final class Code128Reader extends OneDReader {
    private static final int CODE_CODE_A = 101;
    private static final int CODE_CODE_B = 100;
    private static final int CODE_CODE_C = 99;
    private static final int CODE_FNC_1 = 102;
    private static final int CODE_FNC_2 = 97;
    private static final int CODE_FNC_3 = 96;
    private static final int CODE_FNC_4_A = 101;
    private static final int CODE_FNC_4_B = 100;
    static final int[][] CODE_PATTERNS = {new int[]{2, 1, 2, 2, 2, 2}, new int[]{2, 2, 2, 1, 2, 2}, new int[]{2, 2, 2, 2, 2, 1}, new int[]{1, 2, 1, 2, 2, 3}, new int[]{1, 2, 1, 3, 2, 2}, new int[]{1, 3, 1, 2, 2, 2}, new int[]{1, 2, 2, 2, 1, 3}, new int[]{1, 2, 2, 3, 1, 2}, new int[]{1, 3, 2, 2, 1, 2}, new int[]{2, 2, 1, 2, 1, 3}, new int[]{2, 2, 1, 3, 1, 2}, new int[]{2, 3, 1, 2, 1, 2}, new int[]{1, 1, 2, 2, 3, 2}, new int[]{1, 2, 2, 1, 3, 2}, new int[]{1, 2, 2, 2, 3, 1}, new int[]{1, 1, 3, 2, 2, 2}, new int[]{1, 2, 3, 1, 2, 2}, new int[]{1, 2, 3, 2, 2, 1}, new int[]{2, 2, 3, 2, 1, 1}, new int[]{2, 2, 1, 1, 3, 2}, new int[]{2, 2, 1, 2, 3, 1}, new int[]{2, 1, 3, 2, 1, 2}, new int[]{2, 2, 3, 1, 1, 2}, new int[]{3, 1, 2, 1, 3, 1}, new int[]{3, 1, 1, 2, 2, 2}, new int[]{3, 2, 1, 1, 2, 2}, new int[]{3, 2, 1, 2, 2, 1}, new int[]{3, 1, 2, 2, 1, 2}, new int[]{3, 2, 2, 1, 1, 2}, new int[]{3, 2, 2, 2, 1, 1}, new int[]{2, 1, 2, 1, 2, 3}, new int[]{2, 1, 2, 3, 2, 1}, new int[]{2, 3, 2, 1, 2, 1}, new int[]{1, 1, 1, 3, 2, 3}, new int[]{1, 3, 1, 1, 2, 3}, new int[]{1, 3, 1, 3, 2, 1}, new int[]{1, 1, 2, 3, 1, 3}, new int[]{1, 3, 2, 1, 1, 3}, new int[]{1, 3, 2, 3, 1, 1}, new int[]{2, 1, 1, 3, 1, 3}, new int[]{2, 3, 1, 1, 1, 3}, new int[]{2, 3, 1, 3, 1, 1}, new int[]{1, 1, 2, 1, 3, 3}, new int[]{1, 1, 2, 3, 3, 1}, new int[]{1, 3, 2, 1, 3, 1}, new int[]{1, 1, 3, 1, 2, 3}, new int[]{1, 1, 3, 3, 2, 1}, new int[]{1, 3, 3, 1, 2, 1}, new int[]{3, 1, 3, 1, 2, 1}, new int[]{2, 1, 1, 3, 3, 1}, new int[]{2, 3, 1, 1, 3, 1}, new int[]{2, 1, 3, 1, 1, 3}, new int[]{2, 1, 3, 3, 1, 1}, new int[]{2, 1, 3, 1, 3, 1}, new int[]{3, 1, 1, 1, 2, 3}, new int[]{3, 1, 1, 3, 2, 1}, new int[]{3, 3, 1, 1, 2, 1}, new int[]{3, 1, 2, 1, 1, 3}, new int[]{3, 1, 2, 3, 1, 1}, new int[]{3, 3, 2, 1, 1, 1}, new int[]{3, 1, 4, 1, 1, 1}, new int[]{2, 2, 1, 4, 1, 1}, new int[]{4, 3, 1, 1, 1, 1}, new int[]{1, 1, 1, 2, 2, 4}, new int[]{1, 1, 1, 4, 2, 2}, new int[]{1, 2, 1, 1, 2, 4}, new int[]{1, 2, 1, 4, 2, 1}, new int[]{1, 4, 1, 1, 2, 2}, new int[]{1, 4, 1, 2, 2, 1}, new int[]{1, 1, 2, 2, 1, 4}, new int[]{1, 1, 2, 4, 1, 2}, new int[]{1, 2, 2, 1, 1, 4}, new int[]{1, 2, 2, 4, 1, 1}, new int[]{1, 4, 2, 1, 1, 2}, new int[]{1, 4, 2, 2, 1, 1}, new int[]{2, 4, 1, 2, 1, 1}, new int[]{2, 2, 1, 1, 1, 4}, new int[]{4, 1, 3, 1, 1, 1}, new int[]{2, 4, 1, 1, 1, 2}, new int[]{1, 3, 4, 1, 1, 1}, new int[]{1, 1, 1, 2, 4, 2}, new int[]{1, 2, 1, 1, 4, 2}, new int[]{1, 2, 1, 2, 4, 1}, new int[]{1, 1, 4, 2, 1, 2}, new int[]{1, 2, 4, 1, 1, 2}, new int[]{1, 2, 4, 2, 1, 1}, new int[]{4, 1, 1, 2, 1, 2}, new int[]{4, 2, 1, 1, 1, 2}, new int[]{4, 2, 1, 2, 1, 1}, new int[]{2, 1, 2, 1, 4, 1}, new int[]{2, 1, 4, 1, 2, 1}, new int[]{4, 1, 2, 1, 2, 1}, new int[]{1, 1, 1, 1, 4, 3}, new int[]{1, 1, 1, 3, 4, 1}, new int[]{1, 3, 1, 1, 4, 1}, new int[]{1, 1, 4, 1, 1, 3}, new int[]{1, 1, 4, 3, 1, 1}, new int[]{4, 1, 1, 1, 1, 3}, new int[]{4, 1, 1, 3, 1, 1}, new int[]{1, 1, 3, 1, 4, 1}, new int[]{1, 1, 4, 1, 3, 1}, new int[]{3, 1, 1, 1, 4, 1}, new int[]{4, 1, 1, 1, 3, 1}, new int[]{2, 1, 1, 4, 1, 2}, new int[]{2, 1, 1, 2, 1, 4}, new int[]{2, 1, 1, 2, 3, 2}, new int[]{2, 3, 3, 1, 1, 1, 2}};
    private static final int CODE_SHIFT = 98;
    private static final int CODE_START_A = 103;
    private static final int CODE_START_B = 104;
    private static final int CODE_START_C = 105;
    private static final int CODE_STOP = 106;
    private static final float MAX_AVG_VARIANCE = 0.25f;
    private static final float MAX_INDIVIDUAL_VARIANCE = 0.7f;

    private static int[] findStartPattern(BitArray bitArray) throws NotFoundException {
        int size = bitArray.getSize();
        int nextSet = bitArray.getNextSet(0);
        int[] iArr = new int[6];
        int length = iArr.length;
        int i = nextSet;
        boolean z = false;
        int i2 = 0;
        while (nextSet < size) {
            if (bitArray.get(nextSet) ^ z) {
                iArr[i2] = iArr[i2] + 1;
            } else {
                int i3 = length - 1;
                if (i2 == i3) {
                    float f = MAX_AVG_VARIANCE;
                    int i4 = -1;
                    for (int i5 = 103; i5 <= 105; i5++) {
                        float patternMatchVariance = patternMatchVariance(iArr, CODE_PATTERNS[i5], MAX_INDIVIDUAL_VARIANCE);
                        if (patternMatchVariance < f) {
                            i4 = i5;
                            f = patternMatchVariance;
                        }
                    }
                    if (i4 < 0 || !bitArray.isRange(Math.max(0, i - ((nextSet - i) / 2)), i, false)) {
                        i += iArr[0] + iArr[1];
                        int i6 = length - 2;
                        System.arraycopy(iArr, 2, iArr, 0, i6);
                        iArr[i6] = 0;
                        iArr[i3] = 0;
                        i2--;
                    } else {
                        return new int[]{i, nextSet, i4};
                    }
                } else {
                    i2++;
                }
                iArr[i2] = 1;
                z = !z;
            }
            nextSet++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeCode(BitArray bitArray, int[] iArr, int i) throws NotFoundException {
        recordPattern(bitArray, i, iArr);
        float f = MAX_AVG_VARIANCE;
        int i2 = -1;
        for (int i3 = 0; i3 < CODE_PATTERNS.length; i3++) {
            float patternMatchVariance = patternMatchVariance(iArr, CODE_PATTERNS[i3], MAX_INDIVIDUAL_VARIANCE);
            if (patternMatchVariance < f) {
                i2 = i3;
                f = patternMatchVariance;
            }
        }
        if (i2 >= 0) {
            return i2;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX WARNING: type inference failed for: r3v0 */
    /* JADX WARNING: type inference failed for: r3v1, types: [boolean] */
    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: type inference failed for: r3v5 */
    /* JADX WARNING: type inference failed for: r3v6 */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x01ea, code lost:
        r23 = r5;
        r2 = false;
        r22 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x01f3, code lost:
        r23 = r5;
        r22 = r9;
        r2 = false;
        r10 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x0206, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x0207, code lost:
        r6 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0208, code lost:
        r23 = r5;
        r22 = r9;
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0248, code lost:
        r10 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x0250, code lost:
        r10 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x025a, code lost:
        r23 = r5;
        r22 = r9;
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x025f, code lost:
        if (r14 == false) goto L_0x026a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0263, code lost:
        if (r10 != 'e') goto L_0x0268;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0265, code lost:
        r10 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0268, code lost:
        r10 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x026a, code lost:
        r14 = r2;
        r11 = r16;
        r2 = 1;
        r5 = 2;
        r9 = 'c';
        r16 = r12;
        r12 = r8;
        r8 = r24;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0178, code lost:
        if (r9 != false) goto L_0x01e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01a2, code lost:
        r23 = r5;
        r2 = false;
        r22 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x01b4, code lost:
        r2 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x01d4, code lost:
        r2 = false;
        r22 = false;
        r23 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01df, code lost:
        if (r9 != false) goto L_0x01e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x01e1, code lost:
        r2 = false;
        r22 = false;
        r23 = false;
     */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r3v1, types: [boolean]
  assigns: []
  uses: [boolean, ?[int, short, byte, char]]
  mth insns count: 269
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 3 */
    public Result decodeRow(int i, BitArray bitArray, Map<DecodeHintType, ?> map) throws NotFoundException, FormatException, ChecksumException {
        char c;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        BitArray bitArray2 = bitArray;
        Map<DecodeHintType, ?> map2 = map;
        char c2 = 1;
        ? r3 = 0;
        boolean z9 = map2 != null && map2.containsKey(DecodeHintType.ASSUME_GS1);
        int[] findStartPattern = findStartPattern(bitArray);
        int i2 = 2;
        int i3 = findStartPattern[2];
        ArrayList arrayList = new ArrayList(20);
        arrayList.add(Byte.valueOf((byte) i3));
        char c3 = 'c';
        switch (i3) {
            case 103:
                c = 'e';
                break;
            case 104:
                c = 'd';
                break;
            case 105:
                c = 'c';
                break;
            default:
                throw FormatException.getFormatInstance();
        }
        StringBuilder sb = new StringBuilder(20);
        int[] iArr = new int[6];
        int i4 = i3;
        char c4 = c;
        boolean z10 = false;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        boolean z11 = true;
        boolean z12 = false;
        boolean z13 = false;
        int i8 = findStartPattern[0];
        int i9 = findStartPattern[1];
        boolean z14 = false;
        ? r32 = r3;
        while (!z10) {
            int i10 = i;
            int decodeCode = decodeCode(bitArray2, iArr, i9);
            arrayList.add(Byte.valueOf((byte) decodeCode));
            if (decodeCode != 106) {
                z11 = true;
            }
            if (decodeCode != 106) {
                i7++;
                i4 += i7 * decodeCode;
            }
            int i11 = i9;
            int i12 = 0;
            while (i12 < iArr.length) {
                int i13 = i12;
                i11 += iArr[i13];
                z13 = z13;
                z12 = z12;
                i12 = i13 + 1;
            }
            switch (decodeCode) {
                case 103:
                case 104:
                case 105:
                    throw FormatException.getFormatInstance();
                default:
                    switch (c4) {
                        case 'c':
                            z2 = z12;
                            z3 = z13;
                            if (decodeCode >= 100) {
                                if (decodeCode != 106) {
                                    z11 = false;
                                }
                                if (decodeCode == 106) {
                                    z13 = z3;
                                    z12 = z2;
                                    z = false;
                                    z10 = true;
                                    break;
                                } else {
                                    switch (decodeCode) {
                                        case 100:
                                            z13 = z3;
                                            z12 = z2;
                                            z = false;
                                            break;
                                        case 101:
                                            z4 = z3;
                                            z5 = z2;
                                            z6 = false;
                                            break;
                                        case 102:
                                            if (z9) {
                                                if (sb.length() != 0) {
                                                    sb.append(29);
                                                    break;
                                                } else {
                                                    sb.append("]C1");
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                }
                            } else {
                                if (decodeCode < 10) {
                                    sb.append('0');
                                }
                                sb.append(decodeCode);
                                break;
                            }
                            break;
                        case 'd':
                            z7 = z12;
                            z8 = z13;
                            if (decodeCode < 96) {
                                if (z7 != z8) {
                                    sb.append((char) (decodeCode + 32 + 128));
                                    break;
                                } else {
                                    sb.append((char) (decodeCode + 32));
                                    break;
                                }
                            } else {
                                if (decodeCode != 106) {
                                    z11 = false;
                                }
                                if (decodeCode != 106) {
                                    switch (decodeCode) {
                                        case 96:
                                        case 97:
                                            break;
                                        case 98:
                                            z4 = z8;
                                            z5 = z7;
                                            z6 = true;
                                            break;
                                        case 99:
                                            break;
                                        case 100:
                                            if (z8 || !z7) {
                                                if (z8) {
                                                }
                                            }
                                            break;
                                        case 101:
                                            z4 = z8;
                                            z5 = z7;
                                            z6 = false;
                                            break;
                                        case 102:
                                            if (z9) {
                                                if (sb.length() != 0) {
                                                    sb.append(29);
                                                    break;
                                                } else {
                                                    sb.append("]C1");
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                }
                            }
                            break;
                        case 'e':
                            if (decodeCode >= 64) {
                                z7 = z12;
                                z8 = z13;
                                if (decodeCode < 96) {
                                    if (z7 != z8) {
                                        sb.append((char) (decodeCode + 64));
                                        break;
                                    } else {
                                        sb.append((char) (decodeCode - 64));
                                        break;
                                    }
                                } else {
                                    if (decodeCode != 106) {
                                        z11 = false;
                                    }
                                    if (decodeCode != 106) {
                                        switch (decodeCode) {
                                            case 96:
                                            case 97:
                                                break;
                                            case 98:
                                                z13 = z8;
                                                z12 = z7;
                                                z = true;
                                                break;
                                            case 99:
                                                break;
                                            case 100:
                                                z13 = z8;
                                                z12 = z7;
                                                z = false;
                                                break;
                                            case 101:
                                                if (z8 || !z7) {
                                                    if (z8) {
                                                    }
                                                }
                                                break;
                                            case 102:
                                                if (z9) {
                                                    if (sb.length() != 0) {
                                                        sb.append(29);
                                                        break;
                                                    } else {
                                                        sb.append("]C1");
                                                        break;
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                }
                            } else {
                                z8 = z13;
                                if (z12 != z8) {
                                    sb.append((char) (decodeCode + 32 + 128));
                                    break;
                                } else {
                                    sb.append((char) (decodeCode + 32));
                                    break;
                                }
                            }
                            break;
                        default:
                            z2 = z12;
                            z3 = z13;
                            break;
                    }
            }
        }
        int i14 = i9 - i8;
        int nextUnset = bitArray2.getNextUnset(i9);
        if (!bitArray2.isRange(nextUnset, Math.min(bitArray.getSize(), ((nextUnset - i8) / i2) + nextUnset), r32)) {
            throw NotFoundException.getNotFoundInstance();
        } else if ((i4 - (i7 * i5)) % 103 == i5) {
            int length = sb.length();
            if (length != 0) {
                if (length > 0 && z11) {
                    if (c4 == c3) {
                        sb.delete(length - 2, length);
                    } else {
                        sb.delete(length - 1, length);
                    }
                }
                float f = ((float) (findStartPattern[c2] + findStartPattern[r32])) / 2.0f;
                float f2 = ((float) i8) + (((float) i14) / 2.0f);
                int size = arrayList.size();
                byte[] bArr = new byte[size];
                for (int i15 = 0; i15 < size; i15++) {
                    int i16 = i;
                    bArr[i15] = ((Byte) arrayList.get(i15)).byteValue();
                }
                String sb2 = sb.toString();
                ResultPoint[] resultPointArr = new ResultPoint[i2];
                float f3 = (float) i;
                resultPointArr[r32] = new ResultPoint(f, f3);
                resultPointArr[c2] = new ResultPoint(f2, f3);
                return new Result(sb2, bArr, resultPointArr, BarcodeFormat.CODE_128);
            }
            throw NotFoundException.getNotFoundInstance();
        } else {
            throw ChecksumException.getChecksumInstance();
        }
    }
}
