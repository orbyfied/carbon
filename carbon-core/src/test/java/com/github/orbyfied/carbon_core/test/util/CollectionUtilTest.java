package com.github.orbyfied.carbon_core.test.util;

import net.orbyfied.carbon.util.CollectionUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class CollectionUtilTest {

    public static <T> void printarr(T[] arr) {
        System.out.println("len: " + arr.length + " " + Arrays.toString(arr));
    }

    @Test
    public void testResize() {

        String[] myArray = new String[] { // len = 5
                "hi",
                "hello",
                "bonjour",
                "fuckyou",
                "sussybaka"
        };

        printarr(myArray);

        myArray = CollectionUtil.resize(myArray, 7); // len = 7

        printarr(myArray);

        myArray[5] = "amogus";
        myArray[6] = "sugoma";

        printarr(myArray);

    }

}
