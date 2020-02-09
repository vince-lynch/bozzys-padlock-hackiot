package com.consmart.ble;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    static String MOD = "AES/ECB/PKCS5Padding";
    static byte[] sKey = {-48, -7, -12, -116, 89, -94, 105, 29, 24, 83, -53, -38, Byte.MIN_VALUE, -124, 67, -109};

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
            return instance.doFinal(bArr);
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
        byte[] bArr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        byte[] bArr2 = {10, 11, 52, 12};
        System.out.println(bArr2);
        byte[] Encrypt = Encrypt(bArr2);
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
