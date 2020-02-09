package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.pdf417.PDF417ResultMetadata;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

final class DecodedBitStreamParser {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode = null;
    private static final int AL = 28;
    private static final int AS = 27;
    private static final int BEGIN_MACRO_PDF417_CONTROL_BLOCK = 928;
    private static final int BEGIN_MACRO_PDF417_OPTIONAL_FIELD = 923;
    private static final int BYTE_COMPACTION_MODE_LATCH = 901;
    private static final int BYTE_COMPACTION_MODE_LATCH_6 = 924;
    private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1");
    private static final int ECI_CHARSET = 927;
    private static final int ECI_GENERAL_PURPOSE = 926;
    private static final int ECI_USER_DEFINED = 925;
    private static final BigInteger[] EXP900 = new BigInteger[16];
    private static final int LL = 27;
    private static final int MACRO_PDF417_TERMINATOR = 922;
    private static final int MAX_NUMERIC_CODEWORDS = 15;
    private static final char[] MIXED_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '&', 13, 9, ',', ':', '#', '-', '.', '$', '/', '+', '%', '*', '=', '^'};
    private static final int ML = 28;
    private static final int MODE_SHIFT_TO_BYTE_COMPACTION_MODE = 913;
    private static final int NUMBER_OF_SEQUENCE_CODEWORDS = 2;
    private static final int NUMERIC_COMPACTION_MODE_LATCH = 902;
    private static final int PAL = 29;
    private static final int PL = 25;
    private static final int PS = 29;
    private static final char[] PUNCT_CHARS = {';', '<', '>', '@', '[', '\\', ']', '_', '`', '~', '!', 13, 9, ',', ':', 10, '-', '.', '$', '/', '\"', '|', '*', '(', ')', '?', '{', '}', '\''};
    private static final int TEXT_COMPACTION_MODE_LATCH = 900;

    private enum Mode {
        ALPHA,
        LOWER,
        MIXED,
        PUNCT,
        ALPHA_SHIFT,
        PUNCT_SHIFT
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(14:3|4|5|6|7|8|9|10|11|12|13|(2:14|15)|16|18) */
    /* JADX WARNING: Can't wrap try/catch for region: R(15:3|4|5|6|7|8|9|10|11|12|13|14|15|16|18) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0027 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x0030 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0039 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0015 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x001e */
    static /* synthetic */ int[] $SWITCH_TABLE$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode() {
        int[] iArr = $SWITCH_TABLE$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode;
        if (iArr != null) {
            return iArr;
        }
        int[] iArr2 = new int[Mode.values().length];
        iArr2[Mode.ALPHA.ordinal()] = 1;
        iArr2[Mode.ALPHA_SHIFT.ordinal()] = 5;
        iArr2[Mode.LOWER.ordinal()] = 2;
        iArr2[Mode.MIXED.ordinal()] = 3;
        iArr2[Mode.PUNCT.ordinal()] = 4;
        try {
            iArr2[Mode.PUNCT_SHIFT.ordinal()] = 6;
        } catch (NoSuchFieldError unused) {
        }
        $SWITCH_TABLE$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode = iArr2;
        return iArr2;
    }

    static {
        EXP900[0] = BigInteger.ONE;
        BigInteger valueOf = BigInteger.valueOf(900);
        EXP900[1] = valueOf;
        for (int i = 2; i < EXP900.length; i++) {
            EXP900[i] = EXP900[i - 1].multiply(valueOf);
        }
    }

    private DecodedBitStreamParser() {
    }

    static DecoderResult decode(int[] iArr, String str) throws FormatException {
        int i;
        int i2 = 2;
        StringBuilder sb = new StringBuilder(iArr.length * 2);
        Charset charset = DEFAULT_ENCODING;
        int i3 = iArr[1];
        PDF417ResultMetadata pDF417ResultMetadata = new PDF417ResultMetadata();
        while (i2 < iArr[0]) {
            if (i3 != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                switch (i3) {
                    case TEXT_COMPACTION_MODE_LATCH /*900*/:
                        i = textCompaction(iArr, i2, sb);
                        break;
                    case BYTE_COMPACTION_MODE_LATCH /*901*/:
                        i = byteCompaction(i3, iArr, charset, i2, sb);
                        break;
                    case NUMERIC_COMPACTION_MODE_LATCH /*902*/:
                        i = numericCompaction(iArr, i2, sb);
                        break;
                    default:
                        switch (i3) {
                            case MACRO_PDF417_TERMINATOR /*922*/:
                            case BEGIN_MACRO_PDF417_OPTIONAL_FIELD /*923*/:
                                throw FormatException.getFormatInstance();
                            case BYTE_COMPACTION_MODE_LATCH_6 /*924*/:
                                break;
                            case ECI_USER_DEFINED /*925*/:
                                i = i2 + 1;
                                break;
                            case ECI_GENERAL_PURPOSE /*926*/:
                                i = i2 + 2;
                                break;
                            case ECI_CHARSET /*927*/:
                                int i4 = i2 + 1;
                                Charset forName = Charset.forName(CharacterSetECI.getCharacterSetECIByValue(iArr[i2]).name());
                                i = i4;
                                charset = forName;
                                break;
                            case 928:
                                i = decodeMacroBlock(iArr, i2, pDF417ResultMetadata);
                                break;
                            default:
                                i = textCompaction(iArr, i2 - 1, sb);
                                break;
                        }
                        i = byteCompaction(i3, iArr, charset, i2, sb);
                        break;
                }
            } else {
                int i5 = i2 + 1;
                sb.append((char) iArr[i2]);
                i = i5;
            }
            if (i < iArr.length) {
                int i6 = i + 1;
                i3 = iArr[i];
                i2 = i6;
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        if (sb.length() != 0) {
            DecoderResult decoderResult = new DecoderResult(null, sb.toString(), null, str);
            decoderResult.setOther(pDF417ResultMetadata);
            return decoderResult;
        }
        throw FormatException.getFormatInstance();
    }

    private static int decodeMacroBlock(int[] iArr, int i, PDF417ResultMetadata pDF417ResultMetadata) throws FormatException {
        if (i + 2 <= iArr[0]) {
            int[] iArr2 = new int[2];
            int i2 = i;
            int i3 = 0;
            while (i3 < 2) {
                iArr2[i3] = iArr[i2];
                i3++;
                i2++;
            }
            pDF417ResultMetadata.setSegmentIndex(Integer.parseInt(decodeBase900toBase10(iArr2, 2)));
            StringBuilder sb = new StringBuilder();
            int textCompaction = textCompaction(iArr, i2, sb);
            pDF417ResultMetadata.setFileId(sb.toString());
            if (iArr[textCompaction] == BEGIN_MACRO_PDF417_OPTIONAL_FIELD) {
                int i4 = textCompaction + 1;
                int[] iArr3 = new int[(iArr[0] - i4)];
                boolean z = false;
                int i5 = 0;
                while (i4 < iArr[0] && !z) {
                    int i6 = i4 + 1;
                    int i7 = iArr[i4];
                    if (i7 < TEXT_COMPACTION_MODE_LATCH) {
                        int i8 = i5 + 1;
                        iArr3[i5] = i7;
                        i4 = i6;
                        i5 = i8;
                    } else if (i7 == MACRO_PDF417_TERMINATOR) {
                        pDF417ResultMetadata.setLastSegment(true);
                        i4 = i6 + 1;
                        z = true;
                    } else {
                        throw FormatException.getFormatInstance();
                    }
                }
                pDF417ResultMetadata.setOptionalData(Arrays.copyOf(iArr3, i5));
                return i4;
            } else if (iArr[textCompaction] != MACRO_PDF417_TERMINATOR) {
                return textCompaction;
            } else {
                pDF417ResultMetadata.setLastSegment(true);
                return textCompaction + 1;
            }
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    private static int textCompaction(int[] iArr, int i, StringBuilder sb) {
        int[] iArr2 = new int[((iArr[0] - i) * 2)];
        int[] iArr3 = new int[((iArr[0] - i) * 2)];
        boolean z = false;
        int i2 = 0;
        while (i < iArr[0] && !z) {
            int i3 = i + 1;
            int i4 = iArr[i];
            if (i4 < TEXT_COMPACTION_MODE_LATCH) {
                iArr2[i2] = i4 / 30;
                iArr2[i2 + 1] = i4 % 30;
                i2 += 2;
            } else if (i4 != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                if (i4 != 928) {
                    switch (i4) {
                        case TEXT_COMPACTION_MODE_LATCH /*900*/:
                            int i5 = i2 + 1;
                            iArr2[i2] = TEXT_COMPACTION_MODE_LATCH;
                            i2 = i5;
                            break;
                        case BYTE_COMPACTION_MODE_LATCH /*901*/:
                        case NUMERIC_COMPACTION_MODE_LATCH /*902*/:
                            break;
                        default:
                            switch (i4) {
                                case MACRO_PDF417_TERMINATOR /*922*/:
                                case BEGIN_MACRO_PDF417_OPTIONAL_FIELD /*923*/:
                                case BYTE_COMPACTION_MODE_LATCH_6 /*924*/:
                                    break;
                            }
                    }
                }
                i = i3 - 1;
                z = true;
            } else {
                iArr2[i2] = MODE_SHIFT_TO_BYTE_COMPACTION_MODE;
                i = i3 + 1;
                iArr3[i2] = iArr[i3];
                i2++;
            }
            i = i3;
        }
        decodeTextCompaction(iArr2, iArr3, i2, sb);
        return i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004c, code lost:
        r4 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0059, code lost:
        r4 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0086, code lost:
        r4 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00b7, code lost:
        r5 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00d9, code lost:
        r3 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x00f9, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x00fa, code lost:
        if (r3 == 0) goto L_0x00ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x00fc, code lost:
        r0.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x00ff, code lost:
        r2 = r2 + 1;
     */
    private static void decodeTextCompaction(int[] iArr, int[] iArr2, int i, StringBuilder sb) {
        char c;
        Mode mode;
        StringBuilder sb2 = sb;
        Mode mode2 = Mode.ALPHA;
        Mode mode3 = Mode.ALPHA;
        int i2 = 0;
        int i3 = i;
        while (i2 < i3) {
            int i4 = iArr[i2];
            switch ($SWITCH_TABLE$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode()[mode2.ordinal()]) {
                case 1:
                    if (i4 >= 26) {
                        if (i4 != 26) {
                            if (i4 != 27) {
                                if (i4 != 28) {
                                    if (i4 != 29) {
                                        if (i4 != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                            if (i4 == TEXT_COMPACTION_MODE_LATCH) {
                                                mode = Mode.ALPHA;
                                                break;
                                            }
                                        } else {
                                            sb2.append((char) iArr2[i2]);
                                            break;
                                        }
                                    } else {
                                        mode = Mode.PUNCT_SHIFT;
                                        break;
                                    }
                                } else {
                                    mode = Mode.MIXED;
                                    break;
                                }
                            } else {
                                mode = Mode.LOWER;
                                break;
                            }
                        }
                    } else {
                        c = (char) (i4 + 65);
                        break;
                    }
                    break;
                case 2:
                    if (i4 >= 26) {
                        if (i4 != 26) {
                            if (i4 != 27) {
                                if (i4 != 28) {
                                    if (i4 != 29) {
                                        if (i4 != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                            if (i4 == TEXT_COMPACTION_MODE_LATCH) {
                                                mode = Mode.ALPHA;
                                                break;
                                            }
                                        } else {
                                            sb2.append((char) iArr2[i2]);
                                            break;
                                        }
                                    } else {
                                        mode = Mode.PUNCT_SHIFT;
                                        break;
                                    }
                                } else {
                                    mode = Mode.MIXED;
                                    break;
                                }
                            } else {
                                mode = Mode.ALPHA_SHIFT;
                                break;
                            }
                        }
                    } else {
                        c = (char) (i4 + 97);
                        break;
                    }
                    break;
                case 3:
                    if (i4 >= 25) {
                        if (i4 != 25) {
                            if (i4 != 26) {
                                if (i4 != 27) {
                                    if (i4 != 28) {
                                        if (i4 != 29) {
                                            if (i4 != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                                if (i4 == TEXT_COMPACTION_MODE_LATCH) {
                                                    mode = Mode.ALPHA;
                                                    break;
                                                }
                                            } else {
                                                sb2.append((char) iArr2[i2]);
                                                break;
                                            }
                                        } else {
                                            mode = Mode.PUNCT_SHIFT;
                                            break;
                                        }
                                    } else {
                                        mode = Mode.ALPHA;
                                        break;
                                    }
                                } else {
                                    mode = Mode.LOWER;
                                    break;
                                }
                            }
                        } else {
                            mode = Mode.PUNCT;
                            break;
                        }
                    } else {
                        c = MIXED_CHARS[i4];
                        break;
                    }
                    break;
                case 4:
                    if (i4 >= 29) {
                        if (i4 != 29) {
                            if (i4 != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                if (i4 == TEXT_COMPACTION_MODE_LATCH) {
                                    mode = Mode.ALPHA;
                                    break;
                                }
                            } else {
                                sb2.append((char) iArr2[i2]);
                                break;
                            }
                        } else {
                            mode = Mode.ALPHA;
                            break;
                        }
                    } else {
                        c = PUNCT_CHARS[i4];
                        break;
                    }
                    break;
                case 5:
                    if (i4 >= 26) {
                        if (i4 != 26) {
                            if (i4 == TEXT_COMPACTION_MODE_LATCH) {
                                mode = Mode.ALPHA;
                                break;
                            }
                        } else {
                            mode2 = mode3;
                            break;
                        }
                    } else {
                        c = (char) (i4 + 65);
                        break;
                    }
                    break;
                case 6:
                    if (i4 >= 29) {
                        if (i4 != 29) {
                            if (i4 != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                                if (i4 == TEXT_COMPACTION_MODE_LATCH) {
                                    mode = Mode.ALPHA;
                                    break;
                                }
                            } else {
                                sb2.append((char) iArr2[i2]);
                                break;
                            }
                        } else {
                            mode = Mode.ALPHA;
                            break;
                        }
                    } else {
                        c = PUNCT_CHARS[i4];
                        break;
                    }
                    break;
            }
        }
    }

    private static int byteCompaction(int i, int[] iArr, Charset charset, int i2, StringBuilder sb) {
        int i3;
        long j;
        int i4;
        int i5;
        int i6 = i;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i7 = MACRO_PDF417_TERMINATOR;
        int i8 = BEGIN_MACRO_PDF417_OPTIONAL_FIELD;
        int i9 = 928;
        int i10 = NUMERIC_COMPACTION_MODE_LATCH;
        long j2 = 900;
        if (i6 == BYTE_COMPACTION_MODE_LATCH) {
            int[] iArr2 = new int[6];
            int i11 = i2 + 1;
            int i12 = iArr[i2];
            int i13 = i11;
            boolean z = false;
            loop0:
            while (true) {
                i4 = 0;
                long j3 = 0;
                while (i3 < iArr[0] && !z) {
                    int i14 = i4 + 1;
                    iArr2[i4] = i12;
                    j3 = (j3 * j) + ((long) i12);
                    int i15 = i3 + 1;
                    i12 = iArr[i3];
                    if (i12 == TEXT_COMPACTION_MODE_LATCH || i12 == BYTE_COMPACTION_MODE_LATCH || i12 == NUMERIC_COMPACTION_MODE_LATCH || i12 == BYTE_COMPACTION_MODE_LATCH_6 || i12 == 928 || i12 == i8 || i12 == i7) {
                        i3 = i15 - 1;
                        i4 = i14;
                        i7 = MACRO_PDF417_TERMINATOR;
                        i8 = BEGIN_MACRO_PDF417_OPTIONAL_FIELD;
                        j = 900;
                        z = true;
                    } else if (i14 % 5 != 0 || i14 <= 0) {
                        i3 = i15;
                        i4 = i14;
                        i7 = MACRO_PDF417_TERMINATOR;
                        i8 = BEGIN_MACRO_PDF417_OPTIONAL_FIELD;
                        j = 900;
                    } else {
                        int i16 = 0;
                        while (i16 < 6) {
                            byteArrayOutputStream.write((byte) ((int) (j3 >> ((5 - i16) * 8))));
                            i16++;
                            i7 = MACRO_PDF417_TERMINATOR;
                            i8 = BEGIN_MACRO_PDF417_OPTIONAL_FIELD;
                        }
                        i13 = i15;
                        j2 = 900;
                    }
                }
            }
            if (i3 != iArr[0] || i12 >= TEXT_COMPACTION_MODE_LATCH) {
                i5 = i4;
            } else {
                int i17 = i4 + 1;
                iArr2[i4] = i12;
                i5 = i17;
            }
            for (int i18 = 0; i18 < i5; i18++) {
                byteArrayOutputStream.write((byte) iArr2[i18]);
            }
        } else if (i6 == BYTE_COMPACTION_MODE_LATCH_6) {
            int i19 = i2;
            boolean z2 = false;
            loop4:
            while (true) {
                int i20 = 0;
                long j4 = 0;
                while (i3 < iArr[0] && !z2) {
                    int i21 = i3 + 1;
                    int i22 = iArr[i3];
                    if (i22 < TEXT_COMPACTION_MODE_LATCH) {
                        i20++;
                        j4 = (j4 * 900) + ((long) i22);
                        i19 = i21;
                    } else {
                        if (i22 != TEXT_COMPACTION_MODE_LATCH && i22 != BYTE_COMPACTION_MODE_LATCH && i22 != i10 && i22 != BYTE_COMPACTION_MODE_LATCH_6 && i22 != i9) {
                            if (i22 != BEGIN_MACRO_PDF417_OPTIONAL_FIELD) {
                                if (i22 != MACRO_PDF417_TERMINATOR) {
                                    i19 = i21;
                                }
                                i19 = i21 - 1;
                                z2 = true;
                            }
                        }
                        i19 = i21 - 1;
                        z2 = true;
                    }
                    if (i20 % 5 != 0 || i20 <= 0) {
                        i9 = 928;
                        i10 = NUMERIC_COMPACTION_MODE_LATCH;
                    } else {
                        int i23 = 0;
                        while (i23 < 6) {
                            byteArrayOutputStream.write((byte) ((int) (j4 >> ((5 - i23) * 8))));
                            i23++;
                            i9 = 928;
                            i10 = NUMERIC_COMPACTION_MODE_LATCH;
                        }
                    }
                }
            }
        } else {
            i3 = i2;
        }
        sb.append(new String(byteArrayOutputStream.toByteArray(), charset));
        return i3;
    }

    private static int numericCompaction(int[] iArr, int i, StringBuilder sb) throws FormatException {
        int[] iArr2 = new int[15];
        boolean z = false;
        loop0:
        while (true) {
            int i2 = 0;
            while (i < iArr[0] && !z) {
                int i3 = i + 1;
                int i4 = iArr[i];
                if (i3 == iArr[0]) {
                    z = true;
                }
                if (i4 < TEXT_COMPACTION_MODE_LATCH) {
                    iArr2[i2] = i4;
                    i2++;
                } else if (i4 == TEXT_COMPACTION_MODE_LATCH || i4 == BYTE_COMPACTION_MODE_LATCH || i4 == BYTE_COMPACTION_MODE_LATCH_6 || i4 == 928 || i4 == BEGIN_MACRO_PDF417_OPTIONAL_FIELD || i4 == MACRO_PDF417_TERMINATOR) {
                    i3--;
                    z = true;
                }
                if ((i2 % 15 == 0 || i4 == NUMERIC_COMPACTION_MODE_LATCH || z) && i2 > 0) {
                    sb.append(decodeBase900toBase10(iArr2, i2));
                    i = i3;
                } else {
                    i = i3;
                }
            }
        }
        return i;
    }

    private static String decodeBase900toBase10(int[] iArr, int i) throws FormatException {
        BigInteger bigInteger = BigInteger.ZERO;
        for (int i2 = 0; i2 < i; i2++) {
            bigInteger = bigInteger.add(EXP900[(i - i2) - 1].multiply(BigInteger.valueOf((long) iArr[i2])));
        }
        String bigInteger2 = bigInteger.toString();
        if (bigInteger2.charAt(0) == '1') {
            return bigInteger2.substring(1);
        }
        throw FormatException.getFormatInstance();
    }
}
