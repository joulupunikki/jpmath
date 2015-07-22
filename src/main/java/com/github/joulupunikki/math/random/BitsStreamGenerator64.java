/*
 * Copyright 2015 joulupunikki joulupunikki@gmail.communist.invalid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.joulupunikki.math.random;

import com.github.joulupunikki.math.util.PrimitiveConversion;
import java.security.MessageDigest;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.random.BitsStreamGenerator;
import org.apache.commons.math3.util.FastMath;

/**
 * 64-bit extension of BitsStreamGenerator, a base class for random number
 * generators that generates bits streams.
 *
 * @author joulupunikki joulupunikki@gmail.communist.invalid
 */
public abstract class BitsStreamGenerator64 extends BitsStreamGenerator {

    /**
     * Bit size of generator words.
     */
    public static final int WORD_BITS = 64;
    /**
     * Mask of 32 LSBs of a Long.
     */
    public static final long LSB32_MASK_LONG = 0xffffffffL;
    /**
     * State bit count.
     */
    protected int STATE_BITS;
    /**
     * Word count of state bits, STATE_BITS / WORD BITS.
     */
    protected int STATE_WORDS;
    /**
     * state initialization vector
     */
    protected long[] init_vector;
    /* date in hex, append version (eg. "a2") if necessary */
    private static final long serialVersionUID = 0x20150704L;
    /* time based seeds will have value of this counter added */
    private static long seed_uniquefier = 0L;

    /* from Random */
    private static final double DOUBLE_UNIT = 0x1.0p-53;
    /* when a gaussian is requested, the algorithm always produces two, and one is stored */
    private double nextGaussian;
    /* when requesting an int, a long is created, and the 32 LSBs are stored */
    private int intLeft = 0;
    /* true if there are 32 bits left. */
    private boolean isIntLeft = false;

    /**
     * For 64 bit generators, {@link #nextL(int bits)} is the core generator
     * function and this method will delegate.
     *
     * @param bits
     * @return
     */
    @Override
    protected int next(int bits) {
        if (isIntLeft) {
            isIntLeft = false;
            return intLeft >>> (Integer.SIZE - bits);
        } else {
            return nextInt() >>> (Integer.SIZE - bits);
        }
    }

    /**
     * Since we have a 64-bit generator, we use a long returning function as the
     * core generator function which is used by all other functions.
     *
     * @param bits
     * @return
     */
    protected abstract long nextL(int bits);

    @Override
    public int nextInt() {
        if (isIntLeft) {
            // use the 32 LSBs of the long already generated
            isIntLeft = false;
            return intLeft;
        } else {
            // generate a new pair of integers (long split in half)
            long t = nextLong();
            intLeft = (int) t; // 32 LSBs stored
            isIntLeft = true;
            return (int) (t >>> Integer.SIZE); // 32 MSBs returned
        }
    }

    @Override
    public long nextLong() {
        return nextL(WORD_BITS);
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code long} value between
     * 0 (inclusive) and the specified value (exclusive), drawn from this random
     * number generator's sequence. Identical with super, except with long
     * primitives we need just one call to nextL(int bits).
     *
     * @param n the bound on the random number to be returned. Must be positive.
     * @return a pseudorandom, uniformly distributed {@code long} value between
     * 0 (inclusive) and n (exclusive).
     * @throws IllegalArgumentException if n is not positive.
     */
    @Override
    public long nextLong(long n) throws IllegalArgumentException {
        if (n > 0) {
            long bits;
            long val;
            do {
                // the only modification, 2 * next() => 1 * nextL()
                bits = nextL(63);
                val = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }
        throw new NotStrictlyPositiveException(n);
    }

    /**
     * Returns float from Uniform(0,1).
     *
     * @return the float
     */
    @Override
    public float nextFloat() {
        return next(24) / ((float) (1 << 24));
    }

    /**
     * Returns double from Uniform(0,1).
     *
     * @return the double
     */
    @Override
    public double nextDouble() {
        return nextL(53) * DOUBLE_UNIT;
    }

    /**
     * Copied verbatim from super, need to re-implement here since some
     * persistent state used is private in super.
     *
     * @return
     */
    @Override
    public double nextGaussian() {
        final double random;
        if (Double.isNaN(nextGaussian)) {
            // generate a new pair of gaussian numbers
            final double x = nextDouble();
            final double y = nextDouble();
            final double alpha = 2 * FastMath.PI * x;
            final double r = FastMath.sqrt(-2 * FastMath.log(y));
            random = r * FastMath.cos(alpha);
            nextGaussian = r * FastMath.sin(alpha);
        } else {
            // use the second element of the pair already generated
            random = nextGaussian;
            nextGaussian = Double.NaN;
        }

        return random;
    }

    /**
     * Attention: subclassing generators need to call this when re-seeding to
     * properly reset the generator, calling reset will zero the storage of the
     * next gaussian and next integer.
     */
    @Override
    public void clear() {
        nextGaussian = Double.NaN;
        isIntLeft = false;
    }

    /**
     * @param seed
     */
    @Override
    public void setSeed(int seed) {
        setSeed((long) seed);
    }

    @Override
    public void setSeed(int[] seed_in) {
        // if null seed set from time
        if (seed_in == null) {
            timeSeed();
            return;
        }
        long[] seed_out = new long[seed_in.length];
        for (int i = 0; i < seed_out.length; i++) {
            seed_out[i] = seed_in[i];
        }
        setSeed(seed_out);
    }

    @Override
    public void setSeed(long seed) {
        setSeed(new long[]{seed});
    }

    public void setSeed(long[] seed_in) {
        // if null seed set from time
        if (seed_in == null) {
            timeSeed();
            return;
        }
        setState(hashSeed(seed_in));
    }

    /**
     * nanoTime() is increasing and uniqueSeed() is strictly increasing so no
     * two timeSeed() invocations should use the same seed (the underlying long
     * will overflow in less than 2^64 invocations however).
     */
    void timeSeed() {
        setSeed(System.nanoTime() + uniqueSeed());
    }

    static long uniqueSeed() {
        return seed_uniquefier++;
    }

    /**
     * Will hash the seed with the SHA-512 digest. Enough bits are generated to
     * fill all the state bits of the generator. The digests will be chained so
     * that each new set of 512 bits receives the digest value of the previous
     * set as initial digest data. This should provide well mixed and
     * uncorrelated initial states with all seeds. Secure hashing is however
     * slower than less involved methods of state initialization.
     *
     * @param seed_in the seed
     * @return hashed state array of longs
     */
    public long[] hashSeed(long[] seed_in) {
        // prepare to hash seed
        int seed_len = seed_in.length;
        long[] seed_out = new long[STATE_WORDS];
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (Exception e) { //SHA-512 should be available in java 1.5+
            throw new RuntimeException(null, e);
        }
        int digest_count = (STATE_BITS - 1) / md.getDigestLength() + 1;
        int digest_bytes = md.getDigestLength() / Byte.SIZE;
        if (seed_len > digest_count) {
            seed_len = digest_count;
        }
        byte[] hashed_seed = new byte[STATE_WORDS * Long.BYTES];
        int s = 0;
        byte[] r = null;
        // hash seed
        for (; s < seed_len; s++) {
            r = md.digest(PrimitiveConversion.longToByteArray(seed_in[s]));
            System.arraycopy(r, 0, hashed_seed, s * digest_bytes, digest_bytes);
            md.update(r); // chain digests
        }
        // if out of seed just chain digests
        for (; s < digest_count; s++) {
            r = md.digest();
            System.arraycopy(r, 0, hashed_seed, s * digest_bytes, digest_bytes);
            md.update(r);
            
        }
        // convert digest bytes to longs
        byte[] t = new byte[Long.BYTES];
        for (int i = 0; i < STATE_WORDS; i++) {
            System.arraycopy(hashed_seed, i * Long.BYTES, t, 0, Long.BYTES);
            seed_out[i] = PrimitiveConversion.byteArrayToLong(t);
        }
        return seed_out;
    }

//    public void mixSeed(int seed) {
//        long mixer = Double.doubleToRawLongBits(FastMath.sin(seed));
//
//    }

    /**
     * Seed with strictly positive long seed. The high and low 32 bits of seed
     * are converted to two doubles
     * <code>cos_idx = 1 + (seed >> 32) * dim;</code> and
     * <code>sin_idx = 1 + ((seed & LSB32_MASK_LONG) - 1) * dim;</code> where
     * <code>dim = 2 * STATE_WORDS</code>. These doubles are used as indexes to
     * create an int array of length <code>dim<\code> like so
     * <pre>array[i] =
     * (int) (Double.doubleToRawLongBits(Math.sin(sin_idx + i)) ^
     * Double.doubleToRawLongBits(Math.cos(cos_idx + i)));</pre> so the 32 LSBs
     * of (sin(sin_idx + i) XOR cos(cos_idx + i)) are used. Since the period of
     * sin and cos is &Pi;, in theory these values should never repeat for
     * integer arguments. In practice this may not hold due to double
     * approximation of numbers in &reals;
     *
     * @param seed
     * @return
     */
    public int[] sineSeed(long seed) {
        if (seed < 1) {
            throw new NotStrictlyPositiveException(seed);
        }
        int dim = STATE_WORDS * 2 + 1;
        int[] seed_array = new int[dim];
        double cos_idx = 1 + (seed >> 32) * dim;
        double sin_idx = 1 + ((seed & LSB32_MASK_LONG) - 1) * dim;
        for (int i = 0; i < seed_array.length; i++) {
            sin_idx += 1;
            cos_idx += 1;
            int a = (int) Double.doubleToRawLongBits(FastMath.sin(sin_idx));
            int b = (int) Double.doubleToRawLongBits(FastMath.cos(cos_idx));
            seed_array[i] = a ^ b;
        }
        for (int i = 0; i < seed_array.length - 1; i++) {
            seed_array[i] ^= seed_array[i + 1];
        }
        return seed_array;
    }

    public abstract void setState(Object state);
}
