package com.google.zxing.datamatrix.decoder;

final class DataBlock {
    private final byte[] codewords;
    private final int numDataCodewords;

    private DataBlock(int i, byte[] bArr) {
        this.numDataCodewords = i;
        this.codewords = bArr;
    }

    static DataBlock[] getDataBlocks(byte[] bArr, Version version) {
        ECBlocks eCBlocks = version.getECBlocks();
        ECB[] eCBlocks2 = eCBlocks.getECBlocks();
        int i = 0;
        for (ECB count : eCBlocks2) {
            i += count.getCount();
        }
        DataBlock[] dataBlockArr = new DataBlock[i];
        int length = eCBlocks2.length;
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            ECB ecb = eCBlocks2[i3];
            int i4 = i2;
            int i5 = 0;
            while (i5 < ecb.getCount()) {
                int dataCodewords = ecb.getDataCodewords();
                int i6 = i4 + 1;
                dataBlockArr[i4] = new DataBlock(dataCodewords, new byte[(eCBlocks.getECCodewords() + dataCodewords)]);
                i5++;
                i4 = i6;
            }
            i3++;
            i2 = i4;
        }
        int length2 = dataBlockArr[0].codewords.length - eCBlocks.getECCodewords();
        int i7 = length2 - 1;
        int i8 = 0;
        int i9 = 0;
        while (i9 < i7) {
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
        boolean z = version.getVersionNumber() == 24;
        int i13 = z ? 8 : i2;
        int i14 = 0;
        while (i14 < i13) {
            int i15 = i8 + 1;
            dataBlockArr[i14].codewords[i7] = bArr[i8];
            i14++;
            i8 = i15;
        }
        int length3 = dataBlockArr[0].codewords.length;
        for (int i16 = length2; i16 < length3; i16++) {
            int i17 = 0;
            while (i17 < i2) {
                int i18 = i8 + 1;
                dataBlockArr[i17].codewords[(!z || i17 <= 7) ? i16 : i16 - 1] = bArr[i8];
                i17++;
                i8 = i18;
            }
        }
        if (i8 == bArr.length) {
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
