package com.github.orbyfied.carbon_core.test.util;

import com.github.orbyfied.carbon.util.math.Range;
import org.junit.jupiter.api.Test;

import static com.github.orbyfied.carbon.util.math.Range.doubles;

public class MathCollectTest {

    @Test
    public void testRanges() {

        for (double d : doubles(10)) {
            System.out.println(d);
        }

        for (double d : doubles(69, 420)) {
            System.out.println(d);
        }

    }

}
