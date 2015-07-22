/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.joulupunikki.math.util;

/**
 *
 * @author joulupunikki joulupunikki@gmail.communist.invalid
 */
public class PrimitiveConversion {

    public static long[] byteArrayToLongArray(byte[] b) {
        long[] r = new long[(b.length - 1) / Long.BYTES + 1];
        byte[] t = new byte[Long.BYTES];
        for (int i = 0; i < r.length; i++) {
            System.arraycopy(b, i * Long.BYTES, t, 0, Long.BYTES);
            r[i] = byteArrayToLong(t);
        }
        return r;
    }

    public static final int byteArrayToInt(byte[] b) {
        return ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16)
                | ((b[2] & 0xFF) << 8) | (b[3] & 0xFF);
    }

    public static final long byteArrayToLong(byte[] b) {
        return ((b[0] & 0xFFL) << 56) | ((b[1] & 0xFFL) << 48)
                | ((b[2] & 0xFFL) << 40) | ((b[3] & 0xFFL) << 32)
                | ((b[4] & 0xFFL) << 24) | ((b[5] & 0xFFL) << 16)
                | ((b[6] & 0xFFL) << 8) | (b[7] & 0xFFL);
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{
            (byte) (value >>> 3 * Byte.SIZE),
            (byte) (value >>> 2 * Byte.SIZE),
            (byte) (value >>> Byte.SIZE),
            (byte) value};
    }

    public static final byte[] longToByteArray(long value) {
        return new byte[]{
            (byte) (value >>> 7 * Byte.SIZE),
            (byte) (value >>> 6 * Byte.SIZE),
            (byte) (value >>> 5 * Byte.SIZE),
            (byte) (value >>> 4 * Byte.SIZE),
            (byte) (value >>> 3 * Byte.SIZE),
            (byte) (value >>> 2 * Byte.SIZE),
            (byte) (value >>> Byte.SIZE),
            (byte) value};
    }
}
