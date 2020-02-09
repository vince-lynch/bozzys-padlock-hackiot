package com.consmart.ble;

import java.io.PrintStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES2 {
    static String MOD = "AES/ECB/NoPadding";
    static byte[] sKey = {-48, -7, -12, -116, 89, -94, 105, 29, 32, 83, -53, -38, Byte.MIN_VALUE, -124, 67, -109};

    public static void setKey(byte[] bArr) {
        if (bArr != null && bArr.length == sKey.length) {
            for (int i = 0; i < sKey.length; i++) {
                sKey[i] = bArr[i];
            }
        }
    }

    public static byte[] Encrypt(byte[] bArr) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        } else if (sKey.length != 16) {
            System.out.print("Key长度不是16位");
            return null;
        } else {
            SecretKeySpec secretKeySpec = new SecretKeySpec(sKey, "AES");
            Cipher instance = Cipher.getInstance(MOD);
            instance.init(1, secretKeySpec);
            byte[] doFinal = instance.doFinal(bArr);
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder("encrypted length = ");
            sb.append(doFinal.length);
            printStream.println(sb.toString());
            return doFinal;
        }
    }

    public static byte[] Decrypt(byte[] bArr) throws Exception {
        try {
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            } else if (sKey.length != 16) {
                System.out.print("Key长度不是16位");
                return null;
            } else {
                SecretKeySpec secretKeySpec = new SecretKeySpec(sKey, "AES");
                Cipher instance = Cipher.getInstance(MOD);
                instance.init(2, secretKeySpec);
                try {
                    return instance.doFinal(bArr);
                } catch (Exception e) {
                    System.out.println(e.toString());
                    return null;
                }
            }
        } catch (Exception e2) {
            System.out.println(e2.toString());
            return null;
        }
    }

    public static void main(String[] strArr) throws Exception {
        byte[] bArr = new byte[16];
        bArr[0] = 2;
        bArr[1] = 5;
        bArr[2] = 5;
        bArr[3] = 16;
        bArr[4] = 8;
        bArr[5] = 35;
        bArr[6] = 1;
        bArr[7] = 2;
        bArr[9] = 5;
        bArr[10] = 85;
        bArr[11] = 34;
        bArr[12] = 1;
        bArr[13] = 18;
        bArr[14] = 19;
        bArr[15] = 20;
        System.out.println(bArr);
        byte[] Encrypt = Encrypt(bArr);
        String str = "原数据加密后- ";
        if (Encrypt != null) {
            String str2 = str;
            for (byte b : Encrypt) {
                StringBuilder sb = new StringBuilder(String.valueOf(str2));
                sb.append(Integer.toHexString(b & 255));
                sb.append(" ");
                str2 = sb.toString();
            }
            str = str2;
        }
        System.out.println(str);
        byte[] Decrypt = Decrypt(Encrypt);
        String str3 = "解密后的-";
        if (Decrypt != null) {
            for (byte b2 : Decrypt) {
                StringBuilder sb2 = new StringBuilder(String.valueOf(str3));
                sb2.append(Integer.toHexString(b2 & 255));
                sb2.append(" ");
                str3 = sb2.toString();
            }
        }
        System.out.println(str3);
    }
}
