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

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorAbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author joulupunikki joulupunikki@gmail.communist.invalid
 */
public class XorShift1024StarTest extends RandomGeneratorAbstractTest {

    private static final long[][] results = {null,
        /* results[1]: first 32 nextLongs after setSeed(1) */
        {-6731447495507462773L, -4321251632094009357L, 3846836475403551797L, -4179190190310888026L,
            -5163379872604841636L, 2417303607326379667L, -4928126629782966818L, -8940780735456425959L,
            -659000017206460108L, -7665203889457398303L, 7051262881396637590L, 5974633292067792670L,
            -8673364396878274543L, -7186666509809081671L, 1227667337782302289L, -7279269352365692392L,
            6697831296367869562L, -6784525060781272905L, -7722326457878381776L, 8085937602027231291L,
            -3577466533015016859L, -112636314330332566L, 3107984479231801009L, 4187632844800328577L,
            2222621926784930696L, 1069576622355981787L, -2796236617197757262L, 8413919051106068350L,
            -3909802252312361105L, -6776596902021419042L, 8336832711139581716L, 4009155462843208868L
        },
        /*results[2]:  first 32 nextLongs after setSeed(2) */
        {3741884067842678063L, 9211058367417016686L, 4310264177111795301L, 8495810595752488426L,
            5310401389409799249L, 6369174492861305399L, 8298342156702042535L, -7175115937274401711L,
            -5378736402258197818L, -8139366996593959192L, 3097874634342027283L, -8288693267343797180L,
            -8166763890767911679L, -7472456253628252349L, -7178976084420842350L, -5040339783423211459L,
            -9219954574812657150L, 8933352253303372971L, -3419837047032535852L, 9078237528846752214L,
            -7630680471490647612L, -8779685459654430552L, 6537278255890202306L, -2238300555710332632L,
            6969394351338934998L, 9182090002316229827L, -3109900018583170459L, 6085393688596271351L,
            6826101381379634991L, 8399916363103176841L, 6544846483277877029L, -8539021703400810817L
        }
    };

    @Override
    public RandomGenerator makeGenerator() {
        return new XorShift1024Star(1);
    }

    public XorShift1024StarTest() {
    }

    @Test
    public void testNextLong() {
        XorShift1024Star instance = new XorShift1024Star(1);
        for (int i = 0; i < results[1].length; i++) {
            Assert.assertEquals(results[1][i], instance.nextLong());
        }
        instance.setSeed(2);
        for (int i = 0; i < results[2].length; i++) {
            Assert.assertEquals(results[2][i], instance.nextLong());
        }
    }

    @Test
    public void testSeed() {
        // identical seed elements should create different states because of
        // hash chaining
        XorShift1024Star instance = new XorShift1024Star(new int[]{1, 1});
        long[] s0 = (long[]) instance.getState();
        instance.setSeed(new int[]{2, 2});
        long[] s1 = (long[]) instance.getState();
        Assert.assertNotEquals(s0[0], s0[8]);
        Assert.assertNotEquals(s1[0], s1[8]);
        Assert.assertNotEquals(s0[0], s1[0]);
        // the first state long should differ with different integer seeds
        instance.setSeed(1);
        s0 = (long[]) instance.getState();
        instance.setSeed(2);
        s1 = (long[]) instance.getState();
        Assert.assertNotEquals(s0[0], s1[0]);
        // identical value seeds of int and long => identical state
        instance.setSeed(1L);
        s1 = (long[]) instance.getState();
        Assert.assertEquals(s0[0], s1[0]);
        // sequent null seeds should result in differing states
        // this should fail randomly once in 2^512
        int[] null_array = null;
        instance.setSeed(null_array);
        s0 = (long[]) instance.getState();
        instance.setSeed(null_array);
        s1 = (long[]) instance.getState();
        Assert.assertNotEquals(s0, s1);
    }
}
