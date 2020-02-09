package com.google.zxing.oned.rss;

import android.support.v4.media.TransportMediator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class RSS14Reader extends AbstractRSSReader {
    private static final int[][] FINDER_PATTERNS = {new int[]{3, 8, 2, 1}, new int[]{3, 5, 5, 1}, new int[]{3, 3, 7, 1}, new int[]{3, 1, 9, 1}, new int[]{2, 7, 4, 1}, new int[]{2, 5, 6, 1}, new int[]{2, 3, 8, 1}, new int[]{1, 5, 7, 1}, new int[]{1, 3, 9, 1}};
    private static final int[] INSIDE_GSUM;
    private static final int[] INSIDE_ODD_TOTAL_SUBSET = {4, 20, 48, 81};
    private static final int[] INSIDE_ODD_WIDEST = {2, 4, 6, 8};
    private static final int[] OUTSIDE_EVEN_TOTAL_SUBSET = {1, 10, 34, 70, TransportMediator.KEYCODE_MEDIA_PLAY};
    private static final int[] OUTSIDE_GSUM;
    private static final int[] OUTSIDE_ODD_WIDEST = {8, 6, 4, 3, 1};
    private final List<Pair> possibleLeftPairs = new ArrayList();
    private final List<Pair> possibleRightPairs = new ArrayList();

    static {
        int[] iArr = new int[5];
        iArr[1] = 161;
        iArr[2] = 961;
        iArr[3] = 2015;
        iArr[4] = 2715;
        OUTSIDE_GSUM = iArr;
        int[] iArr2 = new int[4];
        iArr2[1] = 336;
        iArr2[2] = 1036;
        iArr2[3] = 1516;
        INSIDE_GSUM = iArr2;
    }

    public Result decodeRow(int i, BitArray bitArray, Map<DecodeHintType, ?> map) throws NotFoundException {
        addOrTally(this.possibleLeftPairs, decodePair(bitArray, false, i, map));
        bitArray.reverse();
        addOrTally(this.possibleRightPairs, decodePair(bitArray, true, i, map));
        bitArray.reverse();
        int size = this.possibleLeftPairs.size();
        for (int i2 = 0; i2 < size; i2++) {
            Pair pair = (Pair) this.possibleLeftPairs.get(i2);
            if (pair.getCount() > 1) {
                int size2 = this.possibleRightPairs.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    Pair pair2 = (Pair) this.possibleRightPairs.get(i3);
                    if (pair2.getCount() > 1 && checkChecksum(pair, pair2)) {
                        return constructResult(pair, pair2);
                    }
                }
                continue;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static void addOrTally(Collection<Pair> collection, Pair pair) {
        if (pair != null) {
            boolean z = false;
            Iterator it = collection.iterator();
            while (true) {
                if (it.hasNext()) {
                    Pair pair2 = (Pair) it.next();
                    if (pair2.getValue() == pair.getValue()) {
                        pair2.incrementCount();
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!z) {
                collection.add(pair);
            }
        }
    }

    public void reset() {
        this.possibleLeftPairs.clear();
        this.possibleRightPairs.clear();
    }

    private static Result constructResult(Pair pair, Pair pair2) {
        String valueOf = String.valueOf((((long) pair.getValue()) * 4537077) + ((long) pair2.getValue()));
        StringBuilder sb = new StringBuilder(14);
        for (int length = 13 - valueOf.length(); length > 0; length--) {
            sb.append('0');
        }
        sb.append(valueOf);
        int i = 0;
        for (int i2 = 0; i2 < 13; i2++) {
            int charAt = sb.charAt(i2) - '0';
            if ((i2 & 1) == 0) {
                charAt *= 3;
            }
            i += charAt;
        }
        int i3 = 10 - (i % 10);
        if (i3 == 10) {
            i3 = 0;
        }
        sb.append(i3);
        ResultPoint[] resultPoints = pair.getFinderPattern().getResultPoints();
        ResultPoint[] resultPoints2 = pair2.getFinderPattern().getResultPoints();
        return new Result(String.valueOf(sb.toString()), null, new ResultPoint[]{resultPoints[0], resultPoints[1], resultPoints2[0], resultPoints2[1]}, BarcodeFormat.RSS_14);
    }

    private static boolean checkChecksum(Pair pair, Pair pair2) {
        int checksumPortion = (pair.getChecksumPortion() + (pair2.getChecksumPortion() * 16)) % 79;
        int value = (pair.getFinderPattern().getValue() * 9) + pair2.getFinderPattern().getValue();
        if (value > 72) {
            value--;
        }
        if (value > 8) {
            value--;
        }
        return checksumPortion == value;
    }

    private Pair decodePair(BitArray bitArray, boolean z, int i, Map<DecodeHintType, ?> map) {
        ResultPointCallback resultPointCallback;
        try {
            int[] findFinderPattern = findFinderPattern(bitArray, 0, z);
            FinderPattern parseFoundFinderPattern = parseFoundFinderPattern(bitArray, i, z, findFinderPattern);
            if (map == null) {
                resultPointCallback = null;
            } else {
                resultPointCallback = (ResultPointCallback) map.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
            }
            if (resultPointCallback != null) {
                float f = ((float) (findFinderPattern[0] + findFinderPattern[1])) / 2.0f;
                if (z) {
                    f = ((float) (bitArray.getSize() - 1)) - f;
                }
                resultPointCallback.foundPossibleResultPoint(new ResultPoint(f, (float) i));
            }
            DataCharacter decodeDataCharacter = decodeDataCharacter(bitArray, parseFoundFinderPattern, true);
            DataCharacter decodeDataCharacter2 = decodeDataCharacter(bitArray, parseFoundFinderPattern, false);
            return new Pair((decodeDataCharacter.getValue() * 1597) + decodeDataCharacter2.getValue(), decodeDataCharacter.getChecksumPortion() + (decodeDataCharacter2.getChecksumPortion() * 4), parseFoundFinderPattern);
        } catch (NotFoundException unused) {
            return null;
        }
    }

    private DataCharacter decodeDataCharacter(BitArray bitArray, FinderPattern finderPattern, boolean z) throws NotFoundException {
        BitArray bitArray2 = bitArray;
        boolean z2 = z;
        int[] dataCharacterCounters = getDataCharacterCounters();
        boolean z3 = false;
        dataCharacterCounters[0] = 0;
        dataCharacterCounters[1] = 0;
        int i = 2;
        dataCharacterCounters[2] = 0;
        dataCharacterCounters[3] = 0;
        dataCharacterCounters[4] = 0;
        dataCharacterCounters[5] = 0;
        dataCharacterCounters[6] = 0;
        dataCharacterCounters[7] = 0;
        if (z2) {
            recordPatternInReverse(bitArray2, finderPattern.getStartEnd()[0], dataCharacterCounters);
        } else {
            recordPattern(bitArray2, finderPattern.getStartEnd()[1] + 1, dataCharacterCounters);
            int length = dataCharacterCounters.length - 1;
            int i2 = 0;
            while (i2 < length) {
                int i3 = dataCharacterCounters[i2];
                dataCharacterCounters[i2] = dataCharacterCounters[length];
                dataCharacterCounters[length] = i3;
                i2++;
                length--;
                z3 = false;
                i = 2;
            }
        }
        int i4 = z2 ? 16 : 15;
        float count = ((float) count(dataCharacterCounters)) / ((float) i4);
        int[] oddCounts = getOddCounts();
        int[] evenCounts = getEvenCounts();
        float[] oddRoundingErrors = getOddRoundingErrors();
        float[] evenRoundingErrors = getEvenRoundingErrors();
        int i5 = 0;
        while (i5 < dataCharacterCounters.length) {
            float f = ((float) dataCharacterCounters[i5]) / count;
            int i6 = (int) (f + 0.5f);
            if (i6 < 1) {
                i6 = 1;
            } else if (i6 > 8) {
                i6 = 8;
            }
            int i7 = i5 / 2;
            if ((i5 & 1) == 0) {
                oddCounts[i7] = i6;
                oddRoundingErrors[i7] = f - ((float) i6);
            } else {
                evenCounts[i7] = i6;
                evenRoundingErrors[i7] = f - ((float) i6);
            }
            i5++;
            z3 = false;
            i = 2;
        }
        adjustOddEvenCounts(z2, i4);
        int i8 = 0;
        int i9 = 0;
        for (int length2 = oddCounts.length - 1; length2 >= 0; length2--) {
            i8 = (i8 * 9) + oddCounts[length2];
            i9 += oddCounts[length2];
        }
        int i10 = 0;
        int i11 = 0;
        for (int length3 = evenCounts.length - 1; length3 >= 0; length3--) {
            i10 = (i10 * 9) + evenCounts[length3];
            i11 += evenCounts[length3];
        }
        int i12 = i8 + (i10 * 3);
        if (z2) {
            if ((i9 & 1) != 0 || i9 > 12 || i9 < 4) {
                throw NotFoundException.getNotFoundInstance();
            }
            int i13 = (12 - i9) / i;
            int i14 = OUTSIDE_ODD_WIDEST[i13];
            int i15 = 9 - i14;
            return new DataCharacter((RSSUtils.getRSSvalue(oddCounts, i14, z3) * OUTSIDE_EVEN_TOTAL_SUBSET[i13]) + RSSUtils.getRSSvalue(evenCounts, i15, true) + OUTSIDE_GSUM[i13], i12);
        } else if ((i11 & 1) != 0 || i11 > 10 || i11 < 4) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            int i16 = (10 - i11) / i;
            int i17 = INSIDE_ODD_WIDEST[i16];
            return new DataCharacter((RSSUtils.getRSSvalue(evenCounts, 9 - i17, z3) * INSIDE_ODD_TOTAL_SUBSET[i16]) + RSSUtils.getRSSvalue(oddCounts, i17, true) + INSIDE_GSUM[i16], i12);
        }
    }

    private int[] findFinderPattern(BitArray bitArray, int i, boolean z) throws NotFoundException {
        int[] decodeFinderCounters = getDecodeFinderCounters();
        decodeFinderCounters[0] = 0;
        decodeFinderCounters[1] = 0;
        decodeFinderCounters[2] = 0;
        decodeFinderCounters[3] = 0;
        int size = bitArray.getSize();
        boolean z2 = false;
        while (i < size) {
            z2 = !bitArray.get(i);
            if (z == z2) {
                break;
            }
            i++;
        }
        int i2 = i;
        int i3 = 0;
        while (i < size) {
            if (bitArray.get(i) ^ z2) {
                decodeFinderCounters[i3] = decodeFinderCounters[i3] + 1;
            } else {
                if (i3 != 3) {
                    i3++;
                } else if (isFinderPattern(decodeFinderCounters)) {
                    return new int[]{i2, i};
                } else {
                    i2 += decodeFinderCounters[0] + decodeFinderCounters[1];
                    decodeFinderCounters[0] = decodeFinderCounters[2];
                    decodeFinderCounters[1] = decodeFinderCounters[3];
                    decodeFinderCounters[2] = 0;
                    decodeFinderCounters[3] = 0;
                    i3--;
                }
                decodeFinderCounters[i3] = 1;
                z2 = !z2;
            }
            i++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private FinderPattern parseFoundFinderPattern(BitArray bitArray, int i, boolean z, int[] iArr) throws NotFoundException {
        int i2;
        int i3;
        boolean z2 = bitArray.get(iArr[0]);
        int i4 = iArr[0] - 1;
        while (i4 >= 0 && (bitArray.get(i4) ^ z2)) {
            i4--;
        }
        int i5 = i4 + 1;
        int i6 = iArr[0] - i5;
        int[] decodeFinderCounters = getDecodeFinderCounters();
        System.arraycopy(decodeFinderCounters, 0, decodeFinderCounters, 1, decodeFinderCounters.length - 1);
        decodeFinderCounters[0] = i6;
        int parseFinderValue = parseFinderValue(decodeFinderCounters, FINDER_PATTERNS);
        int i7 = iArr[1];
        if (z) {
            i2 = (bitArray.getSize() - 1) - i7;
            i3 = (bitArray.getSize() - 1) - i5;
        } else {
            i2 = i7;
            i3 = i5;
        }
        FinderPattern finderPattern = new FinderPattern(parseFinderValue, new int[]{i5, iArr[1]}, i3, i2, i);
        return finderPattern;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0056, code lost:
        if (r1 < 4) goto L_0x003d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00bd  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:85:? A[RETURN, SYNTHETIC] */
    private void adjustOddEvenCounts(boolean z, int i) throws NotFoundException {
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        int count = count(getOddCounts());
        int count2 = count(getEvenCounts());
        int i2 = (count + count2) - i;
        boolean z7 = false;
        boolean z8 = (count & true) == z;
        boolean z9 = (count2 & 1) == 1;
        if (z) {
            if (count > 12) {
                z6 = false;
                z5 = true;
            } else {
                z6 = count < 4;
                z5 = false;
            }
            if (count2 > 12) {
                z2 = z6;
                z3 = z5;
            } else if (count2 < 4) {
                z2 = z6;
                z3 = z5;
                z7 = true;
                z4 = false;
                if (i2 != 1) {
                    if (i2 == -1) {
                        if (z8) {
                            if (!z9) {
                                z2 = true;
                            } else {
                                throw NotFoundException.getNotFoundInstance();
                            }
                        } else if (z9) {
                            z7 = true;
                        } else {
                            throw NotFoundException.getNotFoundInstance();
                        }
                    } else if (i2 != 0) {
                        throw NotFoundException.getNotFoundInstance();
                    } else if (z8) {
                        if (!z9) {
                            throw NotFoundException.getNotFoundInstance();
                        } else if (count < count2) {
                            z2 = true;
                            z4 = true;
                        } else {
                            z7 = true;
                        }
                    } else if (z9) {
                        throw NotFoundException.getNotFoundInstance();
                    }
                    if (z2) {
                    }
                    if (z3) {
                    }
                    if (z7) {
                    }
                    if (z4) {
                    }
                } else if (!z8) {
                    if (!z9) {
                        throw NotFoundException.getNotFoundInstance();
                    }
                    z4 = true;
                    if (z2) {
                        if (!z3) {
                            increment(getOddCounts(), getOddRoundingErrors());
                        } else {
                            throw NotFoundException.getNotFoundInstance();
                        }
                    }
                    if (z3) {
                        decrement(getOddCounts(), getOddRoundingErrors());
                    }
                    if (z7) {
                        if (!z4) {
                            increment(getEvenCounts(), getOddRoundingErrors());
                        } else {
                            throw NotFoundException.getNotFoundInstance();
                        }
                    }
                    if (z4) {
                        decrement(getEvenCounts(), getEvenRoundingErrors());
                        return;
                    }
                    return;
                } else if (z9) {
                    throw NotFoundException.getNotFoundInstance();
                }
                z3 = true;
                if (z2) {
                }
                if (z3) {
                }
                if (z7) {
                }
                if (z4) {
                }
            } else {
                z2 = z6;
                z3 = z5;
                z4 = false;
                if (i2 != 1) {
                }
                z3 = true;
                if (z2) {
                }
                if (z3) {
                }
                if (z7) {
                }
                if (z4) {
                }
            }
        } else {
            if (count > 11) {
                z2 = false;
                z3 = true;
            } else {
                z2 = count < 5;
                z3 = false;
            }
            if (count2 <= 10) {
            }
        }
        z4 = true;
        if (i2 != 1) {
        }
        z3 = true;
        if (z2) {
        }
        if (z3) {
        }
        if (z7) {
        }
        if (z4) {
        }
    }
}
