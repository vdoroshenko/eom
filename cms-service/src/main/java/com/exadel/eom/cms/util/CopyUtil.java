package com.exadel.eom.cms.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CopyUtil {
    private static final int COPY_BUF_LEN = 0x7000; // 7*4k
    private static final int READ_BUF_LEN = 0x400; // 1k

    public static long copy(InputStream from, OutputStream to)
            throws IOException {
        return copyWithCalcDigest(from, to, null);
    }

    public static long copyWithCalcDigest(InputStream from, OutputStream to, MessageDigest md)
            throws IOException {
        byte[] buf = new byte[COPY_BUF_LEN];
        long total = 0;
        int length;
        while ((length = from.read(buf)) != -1) {
            if(md != null) {
                md.update(buf, 0, length);
            }
            to.write(buf, 0, length);
            total += length;
        }
        return total;
    }

    public static String readAsString(InputStream is, String cp) {
        ByteArrayOutputStream bArrOutStrem = new ByteArrayOutputStream();
        byte[] buf = new byte[READ_BUF_LEN];
        int length;
        try {
            while ((length = is.read(buf)) != -1) {
                bArrOutStrem.write(buf, 0, length);
            }
            return bArrOutStrem.toString(cp);
        } catch (Exception e) {
            return null;
        }
    }

    public static long calcDigest(InputStream is, MessageDigest md)
            throws IOException {
        byte[] buf = new byte[COPY_BUF_LEN];
        long total = 0;
        while (true) {
            int r = is.read(buf);
            if (r == -1) {
                break;
            }
            md.update(buf, 0, r);
            total += r;
        }
        return total;
    }

    public static long copyWithCalcHexHash(InputStream from, OutputStream to, String alg, StringBuilder hex)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(alg);
        long size = copyWithCalcDigest(from, to, md);
        hex.append(bytes2Hex(md.digest()));
        return size;
    }

    public static long calcHexHash(InputStream is, String alg, StringBuilder hex)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(alg);
        long size = calcDigest(is, md);
        hex.append(bytes2Hex(md.digest()));
        return size;
    }

    public static String bytes2Hex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static byte[] calcStringHash(String s, String alg)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(alg);
        md.update(s.getBytes());
        return md.digest();
    }
}
