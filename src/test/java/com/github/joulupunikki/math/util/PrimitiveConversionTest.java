/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.joulupunikki.math.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author joulupunikki joulupunikki@gmail.communist.invalid
 */
public class PrimitiveConversionTest {

    public PrimitiveConversionTest() {
    }

    /**
     * Test of byteArrayToLongArray method, of class PrimitiveConversion.
     */
    @Test
    public void testByteArrayToLongArray() {
        System.out.println("byteArrayToLongArray");
        byte[] t = new byte[16];
        long a = 0x12345678_23456789L;
        long b = 0x7890abcd_890abcdeL;
        long[] expResult = {a, b};
        System.arraycopy(PrimitiveConversion.longToByteArray(a), 0, t, 0, Long.BYTES);
        System.arraycopy(PrimitiveConversion.longToByteArray(b), 0, t, Long.BYTES, Long.BYTES);
        long[] result = PrimitiveConversion.byteArrayToLongArray(t);
        Assert.assertArrayEquals(expResult, result);

        t = new byte[3];
        try {
            PrimitiveConversion.byteArrayToLongArray(t);
            Assert.fail("Expected IndexOutOfBoundsException!");
        } catch (IndexOutOfBoundsException e) {
            // ignored
        }
    }

    /**
     * Test of byteArrayToInt method, of class PrimitiveConversion.
     */
    @Test
    public void testByteArrayToInt() {
        System.out.println("byteArrayToInt");
        int expResult = 0x12345678;
        int result = PrimitiveConversion.byteArrayToInt(PrimitiveConversion.intToByteArray(expResult));
        Assert.assertEquals(expResult, result);
        expResult = 0x890abcde;
        result = PrimitiveConversion.byteArrayToInt(PrimitiveConversion.intToByteArray(expResult));
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of byteArrayToLong method, of class PrimitiveConversion.
     */
    @Test
    public void testByteArrayToLong() {
        System.out.println("byteArrayToLong");
        long expResult = 0x12345678_23456789L;
        long result = PrimitiveConversion.byteArrayToLong(PrimitiveConversion.longToByteArray(expResult));
        Assert.assertEquals(expResult, result);
        expResult = 0x7890abcd_890abcdeL;
        result = PrimitiveConversion.byteArrayToLong(PrimitiveConversion.longToByteArray(expResult));
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of intToByteArray method, of class PrimitiveConversion.
     */
    @Test
    public void testIntToByteArray() {
        // included in testByteArrayToInt()
    }

    /**
     * Test of longToByteArray method, of class PrimitiveConversion.
     */
    @Test
    public void testLongToByteArray() {
        // included in testByteArrayToLong()
    }

}
