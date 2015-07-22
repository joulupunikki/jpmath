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

import java.util.Arrays;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well44497b;

/**
 * Implements the Xorshift1024* prng as defined by Vigna in {@link http://arxiv.org/abs/1402.6246
 * }, Xorshift generators by Marsaglia in wikipedia {@link https://en.wikipedia.org/wiki/Xorshift
 * }. Features: 64-bit primitives; period 2^1024-1; generates 16-dimensionally
 * equidistributed longs; passes BigCrush of TestU01-suite (this implementation
 * has been tested with dieharder on Ubuntu 14.04); very fast.
 *
 * @author joulupunikki joulupunikki@gmail.com
 */
public class XorShift1024Star extends BitsStreamGenerator64 {

    /* date of mod in hex, append version (eg. "a2") if necessary */
    private static final long serialVersionUID = 0x20150704L;
    /* state bit count of this generator */
    private static final int S_BITS = 1024;
    /* initial state */
    private static final long[] init_state = {
        0x3abb6677_af34ac57L, 0xc0ca5828_fd94f9d8L, 0x86c26ce5_9a8ce60eL, 0xcf677807_9423dccfL, 0xf1d6f19c_b655805dL, 0x56098e6d_38a1a710L, 0xdee59523_eed7511eL, 0x5a9e4b8c_cb3a4686L,
        0x63e22ec2_fbeebabfL, 0x005e58fb_fb0eee60L, 0x7c4aa417_045a68a0L, 0xcc63767b_048e3559L, 0x268d35e7_2f367d3bL, 0x2dbd5dbd_df12fc43L, 0x97762ba1_49260b37L, 0x95a03917_13bddcd7L
    };
    /* state */
    private final long[] s;
    /* state index */
    private int p = 0;

    private void init() {
        STATE_BITS = S_BITS;
        STATE_WORDS = STATE_BITS / WORD_BITS;
    }

    /**
     */
    public XorShift1024Star() {
        init();
        this.s = new long[STATE_WORDS];
        setSeed(System.nanoTime());
    }

    public XorShift1024Star(int seed) {
        init();
        this.s = new long[STATE_WORDS];
        setSeed(seed);
    }

    public XorShift1024Star(int[] seed) {
        init();
        this.s = new long[STATE_WORDS];
        setSeed(seed);
    }

    public XorShift1024Star(long seed) {
        init();
        this.s = new long[STATE_WORDS];
        setSeed(seed);
    }

    public double stateOnes() {
        double r = 0;
        for (long t : s) {
            r += Long.bitCount(t);
        }
        return r / (WORD_BITS * STATE_WORDS);
    }

    public Object getState() {
        long[] r = new long[STATE_WORDS];
        System.arraycopy(s, 0, r, 0, STATE_WORDS);
        return (Object) r;
    }

    public void printState() {
        for (int i = 0; i < s.length; i += 4) {
            System.out.println(s[i] + "L, " + s[i + 1] + "L, " + s[i + 2] + "L, " + s[i + 3] + "L,");

        }
    }

    /**
     * {@inheritDoc}
     *
     * The core function of XorShift1024Star.
     *
     * @return
     */
    @Override
    protected long nextL(int bits) {
        long s0 = s[p];
        p = (p + 1) & 15;
        long s1 = s[p];
        s1 ^= (s1 << 31);
        s1 ^= (s1 >>> 11);
        s0 ^= (s0 >>> 30);
        s[p] = s0 ^ s1;
        return (s[p] * 1181783497276652981L) >>> (WORD_BITS - bits);
    }

    /**
     * Attention: this method resets the state vector to bit for bit equivalence
     * with the seed vector via System.arraycopy(), a zero seed vector will
     * result in a zero state vector, seed vector shorter than state will only
     * reset state partially. In general the setSeed() methods should be used to
     * seed the generator.
     *
     * @param state
     */
    @Override
    public void setState(Object state) {
        long[] seed = (long[]) state;
        int len = seed.length;
        if (len > STATE_WORDS) {
            len = STATE_WORDS;
        }
        System.arraycopy(seed, 0, s, 0, len);
        p = 0;
        clear(); // reset stored values in super
    }

    public static void main(String[] args) {
        XorShift1024Star rng = new XorShift1024Star(1);
        rng.printState();
        System.out.println("");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(rng.nextLong() + "L, ");
            }
            System.out.println("");
        }
        System.out.println("");
        rng.setSeed(2);
        rng.printState();
        System.out.println("");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(rng.nextLong() + "L, ");
            }
            System.out.println("");
        }

        speedTest();

    }

    public static void speedTest() {
        RandomGenerator rng = null;
        double[][] r = new double[2][];
//        rng = new Well19937c(1);
        rng = new Well44497b(1);
        r[0] = speedTest(rng);
        rng = new XorShift1024Star(1);
        r[1] = speedTest(rng);
        for (int i = 0; i < r[0].length; i++) {
            System.out.println(r[0][i] / r[1][i]);
        }
    }

    public static double[] speedTest(RandomGenerator rng) {
        final int WARM_UP = 10000;
        final int S = 40;
        final int N = 1000000;
        double[] times = new double[S];
        for (int j = 0; j < S; j++) {

            System.gc();
            for (int i = 0; i < WARM_UP; i++) {
                rng.nextDouble();
            }
            long start = System.nanoTime();
            for (int i = 0; i < N; i++) {
                rng.nextDouble();
            }
            double time = System.nanoTime() - start;
            System.out.println("" + rng.getClass().getCanonicalName() + " " + time / 1000);
            times[j] = time;
        }
        Arrays.sort(times);
        return times;
    }
}
