/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.joulupunikki.math.random;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorAbstractTest;

/**
 *
 * @author joulupunikki joulupunikki@gmail.communist.invalid
 */
public class BitsStreamGenerator64SeedTest extends RandomGeneratorAbstractTest {

    public BitsStreamGenerator64SeedTest() {
    }

    @Override
    protected RandomGenerator makeGenerator() {
        RandomGenerator generator = new TestBitStreamGenerator64Seed();
        generator.setSeed(1);
        return generator;
    }

    /**
     * Test BitStreamGenerator using seed hasher as bit source.
     */
    static class TestBitStreamGenerator64Seed extends BitsStreamGenerator64 {

        private static final long serialVersionUID = 1L;
        private int seed_left = 0;
        private int counter = 0;
        private long[] seed = null;
        private long[] tmp = new long[1];
        private int int_left = 0;
        private boolean is_int_left = false;

        public TestBitStreamGenerator64Seed() {
            STATE_BITS = 1024;
            STATE_WORDS = STATE_BITS / WORD_BITS;
        }

        @Override
        public void setSeed(int seed) {
            counter = seed;
            this.seed = null;
            seed_left = 0;
            is_int_left = false;
            clear();
        }

        @Override
        public void setSeed(int[] seed) {
            counter = seed[0];
            this.seed = null;
            seed_left = 0;
            is_int_left = false;
        }

        @Override
        public void setSeed(long seed) {
            counter = (int) seed;
            this.seed = null;
            seed_left = 0;
            is_int_left = false;
        }

        @Override
        protected int next(int bits) {
            moreSeed();
            if (is_int_left) {
                is_int_left = false;
                return int_left >>> (Integer.SIZE - bits);
            } else {
                is_int_left = true;
                int_left = (int) ((seed[STATE_WORDS - (seed_left)]) >>> Integer.SIZE);
                return ((int) seed[STATE_WORDS - (seed_left--)]) >>> (Integer.SIZE - bits);
            }
            
        }

        public void setStateWordCount(int count) {
            STATE_WORDS = count;
        }

        @Override
        protected long nextL(int bits) {
            moreSeed();
            return seed[STATE_WORDS - (seed_left--)] >>> (Long.SIZE - bits);
        }

        private void moreSeed() {
            if (seed_left == 0) {
                tmp[0] = ++counter;
                seed = hashSeed(tmp);
                seed_left = STATE_WORDS;
//                for (int i = 0; i < seed.length; i++) {
//                    System.out.println(seed[i]);
//                }
//                System.out.println(counter);
            }

        }

        @Override
        public void setState(Object state) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
