package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.util.HashMap;

public final class ExpandedProductResultParser extends ResultParser {
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0209, code lost:
        continue;
        continue;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x007a, code lost:
        if (r1.equals("3933") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0084, code lost:
        if (r1.equals("3932") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x008e, code lost:
        if (r1.equals("3931") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0098, code lost:
        if (r1.equals("3930") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a1, code lost:
        if (r2.length() >= 4) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a4, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a5, code lost:
        r15 = r2.substring(3);
        r17 = r2.substring(0, 3);
        r16 = r1.substring(3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00bb, code lost:
        if (r1.equals("3923") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00c5, code lost:
        if (r1.equals("3922") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00cf, code lost:
        if (r1.equals("3921") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00d9, code lost:
        if (r1.equals("3920") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00dd, code lost:
        r16 = r1.substring(3);
        r15 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00ea, code lost:
        if (r1.equals("3209") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00f4, code lost:
        if (r1.equals("3208") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00fe, code lost:
        if (r1.equals("3207") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0108, code lost:
        if (r1.equals("3206") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0112, code lost:
        if (r1.equals("3205") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x011c, code lost:
        if (r1.equals("3204") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0126, code lost:
        if (r1.equals("3203") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0130, code lost:
        if (r1.equals("3202") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x013a, code lost:
        if (r1.equals("3201") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0144, code lost:
        if (r1.equals("3200") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0148, code lost:
        r13 = com.google.zxing.client.result.ExpandedProductParsedResult.POUND;
        r14 = r1.substring(3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0156, code lost:
        if (r1.equals("3109") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0160, code lost:
        if (r1.equals("3108") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x016a, code lost:
        if (r1.equals("3107") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0174, code lost:
        if (r1.equals("3106") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x017e, code lost:
        if (r1.equals("3105") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0188, code lost:
        if (r1.equals("3104") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0192, code lost:
        if (r1.equals("3103") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x019c, code lost:
        if (r1.equals("3102") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01a6, code lost:
        if (r1.equals("3101") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x01b0, code lost:
        if (r1.equals("3100") == false) goto L_0x0206;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x01b4, code lost:
        r13 = com.google.zxing.client.result.ExpandedProductParsedResult.KILOGRAM;
        r14 = r1.substring(3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x01ba, code lost:
        r12 = r2;
     */
    public ExpandedProductParsedResult parse(Result result) {
        ExpandedProductParsedResult expandedProductParsedResult = null;
        if (result.getBarcodeFormat() != BarcodeFormat.RSS_EXPANDED) {
            return null;
        }
        String massagedText = getMassagedText(result);
        HashMap hashMap = new HashMap();
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        String str8 = null;
        String str9 = null;
        String str10 = null;
        String str11 = null;
        String str12 = null;
        String str13 = null;
        int i = 0;
        while (i < massagedText.length()) {
            String findAIvalue = findAIvalue(i, massagedText);
            if (findAIvalue == null) {
                return expandedProductParsedResult;
            }
            int length = i + findAIvalue.length() + 2;
            String findValue = findValue(length, massagedText);
            int length2 = length + findValue.length();
            int hashCode = findAIvalue.hashCode();
            String str14 = massagedText;
            if (hashCode != 1570) {
                if (hashCode != 1572) {
                    if (hashCode != 1574) {
                        switch (hashCode) {
                            case 1536:
                                if (findAIvalue.equals("00")) {
                                    str2 = findValue;
                                    continue;
                                }
                            case 1537:
                                if (findAIvalue.equals("01")) {
                                    str = findValue;
                                    continue;
                                }
                            default:
                                switch (hashCode) {
                                    case 1567:
                                        if (findAIvalue.equals("10")) {
                                            str3 = findValue;
                                            continue;
                                        }
                                    case 1568:
                                        if (findAIvalue.equals("11")) {
                                            str4 = findValue;
                                            continue;
                                        }
                                    default:
                                        switch (hashCode) {
                                            case 1567966:
                                                break;
                                            case 1567967:
                                                break;
                                            case 1567968:
                                                break;
                                            case 1567969:
                                                break;
                                            case 1567970:
                                                break;
                                            case 1567971:
                                                break;
                                            case 1567972:
                                                break;
                                            case 1567973:
                                                break;
                                            case 1567974:
                                                break;
                                            case 1567975:
                                                break;
                                            default:
                                                switch (hashCode) {
                                                    case 1568927:
                                                        break;
                                                    case 1568928:
                                                        break;
                                                    case 1568929:
                                                        break;
                                                    case 1568930:
                                                        break;
                                                    case 1568931:
                                                        break;
                                                    case 1568932:
                                                        break;
                                                    case 1568933:
                                                        break;
                                                    case 1568934:
                                                        break;
                                                    case 1568935:
                                                        break;
                                                    case 1568936:
                                                        break;
                                                    default:
                                                        switch (hashCode) {
                                                            case 1575716:
                                                                break;
                                                            case 1575717:
                                                                break;
                                                            case 1575718:
                                                                break;
                                                            case 1575719:
                                                                break;
                                                            default:
                                                                switch (hashCode) {
                                                                    case 1575747:
                                                                        break;
                                                                    case 1575748:
                                                                        break;
                                                                    case 1575749:
                                                                        break;
                                                                    case 1575750:
                                                                        break;
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                    } else if (findAIvalue.equals("17")) {
                        str7 = findValue;
                        i = length2;
                        massagedText = str14;
                        expandedProductParsedResult = null;
                    }
                } else if (findAIvalue.equals("15")) {
                    str6 = findValue;
                    i = length2;
                    massagedText = str14;
                    expandedProductParsedResult = null;
                }
            } else if (findAIvalue.equals("13")) {
                str5 = findValue;
                i = length2;
                massagedText = str14;
                expandedProductParsedResult = null;
            }
            hashMap.put(findAIvalue, findValue);
            i = length2;
            massagedText = str14;
            expandedProductParsedResult = null;
        }
        ExpandedProductParsedResult expandedProductParsedResult2 = new ExpandedProductParsedResult(massagedText, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, hashMap);
        return expandedProductParsedResult2;
    }

    private static String findAIvalue(int i, String str) {
        if (str.charAt(i) != '(') {
            return null;
        }
        String substring = str.substring(i + 1);
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < substring.length(); i2++) {
            char charAt = substring.charAt(i2);
            if (charAt == ')') {
                return sb.toString();
            }
            if (charAt < '0' || charAt > '9') {
                return null;
            }
            sb.append(charAt);
        }
        return sb.toString();
    }

    private static String findValue(int i, String str) {
        StringBuilder sb = new StringBuilder();
        String substring = str.substring(i);
        for (int i2 = 0; i2 < substring.length(); i2++) {
            char charAt = substring.charAt(i2);
            if (charAt == '(') {
                if (findAIvalue(i2, substring) != null) {
                    break;
                }
                sb.append('(');
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }
}
