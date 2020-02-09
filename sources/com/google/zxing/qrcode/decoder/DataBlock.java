package com.google.zxing.qrcode.decoder;

import com.google.zxing.qrcode.decoder.Version.ECB;
import com.google.zxing.qrcode.decoder.Version.ECBlocks;

final class DataBlock {
    private final byte[] codewords;
    private final int numDataCodewords;

    private DataBlock(int i, byte[] bArr) {
        this.numDataCodewords = i;
        this.codewords = bArr;
    }

    static DataBlock[] getDataBlocks(byte[] bArr, Version version, ErrorCorrectionLevel errorCorrectionLevel) {
        if (bArr.length == version.getTotalCodewords()) {
            ECBlocks eCBlocksForLevel = version.getECBlocksForLevel(errorCorrectionLevel);
            ECB[] eCBlocks = eCBlocksForLevel.getECBlocks();
            int i = 0;
            for (ECB count : eCBlocks) {
                i += count.getCount();
            }
            DataBlock[] dataBlockArr = new DataBlock[i];
            int length = eCBlocks.length;
            int i2 = 0;
            int i3 = 0;
            while (i3 < length) {
                ECB ecb = eCBlocks[i3];
                int i4 = i2;
                int i5 = 0;
                while (i5 < ecb.getCount()) {
                    int dataCodewords = ecb.getDataCodewords();
                    int i6 = i4 + 1;
                    dataBlockArr[i4] = new DataBlock(dataCodewords, new byte[(eCBlocksForLevel.getECCodewordsPerBlock() + dataCodewords)]);
                    i5++;
                    i4 = i6;
                }
                i3++;
                i2 = i4;
            }
            int length2 = dataBlockArr[0].codewords.length;
            int length3 = dataBlockArr.length - 1;
            while (length3 >= 0 && dataBlockArr[length3].codewords.length != length2) {
                length3--;
            }
            int i7 = length3 + 1;
            int eCCodewordsPerBlock = length2 - eCBlocksForLevel.getECCodewordsPerBlock();
            int i8 = 0;
            int i9 = 0;
            while (i9 < eCCodewordsPerBlock) {
                int i10 = i8;
                int i11 = 0;
                while (i11 < i2) {
                    int i12 = i10 + 1;
                    dataBlockArr[i11].codewords[i9] = bArr[i10];
                    i11++;
                    i10 = i12;
                }
                i9++;
                i8 = i10;
            }
            int i13 = i8;
            int i14 = i7;
            while (i14 < i2) {
                int i15 = i13 + 1;
                dataBlockArr[i14].codewords[eCCodewordsPerBlock] = bArr[i13];
                i14++;
                i13 = i15;
            }
            int length4 = dataBlockArr[0].codewords.length;
            int i16 = eCCodewordsPerBlock;
            while (i16 < length4) {
                int i17 = 0;
                while (i17 < i2) {
                    int i18 = i13 + 1;
                    dataBlockArr[i17].codewords[i17 < i7 ? i16 : i16 + 1] = bArr[i13];
                    i17++;
                    i13 = i18;
                }
                i16++;
            }
            return dataBlockArr;
        }
        throw new IllegalArgumentException();
    }

    /* access modifiers changed from: 0000 */
    public int getNumDataCodewords() {
        return this.numDataCodewords;
    }

    /* access modifiers changed from: 0000 */
    public byte[] getCodewords() {
        return this.codewords;
    }
}
