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

import java.util.Random;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorAbstractTest;

/**
 * Test cases for the BitStreamGenerator64 class, adapted from
 * BitsStreamGeneratorTest
 *
 */

public class BitsStreamGenerator64Test extends RandomGeneratorAbstractTest {

    public BitsStreamGenerator64Test() {
        super();
    }
    
    @Override
    protected RandomGenerator makeGenerator() {
        RandomGenerator generator = new TestBitStreamGenerator64();
        generator.setSeed(1000);
        return generator;
    }
    
    /**
     * Test BitStreamGenerator using a Random as bit source.
     */
    static class TestBitStreamGenerator64 extends BitsStreamGenerator64 {

        private static final long serialVersionUID = 1L;
        private BitRandom ran = new BitRandom();

        @Override
        public void setSeed(int seed) {
           ran.setSeed(seed);
           clear();
        }

        @Override
        public void setSeed(int[] seed) {
            ran.setSeed(seed[0]);
        }

        @Override
        public void setSeed(long seed) {
            ran.setSeed((int) seed);

        }

        @Override
        protected int next(int bits) {
            return ran.nextBits(bits);
        }

        public void setStateWordCount(int count) {
            STATE_WORDS = count;
        }

        @Override
        protected long nextL(int bits) {
            return (((ran.nextBits(Integer.SIZE) & LSB32_MASK_LONG)
                    << Integer.SIZE) | (ran.nextBits(Integer.SIZE) & LSB32_MASK_LONG)) >>> (Long.SIZE - bits);
        }

        @Override
        public void setState(Object state) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * Extend Random to expose next(bits)
     */
    @SuppressWarnings("serial")
    static class BitRandom extends Random {
        public BitRandom() {
            super();
        }
        public int nextBits(int bits) {
            return next(bits);
        }
    }
//    @Test
//    public void testSeed() {
//        TestBitStreamGenerator64 gen = new TestBitStreamGenerator64();
//        gen.setStateWordCount(200000000);
//        int[] t = gen.sineSeed(1);
//
//    }
}
