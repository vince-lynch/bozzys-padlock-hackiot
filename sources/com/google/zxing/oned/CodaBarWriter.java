package com.google.zxing.oned;

public final class CodaBarWriter extends OneDimensionalCodeWriter {
    private static final char[] ALT_START_END_CHARS = {'T', 'N', '*', 'E'};
    private static final char[] CHARS_WHICH_ARE_TEN_LENGTH_EACH_AFTER_DECODED = {'/', ':', '+', '.'};
    private static final char DEFAULT_GUARD = START_END_CHARS[0];
    private static final char[] START_END_CHARS = {'A', 'B', 'C', 'D'};

    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0118  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0115 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x00f7 A[SYNTHETIC] */
    public boolean[] encode(String str) {
        char c;
        int i;
        int i2;
        int i3;
        boolean z;
        if (str.length() < 2) {
            StringBuilder sb = new StringBuilder(String.valueOf(DEFAULT_GUARD));
            sb.append(str);
            sb.append(DEFAULT_GUARD);
            str = sb.toString();
        } else {
            char upperCase = Character.toUpperCase(str.charAt(0));
            char upperCase2 = Character.toUpperCase(str.charAt(str.length() - 1));
            boolean arrayContains = CodaBarReader.arrayContains(START_END_CHARS, upperCase);
            boolean arrayContains2 = CodaBarReader.arrayContains(START_END_CHARS, upperCase2);
            boolean arrayContains3 = CodaBarReader.arrayContains(ALT_START_END_CHARS, upperCase);
            boolean arrayContains4 = CodaBarReader.arrayContains(ALT_START_END_CHARS, upperCase2);
            if (arrayContains) {
                if (!arrayContains2) {
                    StringBuilder sb2 = new StringBuilder("Invalid start/end guards: ");
                    sb2.append(str);
                    throw new IllegalArgumentException(sb2.toString());
                }
            } else if (arrayContains3) {
                if (!arrayContains4) {
                    StringBuilder sb3 = new StringBuilder("Invalid start/end guards: ");
                    sb3.append(str);
                    throw new IllegalArgumentException(sb3.toString());
                }
            } else if (arrayContains2 || arrayContains4) {
                StringBuilder sb4 = new StringBuilder("Invalid start/end guards: ");
                sb4.append(str);
                throw new IllegalArgumentException(sb4.toString());
            } else {
                StringBuilder sb5 = new StringBuilder(String.valueOf(DEFAULT_GUARD));
                sb5.append(str);
                sb5.append(DEFAULT_GUARD);
                str = sb5.toString();
            }
        }
        String str2 = str;
        int i4 = 20;
        for (int i5 = 1; i5 < str2.length() - 1; i5++) {
            if (Character.isDigit(str2.charAt(i5)) || str2.charAt(i5) == '-' || str2.charAt(i5) == '$') {
                i4 += 9;
            } else if (CodaBarReader.arrayContains(CHARS_WHICH_ARE_TEN_LENGTH_EACH_AFTER_DECODED, str2.charAt(i5))) {
                i4 += 10;
            } else {
                StringBuilder sb6 = new StringBuilder("Cannot encode : '");
                sb6.append(str2.charAt(i5));
                sb6.append('\'');
                throw new IllegalArgumentException(sb6.toString());
            }
        }
        boolean[] zArr = new boolean[(i4 + (str2.length() - 1))];
        int i6 = 0;
        for (int i7 = 0; i7 < str2.length(); i7++) {
            char upperCase3 = Character.toUpperCase(str2.charAt(i7));
            if (i7 == 0 || i7 == str2.length() - 1) {
                if (upperCase3 == '*') {
                    c = 'C';
                } else if (upperCase3 == 'E') {
                    c = 'D';
                } else if (upperCase3 == 'N') {
                    c = 'B';
                } else if (upperCase3 == 'T') {
                    c = 'A';
                }
                i = 0;
                while (true) {
                    if (i < CodaBarReader.ALPHABET.length) {
                        i2 = 0;
                        break;
                    } else if (c == CodaBarReader.ALPHABET[i]) {
                        i2 = CodaBarReader.CHARACTER_ENCODINGS[i];
                        break;
                    } else {
                        i++;
                    }
                }
                i3 = 0;
                z = true;
                while (true) {
                    int i8 = 0;
                    while (i3 < 7) {
                        zArr[i6] = z;
                        i6++;
                        if (((i2 >> (6 - i3)) & 1) == 0 || i8 == 1) {
                            z = !z;
                            i3++;
                        } else {
                            i8++;
                        }
                    }
                    break;
                }
                if (i7 >= str2.length() - 1) {
                    zArr[i6] = false;
                    i6++;
                }
            }
            c = upperCase3;
            i = 0;
            while (true) {
                if (i < CodaBarReader.ALPHABET.length) {
                }
                i++;
            }
            i3 = 0;
            z = true;
            while (true) {
                int i82 = 0;
                while (i3 < 7) {
                }
                break;
                z = !z;
                i3++;
            }
            if (i7 >= str2.length() - 1) {
            }
        }
        return zArr;
    }
}
